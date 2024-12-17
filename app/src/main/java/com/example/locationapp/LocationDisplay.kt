package com.example.locationapp

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.widget.Toast
import androidx.core.app.ActivityCompat

@Composable
fun LocationDisplay(locationUtils: LocationUtils, context: Context, viewModel: LocationViewModel) {
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            ) {
                locationUtils.requestLocationUpdates(viewModel = viewModel)
            } else {
                val rationaleRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )

                if (rationaleRequired){
                    Toast.makeText(context,
                        "Location Permission is required for this feature to work", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context,
                        "Location Permission is required. Please enable it in the Android Settings", Toast.LENGTH_LONG).show()
                }

            }
        }
    )


    val location = viewModel.location.value
    val address = location?.let {
        locationUtils.reverseGeocodeLocation(location)
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        if (location != null) {
            Text(text = "Address: ${location.latitude} ${location.longitude}\n $address")
        }
        else {
            Text("Location not available")
        }

        Button(onClick = {
            if (locationUtils.hasLocationPermission(context)) {
                Toast.makeText(context, "Permission already granted!", Toast.LENGTH_SHORT).show()
                locationUtils.requestLocationUpdates(viewModel)
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                    )
                )
            }
        }) {
            Text("Get Location")
        }
    }
}
