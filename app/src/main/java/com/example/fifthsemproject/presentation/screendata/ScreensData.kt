package com.example.fifthsemproject.presentation.screendata

import androidx.compose.ui.graphics.Color
import com.example.fifthsemproject.R
import com.example.fifthsemproject.presentation.navigation.Screen

class AiScreensData {
    companion object {
        val itemsList: List<ItemData> = listOf(
            ItemData("ChatGPT - 3.5", R.drawable.gpt_icon, Color(26, 182, 143, 255), Screen.ChatGPT),
            ItemData(
                "Text Recognition/Translation",
                R.drawable.imgtotext,
                Color(61, 109, 255, 255),
                Screen.ImageToText
            ),
        )
    }
}
class MediaScreenData {
    companion object {
        val itemsList: List<ItemData> = listOf(
            ItemData("Local Music Player", R.drawable.music_icon, Color(233, 30, 99, 255), Screen.Music),
            ItemData("Online Music Player", R.drawable.online_music_icon, Color.Cyan, Screen.OnlineMusic),
        )
    }
}
class LocationScreenData {
    companion object {
        val itemsList: List<ItemData> = listOf(
            ItemData("Live Location Sharing", R.drawable.location_icon, null , Screen.LocationShare),
            ItemData("Live Location Tracking", R.drawable.gps_icon, null , Screen.LocationObserve),
            ItemData("Codeforces", R.drawable.codeforces_icon, null , Screen.CodeforcesUserInfoScreen)
        )
    }
}

class UniversalColors {
    companion object {
        val localMusicColor =  Color(233, 30, 99, 255)
        val gptColor = Color(26, 182, 143, 255)
        val backgroundColor = Color(24, 24, 24, 255)
        val locationShareColor = Color(0, 166, 255, 255)
        val codeforcesColor = Color(243, 195, 66, 200)
    }
}
data class ItemData(
    val title: String,
    val icon: Int,
    val iconColor: Color?,
    val screen: Screen
)