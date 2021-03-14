package com.singularitycoder.systembroadcasts

import android.Manifest.permission
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import java.text.DateFormat
import java.util.*

object AppUtils {

    private const val NOTIFICATION_CHANNEL: String = "CHANNEL_SYSTEM_EVENTS"
    const val KEY_INTENT_SYSTEM_EVENT: String = "KEY_INTENT_SYSTEM_EVENT"
    val permissions = arrayOf(
        permission.ACCESS_COARSE_LOCATION,
        permission.ACCESS_FINE_LOCATION,
        permission.ACCESS_LOCATION_EXTRA_COMMANDS,
        permission.ACCESS_NETWORK_STATE,
        permission.ACCESS_WIFI_STATE,
        permission.ADD_VOICEMAIL,
        permission.BLUETOOTH,
        permission.BLUETOOTH_ADMIN,
        permission.BODY_SENSORS,
        permission.BROADCAST_STICKY,
        permission.CALL_PHONE,
        permission.READ_PHONE_STATE,
        permission.CAMERA,
        permission.CHANGE_NETWORK_STATE,
        permission.CHANGE_WIFI_MULTICAST_STATE,
        permission.CHANGE_WIFI_STATE,
        permission.DISABLE_KEYGUARD,
        permission.EXPAND_STATUS_BAR,
        permission.GET_ACCOUNTS,
        permission.GET_PACKAGE_SIZE,
        permission.GET_TASKS,
        permission.INSTALL_SHORTCUT,
        permission.INTERNET,
        permission.KILL_BACKGROUND_PROCESSES,
        permission.MODIFY_AUDIO_SETTINGS,
        permission.NFC,
        permission.PERSISTENT_ACTIVITY,
        permission.PROCESS_OUTGOING_CALLS,
        permission.READ_CALENDAR,
        permission.READ_CALL_LOG,
        permission.READ_CONTACTS,
        permission.READ_EXTERNAL_STORAGE,
        permission.READ_SMS,
        permission.READ_SYNC_SETTINGS,
        permission.READ_SYNC_STATS,
        permission.READ_VOICEMAIL,
        permission.RECEIVE_BOOT_COMPLETED,
        permission.RECEIVE_MMS,
        permission.RECEIVE_SMS,
        permission.RECEIVE_WAP_PUSH,
        permission.RECORD_AUDIO,
        permission.REORDER_TASKS,
        permission.RESTART_PACKAGES,
        permission.SEND_SMS,
        permission.SET_ALARM,
        permission.SET_WALLPAPER,
        permission.SET_WALLPAPER_HINTS,
        permission.SYSTEM_ALERT_WINDOW,
        permission.TRANSMIT_IR,
        permission.UNINSTALL_SHORTCUT,
        permission.USE_SIP,
        permission.VIBRATE,
        permission.WAKE_LOCK,
        permission.WRITE_CALENDAR,
        permission.WRITE_CALL_LOG,
        permission.WRITE_CONTACTS,
        permission.WRITE_EXTERNAL_STORAGE,
        permission.WRITE_SYNC_SETTINGS,
        permission.WRITE_VOICEMAIL
    )

    fun buildNotification(context: Context, notificationId: Int, intentType: String) {
        val description = "$intentType on ${DateFormat.getDateTimeInstance().format(System.currentTimeMillis())}"
        val intent: Intent = Intent(context, MainActivity::class.java).apply {
            putExtra(KEY_INTENT_SYSTEM_EVENT, description)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("System Event".toUpperCase(Locale.ENGLISH))
            .setContentText(description)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL) == null) {
            notificationManager.createNotificationChannel(NotificationChannel(NOTIFICATION_CHANNEL, "SYSTEM_EVENTS", NotificationManager.IMPORTANCE_HIGH))
        }
        notificationManager.notify(notificationId, builder.build())
    }
}