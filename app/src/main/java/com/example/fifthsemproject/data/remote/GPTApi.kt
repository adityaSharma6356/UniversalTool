package com.example.fifthsemproject.data.remote

import android.content.ContentProviderOperation.newCall
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONStringer
import java.io.IOException
import java.util.concurrent.TimeUnit

const val API_KEY = "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"

class Gpt3ApiManager(private val apiKey: String) {

    fun storeMessage(context: Context, message: Message) {
        val messages = getMessages(context)
        messages.add(message)

        val gson = Gson()
        val json = gson.toJson(messages)

        val sharedPreferences = context.getSharedPreferences("releaseid", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("somenewid", json)
        editor.apply()
    }
    fun getMessages(context: Context): MutableList<Message> {
        val sharedPreferences = context.getSharedPreferences("releaseid", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("somenewid", null)
        val type = object : TypeToken<MutableList<Message>>() {}.type
        return Gson().fromJson(json, type) ?: mutableListOf()
    }
    var loading by mutableStateOf(false)
    var messageReceived by mutableStateOf(false)
    suspend fun makeApiRequest(onDone:() -> Unit, context: Context,prompt: String, onResponse: (String) -> Unit, onError: (String) -> Unit) {
        loading = true
        val url = "https://api.openai.com/v1/chat/completions"

        val history = getMessages(context)
        val jsbArray = JSONArray().apply {

            Log.d("gptapilog", history.toString())
            history.forEach {
                val jsbMessage = JSONObject()
                    .apply {
                        put("role", it.role)
                        put("content", it.content)
                    }
                put(jsbMessage)
            }
            val jsbMessageNew = JSONObject()
                .apply {
                    put("role", "user")
                    put("content", prompt)
                }
            put(jsbMessageNew)
        }

        val jsonObject = JSONObject()
            .apply {
                put("model", "gpt-3.5-turbo")
                put("messages", jsbArray)
                put("temperature", 0.2)
        }

        val mediaType = "application/json".toMediaType()
        val requestBody = jsonObject.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url(url)
            .header("Authorization", "Bearer $apiKey")
            .post(requestBody)
            .build()

        val client = OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        storeMessage(context, Message("user", prompt))

        client.newCall(request) .enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseBody: String = response.body?.string() ?: "Error"
                val gson = Gson()
                val chatCompletionResponse = gson.fromJson(responseBody, ChatCompletionResponse::class.java)
                loading = false
                Log.d("gptapilog", chatCompletionResponse.toString())
                val contentMessage = chatCompletionResponse.choices?.get(0)?.message?.content
                val roleMessage = chatCompletionResponse.choices?.get(0)?.message?.role
                if(contentMessage!=null && roleMessage!=null){
                    val newMessage = Message(content = contentMessage, role = roleMessage)
                    storeMessage(context, newMessage)
                }
                onResponse(contentMessage ?: "")
                onDone()
                messageReceived = true
            }

            override fun onFailure(call: Call, e: IOException) {
                loading = false
                onError("Error: ${e.message}")
            }


        })
    }
}

data class ChatCompletionResponse(
    val id: String? = null,
    val `object`: String? = null,
    val created: Long? = null,
    val model: String? = null,
    val choices: List<Choice>? = null,
    val usage: Usage? = null,
)

data class Choice(
    val index: Int,
    val message: Message,
    val finish_reason: String
)

data class Message(
    val role: String,
    val content: String
)

data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)