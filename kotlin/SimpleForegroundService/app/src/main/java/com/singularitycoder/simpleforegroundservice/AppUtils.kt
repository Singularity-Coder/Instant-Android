package com.singularitycoder.simpleforegroundservice

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import java.util.*

object AppUtils {
    const val NOTIFICATION_ID_FOREGROUND_SERVICE = 1001
    const val INTENT_TASKS = "INTENT_TASKS"
    const val DURATION_THREAD_SLEEP_5_SEC = 5000L
    const val ACTION_START_FOREGROUND = "com.singularitycoder.simpleforegroundservice.startforeground"
    const val ACTION_STOP_FOREGROUND = "com.singularitycoder.simpleforegroundservice.stopforeground"

    fun buildNotification(context: Context, currentTask: String, pendingIntent: PendingIntent): Notification {
        val channelId: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context = context, channelId = context.getString(R.string.app_name), channelName = "UpdateTicker")
        } else ""
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("Task Updates".toUpperCase(Locale.ROOT))
            .setContentText(currentTask)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentIntent(pendingIntent)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context, channelId: String, channelName: String): String {
        val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        val notificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            createNotificationChannel(notificationChannel)
        }
        return channelId
    }
}