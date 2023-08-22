package com.example.fifthsemproject.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fifthsemproject.R
import com.example.fifthsemproject.presentation.viewmodels.GPTViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GPTScreen(
    gptViewModel: GPTViewModel = viewModel()
){
    val state = rememberLazyListState(initialFirstVisibleItemIndex = gptViewModel.messagesHistory.lastIndex)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    Box(modifier = Modifier
        .fillMaxSize()
        .background(Color(24, 24, 24, 255))){
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .padding(bottom = 75.dp)
                .fillMaxSize()) {
            LazyColumn(
                state = state
            ){
                items(gptViewModel.messagesHistory.size){
                    ConversationScreen(question = gptViewModel.messagesHistory[it].question, ans = gptViewModel.messagesHistory[it].ans)
                }
            }
        }
        val lfm = LocalFocusManager.current
        Row(modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)) {
            TextField(
                enabled = !gptViewModel.apiManager.loading,
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
                value = gptViewModel.prompt,
                onValueChange = { gptViewModel.prompt = it },
                trailingIcon = {
                    if (gptViewModel.apiManager.loading)
                        CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                },
                placeholder = { Text(text = "Ask any question") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = {
                    if(gptViewModel.prompt.isNotBlank()){
                        scope.launch {
                            gptViewModel.callGpt(context = context, state)
                            gptViewModel.output = ""
                            lfm.clearFocus()
                        }
                    }
                })
            )
            Icon(
                painter = painterResource(id = R.drawable.send_icon),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier
                    .padding(5.dp, 0.dp, 10.dp, 23.dp)
                    .size(30.dp)
                    .align(Bottom)
                    .clickable {
                        if (gptViewModel.prompt.isNotBlank()) {
                            if (gptViewModel.prompt.isNotBlank()) {
                                scope.launch {
                                    gptViewModel.callGpt(context = context, state = state)
                                    gptViewModel.output = ""
                                    lfm.clearFocus()
                                }
                            }
                        }
                    }
            )
        }
    }
}

@Composable
fun ConversationScreen(question: String, ans: String){
    Row(modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.weight(1f))
        Text(text = " $question ",
            textAlign = TextAlign.End,
            modifier = Modifier
                .padding(15.dp, 10.dp, 20.dp, 10.dp)
                .background(Color(0, 0, 0, 255), RoundedCornerShape(15.dp))
                .padding(5.dp),

            color = Color(255, 255, 255, 255),
            fontSize = 16.sp
        )
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Icon(
            painter = painterResource(id = R.drawable.gpt_icon),
            contentDescription = null,
            modifier = Modifier
                .padding(10.dp, 16.dp, 5.dp, 0.dp)
                .size(20.dp),
            tint = Color(26, 182, 143, 255)
        )
        Text(
            text = ans,
            modifier = Modifier
                .padding(0.dp, 10.dp, 15.dp, 10.dp)
                .fillMaxWidth()
                .background(Color(12, 121, 0, 255), RoundedCornerShape(15.dp))
                .padding(5.dp),
            color = Color(255, 255, 255, 255),
            fontSize = 16.sp
        )
    }
}