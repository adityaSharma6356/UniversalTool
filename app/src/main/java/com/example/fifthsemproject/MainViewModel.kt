package com.example.fifthsemproject

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fifthsemproject.data.local.MusicDatabase
import com.example.fifthsemproject.domain.models.TrackUiStates
import com.example.fifthsemproject.presentation.services.MusicPlaybackListner
import com.example.fifthsemproject.presentation.services.MusicPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var musicPlayer : MusicPlayer? = null
    var visibleAbout by mutableStateOf(false)
    var seekUpdates by mutableStateOf(true)
    val musicListner = MusicPlaybackListner()
    var musicList = mutableStateListOf<TrackUiStates>()
    fun setupMusicPlayer(context: Context){
        if(musicPlayer!=null) return
        viewModelScope.launch(Dispatchers.Main) {
            val player = MusicPlayer(context)
            player.setupListners(musicListner)
            musicPlayer = player
        }
    }

    fun seekTo(posi:Long){
        viewModelScope.launch {
            musicPlayer?.seekTo(posi)
            delay(1000)
            seekUpdates = true
        }
    }
    fun setupLocalMediaSource(context: Context){
        if(musicPlayer==null || musicList.isNotEmpty()) {
            updateService()
            return
        }
        job?.cancel()
        viewModelScope.launch {
            val data = MusicDatabase()
            var temp = data.getLocalMusicTracks(context)
            temp = temp.sortedByDescending { it.dateModified }
            musicList = temp.mapIndexed { index, it ->
                TrackUiStates(
                    title = it.title,
                    artist = it.artist,
                    album = it.album,
                    duration = it.duration,
                    dateAdded = it.dateAdded,
                    dateModified = it.dateModified,
                    index = index,
                    albumUri = it.albumUri
                )
            }.toMutableStateList()
            musicPlayer!!.preparePLayer(temp)
        }

        updateService()
    }

    var job: Job? = null
    private fun updateService(){
        job = viewModelScope.launch {
            while (true){
                delay(1000)
                musicPlayer?.update()
            }
        }
    }

}







