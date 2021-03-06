package com.elementalist.enose.ui.screens

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.lifecycle.ViewModel
import com.elementalist.enose.data.ConnectThread
import com.elementalist.enose.util.MY_TAG
import com.elementalist.enose.R.drawable.nok
import com.elementalist.enose.R.drawable.ok

class MainViewModel(
    private val bluetoothAdapter: BluetoothAdapter
) : ViewModel() {

    var discoveredDevices = mutableStateListOf<BluetoothDevice>()
        private set

    fun addDiscoveredDevice(device: BluetoothDevice) {
        if (!discoveredDevices.contains(device)) {
            discoveredDevices.add(device)
        }
    }

    var scanningForDevices = mutableStateOf(false)
        private set

    fun scanningFinished() {
        scanningForDevices.value = false
    }

    @SuppressLint("MissingPermission")
    fun scanForDevices() {
        scanningForDevices.value = true
        //savedPairedDevices = bluetoothAdapter.bondedDevices
        if (bluetoothAdapter.isDiscovering) {
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter.startDiscovery()
        Log.i(MY_TAG, "discovery started")
    }

    var selectedDevice by mutableStateOf<BluetoothDevice?>(null)
        private set

    fun selectDevice(device: BluetoothDevice) {
        selectedDevice = device
    }

    /**
     * Connect to the selected device and start a server socket connection
     * and then listen to data from connected socket
     */
    @SuppressLint("MissingPermission")
    fun startBluetoothService(){
        changeStateOfConnectivity(StatesOfConnection.CLIENT_STARTED)
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter.cancelDiscovery()
        //Listen for data from selected device
        selectedDevice?.let {
            ConnectThread(it, this).start()
        }
    }

    var displayedText by mutableStateOf(AnnotatedString(""))
        private set

    var buttonText by mutableStateOf("")
        private set

    var buttonAction by mutableStateOf({})

    var image by mutableStateOf(0)
        private set

    var connectionState by mutableStateOf(StatesOfConnection.CLIENT_STARTED)
        private set

    @SuppressLint("MissingPermission")
    fun changeStateOfConnectivity(
        newState: StatesOfConnection,
        dataReceived: String? = null
    ) {
        connectionState = newState
        when (newState) {
            StatesOfConnection.CLIENT_STARTED -> {
                val annotatedText = buildAnnotatedString {
                    append("Listening for data from the device:\n")
                    withStyle(style = SpanStyle(color = Color.Blue)) {
                        append(selectedDevice!!.name)
                    }
                }
                displayedText = annotatedText
                buttonText = ""
                image = 0
            }
            StatesOfConnection.RESPONSE_RECEIVED -> {
                when {
                    dataReceived.equals("1") -> {
                        val annotatedText = buildAnnotatedString {
                            append("Your food is ")
                            withStyle(style = SpanStyle(color = Color.Green)) {
                                append("good for consumption")
                            }
                        }
                        displayedText = annotatedText
                        //buttonText = "Listen again for messages from raspberry?"
                        //buttonAction = { startBluetoothService() }
                        buttonText = ""
                        image = ok
                    }
                    dataReceived.equals("0") -> {
                        val annotatedText = buildAnnotatedString {
                            append("You should ")
                            withStyle(style = SpanStyle(color = Color.Green)) {
                                append("NOT")
                            }
                            append(" eat that food")
                        }
                        displayedText = annotatedText
                        //buttonText = "Listen again for messages from raspberry?"
                        //buttonAction = { startBluetoothService() }
                        buttonText = ""
                        image = nok
                    }
                    else -> {
                        val annotatedText = buildAnnotatedString {
                            append("Not correct response message:")
                            withStyle(style = SpanStyle(color = Color.LightGray)) {
                                if (dataReceived != null) {
                                    append(dataReceived)
                                }
                            }
                        }
                        displayedText = annotatedText
                        buttonText = ""
                    }
                }
            }
            StatesOfConnection.ERROR -> {
                buttonText = "Restart server?"
                buttonAction = { startBluetoothService() }
                val annotatedText = buildAnnotatedString {
                    append("An error occurred:")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        if (dataReceived != null) {
                            append(dataReceived)
                        }
                    }
                }
                displayedText = annotatedText
                image = 0
            }
        }
    }

}

