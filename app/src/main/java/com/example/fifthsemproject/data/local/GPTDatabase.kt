package com.example.fifthsemproject.data.local

import android.content.Context
import android.util.Log
import com.example.fifthsemproject.domain.models.SingleConversation
import com.google.gson.Gson
import javax.inject.Singleton

@Singleton
class GPTDatabase(
    val context: Context
) {
    private val apiKey = "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"
    private val hiTag = "gptallconversations"
    private val lowTag = "gptconversations"

    fun storeNewApiKey(key:String){
        val sharedPreferences = context.getSharedPreferences("gptuserprovidedkey", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userkey", key)
        editor.apply()
    }
    fun getMessagingState(): Boolean {
        val sharedPreferences = context.getSharedPreferences("gptuserprovidedkey", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("messagingstate", true)
    }
    fun getUserProvidedKey(): String{
        val sharedPreferences = context.getSharedPreferences("gptuserprovidedkey", Context.MODE_PRIVATE)
        val key = sharedPreferences.getString("userkey", apiKey)
        return key!!
    }
    fun getAllConversations(): List<SingleConversation>{
        val sharedPreferences = context.getSharedPreferences(hiTag, Context.MODE_PRIVATE)
        val json = sharedPreferences.getString(lowTag, null)
        Log.d("gptlog", "storage opened with data $json")
        if(json==null){
            return listOf()
        }
        val ttp =  Gson().fromJson(json, Array<SingleConversation>::class.java).asList()
        return ttp
    }

    fun storeAllConversation(data: List<SingleConversation>){
        val gson = Gson()
        val json = gson.toJson(data)
        val sharedPreferences = context.getSharedPreferences(hiTag, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(lowTag, json)
        Log.d("gptlog", "storage created with $json")
        editor.apply()
    }

    fun clearChatHistory() {
        val sharedPreferences = context.getSharedPreferences(hiTag, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove(lowTag)
        editor.apply()
    }
    fun clearSingleChatHistory(time: Long) {
        val temp = getAllConversations().toMutableList()
        temp.removeIf { it.startingTime==time }
        val sharedPreferences = context.getSharedPreferences(hiTag, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(temp)
        editor.putString(lowTag, json)
        editor.apply()
    }
}











