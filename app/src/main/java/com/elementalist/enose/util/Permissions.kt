package com.elementalist.enose

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat

/**
 * General function for requesting permissions from an array
 *
 * @param multiplePermissionLauncher
 * An ActivityResultLauncher passed to be invoked using 'multiplePermissionLauncher.launch(requiredPermissions)'
 *
 * @param requiredPermissions
 * An array of Strings defining permissions
 *
 * @param context
 * In order to invoke ActivityCompat.checkSelfPermission we require a context to be passed down
 *
 * @param actionIfAlreadyGranted
 * A function that will be invoked if permissions are already granted
 *
 */
fun askPermissions(
    multiplePermissionLauncher: ActivityResultLauncher<Array<String>>,
    requiredPermissions: Array<String>,
    context: Context,
    actionIfAlreadyGranted: () -> Unit
) {
    if (!hasPermissions(requiredPermissions, context)) {
        //Launching multiple contract permission launcher for ALL the required permissions
        multiplePermissionLauncher.launch(requiredPermissions)
    } else {
        //All permissions are already granted
        actionIfAlreadyGranted()
    }
}

/**
 * Use to ask a single Permission
 *
 * @param singlePermissionLauncher
 * @param permission
 * @param context
 * @param actionIfAlreadyGranted
 */
fun askSinglePermission(
    singlePermissionLauncher: ActivityResultLauncher<String>,
    permission: String,
    context: Context,
    actionIfAlreadyGranted: () -> Unit
) {
    if (!hasSinglePermission(permission, context)) {
        //Launching contract permission launcher for the required permissions
        singlePermissionLauncher.launch(permission)
    } else {
        //Permission is already granted so we execute the actionIfAlreadyGranted
        actionIfAlreadyGranted()
    }
}

/**
 * Checks if all permissions are granted. Returns true is all are granted or false if at least one permission is not granted
 */
fun hasPermissions(permissions: Array<String>?, context: Context): Boolean {
    if (permissions != null) {
        for (permission in permissions) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //Permission is not granted
                return false
            }
            //Permission already granted
        }
        return true
    }
    return false
}

/**
 * Checks if a specific permission is granted
 */
fun hasSinglePermission(permission: String, context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

/**
 * Check if location services are enabled
 *
 * @param context
 * @return
 */
fun isLocationEnabled(context: Context): Boolean {
    val locationManager =
        context.getSystemService(ComponentActivity.LOCATION_SERVICE) as LocationManager
    return locationManager.isLocationEnabled
}

/**
 * Take the user to location settings to enable location services
 *
 * @param activity
 */
fun enableLocation(activity: Activity) {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    activity.startActivity(intent)
}


