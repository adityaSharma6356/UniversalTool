package com.example.fifthsemproject.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.fifthsemproject.MainViewModel
import com.example.fifthsemproject.presentation.navigation.Screen

@Composable
fun AiScreen(
    mainViewModel: MainViewModel,
    navController: NavController,
    screen: Screen
){
    ItemsListScreen(
        mainViewModel = mainViewModel,
        mainViewModel.screenData.itemsList,
        onClick = {
            navController.navigate(it.route) {
                popUpTo(navController.graph.findStartDestination().id){
                    saveState = true
                }
                restoreState = true
                launchSingleTop = true
            }
        }
    )
}