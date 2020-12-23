package com.peterfam.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeoFenceBroadcastReceiver: BroadcastReceiver() {
    private val TAG = "GeofenceBroadcastReceiv"

    override fun onReceive(context: Context?, intent: Intent?) {
        // an Intent broadcast.
        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();

        // an Intent broadcast.
//        Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();
        val notificationHelper = NotificationHelper(context!!)

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent.hasError()) {
            Log.d(TAG, "onReceive: Error receiving geofence event...")
            return
        }

        val geofenceList = geofencingEvent.triggeringGeofences
        for (geofence in geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.requestId)
        }
//        Location location = geofencingEvent.getTriggeringLocation();
        //        Location location = geofencingEvent.getTriggeringLocation();
        val transitionType = geofencingEvent.geofenceTransition

        when (transitionType) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                Toast.makeText(context, "User Entered Area", Toast.LENGTH_SHORT).show()
                notificationHelper.sendHighPriorityNotification(
                    "User Entered Area", "",
                    MainActivity::class.java
                )
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show()
                notificationHelper.sendHighPriorityNotification(
                    "GEOFENCE_TRANSITION_DWELL", "",
                    MainActivity::class.java
                )
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                Toast.makeText(context, "User Exist Area", Toast.LENGTH_SHORT).show()
                notificationHelper.sendHighPriorityNotification(
                    "User Exist Area",
                    "",
                    MainActivity::class.java
                )
            }
        }    }
}