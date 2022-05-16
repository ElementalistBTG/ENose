package com.elementalist.enose

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.elementalist.enose.ui.theme.ENoseTheme


class MainActivity : ComponentActivity() {

    lateinit var bluetoothAdapter: BluetoothAdapter
    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter

        if (!bluetoothAdapter.isEnabled) {
            enableBluetooth()
        }
        //This specific action is required since my personal mobile needs GPS enabled to discover devices
        //(not written in any official documentation but needed nonetheless)
        if (!isLocationEnabled(this) && Build.VERSION.SDK_INT <= 30) {
            enableLocation(this)
        }

        viewModel = MainViewModel(bluetoothAdapter)

        // Register for broadcasts when a device is discovered
        val filter = IntentFilter()
        //register a broadcast receiver to check if the user disables his Bluetooth (or it has it already disabled)
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        //receivers for device discovering
        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(mReceiver, filter)

        setContent {
            ENoseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    NavigationGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()

        // Make sure we're not doing discovery anymore
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }

        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver)
    }

    private val mReceiver = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                //when discovery finds a device
                BluetoothDevice.ACTION_FOUND -> {
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    Log.i(MY_TAG, "device found")
                    if (device != null &&
                        device.name != null
                    ) {
                        viewModel.addDiscoveredDevice(device)
                    }
                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.i(MY_TAG, "ACTION_DISCOVERY_FINISHED")
                    //if there are no device show proper message
                    if (viewModel.discoveredDevices.isEmpty()) {
                        Toast.makeText(
                            applicationContext,
                            "Unfortunately no devices were found in your vicinity",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(applicationContext, "Scan finished", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    //Since our app needs bluetooth to work correctly we don't let the user turn it off
                    if (bluetoothAdapter.state == BluetoothAdapter.STATE_OFF
                    ) {
                        enableBluetooth()
                    }
                }
            }
        }
    }

    private val enableBluetoothResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth Enabled!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Bluetooth is required for this app to run", Toast.LENGTH_SHORT)
                .show()
            this.finish()
        }
    }

    /**
     * Pop-up activation for enabling Bluetooth
     *
     */
    private fun enableBluetooth() {
        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        enableBluetoothResultLauncher.launch(enableBtIntent)
    }


}

