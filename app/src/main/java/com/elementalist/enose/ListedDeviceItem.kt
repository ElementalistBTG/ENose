package com.elementalist.enose.Client


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Transparent

import androidx.compose.ui.unit.dp


@Composable
fun ListedDeviceItem(
    deviceName: String,
    selected: Boolean,
    onItemClick: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .clickable {
                onItemClick(deviceName)
            }
            .background(if (selected) MaterialTheme.colors.secondary else Transparent)
            .fillMaxWidth()
            .padding(12.dp)
    )
    {
        Text(text = deviceName)
    }
}