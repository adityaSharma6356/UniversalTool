package com.example.fifthsemproject.presentation.screens.location

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fifthsemproject.R
import com.example.fifthsemproject.presentation.screendata.UniversalColors
import com.example.fifthsemproject.presentation.viewmodels.LocationObserveViewModel
import kotlinx.coroutines.delay


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun LocationObserverScreen(
    locationObserveViewModel: LocationObserveViewModel = viewModel()
){
    if(locationObserveViewModel.firstTime){
        locationObserveViewModel.loadLocations(LocalContext.current)
        locationObserveViewModel.firstTime = false
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(UniversalColors.backgroundColor)
            .padding(20.dp)) {
        if(locationObserveViewModel.openAddId){
            AddIdScreen(locationObserveViewModel)
        }
        Row(modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
            .background(Color.Black, RoundedCornerShape(15.dp))
            .height(45.dp)
            .padding(5.dp)
            .clickable {
                locationObserveViewModel.openAddId = true
            },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "   Add new tracker", color = UniversalColors.locationShareColor, fontSize = 15.sp)
            Icon(
                painter = painterResource(id = R.drawable.add_icon),
                contentDescription = "add",
                tint = UniversalColors.locationShareColor,
                modifier = Modifier
                    .size(30.dp)
            )
        }

        AnimatedVisibility(visible = locationObserveViewModel.loadingLocations) {
            LinearProgressIndicator(color = UniversalColors.locationShareColor, modifier = Modifier
                .padding(top = 10.dp)
                .height(1.dp)
                .fillMaxWidth())
        }
        val context = LocalContext.current
        if(locationObserveViewModel.locationsList.isNotEmpty()){
            locationObserveViewModel.locationsList.forEach { item ->
                Row(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .fillMaxWidth()
                        .background(Color(41, 41, 41, 255), RoundedCornerShape(15.dp))
                        .padding(10.dp)
                        .clickable {
                            locationObserveViewModel.openMapWithLocation(item.location.latitude.toString(), item.location.longitude.toString(), item.name, context)
                        },
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Column(Modifier.weight(1f)) {
                        Text(text = item.name, fontSize = 17.sp , color = UniversalColors.locationShareColor, fontWeight = FontWeight.ExtraBold)
                        Text(text = "latitude "+item.location.latitude.toString(), fontSize = 15.sp , color = Color.White)
                        Text(text = "longitude "+item.location.longitude.toString(), fontSize = 15.sp , color = Color.White)
                        Text(text = "last seen: "+locationObserveViewModel.lastSeenString(item.lastUpdate.seconds), fontSize = 13.sp , color = Color(255, 214, 214, 255)
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.maps_image),
                        contentDescription = "map",
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(100.dp)
                            .clip(RoundedCornerShape(15.dp))
                    )
                }
            }
        } else {
            Text(text = "No Trackers Available", fontSize = 15.sp, color = Color(
                255,
                255,
                255,
                180
            ), modifier = Modifier.padding(top = 200.dp))
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddIdScreen(
    locationObserveViewModel: LocationObserveViewModel
){
    var exists by remember {
        mutableStateOf(false)
    }
    var notExists by remember {
        mutableStateOf(false)
    }
    AlertDialog(
        text = {
            Column {
                val context = LocalContext.current
                LaunchedEffect(key1 = locationObserveViewModel.checkingId){
                    if(!locationObserveViewModel.checkingId && locationObserveViewModel.idExists && locationObserveViewModel.openAddId){
                        exists = true
                        locationObserveViewModel.storeNewId(locationObserveViewModel.tempId, context)
                        delay(1000)
                        locationObserveViewModel.loadLocations(context)
                        locationObserveViewModel.idExists = false
                        locationObserveViewModel.idNotExists = false
                        locationObserveViewModel.tempId = ""
                        locationObserveViewModel.openAddId = false
                    }
                    if(!locationObserveViewModel.checkingId && locationObserveViewModel.idNotExists && locationObserveViewModel.openAddId){
                        notExists = true
                    }
                }
                TextField(
                    maxLines = 1,
                    enabled = !locationObserveViewModel.checkingId,
                    shape = RoundedCornerShape(10.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        disabledTextColor = Color(184, 192, 255, 255)
                    ),
                    modifier = Modifier
                        .padding(5.dp, 10.dp),
                    value = locationObserveViewModel.tempId,
                    onValueChange = { locationObserveViewModel.tempId = it },
                    label = { Text(text = "Provide Valid ID") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if(!locationObserveViewModel.checkingId && locationObserveViewModel.tempId.isNotBlank()){
                            locationObserveViewModel.idNotExists = false
                            locationObserveViewModel.idExists = false
                            exists = false
                            notExists = false
                            locationObserveViewModel.checkDocumentId(locationObserveViewModel.tempId)
                        }
                    })
                )
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)){
                    if(locationObserveViewModel.checkingId){
                        CircularProgressIndicator(modifier = Modifier
                            .padding(start = 10.dp)
                            .size(12.dp),color = UniversalColors.locationShareColor, strokeWidth = 1.dp)
                        Text(text = "  Checking", color = Color.White, fontSize = 13.sp)
                    }
                    if(exists){
                        Icon(painter = painterResource(id = R.drawable.done_icon), contentDescription = null,modifier = Modifier
                            .padding(start = 10.dp)
                            .size(12.dp), tint = UniversalColors.locationShareColor )
                        Text(text = "  ID Valid", color = UniversalColors.locationShareColor, fontSize = 13.sp)
                    }
                    if(notExists){
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(id = R.drawable.cancel_icon), contentDescription = null,modifier = Modifier
                                .padding(start = 10.dp)
                                .size(12.dp), tint = Color(
                                255,
                                54,
                                54,
                                255
                            )
                            )
                            Text(text = "  ID Invalid", color = Color(
                                255,
                                54,
                                54,
                                255
                            ), fontSize = 13.sp)
                        }
                    }
                }
            }
        },
        containerColor = Color(24, 24, 24, 255),
        onDismissRequest = {
            if (!locationObserveViewModel.checkingId) {
                locationObserveViewModel.idExists = false
                locationObserveViewModel.idNotExists = false
                locationObserveViewModel.tempId = ""
                locationObserveViewModel.openAddId = false
            }
        },
        confirmButton = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(45.dp)
                    .background(
                        Color(
                            41,
                            41,
                            41,
                            255
                        ), RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        if (!locationObserveViewModel.checkingId && locationObserveViewModel.tempId.isNotBlank()) {
                            locationObserveViewModel.idNotExists = false
                            locationObserveViewModel.idExists = false
                            exists = false
                            notExists = false
                            locationObserveViewModel.checkDocumentId(locationObserveViewModel.tempId)
                        }
                    }
                    .padding(5.dp)
            ) {
                Text(text = "CONFIRM", fontSize = 15.sp, color = UniversalColors.locationShareColor)
            }
        },
        dismissButton = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxWidth(0.6f)
                    .height(45.dp)
                    .background(
                        Color(
                            41,
                            41,
                            41,
                            255
                        ), RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        if (!locationObserveViewModel.checkingId) {
                            locationObserveViewModel.idExists = false
                            locationObserveViewModel.idNotExists = false
                            locationObserveViewModel.tempId = ""
                            locationObserveViewModel.openAddId = false
                        }
                    }
                    .padding(5.dp)
            ) {
                Text(text = "CANCEL", fontSize = 15.sp, color = Color.White)
            }
        })
}















