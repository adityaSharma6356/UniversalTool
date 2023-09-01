package com.example.fifthsemproject.data.remote

import android.util.Log
import com.example.fifthsemproject.domain.models.SingleConversation
import com.example.fifthsemproject.domain.models.SingleInteraction
import com.example.fifthsemproject.util.Resource
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Singleton
class Gpt3ApiManager {
    fun makeApiRequest(
        data: SingleConversation,
        apiKey: String,
        prompt: String,
        onResponse: (Resource<SingleInteraction>) -> Unit,
    ) {
        val url = "https://api.openai.com/v1/chat/completions"
        Log.d("gptlog", "entered api")
        val jsbArray = JSONArray().apply {
            data.conversation.forEach {
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
            .readTimeout(120, TimeUnit.SECONDS)
            .build()

        Log.d("gptlog", "starting call with ${request.body.toString()}")
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {

                val responseBody: String = response.body?.string() ?: "Error"
                Log.d("gptlog", "response received first: ${response.toString()}")
                val gson = Gson()
                val chatCompletionResponse = gson.fromJson(responseBody, ChatCompletionResponse::class.java)
                val contentMessage = chatCompletionResponse.choices?.get(0)?.message?.content
                val roleMessage = chatCompletionResponse.choices?.get(0)?.message?.role

                if(contentMessage!=null && roleMessage!=null){
                    val currentDateTime: java.util.Date = java.util.Date()
                    val currentTimestamp: Long = currentDateTime.time
                    onResponse(Resource.Success(SingleInteraction(content = contentMessage, role = roleMessage, time = currentTimestamp)))
                    Log.d("gptlog", "response received ${contentMessage+roleMessage}")
                } else {
                    val errorCode = response.code
                    Log.d("messagelog" , "error code: $errorCode")
                    onResponse(Resource.Error(null, errorCode.toString()))
                    Log.d("gptlog", "data null form api : ${contentMessage.toString()+roleMessage.toString()}")
                }
                onResponse(Resource.Loading(false))
            }
            override fun onFailure(call: Call, e: IOException) {
                onResponse(Resource.Error(null, e.message.toString()))
                onResponse(Resource.Loading(false))
                Log.d("gptlog", "response error message: ${e.message}")
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