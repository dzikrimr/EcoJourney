package com.example.ecojourney.utils

data class TrackingState(
    val isTracking: Boolean = false,
    val distance: Double = 0.0, // dalam kilometer
    val currentSpeed: Float = 0f, // dalam km/h
    val averageSpeed: Float = 0f, // dalam km/h
    val duration: Long = 0, // dalam milliseconds
    val startTime: Long = 0
) {
    fun getFormattedDistance(): String {
        return if (distance < 1.0) {
            "${(distance * 1000).toInt()} m"
        } else {
            String.format("%.2f km", distance)
        }
    }

    fun getFormattedDuration(): String {
        val seconds = duration / 1000
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return when {
            hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
            minutes > 0 -> String.format("%02d:%02d", minutes, remainingSeconds)
            else -> "${remainingSeconds}s"
        }
    }

    fun getFormattedSpeed(): String {
        return String.format("%.1f km/h", currentSpeed)
    }

    fun getFormattedAverageSpeed(): String {
        return String.format("%.1f km/h", averageSpeed)
    }
}