package com.example.fifthsemproject.presentation.viewmodels

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifthsemproject.domain.models.LocationModels
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale

class LocationObserveViewModel:ViewModel() {
    val idList = mutableStateListOf<String>()
    val locationsList = mutableStateListOf<LocationModels>()
    var loadingLocations by mutableStateOf(false)
    var checkingId by mutableStateOf(false)
    var idExists by mutableStateOf(false)
    var idNotExists by mutableStateOf(false)
    var firstTime by mutableStateOf(true)
    var openAddId by mutableStateOf(false)
    var somethingWentWrong by mutableStateOf(false)
    var tempId by mutableStateOf("")


    fun getIdList(context: Context){
        val sharedPreferences = context.getSharedPreferences("universal_location", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("location_ids", "")
        Log.d("locationLog", "storage opened with data $json")
        if(json==null){
            locationsList.clear()
            return
        }
        val ttp =  Gson().fromJson(json, Array<String>::class.java)
        idList.clear()
        if (ttp != null) {
            idList.addAll(ttp)
        }
    }

    fun storeNewId(data:String, context:Context){
        val temp = idList.toMutableList()
        temp.remove(data)
        temp.add(data)
        val gson = Gson()
        val json = gson.toJson(temp)
        val sharedPreferences = context.getSharedPreferences("universal_location", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("location_ids", json)
        Log.d("locationLog", "storage created with $json")
        editor.apply()
    }

    fun checkDocumentId(id:String){
        viewModelScope.launch {
            checkingId = true
            Firebase.firestore.collection("universal_location_service").document(id).get().addOnSuccessListener {
                if(it.exists())
                {
                    idExists = true
                } else {
                    idNotExists = true
                }
            }.addOnFailureListener {
                somethingWentWrong = true
                idNotExists = true
            }.await()
            checkingId = false
        }
    }

    fun loadLocations(context: Context){
        loadingLocations = true
        getIdList(context)
        if(idList.isEmpty()) {
            loadingLocations = false
            return
        }
        viewModelScope.launch {
            Log.d("locationLog", "getting locations")
            try {
                val collectionReference = FirebaseFirestore.getInstance().collection("universal_location_service")
                val query = collectionReference.whereIn(FieldPath.documentId(), idList).get().await().toObjects(LocationModels::class.java)
                if(query.isNotEmpty()){
                    locationsList.clear()
                    locationsList.addAll(query)
                }
                loadingLocations = false
            } catch (e: IOException){
                loadingLocations = false
                Log.i("locationLog", e.message.toString())
            } catch (e: HttpException){
                loadingLocations = false
                Log.i("locationLog", e.message.toString())
            } catch (e: FirebaseTooManyRequestsException){
                loadingLocations = false
                Log.i("locationLog", e.message.toString())
            }
            delay(1000)
            loadingLocations = false
        }
    }
}









