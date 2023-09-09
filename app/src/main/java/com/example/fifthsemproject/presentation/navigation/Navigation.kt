package com.example.fifthsemproject.presentation.navigation

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fifthsemproject.MainViewModel
import com.example.fifthsemproject.R
import com.example.fifthsemproject.presentation.screens.AiScreen
import com.example.fifthsemproject.presentation.screens.MediaScreen
import com.example.fifthsemproject.presentation.screens.MenuScreen
import com.example.fifthsemproject.presentation.screens.SettingsScreen


sealed class Screen(val route: String,val label: String,@DrawableRes val icon: Int, @DrawableRes val offIcon: Int) {
    object Menu : Screen("menu", " Home", R.drawable.menu_icon, R.drawable.ai_icon)
    object Media : Screen("media", "Media", R.drawable.music, R.drawable.music)
    object Ai : Screen("a_i", " AI Tools", R.drawable.ai_icon, R.drawable.ai_icon)
    object Settings : Screen("settings", " Settings", R.drawable.settings, R.drawable.settings)
    object MainFront : Screen("main_front", "MainFront", R.drawable.settings, R.drawable.settings)
    object ChatGPT : Screen("chat_gpt", "ChetGPT", R.drawable.gpt_icon, R.drawable.gpt_icon)
    object ImageToText : Screen("image_to_text", "ImageToText", R.drawable.imgtotext, R.drawable.imgtotext)
    object Music : Screen("music", "Music", R.drawable.music_icon, R.drawable.music_icon)
    object OnlineMusic : Screen("online_music", "Online Music", R.drawable.online_music_icon, R.drawable.online_music_icon)
    object CurrentPlayer : Screen("current_player_screen", "Playing", R.drawable.online_music_icon, R.drawable.online_music_icon)
    object LocationShare : Screen("location_screen", "Location", R.drawable.location_icon, R.drawable.location_icon)
    object LocationObserve : Screen("location_observe_screen", "Location Observer", R.drawable.gps_icon, R.drawable.gps_icon)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNav(mainViewModel: MainViewModel, mainNavController: NavHostController) {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
                if(mainViewModel.visibleAbout){
                    Dialog(onDismissRequest = { mainViewModel.visibleAbout = false}) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .background(
                                    Color(
                                        24,
                                        24,
                                        24,
                                        255
                                    )
                                )) {
                            Text(text = "by Aditya Sharma :)", fontSize = 15.sp, color = Color.White)
                        }
                    }

            }
            Row(horizontalArrangement = Arrangement.End,modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.Transparent), verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.about_icon),
                    contentDescription = "about",
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .size(20.dp)
                        .clickable { mainViewModel.visibleAbout = true },
                    tint = Color.White
                )
            }
        },
        bottomBar = { BottomBar(navController = navController) }
    ) {
        Modifier.padding(it)
        BottomNavGraph(
            mainNavController = mainNavController,
            navController = navController,
            mainViewModel = mainViewModel
        )
    }
}

@Composable
fun BottomNavGraph(
    mainNavController: NavHostController,
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route
    ) {
        composable(route = Screen.Menu.route) {
            MenuScreen(mainNavController)
        }
        composable(route = Screen.Media.route) {
            MediaScreen(mainNavController)
        }
        composable(route = Screen.Ai.route) {
            AiScreen(
                navController = mainNavController
            )
        }
        composable(route = Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
@Composable
fun BottomBar(navController: NavHostController) {
    val screens = listOf(
        Screen.Menu,
        Screen.Media,
        Screen.Ai,
        Screen.Settings
    )

    val navStackBackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navStackBackEntry?.destination

    Row(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)
            .background(Color(0, 0, 0, 255), RoundedCornerShape(15.dp))
            .fillMaxWidth()
            .height(65.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        screens.forEach { screen ->
            AddItem(
                screen = screen,
                currentDestination = currentDestination,
                navController = navController
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    screen: Screen,
    currentDestination: NavDestination?,
    navController: NavHostController
) {
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

    val background =
        if (selected) Color.White else Color.Transparent

    val contentColor =
        if (!selected) Color.White else Color.Black

    Box(
        modifier = Modifier
            .height(40.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(background)
            .clickable(onClick = {
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            })
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, top = 8.dp, bottom = 8.dp)
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(id = if (selected) screen.icon else screen.icon),
                contentDescription = "icon",
                tint = contentColor
            )
            AnimatedVisibility(visible = selected) {
                Text(
                    text = screen.label,
                    color = contentColor,
                    fontSize = 12.sp
                )
            }
        }
    }
}