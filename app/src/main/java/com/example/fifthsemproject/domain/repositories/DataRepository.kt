package com.example.fifthsemproject.domain.repositories

import com.example.fifthsemproject.domain.models.SingleConversation
import com.example.fifthsemproject.domain.models.SingleInteraction
import com.example.fifthsemproject.util.Resource
import kotlinx.coroutines.flow.Flow

interface DataRepository {

    suspend fun getAllChats(): Resource<List<SingleConversation>>

    suspend fun getChatWithTime(time: Long): Resource<SingleConversation>

    suspend fun sendChatMessage(data: SingleInteraction, convoTime: Long, onResult:(Resource<SingleInteraction>)->Unit): Flow<Resource<SingleInteraction>>

    suspend fun deleteAllChats()

    suspend fun deleteConversation(time: Long)

    suspend fun getPrompts()

    suspend fun setPrompt()

    fun saveUserKey(key: String)
}