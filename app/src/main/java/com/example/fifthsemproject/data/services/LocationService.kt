package com.example.fifthsemproject.data.services

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.fifthsemproject.R
import com.example.fifthsemproject.domain.models.LocationModels
import com.example.fifthsemproject.domain.services.LocationClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime


class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient
    private val fireStore = FirebaseFirestore.getInstance()
    private var currentId :String? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        getId()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("locationlog", "starting update")
        when(intent?.action){
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start(){
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Broadcasting Location")
            .setContentText("Location: null")
            .setSmallIcon(R.drawable.universal_logo)
            .setOngoing(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient
            .getLocationUpdates(30000L)
            .catch {
                Log.d("locationlog", "location error ${it.message}")
            }
            .onEach { location ->
                val lat = location.latitude
                val long = location.longitude
                val updatedNotification = notification.setContentText(
                    "Broadcasting location with ($lat ,$long)"
                )
                sendUpdateToServer(long, lat)
                notificationManager.notify(1, updatedNotification.build())
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
        startingService(true)
    }

    private fun stop(){
        setLocationShareInfo(false)
        startingService(false)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private suspend fun sendUpdateToServer(longitude: Double, latitude: Double){
        if(currentId==null) getId()
        val currentDateTime: java.util.Date = java.util.Date()
        val currentTimestamp: Long = currentDateTime.time
        val temp = LocationModels(
            name = getName(),
            location = GeoPoint(latitude, longitude),
            lastUpdate = Timestamp(currentTimestamp/1000, 0)
        )
        if(currentId==null){
            fireStore
                .collection("universal_location_service")
                .add(temp)
                .addOnSuccessListener {
                    setId(it.id)
                    setLocationShareInfo(true)
                    Log.d("locationlog", "new id: ${it.id}")
                }
                .addOnFailureListener {
                    Log.d("locationlog", "failure1 : ${it.message}")
                }.await()
        } else {
            fireStore
                .collection("universal_location_service")
                .document(currentId!!)
                .set(temp)
                .addOnSuccessListener {
                    setLocationShareInfo(true)
                    Log.d("locationlog", "location updated")
                }
                .addOnFailureListener {
                    Log.d("locationlog", "failure1 : ${it.message}")
                }.await()
        }
    }

    private fun setLocationShareInfo(info:Boolean){
        val sharedPreferences = this.getSharedPreferences("universal_location_info", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("sharing_data", info)
        editor.apply()
    }
    private fun startingService(info:Boolean){
        val sharedPreferences = this.getSharedPreferences("universal_location_info", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("starting_service", info)
        editor.apply()
    }
    private fun setId(id:String){
        val sharedPreferences = this.getSharedPreferences("universal_locations", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("personal_locations_id", id)
        editor.apply()
    }

    private fun getId() {
        val sharedPreferences = this.getSharedPreferences("universal_locations", Context.MODE_PRIVATE)
        currentId =  sharedPreferences.getString("personal_locations_id", null)
    }
    private fun getName():String {
        val sharedPreferences = this.getSharedPreferences("universal_locations", Context.MODE_PRIVATE)
        return sharedPreferences.getString("personal_name", "")!!
    }

    override fun onDestroy() {
        super.onDestroy()
        setLocationShareInfo(false)
        startingService(false)
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }


}










