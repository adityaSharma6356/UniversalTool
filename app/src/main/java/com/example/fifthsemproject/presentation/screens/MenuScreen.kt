package com.example.fifthsemproject.presentation.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.fifthsemproject.presentation.screendata.LocationScreenData

@Composable
fun MenuScreen(
    navController: NavController
){
    ItemsListScreen(
        LocationScreenData.itemsList,
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