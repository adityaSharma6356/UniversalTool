package com.example.fifthsemproject.presentation.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifthsemproject.domain.models.codeforces.CodeforcesUserInfoResponse
import com.example.fifthsemproject.domain.models.codeforces.RatingResponse
import com.example.fifthsemproject.domain.models.codeforces.SubmissionsResponse
import com.example.fifthsemproject.domain.repositories.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CodeforcesViewModel @Inject constructor(
    private val repository: DataRepository
) :ViewModel() {
    var userInfo by mutableStateOf(CodeforcesUserInfoResponse())
    var userSubmissions by mutableStateOf(SubmissionsResponse())
    var userRating by mutableStateOf(RatingResponse())
    var handle by mutableStateOf("")
    var incorrectHandle by mutableStateOf(false)
    var loading by mutableStateOf(false)
    var from by mutableStateOf(1)
    var count by mutableStateOf(5)
    var tempRatingCount by mutableStateOf(4)
    var ratingShowCount by mutableStateOf(0)
    var contestToOpen by mutableStateOf(1876)
    var pastHandles = mutableStateListOf<String>()



    fun removeHandle(hd:String, context: Context){
        val sharedPreferences = context.getSharedPreferences("codeforces_storage", Context.MODE_PRIVATE)
        val existingList = sharedPreferences.getStringSet("handle_list", HashSet())?.toMutableList()
        if (existingList != null && existingList.contains(hd)) {
            existingList.remove(hd)
            val editor = sharedPreferences.edit()
            editor.putStringSet("handle_list", HashSet(existingList))
            editor.apply()
        }
    }

    private fun saveHandle(context: Context){
        val sharedPreferences = context.getSharedPreferences("codeforces_storage", Context.MODE_PRIVATE)
        val existingList = sharedPreferences.getStringSet("handle_list", HashSet())?.toMutableList() ?: mutableListOf()
        if(!existingList.contains(handle.lowercase())) {
            existingList.add(handle.lowercase())
        }
        val editor = sharedPreferences.edit()
        editor.putStringSet("handle_list", HashSet(existingList))
        editor.apply()
    }

    fun getHandles(context: Context){
        val sharedPreferences = context.getSharedPreferences("codeforces_storage", Context.MODE_PRIVATE)
        val savedList = sharedPreferences.getStringSet("handle_list", HashSet())?.toList()
        if (savedList != null) {
            pastHandles.clear()
            pastHandles.addAll(savedList.toMutableStateList())
        } else {
            println("List is empty or not available.")
        }
    }

    fun getUserInfoByHandle(context: Context){
        if(handle.isBlank()) return
        viewModelScope.launch {
            loading = true
            val temp = repository.getCodeforcesUser(handle)
            Log.d("cflog", temp.toString())
            if(temp.status=="FAILED" || temp.status.isBlank()){
                incorrectHandle = true
            }else {
                saveHandle(context)
                getHandles(context)
                userInfo = temp
                incorrectHandle = false
                from = 1
                count = 5
                userRating = repository.getUserRating(handle)
                userSubmissions = repository.getCodeforcesUserSubmissions(handle, from, count)
                Log.d("cflog", userRating.toString())
                Log.d("cflog", userSubmissions.toString())
                ratingShowCount = minOf(tempRatingCount, userRating.result.size)
            }
            loading = false
        }
    }

}












