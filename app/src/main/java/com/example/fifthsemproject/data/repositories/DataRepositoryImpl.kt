package com.example.fifthsemproject.data.repositories

import android.util.Log
import com.example.fifthsemproject.data.local.GPTDatabase
import com.example.fifthsemproject.data.remote.Gpt3ApiManager
import com.example.fifthsemproject.domain.models.*
import com.example.fifthsemproject.domain.repositories.DataRepository
import com.example.fifthsemproject.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepositoryImpl @Inject constructor(
    private val database: GPTDatabase,
): DataRepository{
    private var key = "sk-cHQRynnV1E3bjtXHkaoZT3BlbkFJXAu0weonmxSbVNVZ1tZ"//A
    private var tempData: List<SingleConversation>? = null
    private val api = Gpt3ApiManager()
    override suspend fun getAllChats(): Resource<List<SingleConversation>> {
        val data = database.getAllConversations()
        tempData = data
        return Resource.Success(data)
    }

    override suspend fun getChatWithTime(time: Long): Resource<SingleConversation> {
        if(tempData==null || tempData!!.isEmpty()) getAllChats()
        val data = tempData?.find { it.startingTime == time } ?: return Resource.Error(null, "Did not find any chat with time: $time")
        return Resource.Success(data)
    }

    override suspend fun sendChatMessage(
        data: SingleInteraction,
        convoTime: Long,
        onResult: (Resource<SingleInteraction>) -> Unit
    ): Flow<Resource<SingleInteraction>> {
        return flow {

            emit(Resource.Loading(true))
            val currentDateTime: java.util.Date = java.util.Date()
            val currentTimestamp: Long = currentDateTime.time
            data.time = currentTimestamp

            val storedData = getAllChats()
            var dataToSend = SingleConversation()
            if(storedData.data!!.isEmpty()){
                val newConv = SingleConversation(
                    startingTime = data.time,
                    mutableListOf(data)
                )
                val newStuff = mutableListOf<SingleConversation>(newConv)
                database.storeAllConversation(newStuff)
                dataToSend.startingTime = data.time
                dataToSend.conversation.add(data)
                Log.d("gptlog", "new object created with: ${newStuff.toString()}")
            } else {
                if(storedData.data!!.find { it.startingTime==convoTime }==null){
                   Log.d("gptlog2","starting new convo", )
                    val newConv = SingleConversation(
                        startingTime = data.time,
                        mutableListOf(data)
                    )
                    dataToSend = newConv
                    Log.d("gptlog2", newConv.toString())
                    val newItem = mutableListOf(newConv)
                    Log.d("gptlog2", "old one updated with: ${newItem.size}")
                    newItem.addAll(storedData.data!!)
                    val x = newItem.toMutableList().add(newConv)
                    database.storeAllConversation(newItem)
                    Log.d("gptlog2", "old one updated with: ${newItem.size}")
                    Log.d("gptlog2", "modification $x")
                } else {
                    storedData.data!!.forEach {
                        if(it.startingTime==convoTime){
                            it.conversation.add(data)
                            dataToSend = it
                        }
                    }
                    database.storeAllConversation(storedData.data!!)
                    Log.d("gptlog", "old one updated with: ${storedData.data.toString()}")

                }
            }
            val tempTotalData = getAllChats()
            Log.d("gptlog", "calling api with : $dataToSend")
            var tempResponse : Resource<SingleInteraction>? = null
            onResult(Resource.Success(data))
            api.makeApiRequest(
                data = dataToSend,
                apiKey = key,
                prompt = data.content,
                onResponse = { result ->
                    if(result is Resource.Success){
                        tempTotalData.data!!.toMutableList().forEach {
                            if(it.startingTime==dataToSend.startingTime){
                                it.conversation.add(result.data!!)
                            }
                        }
                        database.storeAllConversation(tempTotalData.data!!)
                    }
                    onResult(result)
                    tempResponse = result
                    Log.d("gptlog", "callback recieved with ${result.data}")
                }
            )

            if(tempResponse==null){
                emit(Resource.Error(message = "API error"))
            } else {
                emit(tempResponse!!)
            }
//            emit(Resource.Loading(false))
        }
    }

    override suspend fun deleteAllChats() {
        database.clearChatHistory()
    }

    override suspend fun deleteConversation(time: Long) {
        database.clearSingleChatHistory(time)
    }

    override suspend fun getPrompts() {
        TODO("Not yet implemented")
    }

    override suspend fun setPrompt() {
        TODO("Not yet implemented")
    }

    override fun saveUserKey(key: String) {
        database.storeNewApiKey(key)
    }
    override fun loadKey(): Boolean {
        key = database.getUserProvidedKey()
        return database.getMessagingState()
    }
}