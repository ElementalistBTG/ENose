package com.elementalist.enose

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
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

    @SuppressLint("MissingPermission")
    fun scanForDevices() {
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
     * Connect to the selected device and send data
     *
     */
    @SuppressLint("MissingPermission")
    fun listenForData() {
        // Cancel discovery because it otherwise slows down the connection.
        bluetoothAdapter.cancelDiscovery()
        //Send data to selected device
        selectedDevice?.let {
            ConnectThread(it,this).start()
        }
    }

    var displayedText by mutableStateOf("Permissions granting phase")
        private set

    var buttonText by mutableStateOf("")
        private set

    var buttonAction by mutableStateOf({})

    var image by mutableStateOf(0)
        private set

    fun changeStateOfServer(
        newState: StatesOfServer,
        dataReceived: String? = null
    ) {
        when (newState) {
            StatesOfServer.APP_STARTED -> {
                displayedText = "Press button to start listening. Be sure to allow all permissions"
                buttonText = "Open server socket"
                buttonAction = { listenForData() }
            }
            StatesOfServer.SERVER_STARTED -> {
                displayedText = "Server is listening for connections..."
                buttonText = "Restart Server?"
                buttonAction = { listenForData() }
                image = 0
            }
            StatesOfServer.RESPONSE_RECEIVED -> {
                when {
                    dataReceived.equals("1") -> {
                        displayedText = "Your food is good for consumption"
                        buttonText = "Listen again for messages from raspberry?"
                        buttonAction = { listenForData() }
                        image = ok
                    }
                    dataReceived.equals("0") -> {
                        displayedText = "You should NOT eat that food"
                        buttonText = "Listen again for messages from raspberry?"
                        buttonAction = { listenForData() }
                        image = nok
                    }
                    else -> {
                        displayedText = "Not correct response message: $dataReceived"
                        buttonText = ""
                    }
                }
            }
            StatesOfServer.ERROR -> {
                buttonText = ""
                displayedText = "An error occurred: $dataReceived"
                image = 0
            }
        }
    }

}

