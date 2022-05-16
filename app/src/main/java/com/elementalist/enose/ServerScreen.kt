package com.elementalist.enose

import android.bluetooth.BluetoothManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getSystemService
import org.intellij.lang.annotations.JdkConstants

@Composable
fun ServerScreen(viewModel: MainViewModel) {
    //we observe some viewModel's variables to dynamically change the screen
    val buttonAction = viewModel.buttonAction
    val buttonText = viewModel.buttonText
    val displayedText = viewModel.displayedText
    val imageShown = viewModel.image

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
            .border(5.dp, MaterialTheme.colors.secondary)
            .padding(5.dp),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        //Text for informing the user
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .weight(1f, true)
        ) {
            Text(text = displayedText, textAlign = TextAlign.Center)
        }
        //Button for re-listening for data
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, true)
        ) {
            //hide button if it is not needed
            if (buttonText != "") {
                Button(modifier = Modifier.align(Alignment.Center), onClick = { buttonAction() }) {
                    Text(text = buttonText)
                }
            }
        }
        //Loading / Error / OK-Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(6f, true)
                .align(Alignment.CenterHorizontally)
        ) {
            //We hide the image-result when it is not needed
            if (imageShown != 0) {
                Image(
                    painter = painterResource(imageShown),
                    contentDescription = "Result from sniffing",
                    contentScale = ContentScale.FillWidth
                )
            }else{
                CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

        }

    }

}

@Preview
@Composable
fun ServerScreenPreview() {
    val context = LocalContext.current
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter = bluetoothManager.adapter
    val viewModel = MainViewModel(bluetoothAdapter)
    ServerScreen(viewModel = viewModel)
}