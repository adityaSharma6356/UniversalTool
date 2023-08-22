package com.example.fifthsemproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fifthsemproject.presentation.navigation.BottomNav
import com.example.fifthsemproject.presentation.navigation.Screen
import com.example.fifthsemproject.presentation.screens.GPTScreen
import com.example.fifthsemproject.presentation.screens.ImageToText
import com.example.fifthsemproject.presentation.viewmodels.GPTViewModel
import com.example.fifthsemproject.ui.theme.FifthSemProjectTheme

class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FifthSemProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val mainNavController = rememberNavController()
                    NavHost(navController = mainNavController, startDestination = Screen.MainFront.route){
                        composable(Screen.MainFront.route){
                            BottomNav(mainViewModel, mainNavController)
                        }
                        composable(Screen.ChatGPT.route){
                            val context = LocalContext.current
                            val gptViewModel: GPTViewModel = viewModel()
                            gptViewModel.loadMessages(context = context)
                            GPTScreen(gptViewModel)
                        }
                        composable(route = Screen.ImageToText.route) {
                            ImageToText()
                        }
                    }
                }
            }
        }
    }
}



