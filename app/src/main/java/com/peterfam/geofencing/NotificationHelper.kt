package com.peterfam.geofencing

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.peterfam.geofencing.MainActivity.Companion.CHANNEL_ID
import java.util.*


class NotificationHelper(private val context: Context) {

    fun sendHighPriorityNotification(title: String?, body: String?, activityName: Class<*>?) {
        val intent = Intent(context, activityName)
        val pendingIntent =
            PendingIntent.getActivity(context, 267, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val notification: Notification =
            NotificationCompat.Builder(context, CHANNEL_ID) //                .setContentTitle(title)
                //                .setContentText(body)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(
                    NotificationCompat.BigTextStyle().setSummaryText("summary")
                        .setBigContentTitle(title).bigText(body)
                )
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()
        NotificationManagerCompat.from(context).notify(Random().nextInt(), notification)
    }

}