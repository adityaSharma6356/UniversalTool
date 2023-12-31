package com.example.fifthsemproject

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fifthsemproject.presentation.navigation.BottomNav
import com.example.fifthsemproject.presentation.navigation.Screen
import com.example.fifthsemproject.presentation.screendata.UniversalColors
import com.example.fifthsemproject.presentation.screens.codeforces.UserInfoScreen
import com.example.fifthsemproject.presentation.screens.gpt.GPTScreen
import com.example.fifthsemproject.presentation.screens.image_to_pdf.ImageToText
import com.example.fifthsemproject.presentation.screens.location.LocationObserverScreen
import com.example.fifthsemproject.presentation.screens.location.LocationShareScreen
import com.example.fifthsemproject.presentation.screens.music.LocalMusic
import com.example.fifthsemproject.presentation.screens.music.OnlineMusic
import com.example.fifthsemproject.presentation.screens.music.BottomMusicControllerScreen
import com.example.fifthsemproject.presentation.screens.music.CurrentMusicScreen
import com.example.fifthsemproject.presentation.viewmodels.CodeforcesViewModel
import com.example.fifthsemproject.presentation.viewmodels.LocationObserveViewModel
import com.example.fifthsemproject.presentation.viewmodels.LocationViewModel
import com.example.fifthsemproject.ui.theme.FifthSemProjectTheme
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel by viewModels<MainViewModel>()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().subscribeToTopic("GPT_KEY_UPDATES")
        setContent {
            FifthSemProjectTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(UniversalColors.backgroundColor),
                ) {
                    val mainNavController = rememberNavController()
                    NavHost(modifier = Modifier
                        .zIndex(3f),navController = mainNavController, startDestination = Screen.MainFront.route){
                        composable(Screen.MainFront.route){
                            mainViewModel.job?.cancel()
                            BottomNav(mainViewModel, mainNavController)
                        }
                        composable(Screen.ChatGPT.route){
                            GPTScreen()
                        }
                        composable(Screen.LocationShare.route){
                            val temp : LocationViewModel = viewModel()
                            temp.isSharingLocation = mainViewModel.getLocationShareInfo(this@MainActivity)
                            temp.isServiceOn = mainViewModel.getServiceInfo(this@MainActivity)
                            temp.enabled = temp.isServiceOn && temp.isSharingLocation
                            LocationShareScreen(temp)
                        }
                        composable(Screen.LocationObserve.route){
                            LocationObserverScreen()
                        }
                        composable(route = Screen.ImageToText.route) {
                            ImageToText()
                        }
                        composable(route = Screen.Music.route) {
                            mainViewModel.setupMusicPlayer(this@MainActivity)
                            mainViewModel.setupLocalMediaSource(this@MainActivity)
                            if(mainViewModel.musicList.isNotEmpty()) {
                                BottomMusicControllerScreen(
                                    mainViewModel = mainViewModel,
                                    Modifier
                                        .zIndex(5f)
                                        .align(Alignment.BottomCenter),
                                    mainNavController
                                )
                            }
                            LocalMusic(mainViewModel)
                        }
                        composable(route = Screen.OnlineMusic.route) {
                            OnlineMusic()
                        }
                        composable(route = Screen.CodeforcesUserInfoScreen.route) {
                            val temp : CodeforcesViewModel = hiltViewModel()
                            temp.getHandles(this@MainActivity)
                            UserInfoScreen(temp)
                        }
                        composable(route = Screen.CurrentPlayer.route) {
                            CurrentMusicScreen(
                                mainViewModel,
                                mainNavController
                            )
                        }

                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel.musicPlayer?.releasePlayer()
    }
}



