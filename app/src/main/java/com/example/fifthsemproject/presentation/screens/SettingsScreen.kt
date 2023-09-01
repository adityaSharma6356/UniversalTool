package com.example.fifthsemproject.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fifthsemproject.R
import com.example.fifthsemproject.domain.models.NotificationData
import com.example.fifthsemproject.domain.models.PushNotification
import com.example.fifthsemproject.presentation.navigation.Screen
import com.example.fifthsemproject.presentation.screendata.Colors.Companion.gptColor
import com.example.fifthsemproject.presentation.screendata.ItemData
import com.example.fifthsemproject.presentation.services.Constants.Companion.ADMIN_PASSWORD
import com.example.fifthsemproject.presentation.services.Constants.Companion.TOPIC
import com.example.fifthsemproject.presentation.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settingsViewModel: SettingsViewModel = viewModel()
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(24, 24, 24, 255))) {
        var showKeyDialog by remember { mutableStateOf(false) }

        Spacer(modifier = Modifier.height(50.dp))

        if(showKeyDialog){
            var newKeyData by remember {
                mutableStateOf("")
            }
            var providedPassword by remember {
                mutableStateOf("")
            }
            var buttonColor by remember {
                mutableStateOf(Color(255, 255, 255, 215))
            }
            val animatingColor by animateColorAsState(
                targetValue = buttonColor,
                label = "animatingButtonColor",
                animationSpec = tween(300)
            )
            AlertDialog(
                containerColor = Color(31, 31, 31, 255),
                text = {
                    Column {
                        OutlinedTextField(
                            maxLines = 1,
                            label = {
                                Text(
                                    text = "NEW KEY"
                                )
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor =  gptColor ,
                                focusedBorderColor = gptColor,
                                unfocusedLabelColor = Color.Gray,
                                unfocusedBorderColor = Color.Gray,
                            ),
                            value = newKeyData,
                            onValueChange ={ newKeyData = it}
                        )
                        OutlinedTextField(
                            maxLines = 1,
                            label = {
                                Text(
                                    text = "ADMIN-PASSWORD"
                                )
                            },
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedLabelColor =  gptColor,
                                focusedBorderColor = gptColor,
                                unfocusedLabelColor = Color.Gray,
                                unfocusedBorderColor = Color.Gray,
                            ),
                            value = providedPassword,
                            onValueChange ={
                                providedPassword = it
                                if(providedPassword == ADMIN_PASSWORD){
                                    buttonColor = gptColor
                                } else {
                                    buttonColor = Color(255, 43, 43, 255)
                                }
                            }
                        )
                    }
                },
                icon = { Icon(modifier = Modifier.size(20.dp),painter = painterResource(id = R.drawable.gpt_icon), contentDescription = null, tint = gptColor)},
                onDismissRequest = { showKeyDialog = false },
                confirmButton = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(70.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(Color(36, 36, 36, 255), RoundedCornerShape(15.dp))
                            .clickable {
                                if (newKeyData.isNotBlank()) {
                                    if (providedPassword == ADMIN_PASSWORD) {
                                        val temp = PushNotification(
                                            NotificationData(
                                                "Universal Key Update", newKeyData
                                            ), TOPIC
                                        )
                                        settingsViewModel.sendNotification(temp)
                                        showKeyDialog = false
                                    }
                                }
                            }
                            .border(1.dp, animatingColor, RoundedCornerShape(15.dp))
                    ) {
                        Text(
                            text = "Share new key",
                            color = animatingColor,
                            fontSize = 14.sp
                        )
                    }
                },
                )
        }
        SingleButton(
            itemData = ItemData("ChatGPT - 3.5", R.drawable.gpt_icon, gptColor,Screen.ChatGPT),
            onClickScreen = {}
        ) {
            showKeyDialog = true
        }
    }
}

