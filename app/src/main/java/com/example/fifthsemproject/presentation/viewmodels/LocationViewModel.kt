package com.example.fifthsemproject.presentation.viewmodels

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifthsemproject.data.services.LocationService
import com.example.fifthsemproject.domain.models.LocationModels
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException

class LocationViewModel: ViewModel() {
    var isSharingLocation by mutableStateOf(false)
    var isServiceOn by mutableStateOf(false)
    var loadingSharing by mutableStateOf(false)
    var loadingCC by mutableStateOf(false)
    var isCCAvailable by mutableStateOf(false)
    var showContent by mutableStateOf(false)
    var loadingService by mutableStateOf(false)
    var enabled by mutableStateOf(false)
    var currentCC by mutableStateOf<GeoPoint?>(null)
    var name by mutableStateOf("")
    var currentId by mutableStateOf("")
    val fireStore  = FirebaseFirestore.getInstance()

    fun enableSharing(context: Context){
        viewModelScope.launch {
            loadingSharing = true
            enabled = true
            loadingService = true
            getSharingStatus(context)
            if(isSharingLocation) {
                loadingSharing = false
                loadingService = false
                return@launch
            }
            setName(name, context)
            val intent = Intent(context, LocationService::class.java).apply {
                action = LocationService.ACTION_START
            }
            ContextCompat.startForegroundService(context, intent)
            delay(4000)
            startChecking(context, true)
            startCheckingService(context, true)
        }
    }



    fun getCC(){
        viewModelScope.launch {
            isCCAvailable = false
            loadingCC = true
            delay(4000)
            try {
                val temp = fireStore.collection("universal_location_service").document(currentId).get().await().toObject(LocationModels::class.java)
                currentCC = temp?.location
                loadingCC = false
                isCCAvailable = true
            } catch (e: IOException){
                currentCC = null
            } catch (e: HttpException){
                currentCC = null
            } catch (e: FirebaseTooManyRequestsException){
                currentCC = null
            }
        }
    }

    fun getId(context: Context) {
        val sharedPreferences = context.getSharedPreferences("universal_locations", Context.MODE_PRIVATE)
        currentId =  sharedPreferences.getString("personal_locations_id", "")!!
    }

    private fun getSharingStatus(context: Context) {
        val sharedPreferences = context.getSharedPreferences("universal_location_info", Context.MODE_PRIVATE)
        isSharingLocation = sharedPreferences.getBoolean("sharing_data", false)
    }
    private fun getServiceInfo(context: Context) {
        val sharedPreferences = context.getSharedPreferences("universal_location_info", Context.MODE_PRIVATE)
        isServiceOn = sharedPreferences.getBoolean("starting_service", false)
    }

    private fun setName(name: String, context: Context){
        val sharedPreferences = context.getSharedPreferences("universal_locations", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("personal_name", name)
        editor.apply()
    }



    private fun startChecking(context: Context, tp :Boolean){
        viewModelScope.launch {
            var ct = 0
            while(true){
                getSharingStatus(context)
                if(isSharingLocation==tp) {
                    loadingSharing = false
                    break
                }
                ct++
                if(ct>10){
                    loadingSharing = false
                    break
                }
                delay(1500)
            }
        }
    }
    private fun startCheckingService(context: Context, tp :Boolean){
        viewModelScope.launch {
            var ct=0
            while(true){
                getServiceInfo(context)
                if(isServiceOn==tp) {
                    loadingService = false
                    break
                }
                ct++
                if(ct>10){
                    loadingService = false
                    break
                }
                delay(1500)
            }
        }
    }


    fun disableSharing(context: Context){
        viewModelScope.launch {
            loadingSharing = true
            enabled = false
            val intent = Intent(context, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
            }
            ContextCompat.startForegroundService(context, intent)
            startChecking(context, false)
            startCheckingService(context, false)
        }
    }
}















