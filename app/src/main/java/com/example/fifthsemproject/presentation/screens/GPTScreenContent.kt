package com.example.fifthsemproject.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.fifthsemproject.R
import com.example.fifthsemproject.presentation.viewmodels.GPTViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GPTScreenContent(
    gptViewModel: GPTViewModel,
    drawerState: DrawerState
){
    val state = if(gptViewModel.currentDataToDisplay.isNotEmpty()) rememberLazyListState(initialFirstVisibleItemIndex = gptViewModel.currentDataToDisplay.lastIndex) else rememberLazyListState()
    val scope = rememberCoroutineScope()
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(24, 24, 24, 255))){
        Icon(
            painter = painterResource(id = R.drawable.right_icon),
            contentDescription = "drawer",
            tint = Color.Gray,
            modifier = Modifier
                .padding(5.dp)
                .background(Color(34, 34, 34, 255), RoundedCornerShape(10.dp))
                .size(50.dp)
                .zIndex(2f)
                .padding(5.dp)
                .clickable { scope.launch { drawerState.open() } }
        )
        if(gptViewModel.notifyKeyFailure){
            AlertDialog(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.about_icon),
                        contentDescription = null,
                        tint = Color.Yellow
                    )
                },
                title = {
                    Text(text = "New key required", fontSize = 16.sp, color = Color.Yellow)
                },
                text = {
                    Text(text = "The GPT key will be updated shortly, please be patient", textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.White)
                },
                containerColor = Color(24, 24, 24, 255),
                onDismissRequest = {
                    gptViewModel.sendNotification()
                    gptViewModel.notifyKeyFailure = false
                },
                confirmButton = {
                    Box(
                        contentAlignment = Alignment.Center,
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
                                gptViewModel.sendNotification()
                                gptViewModel.notifyKeyFailure = false
                            }
                            .padding(5.dp)
                    ) {
                        Text(text = "UNDERSTANDABLE", fontSize = 15.sp, color = Color.Yellow)
                    }
                })
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .padding(bottom = 75.dp)
                .fillMaxSize()) {
            if(gptViewModel.currentDataToDisplay.isNotEmpty()){
                LazyColumn(
                    state = state
                ){
                    itemsIndexed(gptViewModel.currentDataToDisplay, key = { _, item -> item.time }){ _, item ->
                        ConversationScreen(
                            role = item.role,
                            conversation = item.content,
                            gptViewModel
                        )
                    }
                }
                TextToSpeechComponent(gptViewModel = gptViewModel)
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Send a message like saying \"hi\"", color = Color.White)
                }
            }
        }
        val lfm = LocalFocusManager.current
        Row(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)) {
            TextField(
                enabled =( !gptViewModel.loadingResponse && gptViewModel.messagingEnabled),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    disabledTextColor = Color(184, 192, 255, 255)
                ),
                modifier = Modifier
                    .padding(5.dp, 10.dp)
                    .weight(1f),
                value = gptViewModel.outgoingMessage.content,
                onValueChange = { gptViewModel.outgoingMessage = gptViewModel.outgoingMessage.copy(content = it) },
                trailingIcon = {
                    if (gptViewModel.loadingResponse)
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                },
                placeholder = { Text(text = "Ask any question") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = {
                    lfm.clearFocus()
                    gptViewModel.sendMessage(state, scope)
                })
            )
            Icon(
                painter = painterResource(id = R.drawable.send_icon),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .padding(5.dp, 0.dp, 10.dp, 23.dp)
                    .size(30.dp)
                    .align(Alignment.Bottom)
                    .clickable {
                        lfm.clearFocus()
                        gptViewModel.sendMessage(state, scope)
                    }
            )
        }
    }
}