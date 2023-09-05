package com.example.fifthsemproject.presentation.screens.music

import android.Manifest
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.fifthsemproject.MainViewModel
import com.example.fifthsemproject.R
import com.example.fifthsemproject.presentation.screendata.UniversalColors
import com.example.fifthsemproject.presentation.screendata.UniversalColors.Companion.backgroundColor
import com.example.fifthsemproject.presentation.viewmodels.LocalMusicVIewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocalMusic(mainViewModel: MainViewModel) {

    val musicPermissionState: PermissionState = rememberPermissionState(Manifest.permission.READ_EXTERNAL_STORAGE)

    if(musicPermissionState.status.isGranted){
        LocalMusicScreenContent(mainViewModel)
    } else {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor), contentAlignment = Alignment.Center) {
            Button(modifier = Modifier.height(60.dp),shape = RoundedCornerShape(10.dp),colors = ButtonDefaults.buttonColors(containerColor = Color(43, 43, 43, 255)),onClick = {musicPermissionState.launchPermissionRequest()}) {
                Icon(modifier = Modifier.size(20.dp),painter = painterResource(id = R.drawable.music_icon), contentDescription = "Camera", tint = Color.White)
                Text(text = "   Grant storage permission", color = Color.White)
            }
        }
    }
}

@Composable
fun LocalMusicScreenContent(
    mainViewModel: MainViewModel,
    localMusicVIewModel: LocalMusicVIewModel = viewModel()
){
    Row(modifier = Modifier.fillMaxSize()) {
        val state = rememberLazyListState()
        LazyColumn(
            state = state,
            modifier = Modifier
            .padding(top = 40.dp)
            .simpleVerticalScrollbar(state), contentPadding = PaddingValues(bottom = 100.dp)) {
            itemsIndexed(mainViewModel.musicList, key = { _, item -> item.index}){ index, _ ->
                Row(modifier = Modifier
                    .padding(10.dp, 5.dp)
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(
                        if (index % 2 == 0) Color(37, 37, 37, 255) else Color.Transparent,
                        RoundedCornerShape(15.dp)
                    )
                    .clickable {
                        mainViewModel.musicPlayer?.playMusic(mainViewModel.musicList[index].index)
                    }
                    .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = mainViewModel.musicList[index].albumUri,
                        error = painterResource(id = R.drawable.music_icon),
                        contentDescription = "image",
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(50.dp),

                        )
                    Column {
                        Text(
                            text = mainViewModel.musicList[index].title,
                            color = Color.White,
                            modifier = Modifier,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 15.sp
                        )
                        Text(
                            text = mainViewModel.musicList[index].artist,
                            color = Color(
                                255,
                                255,
                                255,
                                159
                            ),
                            modifier = Modifier,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

fun Modifier.simpleVerticalScrollbar(
    state: LazyListState,
    width: Dp = 8.dp
): Modifier = composed {
    val targetAlpha = if (state.isScrollInProgress) 1f else 0f
    val duration = if (state.isScrollInProgress) 150 else 500

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration), label = ""
    )

    drawWithContent {
        drawContent()

        val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
        val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f

        // Draw scrollbar if scrolling or if the animation is still running and lazy column has content
        if (needDrawScrollbar && firstVisibleElementIndex != null) {
            val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
            val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
            val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight

            drawRoundRect(
                color = UniversalColors.localMusicColor,
                topLeft = Offset(this.size.width - width.toPx(), scrollbarOffsetY),
                size = Size(width.toPx(), scrollbarHeight),
                alpha = alpha,
                cornerRadius = CornerRadius(10f, 10f)
            )
        }
    }
}














