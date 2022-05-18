package com.elementalist.enose.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import com.elementalist.enose.util.MY_TAG
import com.elementalist.enose.util.myUuid
import com.elementalist.enose.ui.screens.MainViewModel
import com.elementalist.enose.ui.screens.StatesOfConnection
import java.io.IOException

/**
 * Thread for receiving data once from passed socket
 *
 * @property socket
 * @property viewModel
 */
class BluetoothService(
    private val socket: BluetoothSocket,
    private val viewModel: MainViewModel
) : Thread() {
    private val inputStream = socket.inputStream

    //We only need 1Byte for reading 0 or 1 from raspberry result
    private val buffer = ByteArray(1)
    override fun run() {
        // Keep listening to the InputStream until an exception occurs.
        while (true) {
            try {
                //Read from the InputStream
                inputStream.read(buffer)
            } catch (e: IOException) {
                Log.i(MY_TAG, "Input stream was disconnected", e)
                break
            }
            // Send the obtained bytes to the UI activity.
            val text = String(buffer)
            viewModel.changeStateOfConnectivity(
                newState = StatesOfConnection.RESPONSE_RECEIVED,
                dataReceived = text
            )
        }
    }
}

/**
 * A Thread to create a connection as a Client to an existing Server (the device we pass)
 *
 * @property viewModel
 * @constructor
 *
 *
 * @param device
 */
@SuppressLint("MissingPermission")
class ConnectThread(
    device: BluetoothDevice,
    private val viewModel: MainViewModel
) : Thread() {
    private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
        //device.createInsecureRfcommSocketToServiceRecord(myUuid)
        device.createRfcommSocketToServiceRecord(myUuid)
    }

    override fun run() {
        mmSocket?.let { socket ->
            //Connect to the remote device through the socket.
            // This call blocks until it succeeds or throws an exception
            try {
                Log.i(MY_TAG, "attempting connection")
                socket.connect()
                Log.i(MY_TAG, "connection success")
            } catch (e: Exception) {
                Log.i(MY_TAG, "connection was not successful")
                viewModel.changeStateOfConnectivity(
                    StatesOfConnection.ERROR,
                    "Error on connectivity: $e"
                )
            }
            //The connection attempt succeeded.
            //Perform work associated with the connection in a separate thread
            BluetoothService(socket, viewModel).start()
        }
    }

    // Closes the connect socket and causes the thread to finish.
    fun cancel() {
        try {
            mmSocket?.close()
        } catch (e: IOException) {
            Log.e(MY_TAG, "Could not close the connect socket", e)
        }
    }
}