package com.example.periodtracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.Settings.Global.getString
import androidx.compose.runtime.Composable
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.periodtracker.data.UserData


object CycleNotifications {
    private val CHANNEL_ID = "Cycle_Channel"
    private val importance = NotificationManager.IMPORTANCE_DEFAULT

    fun createNotificationChannel(context: Context) {
        val channelName = "Cycle Channel"
        val channel = NotificationChannel(CHANNEL_ID, channelName, importance).apply {
            description = "Cycle updates"
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }

    fun buildNotification(context: Context) {

        calculateCurrentPhase(context)
        val currentPhase = UserData.getCyclePhase(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Cycle Update")
            .setContentText("First day of the $currentPhase phase.")
            .setPriority(importance)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, builder.build())
    }



}