package com.example.fifthsemproject.presentation.screens.location

import android.Manifest
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fifthsemproject.R
import com.example.fifthsemproject.presentation.screendata.UniversalColors
import com.example.fifthsemproject.presentation.viewmodels.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationShareScreen(locationViewModel: LocationViewModel){

    val fineLocation: MultiplePermissionsState = rememberMultiplePermissionsState(listOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))

    if(fineLocation.allPermissionsGranted){
        LocationTab(locationViewModel)
    } else {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(UniversalColors.backgroundColor), contentAlignment = Alignment.Center) {
            Button(modifier = Modifier.height(60.dp),shape = RoundedCornerShape(10.dp),colors = ButtonDefaults.buttonColors(containerColor = Color(43, 43, 43, 255)),onClick = {
                fineLocation.launchMultiplePermissionRequest()
            }) {
                Icon(modifier = Modifier.size(20.dp),painter = painterResource(id = R.drawable.music_icon), contentDescription = "Camera", tint = Color.White)
                Text(text = "   Grant Location permission", color = Color.White)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationTab(
    locationViewModel: LocationViewModel = viewModel()
){
    Column {
        var showDialog by remember { mutableStateOf(false) }
        var usernameDialog by remember { mutableStateOf(false) }
        var stopSharing by remember { mutableStateOf(false) }
        val context = LocalContext.current

        if(showDialog){
            AlertDialog(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.location_icon),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                },
                title = {
                    Text(text = "CAUTION", fontSize = 16.sp, color = UniversalColors.locationShareColor)
                },
                text = {
                    Text(text = "Share you live location? Only people selected by you can see your location", textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.White)
                },
                containerColor = Color(24, 24, 24, 255),
                onDismissRequest = { showDialog = false },
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
                                usernameDialog = true
                                showDialog = false
                            }
                            .padding(5.dp)
                    ) {
                        Text(text = "SHARE", fontSize = 15.sp, color = UniversalColors.locationShareColor)
                    }
                },
                dismissButton = {
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
                                showDialog = false
                            }
                            .padding(5.dp)
                    ) {
                        Text(text = "CANCEL", fontSize = 15.sp, color = Color.White)
                    }
                })
        }
        if(usernameDialog){
            AlertDialog(
                text = {
                    TextField(
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
                        value = locationViewModel.name,
                        onValueChange = { locationViewModel.name = it },
                        label = { Text(text = "set a username") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = {
                            if(locationViewModel.name.isNotBlank()){
                                locationViewModel.enableSharing(context)
                                usernameDialog = false
                            }
                        })
                    )
                },
                containerColor = Color(24, 24, 24, 255),
                onDismissRequest = { usernameDialog = false },
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
                                if (locationViewModel.name.isNotBlank()) {
                                    locationViewModel.enableSharing(context)
                                    usernameDialog = false
                                }
                            }
                            .padding(5.dp)
                    ) {
                        Text(text = "SHARE", fontSize = 15.sp, color = UniversalColors.locationShareColor)
                    }
                },
                dismissButton = {
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
                                usernameDialog = false
                            }
                            .padding(5.dp)
                    ) {
                        Text(text = "CANCEL", fontSize = 15.sp, color = Color.White)
                    }
                })
        }
        if(stopSharing){
            AlertDialog(
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.location_icon),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )
                },
                title = {
                    Text(text = "CAUTION", fontSize = 16.sp, color = UniversalColors.locationShareColor)
                },
                text = {
                    Text(modifier = Modifier.fillMaxWidth(),text = "Stop sharing location?", textAlign = TextAlign.Center, fontSize = 14.sp, color = Color.White)
                },
                containerColor = Color(24, 24, 24, 255),
                onDismissRequest = { stopSharing = false },
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
                                locationViewModel.disableSharing(context)
                                stopSharing = false
                            }
                            .padding(5.dp)
                    ) {
                        Text(text = "STOP", fontSize = 15.sp, color = UniversalColors.locationShareColor)
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
                                stopSharing = false
                            }
                            .padding(5.dp)
                    ) {
                        Text(text = "CANCEL", fontSize = 15.sp, color = Color.White)
                    }
                })
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(15.dp, 10.dp, 15.dp, 15.dp)
                .fillMaxWidth()
                .background(
                    Color(39, 39, 39, 255),
                    RoundedCornerShape(15.dp)
                )
                .padding(10.dp)
        ) {
            Text(
                text = "   Location Sharing ${if (locationViewModel.isSharingLocation) "Enabled" else "Disabled"}",
                fontSize = 15.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Switch(
                colors = SwitchDefaults.colors(
                    checkedTrackColor = UniversalColors.locationShareColor,
                    checkedIconColor = Color.White,
                    checkedBorderColor = Color.White
                ),
                checked = locationViewModel.isSharingLocation, onCheckedChange = {
                if(it){
                    showDialog = true
                } else {
                    stopSharing = true
                }
            })
        }
        if(locationViewModel.enabled){
            SharingSuccessScreen(locationViewModel)
        }
    }
}

@Composable
fun SharingSuccessScreen(locationViewModel: LocationViewModel) {
    val context = LocalContext.current
    LaunchedEffect(key1 = locationViewModel.isServiceOn, key2 = locationViewModel.isSharingLocation){
        if(locationViewModel.isSharingLocation && locationViewModel.isServiceOn && !locationViewModel.loadingService && !locationViewModel.loadingService){
            delay(1500)
            locationViewModel.getId(context)
            locationViewModel.showContent = true
            locationViewModel.getCC()
        } else {
            locationViewModel.showContent = false
        }
    }
    if(!locationViewModel.showContent){
        if(locationViewModel.loadingSharing){
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(20.dp,10.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp),color = UniversalColors.locationShareColor, strokeWidth = 2.dp)
                Text(text = "Connecting to server", modifier = Modifier.padding(10.dp), fontSize = 14.sp, color = Color.White)
            }
        }else {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(20.dp,10.dp)) {
                Icon(modifier = Modifier.size(20.dp),painter = painterResource(id = R.drawable.done_icon), contentDescription = null, tint = UniversalColors.locationShareColor)
                Text(text = "Connected to server", modifier = Modifier.padding(10.dp), fontSize = 14.sp, color = Color.White)
            }
        }
        if(locationViewModel.loadingSharing){
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(20.dp, 10.dp)) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp),color = UniversalColors.locationShareColor, strokeWidth = 2.dp)
                Text(text = "Starting location service", modifier = Modifier.padding(10.dp), fontSize = 14.sp, color = Color.White)
            }
        }else {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(20.dp,10.dp)) {
                Icon(modifier = Modifier.size(20.dp),painter = painterResource(id = R.drawable.done_icon), contentDescription = null, tint = UniversalColors.locationShareColor)
                Text(text = "Service started", modifier = Modifier.padding(10.dp), fontSize = 14.sp, color = Color.White)
            }
        }
    }
    AnimatedVisibility(visible = locationViewModel.showContent, enter = fadeIn(), exit = fadeOut()) {
        Column {
            val clipboardManager: ClipboardManager = LocalClipboardManager.current
            Text(text = "     Your share key:", color = Color.White)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(20.dp, 10.dp)
                    .background(Color.Black, RoundedCornerShape(10.dp))
                    .fillMaxWidth()
                    .padding(10.dp)) {
                Text(text = locationViewModel.currentId, color = Color.White)
                Icon(
                    painter = painterResource(id = R.drawable.copy_icon),
                    contentDescription = null,
                    tint = UniversalColors.locationShareColor,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            clipboardManager.setText(AnnotatedString((locationViewModel.currentId)))
                            Toast
                                .makeText(context, "copied", Toast.LENGTH_SHORT)
                                .show()
                        }
                )
            }
            if(locationViewModel.loadingCC){
                CircularProgressIndicator(modifier = Modifier.padding(20.dp).size(20.dp),color = UniversalColors.locationShareColor, strokeWidth = 2.dp)
            }
            AnimatedVisibility(modifier = Modifier.padding(20.dp, 10.dp),visible = locationViewModel.isCCAvailable, enter = fadeIn(), exit = fadeOut()){
                if(locationViewModel.currentCC!=null){
                    Column {
                        Text(text = "Broadcasting current location with coordinates:\n", color = Color.White)
                        Text(text = "Longitude: ${locationViewModel.currentCC!!.longitude}", color = Color.White)
                        Text(text = "Latitude: ${locationViewModel.currentCC!!.latitude}", color = Color.White)
                    }
                }else {
                    Text(text = "oops...looks like something went wrong  [TAP TO RELOAD LOCATION]", color = Color.White, modifier = Modifier.clickable { locationViewModel.getCC() })
                }
            }
        }
    }
}

































