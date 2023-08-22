package com.example.fifthsemproject

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifthsemproject.data.remote.API_KEY
import com.example.fifthsemproject.data.remote.Gpt3ApiManager
import com.example.fifthsemproject.presentation.screendata.AiScreensData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class MainViewModel : ViewModel() {
    val screenData by mutableStateOf(AiScreensData())

    var visibleAbout by mutableStateOf(false)
}