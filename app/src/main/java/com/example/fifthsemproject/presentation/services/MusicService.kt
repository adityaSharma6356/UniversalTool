package com.example.fifthsemproject.presentation.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.MediaController2
import android.media.session.MediaController
import android.media.session.MediaSession
import android.provider.MediaStore.Audio.Media
import android.service.media.MediaBrowserService
import androidx.annotation.Nullable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Player.Listener
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import androidx.media3.ui.PlayerNotificationManager.BitmapCallback
import androidx.media3.ui.PlayerNotificationManager.MediaDescriptionAdapter
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import com.example.fifthsemproject.R
import com.example.fifthsemproject.domain.models.MusicTrack
import com.example.fifthsemproject.domain.models.TrackUiStates
import com.example.fifthsemproject.presentation.services.Constants.Companion.NOTIFICATION_CHANNEL_ID
import com.example.fifthsemproject.presentation.services.Constants.Companion.NOTIFICATION_ID


class MusicPlayer(val context: Context) {
    private var player = ExoPlayer.Builder(context).build()

    var isServiceActive = true
    fun setupListners(listner: Listener) {
        if(!isServiceActive){
            setupPlayer()
        }
        player.addListener(listner)

        // Set up player settings here (e.g., audio attributes, playback listeners).
    }

    private fun setupPlayer(){
        player = ExoPlayer.Builder(context).build()
        isServiceActive = true
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
        isServiceActive = false
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

@UnstableApi class MusicPlayerNotificationListner(
    private val musicService: MusicPlayer,
): PlayerNotificationManager.NotificationListener {

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        musicService.apply {
            releasePlayer()
        }
    }

    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
    }
}

class MusicPlaybackListner: Listener{
    var isMusicPlaying by mutableStateOf(false)
    var isShuffling by mutableStateOf(false)
    var currentPlayerInfo by mutableStateOf(TrackUiStates())
    var repeatMode by mutableStateOf(Player.REPEAT_MODE_OFF)
    var seekable = true


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

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
class MusicNotificationManager (
    private val context: Context,
    sessionToken: MediaSession.Token,
    notificationListner: PlayerNotificationManager.NotificationListener,
    private val newSongCallback: () -> Unit
) {

    private val notificationManager: PlayerNotificationManager

    init {
        val mediaController = MediaController(context, sessionToken)
        notificationManager = PlayerNotificationManager
            .Builder(
                context,
                NOTIFICATION_ID,
                NOTIFICATION_CHANNEL_ID
            )
            .setChannelNameResourceId(R.string.notification_channel_name)
            .setChannelDescriptionResourceId(R.string.notification_channel_description)
            .setMediaDescriptionAdapter(DescriptionAdapter(mediaController))
            .setNotificationListener(notificationListner)
            .setSmallIconResourceId(R.drawable.universal_logo)
            .build()
    }

    fun showNotification(player: Player){
        notificationManager.setPlayer(player)
    }

    private inner class DescriptionAdapter(
        private val mediaController: MediaController
    ) : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: Player): CharSequence {
            return mediaController.metadata?.description?.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return mediaController.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return mediaController.metadata?.description?.subtitle.toString()
        }

        override fun getCurrentLargeIcon(player: Player, callback: BitmapCallback): Bitmap? {
            val loader = ImageLoader(context)
            val req = ImageRequest.Builder(context)
                .data(mediaController.metadata?.description?.iconUri)
                .target{ result ->
                    val bitmap = (result as BitmapDrawable).bitmap
                    callback.onBitmap(bitmap)
                }
                .build()
            val disposable = loader.enqueue(req)
            return null
        }
    }
}
















