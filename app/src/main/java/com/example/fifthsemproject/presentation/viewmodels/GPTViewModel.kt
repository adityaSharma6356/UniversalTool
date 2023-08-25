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
import com.example.fifthsemproject.domain.models.SingleConversation
import com.example.fifthsemproject.domain.models.SingleInteraction
import com.example.fifthsemproject.domain.repositories.DataRepository
import com.example.fifthsemproject.util.Resource
import com.google.mlkit.nl.languageid.LanguageIdentification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.intellij.lang.annotations.Language
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GPTViewModel @Inject constructor(
    private val dataRepository: DataRepository
): ViewModel() {

    var loadingResponse by mutableStateOf(false)
    var outgoingMessage by mutableStateOf(SingleInteraction("user", ""))
    var messagesHistory = mutableStateListOf<SingleConversation>()
    var currentChat by mutableStateOf(SingleConversation())
    var selectedItemIndex by mutableStateOf(0)
    var loadingLanguage by mutableStateOf(false)
    var currentDataToDisplay = mutableStateListOf<SingleInteraction>()
    var ttsText by mutableStateOf("")
    var ttsEngine : TextToSpeech? = null
    var detectedLanguage = "en"

    init {
        getChats()
    }
    fun detectLanguage(text: String) {
        loadingLanguage = true
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
        if(text.isBlank()) return
        viewModelScope.launch {
            if(ttsEngine!=null){
                if(ttsEngine!!.isSpeaking && text==ttsText){
                    ttsEngine!!.stop()
                    detectedLanguage = "en"
                    loadingLanguage = false
                    return@launch
                }
                ttsText = text
                ttsEngine!!.stop()
                Log.d("languagelog", ttsEngine!!.availableLanguages.toString())
                ttsEngine!!.language = Locale(detectedLanguage)
                ttsEngine!!.speak(ttsText, TextToSpeech.QUEUE_FLUSH, null, null)
                detectedLanguage = "en"
                loadingLanguage = false
            }
            loadingLanguage = false
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
                        Log.d("gptlog", result.message.toString())
                    }
                    is Resource.Loading ->{
                        Log.d("gptlog", "loading: ${result.isLoading}")
                        loadingResponse = result.isLoading
                    }
                }
            }.collect{ result ->
                when(result){
                    is Resource.Success ->{

                    }
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