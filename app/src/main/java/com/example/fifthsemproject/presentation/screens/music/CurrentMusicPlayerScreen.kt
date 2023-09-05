package com.example.fifthsemproject.presentation.screens.music

import android.text.format.DateFormat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.media3.common.Player
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.fifthsemproject.MainViewModel
import com.example.fifthsemproject.R
import com.example.fifthsemproject.presentation.screendata.UniversalColors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurrentMusicScreen(mainViewModel: MainViewModel, mainNavController: NavHostController) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (backIcon, image, controller, seekbar, infoText, elapsedTime, totalTime) = createRefs()
        IconButton(
            modifier = Modifier
                .constrainAs(backIcon){
                    top.linkTo(parent.top, margin = 50.dp)
                    start.linkTo(parent.start , margin = 10.dp)
                },
            colors = IconButtonDefaults.iconButtonColors(),
            onClick = { mainNavController.popBackStack() }
        ) {
            Icon(painter = painterResource(id = R.drawable.back_icon), contentDescription = "back", tint = Color.White)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .constrainAs(controller) {
                    bottom.linkTo(parent.bottom, margin = 50.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }) {
            Icon(
                painter = painterResource(id = R.drawable.shuffle_icon),
                contentDescription = "previous",
                tint = if(mainViewModel.musicListner.isShuffling) UniversalColors.localMusicColor else Color.White,
                modifier = Modifier
                    .padding(10.dp, 0.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        mainViewModel.musicPlayer?.toggleShuffle()
                    }
            )
            Icon(
                painter = painterResource(id = R.drawable.prev_icon),
                contentDescription = "previous",
                tint = Color.White,
                modifier = Modifier
                    .padding(10.dp, 0.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable {
                        mainViewModel.musicPlayer?.playPrevious()
                    }
            )
            Icon(
                painter = painterResource(id = if (mainViewModel.musicListner.isMusicPlaying) R.drawable.pause_circle else R.drawable.play_circle),
                contentDescription = "play/pause",
                tint = UniversalColors.localMusicColor,
                modifier = Modifier
                    .padding(10.dp, 0.dp)
                    .size(80.dp)
                    .clip(CircleShape)
                    .clickable {
                        mainViewModel.musicPlayer?.toggleMusic()
                    }
            )
            Icon(
                painter = painterResource(id = R.drawable.next_icon),
                contentDescription = "next",
                tint = Color.White,
                modifier = Modifier
                    .padding(10.dp, 0.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable {
                        mainViewModel.musicPlayer?.playNext()
                    }
            )
            Icon(
                painter = painterResource(id = if(mainViewModel.musicListner.repeatMode==Player.REPEAT_MODE_ONE) R.drawable.repeat_one else R.drawable.repeat_icon),
                contentDescription = "previous",
                tint = if(mainViewModel.musicListner.repeatMode==Player.REPEAT_MODE_OFF) Color.White else UniversalColors.localMusicColor,
                modifier = Modifier
                    .padding(10.dp, 0.dp)
                    .size(30.dp)
                    .clip(RoundedCornerShape(50))
                    .clickable {
                        mainViewModel.musicPlayer?.toggleRepeat()
                    }
            )
        }
        var tempValue by remember { mutableStateOf(0f) }
        Slider(
            modifier = Modifier
                .offset(0.dp, (-20).dp)
                .fillMaxWidth(0.9f)
                .height(15.dp)
                .constrainAs(seekbar) {
                    bottom.linkTo(controller.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            valueRange = 0f..(mainViewModel.musicList[mainViewModel.musicListner.currentPlayerInfo.index].duration).toFloat(),
            colors = SliderDefaults.colors(
                thumbColor = UniversalColors.localMusicColor,
                activeTrackColor = UniversalColors.localMusicColor,
            ),
            value = if(mainViewModel.seekUpdates) mainViewModel.musicPlayer!!.seekPosition else tempValue,
            onValueChangeFinished = {
                if(mainViewModel.musicListner.seekable) mainViewModel.seekTo(tempValue.toLong())
            },
            onValueChange = {
                if(mainViewModel.musicListner.seekable){
                    mainViewModel.seekUpdates = false
                    tempValue = it
                }
            })
        val width = LocalConfiguration.current.screenWidthDp
        Box(modifier = Modifier
            .fillMaxWidth()
            .size(width.dp)
            .constrainAs(image) {
                top.linkTo(backIcon.bottom, margin = 10.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            AsyncImage(
                colorFilter = ColorFilter.tint(Color(0, 0, 0, 99), BlendMode.Darken),
                model = mainViewModel.musicList[mainViewModel.musicListner.currentPlayerInfo.index].albumUri,
                error = painterResource(id = R.drawable.music_icon),
                contentDescription = "image",
                modifier = Modifier
                    .fillMaxWidth()
                    .blur(10.dp)
                    .align(Alignment.Center),
                contentScale = ContentScale.Crop
            )
            AsyncImage(
                model = mainViewModel.musicList[mainViewModel.musicListner.currentPlayerInfo.index].albumUri,
                error = painterResource(id = R.drawable.music_icon),
                contentDescription = "image",
                modifier = Modifier
                    .padding(20.dp)
                    .size(150.dp)
                    .align(Alignment.BottomStart)
            )
        }
        Text(
            fontSize = 11.sp,
            color = Color.White,
            text = DateFormat.format(
                "mm:ss",
                mainViewModel.musicPlayer!!.seekPosition.toLong()-1800000
            ).toString(),
            modifier = Modifier.constrainAs(elapsedTime) {
                start.linkTo(seekbar.start, margin = 10.dp)
                top.linkTo(seekbar.bottom, margin = (-10).dp)
            }
        )

        Text(
            fontSize = 11.sp,
            color = Color.White,
            text = DateFormat.format(
                "mm:ss",
                mainViewModel.musicListner.currentPlayerInfo.duration-1800000
            ).toString(),
            modifier = Modifier.constrainAs(totalTime) {
                end.linkTo(seekbar.end, margin = 10.dp)
                top.linkTo(seekbar.bottom, margin = (-10).dp)
            }
        )
        Column(modifier = Modifier
            .fillMaxWidth(0.8f)
            .constrainAs(infoText) {
                bottom.linkTo(seekbar.top, margin = 45.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }) {
            Text(
                text = mainViewModel.musicList[mainViewModel.musicListner.currentPlayerInfo.index].title,
                color = Color.White,
                fontSize = 22.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.basicMarquee(iterations = 20)
            )
            Text(
                text = mainViewModel.musicList[mainViewModel.musicListner.currentPlayerInfo.index].artist,
                color = Color(255, 255, 255, 201),
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
















