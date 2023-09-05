package com.example.fifthsemproject.domain.models

import android.net.Uri

data class MusicTrack(
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val filePath: String,
    val dateAdded: Long,
    val dateModified: Long,
    val id: String,
    val albumUri: String
    // Add other attributes as needed
)
data class TrackUiStates(
    val title: String = "",
    val artist: String = "",
    val album: String = "",
    val duration: Long = 1L,
    val dateAdded: Long = 0L,
    val dateModified: Long = 0L,
    val index: Int = 0,
    val seekPosition: Long = 0L,
    val albumUri: String = ""
    // Add other attributes as needed
)












