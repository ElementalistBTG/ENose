package com.elementalist.enose

import android.Manifest
import android.os.Build
import java.util.*

//For a RFComm connection to exist we use a hardcoded UUID
val myUuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
//tag used for logging purposes
const val MY_TAG = "mytag"
//Since the permissions needed for this app are fixed we define them here
val requiredPermissionsInitialClient =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }