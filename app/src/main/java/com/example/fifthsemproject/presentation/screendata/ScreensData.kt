package com.example.fifthsemproject.presentation.screendata

import androidx.compose.ui.graphics.Color
import com.example.fifthsemproject.R
import com.example.fifthsemproject.presentation.navigation.Screen

data class AiScreensData(
    val itemsList: List<ItemData> = listOf(
        ItemData("ChatGPT - 3.5", R.drawable.gpt_icon, Color(26, 182, 143, 255),Screen.ChatGPT),
        ItemData("Text Recognition/Translation", R.drawable.imgtotext, Color(61, 109, 255, 255), Screen.ImageToText),
    )
)
class Colors {
    companion object {
        val gptColor = Color(26, 182, 143, 255)
    }

}
data class ItemData(
    val title: String,
    val icon: Int,
    val iconColor: Color,
    val screen: Screen
)