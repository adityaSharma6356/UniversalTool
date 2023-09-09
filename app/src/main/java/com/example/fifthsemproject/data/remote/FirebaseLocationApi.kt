package com.example.fifthsemproject.data.remote

import com.example.fifthsemproject.domain.models.LocationModels
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseLocationApi {
    private val fireStore = FirebaseFirestore.getInstance()



    suspend fun getLocationOfAll(id:List<String>): List<LocationModels>{
        TODO()
    }

}