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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.w2d3locationmaplab.ui.theme.W2D3LocationMapLabTheme
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.location.LocationResult.create
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.libraries.places.api.Places
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings


class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient:
            FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback


    override fun onCreate(savedInstanceState: Bundle?) {
        checkPermissions()
        super.onCreate(savedInstanceState)
        Places.initialize(this, BuildConfig.apiKey)
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                Log.d(
                    "GEOLOCATION",
                    "last location latitude: ${it?.latitude} and longitude: ${it?.longitude}"
                )
            }
        }
        locationCallback = object: LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return
                for (location in p0.locations) { Log.d("GEOLOCATION",
                    "location latitude:${location.latitude} and longitude: ${location.longitude}" )
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
                    Greeting("Android")
//                    GoogleMap(
//                        modifier = Modifier.fillMaxSize(),
//                        uiSettings = MapUiSettings(zoomControlsEnabled = true),
//                        cameraPositionState = CameraPositionState(CameraPosition(LatLng(37.42, -122.08),12f,0f,0f))
//                    ){}
                    Column(verticalArrangement = Arrangement.Center){
                        Button(onClick = {
                            val locationRequest = LocationRequest.create()
                                .setInterval(1000)
                                .setPriority(PRIORITY_HIGH_ACCURACY)
                            fusedLocationClient.requestLocationUpdates(locationRequest,
                                locationCallback, Looper.getMainLooper())
                        }){
                            Text("Start tracking")
                        }
                        Button(onClick = {
                            fusedLocationClient.removeLocationUpdates(locationCallback)
                        }){
                            Text("Stop tracking")
                        }
                    }

                }
            }
        }
    }

    private fun checkPermissions (){
        if ( ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION,

            ) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                0)
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
