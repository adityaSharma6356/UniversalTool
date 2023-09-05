package com.example.fifthsemproject.presentation.screens.music

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.fifthsemproject.MainViewModel
import com.example.fifthsemproject.R
import com.example.fifthsemproject.presentation.navigation.Screen
import com.example.fifthsemproject.presentation.screendata.UniversalColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun BottomMusicControllerScreen(
    mainViewModel: MainViewModel,
    modifier: Modifier,
    mainNavController: NavHostController
){
    Column(
        modifier = modifier
            .offset(y = 2.dp)
            .fillMaxWidth()
            .height(65.dp)
            .background(Color.Black)
            .clickable {
                mainNavController.navigate(Screen.CurrentPlayer.route)
            },
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            color = UniversalColors.localMusicColor, trackColor = Color(
            41,
            41,
            41,
            255
        ),progress = mainViewModel.musicPlayer!!.seekPosition/mainViewModel.musicListner.currentPlayerInfo.duration)

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = mainViewModel.musicList[mainViewModel.musicListner.currentPlayerInfo.index].albumUri,
                error = painterResource(id = R.drawable.music_icon),
                contentDescription = "image",
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(50.dp)
            )
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    ) {
                Text(
                    modifier = Modifier.basicMarquee(),
                    text = mainViewModel.musicList[mainViewModel.musicListner.currentPlayerInfo.index].title,
                    color = Color.White,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text =  mainViewModel.musicList[mainViewModel.musicListner.currentPlayerInfo.index].artist,
                    color = Color(255, 255, 255, 183),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                modifier = Modifier
                    .padding(8.dp, 0.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable { mainViewModel.musicPlayer!!.playPrevious() },
                painter = painterResource(id = R.drawable.prev_icon),
                contentDescription = "play",
                tint = Color.White
            )
            Icon(
                modifier = Modifier
                    .padding(8.dp, 0.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable { mainViewModel.musicPlayer!!.toggleMusic() },
                painter = painterResource(id = if(mainViewModel.musicListner.isMusicPlaying) R.drawable.pause_icon else  R.drawable.play_icon),
                contentDescription = "play",
                tint = Color.White
            )
            Icon(
                modifier = Modifier
                    .padding(8.dp, 0.dp)
                    .size(30.dp)
                    .clip(CircleShape)
                    .clickable { mainViewModel.musicPlayer!!.playNext() },
                painter = painterResource(id = R.drawable.next_icon),
                contentDescription = "play",
                tint = Color.White
            )
        }

    }

}