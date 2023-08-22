package com.example.fifthsemproject.presentation.screens

import android.content.Context
import android.preference.PreferenceManager
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.fifthsemproject.R
import com.example.fifthsemproject.presentation.navigation.Screen
import com.example.fifthsemproject.presentation.screendata.ItemData

@Composable
fun SettingsScreen(){
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(24, 24, 24, 255))) {
        Spacer(modifier = Modifier.height(50.dp))
        SingleButton(
            itemData = ItemData(
                "Delete GPT Conversation", R.drawable.gpt_icon, Color(
                    26,
                    182,
                    143,
                    255
                ), Screen.Settings
            ),
            onClickScreen = {},
            onClick = { clearChatHistory(context) }
        )
    }
}

fun clearChatHistory(context: Context) {
    val sharedPreferences = context.getSharedPreferences("releaseid", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.remove("somenewid")
    editor.apply()
    Toast.makeText(context, "Chats Deleted", Toast.LENGTH_SHORT).show()
}