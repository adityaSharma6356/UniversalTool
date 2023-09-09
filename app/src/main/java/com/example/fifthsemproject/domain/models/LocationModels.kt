package com.example.fifthsemproject.domain.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.GeoPoint

data class LocationModels (
    val name: String = "",
    val location: GeoPoint = GeoPoint(0.0, 0.0),
    val lastUpdate: Timestamp = Timestamp(0, 0)
)