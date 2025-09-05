package com.example.ecojourney.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoData(
    val videoId: String,
    val title: String,
    val description: String,
    val channelTitle: String,
    val thumbnailUrl: String,
    val publishedAt: String
) : Parcelable

// Singleton object untuk menyimpan data video yang dipilih
object SelectedVideoData {
    var currentVideo: VideoData? = null
}