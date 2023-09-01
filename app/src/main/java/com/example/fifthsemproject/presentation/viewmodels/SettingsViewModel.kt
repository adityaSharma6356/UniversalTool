package com.example.fifthsemproject.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifthsemproject.domain.models.PushNotification
import com.example.fifthsemproject.presentation.services.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel: ViewModel() {
    fun sendNotification(notification : PushNotification) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d("messagelog" , "Response: ${response.body()?.string()}")
                } else {
                    response.errorBody()?.let { Log.e("messagelog", it.string()) }
                }
            } catch (e: Exception) {
                Log.i("messagelog", e.toString())
            }
        }
    }
}