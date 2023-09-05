package com.example.fifthsemproject.presentation.services

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.Metadata
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.Player.RepeatMode
import androidx.media3.common.Tracks
import androidx.media3.exoplayer.ExoPlayer
import com.example.fifthsemproject.domain.models.MusicTrack
import com.example.fifthsemproject.domain.models.TrackUiStates
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Objects

class MusicPlayer(context: Context) {
    private val player = ExoPlayer.Builder(context).build()

    fun setupListners(listner: Listener) {
        player.addListener(listner)
        // Set up player settings here (e.g., audio attributes, playback listeners).
    }
    fun playNext():Boolean{
        if(player.hasNextMediaItem()){
            player.seekToNext()
            return true
        }
        return false
    }
    fun playPrevious():Boolean{
        if(player.hasPreviousMediaItem()){
            player.seekToPrevious()
            return true
        }
        return false
    }
    fun toggleMusic(){
        if(player.isPlaying)player.pause()
        else {
            player.prepare()
            player.play()
        }
    }
    fun toggleRepeat(){
        player.repeatMode = when(player.repeatMode){
            Player.REPEAT_MODE_OFF -> {
                Player.REPEAT_MODE_ALL
            }
            Player.REPEAT_MODE_ALL -> {
                Player.REPEAT_MODE_ONE
            }
            Player.REPEAT_MODE_ONE -> {
                Player.REPEAT_MODE_OFF
            }
            else -> {
                player.repeatMode
            }
        }
    }
    fun seekTo(position:Long){
        player.seekTo(position)
    }
    fun releasePlayer() {
        player.release()
    }

    fun preparePLayer(data:List<MusicTrack>){
        player.clearMediaItems()
        data.forEachIndexed { index, item->
            val mediaItem = MediaItem.Builder().setUri(item.filePath).build()
            player.addMediaItem(index, mediaItem)
        }
        player.prepare()
    }
    fun playMusic(){
        player.prepare()
        player.play()
    }
    fun playMusic(index: Int) {
        player.seekTo(index, 0L)
        player.prepare()
        player.play()
    }
    fun pauseMusic() {
        player.pause()
    }
    fun toggleShuffle(){
        player.shuffleModeEnabled = !player.shuffleModeEnabled
    }
    var seekPosition by mutableStateOf(0f)
    fun update(){
        seekPosition = player.currentPosition.toFloat()
    }


}


class MusicPlaybackListner: Listener{
    var isMusicPlaying by mutableStateOf(false)
    var isShuffling by mutableStateOf(false)
    var currentPlayerInfo by mutableStateOf(TrackUiStates())
    var repeatMode by mutableStateOf(Player.REPEAT_MODE_OFF)
    var seekable = true
//    var seekPosition by mutableStateOf(0f)


    override fun onEvents(player: Player, events: Player.Events) {
        super.onEvents(player, events)
        player.currentMediaItem?.mediaMetadata?.let {
            currentPlayerInfo = currentPlayerInfo.copy(
                title = it.title.toString(),
                artist = it.artist.toString(),
                album = it.albumTitle.toString(),
                duration = player.duration,
                index = player.currentMediaItemIndex
            )
        }
        repeatMode = player.repeatMode
        isMusicPlaying = player.isPlaying
        isShuffling = player.shuffleModeEnabled
        seekable = player.isCurrentMediaItemSeekable
    }
}












