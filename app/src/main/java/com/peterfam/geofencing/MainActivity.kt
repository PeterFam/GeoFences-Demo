package com.peterfam.geofencing

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    companion object {
        const val CHANNEL_NAME = "High priority channel"
        const val CHANNEL_ID = "Channel_001"
    }

    private lateinit var mMap: GoogleMap
    private lateinit var geoFencingClient: GeofencingClient
    private lateinit var geoFenceHelper: GeoFenceHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
        // Get the SupportMapFragment and request notification when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment!!.getMapAsync(this)

        geoFencingClient = LocationServices.getGeofencingClient(this)
        geoFenceHelper = GeoFenceHelper(this)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap!!
        mMap.apply {
//            val sydney = LatLng(-33.852, 151.211)
//            addMarker(
//                MarkerOptions()
//                    .position(sydney)
//                    .title("Marker in Sydney")
//            )
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16f))
            enableUserLocation()

            mMap.setOnMapLongClickListener(this@MainActivity)
        }
    }

    private fun enableUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 200
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 200
                )
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 200) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.isMyLocationEnabled = true
            } else {

            }
        }

        if (requestCode == 201) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //We have the permission
                Toast.makeText(this, "You can add geofences...", Toast.LENGTH_SHORT).show();
            } else {
                //We do not have the permission..
                Toast.makeText(
                    this,
                    "Background location access is neccessary for geofences to trigger...",
                    Toast.LENGTH_SHORT
                ).show();
            }
        }

    }

    override fun onMapLongClick(latLng: LatLng?) {
        mMap.clear()
        addMarker(latLng)
        addCircle(latLng, 200.0)
        addGeofence(latLng, 200f)
    }

    private fun addMarker(latLng: LatLng?) {
        mMap.addMarker(MarkerOptions().position(latLng!!))
    }

    private fun addCircle(latLng: LatLng?, radius: Double) {
        mMap.addCircle(
            CircleOptions()
                .center(latLng)
                .radius(radius)
                .strokeColor(Color.argb(225, 225, 0, 0))
                .fillColor(Color.argb(65, 225, 0, 0))
                .strokeWidth(4f)
        )
    }

    @SuppressLint("MissingPermission")
    private fun addGeofence(latLng: LatLng?, radius: Float) {
        val geofence: Geofence = geoFenceHelper.getGeofence(
            "SOME_GEOFENCE_ID",
            latLng!!,
            radius,
            Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
        )!!
        val geofencingRequest: GeofencingRequest = geoFenceHelper.getGeofencingRequest(geofence)!!
        val pendingIntent: PendingIntent = geoFenceHelper.getPendingIntent()
        geoFencingClient.addGeofences(geofencingRequest, pendingIntent)
            .addOnSuccessListener {
                Log.d(
                    "MainActivity",
                    "onSuccess: Geofence Added..."
                )
            }
            .addOnFailureListener { e ->
                val errorMessage: String = geoFenceHelper.getErrorString(e)!!
                Log.d("MainActivity", "onFailure: $errorMessage")
            }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannels() {
        val notificationChannel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.enableLights(true)
        notificationChannel.enableVibration(true)
        notificationChannel.description = "this is the description of the channel."
        notificationChannel.lightColor = Color.RED
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        manager!!.createNotificationChannel(notificationChannel)
    }
}