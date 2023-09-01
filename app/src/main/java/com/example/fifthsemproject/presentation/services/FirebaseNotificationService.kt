package com.example.fifthsemproject.presentation.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.fifthsemproject.MainActivity
import com.example.fifthsemproject.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

private const val CHANNEL_ID = "GPT_UNIVERSAL_KEY_UPDATE"
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Handle the received message here
        val title = remoteMessage.data["title"].toString()
        var body = remoteMessage.data["message"].toString()

        if(body.length>50){
            messagingEnabled(true, this)
            storeNewApiKey(body, this)
            body = "New key acquired: $body"
        }
        if(body.isBlank()){
            messagingEnabled(false, this)
            body = "code 401"
        }

        val intent = Intent(this , MainActivity::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        createNotificationChannel(notificationManager)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this , CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.universal_logo)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .build()

        notificationManager.notify(notificationID , notification)
    }
    private fun createNotificationChannel(
        notificationManager : NotificationManager
    ) {
        val channelName = "gptChannel"
        val channel = NotificationChannel(CHANNEL_ID, channelName,
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for gpt key updates"
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun storeNewApiKey(key:String, context:Context){
        val sharedPreferences = context.getSharedPreferences("gptuserprovidedkey", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userkey", key)
        editor.apply()
    }
    fun messagingEnabled(state:Boolean, context:Context){
        val sharedPreferences = context.getSharedPreferences("gptuserprovidedkey", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("messagingstate", state)
        editor.apply()
    }
    override fun onNewToken(token: String) {
        // Handle token refresh or registration here
        // You might want to send the token to your server for tracking
    }
}