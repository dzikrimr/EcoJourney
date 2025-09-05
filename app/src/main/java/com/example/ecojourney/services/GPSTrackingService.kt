// GPSTrackingService.kt
package com.example.ecojourney.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.*
import kotlinx.coroutines.*
import kotlin.math.*

class GPSTrackingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var isTracking = false
    private var startTime: Long = 0
    private var totalDistance = 0.0 // dalam meter
    private var lastLocation: Location? = null
    private var speedReadings = mutableListOf<Float>()
    private var trackingData = TrackingData()

    data class TrackingData(
        var totalDistance: Double = 0.0,
        var averageSpeed: Float = 0f,
        var maxSpeed: Float = 0f,
        var duration: Long = 0,
        var locations: MutableList<Location> = mutableListOf()
    )

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "GPS_TRACKING_CHANNEL"
        const val ACTION_START_TRACKING = "START_TRACKING"
        const val ACTION_STOP_TRACKING = "STOP_TRACKING"
        const val ACTION_GET_DATA = "GET_DATA"

        // Broadcast actions
        const val BROADCAST_TRACKING_UPDATE = "com.example.ecojourney.TRACKING_UPDATE"
        const val EXTRA_DISTANCE = "distance"
        const val EXTRA_SPEED = "speed"
        const val EXTRA_AVERAGE_SPEED = "average_speed"
        const val EXTRA_DURATION = "duration"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("GPSTracking", "Service created with package: ${packageName}, context: ${applicationContext}")
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.e("GPSTracking", "Google Play Services unavailable: $resultCode")
            if (resultCode == ConnectionResult.DEVELOPER_ERROR) {
                Log.e("GPSTracking", "DEVELOPER_ERROR: Check package name, SHA-1, and API key in Firebase/Google Cloud Console")
            }
            stopSelf()
            return
        }
        Log.d("GPSTracking", "Google Play Services available")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupLocationCallback()
        createNotificationChannel()
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_TRACKING -> startTracking()
            ACTION_STOP_TRACKING -> stopTracking()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun setupLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    updateLocation(location)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun startTracking() {
        if (!hasLocationPermissions()) {
            Log.e("GPSTracking", "Location permissions not granted")
            return
        }

        if (isTracking) return

        isTracking = true
        startTime = System.currentTimeMillis()
        totalDistance = 0.0
        speedReadings.clear()
        trackingData = TrackingData()

        // Fixed: Use correct LocationRequest.Builder API
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L // Update setiap 2 detik
        ).apply {
            setMinUpdateIntervalMillis(1000L) // Minimum 1 detik
            setMaxUpdateDelayMillis(5000L) // Maximum delay 5 detik
        }.build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        startForeground(NOTIFICATION_ID, createNotification())
        Log.d("GPSTracking", "Tracking started")
    }

    private fun stopTracking() {
        if (!isTracking) return

        isTracking = false
        fusedLocationClient.removeLocationUpdates(locationCallback)

        // Calculate final statistics
        val duration = System.currentTimeMillis() - startTime
        trackingData.duration = duration
        trackingData.totalDistance = totalDistance
        trackingData.averageSpeed = if (speedReadings.isNotEmpty()) {
            speedReadings.average().toFloat()
        } else 0f

        // Broadcast final results
        broadcastTrackingData()

        stopForeground(true)
        stopSelf()

        Log.d("GPSTracking", "Tracking stopped. Distance: ${totalDistance}m, Duration: ${duration}ms")
    }

    private fun updateLocation(location: Location) {
        if (!isTracking) return

        trackingData.locations.add(location)

        lastLocation?.let { prevLocation ->
            val distance = calculateDistance(prevLocation, location)

            // Filter out unrealistic movements (lebih dari 200 km/h)
            val timeDiff = (location.time - prevLocation.time) / 1000.0 // dalam detik
            if (timeDiff > 0) {
                val speed = (distance / timeDiff * 3.6).toFloat() // Fixed: convert to Float and m/s to km/h
                if (speed <= 200) { // Filter kecepatan tidak masuk akal
                    totalDistance += distance
                    speedReadings.add(speed)

                    if (speed > trackingData.maxSpeed) {
                        trackingData.maxSpeed = speed
                    }
                }
            }
        }

        lastLocation = location

        // Broadcast update setiap 5 detik
        if (trackingData.locations.size % 3 == 0) {
            broadcastTrackingData()
        }
    }

    private fun calculateDistance(loc1: Location, loc2: Location): Double {
        val earthRadius = 6371000.0 // Earth radius in meters

        val lat1Rad = Math.toRadians(loc1.latitude)
        val lat2Rad = Math.toRadians(loc2.latitude)
        val deltaLatRad = Math.toRadians(loc2.latitude - loc1.latitude)
        val deltaLonRad = Math.toRadians(loc2.longitude - loc1.longitude)

        val a = sin(deltaLatRad / 2).pow(2) +
                cos(lat1Rad) * cos(lat2Rad) *
                sin(deltaLonRad / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }

    private fun broadcastTrackingData() {
        val intent = Intent(BROADCAST_TRACKING_UPDATE).apply {
            putExtra(EXTRA_DISTANCE, totalDistance / 1000.0) // Convert to km
            putExtra(EXTRA_SPEED, lastLocation?.speed?.times(3.6f) ?: 0f) // Current speed in km/h
            putExtra(EXTRA_AVERAGE_SPEED, if (speedReadings.isNotEmpty()) speedReadings.average().toFloat() else 0f)
            putExtra(EXTRA_DURATION, System.currentTimeMillis() - startTime)
        }
        sendBroadcast(intent)
    }

    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "GPS Tracking",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Tracking GPS untuk perhitungan jejak karbon"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("EcoJourney - Tracking Aktif")
            .setContentText("Menghitung jejak karbon perjalanan Anda...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (isTracking) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }
}