package com.example.fifthsemproject

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fifthsemproject.presentation.screendata.AiScreensData

class MainViewModel : ViewModel() {
    val screenData by mutableStateOf(AiScreensData())

    var visibleAbout by mutableStateOf(false)
}