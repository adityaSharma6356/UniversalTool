package com.example.fifthsemproject.presentation.viewmodels

import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifthsemproject.domain.models.NotificationData
import com.example.fifthsemproject.domain.models.PushNotification
import com.example.fifthsemproject.domain.models.SingleConversation
import com.example.fifthsemproject.domain.models.SingleInteraction
import com.example.fifthsemproject.domain.repositories.DataRepository
import com.example.fifthsemproject.presentation.services.Constants.Companion.TOPIC
import com.example.fifthsemproject.presentation.services.RetrofitInstance
import com.example.fifthsemproject.util.Resource
import com.google.mlkit.nl.languageid.LanguageIdentification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GPTViewModel @Inject constructor(
    private val dataRepository: DataRepository
): ViewModel() {

    var loadingResponse by mutableStateOf(false)
    var outgoingMessage by mutableStateOf(SingleInteraction("user", ""))
    var messagesHistory = mutableStateListOf<SingleConversation>()
    private var currentChat by mutableStateOf(SingleConversation())
    var selectedItemIndex by mutableStateOf(0)
    var messagingEnabled by mutableStateOf(true)
    var currentDataToDisplay = mutableStateListOf<SingleInteraction>()
    private var ttsText by mutableStateOf("")
    var ttsEngine : TextToSpeech? = null
    var notifyKeyFailure by mutableStateOf(false)
    private var detectedLanguage = "en"
    init {
        getChats()
        messagingEnabled = dataRepository.loadKey()
    }
    fun sendNotification() {
        viewModelScope.launch(Dispatchers.IO) {
            val temp = PushNotification(
                NotificationData("GPT currently unavailable, key will be updated shortly", "")
                ,TOPIC
            )
            try {
                val response = RetrofitInstance.api.postNotification(temp)
                if (response.isSuccessful) {
//                    Log.d("messagelog" , "Response: ${Gson().toJson(response)}")
                } else {
                    Log.e("messagelog", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e("messagelog", e.toString())
            }
        }
    }
    fun detectLanguage(text: String) {
        loadingResponse = true
        val languageIdentifier = LanguageIdentification.getClient()
        languageIdentifier.identifyLanguage(text)
            .addOnSuccessListener { languageCode ->
                if (languageCode == "und") {
                    Log.i("languagelog", "Can't identify language.")
                    detectedLanguage = "en"
                    speak(text)
                } else {
                    Log.i("languagelog", "Language: $languageCode")
                    detectedLanguage = languageCode
                    speak(text)
                }
            }
            .addOnFailureListener {
                detectedLanguage = "en"
                speak(text)
                // Model couldn’t be loaded or other internal error.
                // ...
            }
    }
    private fun speak(text: String){
        if(text.isBlank()) {
            loadingResponse = false
            return
        }
        viewModelScope.launch {
            if(ttsEngine!=null){
                if(ttsEngine!!.isSpeaking && text==ttsText){
                    ttsEngine!!.stop()
                    detectedLanguage = "en"
                    loadingResponse = false
                    return@launch
                }
                ttsText = text
                ttsEngine!!.stop()
                Log.d("languagelog", ttsEngine!!.availableLanguages.toString())
                ttsEngine!!.language = Locale(detectedLanguage)
                ttsEngine!!.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
                detectedLanguage = "en"
                loadingResponse = false
            }
            loadingResponse = false
        }
    }

    fun deleteChat(time:Long){
        viewModelScope.launch {
            dataRepository.deleteConversation(time)
            getChats()
        }
    }

    fun setChatToIndex(index: Int){
        currentDataToDisplay.clear()
        currentChat = messagesHistory[index]
        currentDataToDisplay.addAll(messagesHistory[index].conversation)
    }
    fun newChat(){
        if(messagesHistory.isEmpty() || messagesHistory.last().conversation.isEmpty()) return
        currentChat = SingleConversation()
        currentDataToDisplay.clear()
        currentDataToDisplay.addAll(currentChat.conversation)
        loadingResponse = false
    }

    private fun getChats(){
        viewModelScope.launch {
            Log.d("gptlog", "loading all chats")
            when (val temp = dataRepository.getAllChats()){
                is Resource.Success ->{
                    temp.data?.let {
                        Log.d("gptlog", "all chat returned with $it")
                        messagesHistory.clear()
                        messagesHistory.addAll(it)
                        if(messagesHistory.isNotEmpty()){
                            currentChat = if(selectedItemIndex<=0 || selectedItemIndex>messagesHistory.lastIndex){
                                messagesHistory.first()
                            }else{
                                messagesHistory[selectedItemIndex]
                            }
                            currentDataToDisplay.clear()
                            currentDataToDisplay.addAll(currentChat.conversation)
                        } else {
                            currentDataToDisplay.clear()
                            currentChat = SingleConversation()
                        }
                    }
                }
                is Resource.Error ->{
                    Log.d("gptlog", temp.message.toString())
                }
                is Resource.Loading ->{
                }
            }
        }
    }
    fun sendMessage(
        state:LazyListState,
        scope: CoroutineScope
    ){
        Log.d("gptlog", "starting sending")
        if(outgoingMessage.content.isBlank()) return
        Log.d("gptlog", "message not empty, sending")
        viewModelScope.launch {
            dataRepository.sendChatMessage(
                outgoingMessage,
                currentChat.startingTime
            ) { result ->
                when (result) {
                    is Resource.Success -> {
                        Log.d("gptlog", "call back working :D")
                        Log.d("gptlog", "message recieved: ${result.data}")
                        getChats()
                        scope.launch {
                            Log.d("gptlog", "animating")
                            state.animateScrollToItem(currentDataToDisplay.lastIndex)
                        }
                    }
                    is Resource.Error ->{
                        if(result.message=="401"){
                            notifyKeyFailure = true
                            messagingEnabled = false
                        }
                        Log.d("gptlog", result.message.toString())
                    }
                    is Resource.Loading ->{
                        Log.d("gptlog", "loading: ${result.isLoading}")
                        loadingResponse = result.isLoading
                    }
                }
            }.collect{ result ->
                when(result){
                    is Resource.Success -> Unit
                    is Resource.Error ->{
                        Log.d("gptlog", result.message.toString())
                    }
                    is Resource.Loading ->{
                        Log.d("gptlog", "loading: ${result.isLoading}")
                        loadingResponse = result.isLoading
                    }
                }
            }
            outgoingMessage = SingleInteraction("user", "")
        }
        getChats()
        scope.launch {
            state.animateScrollToItem(currentDataToDisplay.lastIndex)
        }
    }
}