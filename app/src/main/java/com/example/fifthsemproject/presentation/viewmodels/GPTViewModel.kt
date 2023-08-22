package com.example.fifthsemproject.presentation.viewmodels

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifthsemproject.data.remote.API_KEY
import com.example.fifthsemproject.data.remote.Gpt3ApiManager
import com.example.fifthsemproject.data.remote.Message
import kotlinx.coroutines.launch

class GPTViewModel : ViewModel() {
    var prompt by mutableStateOf("")
    var output by mutableStateOf("")
    val apiManager = Gpt3ApiManager(API_KEY)
    var messagesHistory = mutableStateListOf<ChatModel>(ChatModel("hey", "How may i help you today?"))

    fun loadMessages(context: Context){
        viewModelScope.launch {
            val temp = apiManager.getMessages(context)
            val questions = temp.filter { it.role == "user" }
            val answers = temp.filter { it.role != "user" }
            val final = mutableListOf<ChatModel>()
            final.add(ChatModel("hey", "How may i help you today?"))
            for(i in 0..questions.lastIndex){
                if(i<=answers.lastIndex){
                    final.add(
                        ChatModel(
                            questions[i].content,
                            answers[i].content
                        )
                    )
                }
            }
            messagesHistory.clear()
            messagesHistory.addAll(final)
        }
    }

    fun callGpt(context: Context, state: LazyListState){
        viewModelScope.launch {
            apiManager.makeApiRequest(
                context = context,
                prompt = prompt,
                onResponse = {
                    output = it
                },
                onError = {
                    output = it
                },
                onDone = {
                    viewModelScope.launch {
                        loadMessages(context)
                        state.scrollToItem(messagesHistory.lastIndex)
                        prompt = ""
                    }
                }
            )
        }
    }
}

data class ChatModel(
    val question: String,
    val ans: String
)