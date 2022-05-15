package com.elementalist.enose.Client


import androidx.compose.foundation.clickable
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun ListedDeviceItem(
    deviceName: String,
    onItemClick: () -> Unit
) {
    Card(modifier = Modifier.clickable { onItemClick() }) {
        Text(text = deviceName)
    }
}