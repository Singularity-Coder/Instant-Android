package com.singularitycoder.rebootbroadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.singularitycoder.rebootbroadcast.AppUtils.buildNotification
import java.text.DateFormat

class SystemEventReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        when(intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                val description: String = "Your device restarted on ${DateFormat.getDateTimeInstance().format(System.currentTimeMillis())}"
                Toast.makeText(context, description, Toast.LENGTH_LONG).show()
                buildNotification(context, description)
            }
        }
    }
}