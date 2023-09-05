package com.example.fifthsemproject.data.local

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.fifthsemproject.domain.models.MusicTrack
import com.google.gson.Gson

class MusicDatabase {

    private fun storeMusicList(data: List<MusicTrack>, context: Context){
        val gson = Gson()
        val json = gson.toJson(data)
        val sharedPreferences = context.getSharedPreferences("universalmusicstorage", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("musiclist", json)
        editor.apply()
    }
    suspend fun getLocalMusicTracks(context: Context): List<MusicTrack> {

        val sharedPreferences = context.getSharedPreferences("universalmusicstorage", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("musiclist", null)
        if(json!=null) {
            Log.d("musiclog", "loaded from cache")
            return Gson().fromJson(json, Array<MusicTrack>::class.java).asList()
        }


        val musicTracks = mutableListOf<MusicTrack>()
        val contentResolver: ContentResolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATE_MODIFIED,
            MediaStore.Audio.Media.ALBUM_ID,

        )

        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        cursor?.use {
            val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val filePathColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val dateAddedColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
            val dateModifiedColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_MODIFIED)
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (it.moveToNext()) {
                val title = it.getString(titleColumn)?: ""
                val artist = it.getString(artistColumn)?: ""
                val album = it.getString(albumColumn)?: ""
                val duration = it.getLong(durationColumn)
                val filePath = it.getString(filePathColumn)?: ""
                val dateAdded = it.getString(dateAddedColumn).toLong()?: 0L
                val dateModified = it.getString(dateModifiedColumn).toLong() ?: 0L
                val id = it.getString(idColumn) ?: ""
                var albumArtUri : Uri?
                albumArtUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    id.toLong()
                )
                val musicTrack = MusicTrack(title, artist, album, duration, filePath, dateAdded, dateModified, id, albumArtUri.toString())
                musicTracks.add(musicTrack)
            }
        }
        storeMusicList(musicTracks, context)
        return musicTracks
    }





}