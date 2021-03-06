package com.elementalist.enose.ui.screens

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ConnectionScreen(viewModel: MainViewModel) {
    //we observe some viewModel's variables to dynamically change the screen
    val buttonAction = viewModel.buttonAction
    val buttonText = viewModel.buttonText
    val displayedText = viewModel.displayedText
    val imageShown = viewModel.image
    val state = viewModel.connectionState

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
        //Button for re-listening for data after a result
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
            when (state) {
                StatesOfConnection.RESPONSE_RECEIVED -> {
                    Image(
                        painter = painterResource(imageShown),
                        contentDescription = "Result from sniffing",
                        contentScale = ContentScale.FillWidth
                    )
                }
                StatesOfConnection.CLIENT_STARTED -> {
                    CircularProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
                else -> {
                    //on error leave blank
                }
            }

        }

    }

}
