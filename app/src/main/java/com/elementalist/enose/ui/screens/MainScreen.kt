package com.elementalist.enose.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.elementalist.enose.ui.model.ListedDeviceItem
import com.elementalist.enose.util.MY_TAG
import com.elementalist.enose.util.askPermissions
import com.elementalist.enose.util.askSinglePermission
import com.elementalist.enose.util.requiredPermissionsInitialClient

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel
) {

    val context = LocalContext.current

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission Accepted: Do something
                viewModel.scanForDevices()
            } else {
                // Permission Denied: Do something
                Log.i(MY_TAG, "Permission Denied")
                Toast.makeText(
                    context,
                    "You must manually select the option 'Allow all the time' for location in order for this app to work!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    /**
     * Requesting the Manifest.permission.ACCESS_BACKGROUND_LOCATION permission after
     * the Manifest.permission.ACCESS_COARSE_LOCATION has been granted
     *
     */
    fun extraLocationPermissionRequest() {
        askSinglePermission(
            locationPermissionLauncher,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            context
        ) {
            viewModel.scanForDevices()
        }
    }

    val multiplePermissionLauncher =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                Log.i(MY_TAG, "Launcher result: $permissions")
                if (permissions.containsValue(false)) {
                    Log.i(MY_TAG, "At least one of the permissions was not granted.")
                    Toast.makeText(
                        context,
                        "At least one of the permissions was not granted. Go to app settings and give permissions manually",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    //do something
                    viewModel.scanForDevices()
                }
            }
        } else {
            rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                Log.i(MY_TAG, "Launcher result: $permissions")
                if (permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
                    //permission for location was granted.
                    //we direct the user to select "Allow all the time option"
                    Toast.makeText(
                        context,
                        "You must select the option 'Allow all the time' for the app to work",
                        Toast.LENGTH_SHORT
                    ).show()
                    extraLocationPermissionRequest()
                } else {
                    Toast.makeText(
                        context,
                        "Location permission was not granted. Please do so manually",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    val discoveredDevices = viewModel.discoveredDevices
    val selectedDevice = viewModel.selectedDevice
    val scanningActive = viewModel.scanningForDevices

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {

        Text(
            text = "Electronic nose app for showing the result of a measurement from an external device (raspberry pi).",
            fontStyle = FontStyle.Italic,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.padding(3.dp))

        Text(
            text = "Press scan to connect to an external device and start",
            textAlign = TextAlign.Center
        )

        Spacer(
            modifier = Modifier
                .padding(5.dp)
                .background(Color.DarkGray)
        )

        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = {
                //check for permissions and launch scanning after they are granted
                askPermissions(
                    multiplePermissionLauncher,
                    requiredPermissionsInitialClient,
                    context
                ) { viewModel.scanForDevices() }
            }) {
            Text(text = "Scan for devices")
        }

        Spacer(modifier = Modifier.padding(5.dp))

        if (scanningActive.value) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Spacer(modifier = Modifier.padding(5.dp))

        if (discoveredDevices.isNotEmpty()) {

            Column(
                Modifier
                    .fillMaxWidth()
                    .border(3.dp, MaterialTheme.colors.primaryVariant)
            ) {
                Text(
                    text = "Discovered Devices",
                    style = TextStyle(textDecoration = TextDecoration.Underline),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.padding(3.dp))

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(discoveredDevices) { device ->
                        ListedDeviceItem(
                            deviceName = device.name,
                            selected = viewModel.selectedDevice == device
                        ) {
                            viewModel.selectDevice(device)
                        }
                        Divider(Modifier.padding(3.dp), color = Color.Green)
                    }
                }
            }
        }

        if (selectedDevice != null) {
            Button(
                modifier = Modifier.padding(top = 20.dp),
                onClick = {
                    viewModel.startBluetoothService()
                    navController.navigate("connection_screen")
                }) {
                Text(text = "Listen for data sent from: ${selectedDevice.name}")
            }
        }
    }
}






