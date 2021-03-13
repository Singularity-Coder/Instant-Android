package com.singularitycoder.rebootbroadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import java.util.*

object AppUtils {

    private const val NOTIFICATION_CHANNEL: String = "CHANNEL_REBOOT"
    private const val NOTIFICATION_ID: Int = 0
    const val KEY_INTENT_REBOOT: String = "KEY_DEVICE_STATE"

    fun buildNotification(context: Context, description: String) {
        val intent: Intent = Intent(context, MainActivity::class.java).apply {
            putExtra(KEY_INTENT_REBOOT, description)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("Device Status".toUpperCase(Locale.ENGLISH))
            .setContentText(description)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL) == null) {
            notificationManager.createNotificationChannel(NotificationChannel(NOTIFICATION_CHANNEL, "REBOOT", NotificationManager.IMPORTANCE_HIGH))
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}