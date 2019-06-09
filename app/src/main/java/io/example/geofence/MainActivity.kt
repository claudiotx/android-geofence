package io.example.geofence

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var geofencingClient: GeofencingClient

    companion object {
        const val TAG = "Geofence"
        const val PERMISSIONS_REQUEST = 100

        const val LATITUDE = 53.579749 // change to your lat
        const val LONGITUDE = -2.525625 // change to your long
        const val RADIUS = 100f // radius in meters - google recommend a minimum of 100m for best results
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        geofencingClient = LocationServices.getGeofencingClient(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            geofencingClient.addGeofences(getGeofence(), geofencePendingIntent)
                .addOnSuccessListener {
                    Log.d(TAG, "added succesfully")
                    status.text = "Geofence successfully added"
                    coordinates.text = "Coordinates = $LATITUDE, $LONGITUDE"
                    radius.text = "Radius = ${RADIUS}m"
                }
                .addOnFailureListener {
                    Log.d(TAG, "failed to add geofence ${it.message}")
                    status.text = "Failed to add geofence, error = ${it.message}"
                    coordinates.text = "Coordinates = $LATITUDE - $LONGITUDE; Radius = $RADIUS"
                    radius.text = "Radius = ${RADIUS}m"
                }

        } else {
            Log.d(TAG, "missing permissions")
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST
                )
            }
        }
    }

    /**
     * Tracks when a user goes in or out of a geo fence.
     * It can also be configured to only trigger after the user has been within the geofence for a certain period of time
     */

    private fun getGeofence(): GeofencingRequest {
        val geofence = Geofence.Builder()
            .setRequestId("geo_id_1")
            .setCircularRegion(LATITUDE, LONGITUDE, RADIUS)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()


        return GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER) // this will cause a trigger when the user is already within the fence
            .addGeofence(geofence)
            .build()
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceTransitionsIntentService::class.java)
        PendingIntent.getService(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
