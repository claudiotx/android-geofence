package io.example.geofence

import android.app.IntentService
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceTransitionsIntentService : IntentService("GeoIntentService") {

    companion object {
        const val TAG = "GeoIntent"
        const val CHANNEL_ID = "LOCATION"
        const val CHANNEL_NAME = "LOCATION"
    }

    override fun onHandleIntent(intent: Intent?) {
        val event = GeofencingEvent.fromIntent(intent)

        Log.d(TAG, "received event")

        if (event.hasError()) {
            Log.d(TAG, "error code = ${event.errorCode}")

        } else {
            handleEvent(event)
        }
    }

    private fun handleEvent(event: GeofencingEvent) {
        when (event.geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER -> showNotification("Geofence", "Inside fence")
            Geofence.GEOFENCE_TRANSITION_EXIT -> showNotification("Geofence", "Outside fence")
            else -> Log.d(TAG, "transition code = ${event.geofenceTransition}")
        }
    }

    private fun showNotification(title: String, content: String) {
        with(NotificationManagerCompat.from(this)) {
            notify(1, getNotification(title, content))
        }
    }

    private fun getNotification(title: String, content: String): Notification {
        Log.d(TAG, content)

        createNotificationChannel()

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_mtrl_chip_checked_circle)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(Notification.DEFAULT_SOUND)
            .build()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "test"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
