package com.singularitycoder.simpleforegroundservice

import android.app.*
import android.content.Intent
import android.os.*
import com.singularitycoder.simpleforegroundservice.AppUtils.DURATION_THREAD_SLEEP_5_SEC
import com.singularitycoder.simpleforegroundservice.AppUtils.INTENT_TASKS
import com.singularitycoder.simpleforegroundservice.AppUtils.NOTIFICATION_ID_FOREGROUND_SERVICE
import kotlin.concurrent.thread

class MyService : Service() {

    private val binder = MyBinder()

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action.equals(AppUtils.ACTION_START_FOREGROUND)) {
            startSuperLongRunningOperation(intent?.getIntExtra(INTENT_TASKS, 5))
        }

        // Not working. Strangely this is not stopping the service
        if (intent?.action.equals(AppUtils.ACTION_STOP_FOREGROUND)) {
            stopForeground(true)
            stopSelfResult(startId)
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startSuperLongRunningOperation(tasks: Int? = 5) {
        thread {
            for (i in 1..tasks!!) {
                if (i == 27) stopForegroundService()
                updateForegroundStatus(currentTask = "Doing Task $i")
                Thread.sleep(DURATION_THREAD_SLEEP_5_SEC)
            }
        }
    }

    private fun updateForegroundStatus(currentTask: String) {
        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }
        startForeground(NOTIFICATION_ID_FOREGROUND_SERVICE, AppUtils.buildNotification(context = this, currentTask = currentTask, pendingIntent = pendingIntent))
    }

    // Not working. Strangely this is not stopping the service
    private fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
    }

    inner class MyBinder : Binder() {
        fun getService(): MyService = this@MyService
    }
}

