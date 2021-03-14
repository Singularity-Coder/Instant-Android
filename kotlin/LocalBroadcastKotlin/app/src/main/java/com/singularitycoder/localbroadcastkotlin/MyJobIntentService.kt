package com.singularitycoder.localbroadcastkotlin

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.singularitycoder.localbroadcastkotlin.AppConstants.INTENT_ACTION_DATE
import com.singularitycoder.localbroadcastkotlin.AppConstants.INTENT_DATA_DATE
import java.util.*

class MyJobIntentService : JobIntentService() {

    companion object {
        fun enqueueMyWork(context: Context, jobId: Int, intent: Intent) {
            enqueueWork(context, MyJobIntentService::class.java, jobId, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        // Send local broadcast
        val myIntent: Intent = Intent(INTENT_ACTION_DATE).putExtra(INTENT_DATA_DATE, Date().toString())
        LocalBroadcastManager.getInstance(this).sendBroadcast(myIntent)
    }
}