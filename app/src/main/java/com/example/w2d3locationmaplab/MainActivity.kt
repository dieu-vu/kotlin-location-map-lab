package com.example.w2d3locationmaplab

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.w2d3locationmaplab.ui.theme.W2D3LocationMapLabTheme
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.location.LocationResult.create
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.libraries.places.api.Places
import com.google.maps.android.compose.*


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient:
            FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback


    override fun onCreate(savedInstanceState: Bundle?) {
//        checkPermission()
        super.onCreate(savedInstanceState)
        Places.initialize(this, BuildConfig.apiKey)
        Log.d("GEOLOCATION", "apiKey ${BuildConfig.apiKey}")


        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        Log.i("GEOLOCATION permission", Manifest.permission.ACCESS_COARSE_LOCATION)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                0
            )
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                Log.d(
                    "GEOLOCATION",
                    "last location latitude: ${it?.latitude} and longitude: ${it?.longitude}"
                )
            }
        }


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return
                for (location in p0.locations) {
                    Log.d(
                        "GEOLOCATION",
                        "location latitude:${location.latitude} and longitude: ${location.longitude}"
                    )
                }
            }
        }

        setContent {
            W2D3LocationMapLabTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    var mapProperties by remember {
                        mutableStateOf(
                            MapProperties(maxZoomPreference = 10f, minZoomPreference = 5f)
                        )
                    }
                    var mapUiSettings by remember {
                        mutableStateOf(
                            MapUiSettings(mapToolbarEnabled = false)
                        )
                    }
                    val cameraPositionState = rememberCameraPositionState{
                        position = CameraPosition.fromLatLngZoom(LatLng(60.16, 24.93), 30f)
                    }


                    Box(modifier = Modifier.fillMaxSize()) {
                            GoogleMap(
                                properties = mapProperties,
                                uiSettings = mapUiSettings,
                                cameraPositionState = cameraPositionState
                            )
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            )
                            {
                            Button(onClick = {
                                val locationRequest = LocationRequest.create()
                                    .setInterval(1000)
                                    .setPriority(PRIORITY_HIGH_ACCURACY)
                                fusedLocationClient.requestLocationUpdates(
                                    locationRequest,
                                    locationCallback, Looper.getMainLooper()
                                )
                            }) {
                                Text("Start tracking")
                            }
                            Button(onClick = {
                                fusedLocationClient.removeLocationUpdates(locationCallback)
                            }) {
                                Text("Stop tracking")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    W2D3LocationMapLabTheme {
        Greeting("Android")
    }
}
