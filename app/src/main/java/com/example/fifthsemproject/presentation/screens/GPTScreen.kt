package com.example.fifthsemproject.presentation.screens

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fifthsemproject.R
import com.example.fifthsemproject.domain.models.SingleInteraction
import com.example.fifthsemproject.presentation.viewmodels.GPTViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GPTScreen(
    gptViewModel: GPTViewModel = hiltViewModel()
){
    val state = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = state,
        drawerContent = { 
            DrawerScreen(gptViewModel, state)
        },
        content = {
            GPTScreenContent(gptViewModel = gptViewModel, state)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerScreen(gptViewModel: GPTViewModel, state: DrawerState){
    ModalDrawerSheet(
        drawerContainerColor = Color(24, 24, 24, 255),
    ) {
        val scope = rememberCoroutineScope()
        NavigationDrawerItem(
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
            label = {
                Text(text = "Chat History", fontSize = 13.sp)
            },
            selected = false,
            onClick = {  },
            modifier = Modifier
                .padding(10.dp, 5.dp)
                .height(30.dp)
        )
        NavigationDrawerItem(
            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color(
                39,
                39,
                39,
                255
            )
            ),
            label = {
                Icon(
                    painter = painterResource(id = R.drawable.add_icon),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    contentDescription = null,
                    tint = Color.White)
            },
            selected = false,
            onClick = {
                scope.launch {
                    state.close()
                }
                gptViewModel.newChat()
                gptViewModel.selectedItemIndex = -1
                      },
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier
                .padding(10.dp, 5.dp)
                .height(60.dp)
        )
        gptViewModel.messagesHistory.forEachIndexed {index, _ ->
            val title = try {
                gptViewModel.messagesHistory[index].conversation.first()
            } catch (e:NoSuchElementException){
                SingleInteraction("", "")
            }
            if(title.content.isNotBlank()){
                NavigationDrawerItem(
                    badge = {
                        var showDialog by remember {
                            mutableStateOf(false)
                        }
                        Icon(
                            painter = painterResource(id = R.drawable.delete_icon),
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                showDialog = true
                            }
                        )
                        if(showDialog){
                            AlertDialog(
                                icon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.about_icon),
                                        contentDescription = null,
                                        tint = Color.Red
                                    )
                                },
                                title = {
                                    Text(text = "CAUTION", fontSize = 16.sp, color = Color.Red)
                                },
                                text = {
                                    Text(text = "This will delete the chat :${title.content} permanently", textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.White)
                                },
                                containerColor = Color(24, 24, 24, 255),
                                onDismissRequest = { showDialog = false },
                                confirmButton = {
                                    Box(
                                        contentAlignment = Center,
                                        modifier = Modifier
                                            .fillMaxWidth(0.6f)
                                            .background(
                                                Color(
                                                    41,
                                                    41,
                                                    41,
                                                    255
                                                ), RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                showDialog = false
                                                gptViewModel.deleteChat(title.time)
                                            }
                                            .padding(5.dp)
                                    ) {
                                        Text(text = "DELETE", fontSize = 15.sp, color = Color.Red)
                                    }
                                },
                                dismissButton = {
                                    Box(
                                        contentAlignment = Center,
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .fillMaxWidth(0.6f)
                                            .background(
                                                Color(
                                                    41,
                                                    41,
                                                    41,
                                                    255
                                                ), RoundedCornerShape(10.dp)
                                            )
                                            .clickable {
                                                showDialog = false
                                            }
                                            .padding(5.dp)
                                    ) {
                                        Text(text = "CANCEL", fontSize = 15.sp, color = Color.White)
                                    }
                                })
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = Color(30, 37, 41, 255),
                        selectedContainerColor = Color(12, 121, 0, 255)),
                    label = { Text(text = title.content, maxLines = 1) },
                    selected = index==gptViewModel.selectedItemIndex,
                    onClick = {
                        scope.launch {
                            gptViewModel.setChatToIndex(index)
                            delay(100)
                            state.close()
                            gptViewModel.selectedItemIndex=index
                        }
                    },
                    shape = RoundedCornerShape(15.dp),
                    modifier = Modifier
                        .padding(10.dp, 5.dp)
                        .height(60.dp)
                )
            }
        }
    }
}

@Composable
fun ConversationScreen(role: String, conversation: String , gptViewModel: GPTViewModel){
    if(role=="user"){
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(0.dp, 50.dp)) {
            Text(text = conversation,
                modifier = Modifier
                    .padding(30.dp, 10.dp, 5.dp, 10.dp)
                    .background(Color(0, 0, 0, 255), RoundedCornerShape(15.dp))
                    .padding(10.dp),
                color = Color(255, 255, 255, 210),
                fontSize = 16.sp
            )
            Icon(
                painter = painterResource(id = R.drawable.person_icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(0.dp, 24.dp, 5.dp, 0.dp)
                    .size(20.dp),
                tint = Color.White
            )
        }
    } else {
        Row(modifier = Modifier.fillMaxWidth()) {
            Icon(
                painter = painterResource(id = R.drawable.gpt_icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(5.dp, 22.dp, 5.dp, 0.dp)
                    .size(20.dp),
                tint = Color(26, 182, 143, 255)
            )
            Column(
                modifier = Modifier
                    .padding(0.dp, 10.dp, 30.dp, 10.dp)
                    .fillMaxWidth()
                    .background(Color(30, 37, 41, 255), RoundedCornerShape(15.dp))
                    .padding(10.dp),
            ) {
                Row(modifier = Modifier
                    .padding(bottom = 5.dp)
                    .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "ChatGPT:",
                        color = Color(255, 255, 255, 148),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "Speak: ",
                        color = Color(102, 140, 255, 255),
                        fontSize = 13.sp
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.speaker_icon),
                        contentDescription = null,
                        tint = Color(102, 140, 255, 255),
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .clickable {
                                gptViewModel.detectLanguage(conversation)
                            }
                    )
                }
                Text(
                    text = conversation,
                    color = Color(255, 255, 255, 210),
                    fontSize = 16.sp
                )
            }
        }
    }
}
@Composable
fun TextToSpeechComponent(gptViewModel: GPTViewModel) {
    val context = LocalContext.current
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        ttsEngine = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                ttsEngine?.language = Locale.getDefault()
            }
        }
        gptViewModel.ttsEngine = ttsEngine
        onDispose {
            ttsEngine?.shutdown()
            gptViewModel.ttsEngine = null
        }
    }
}