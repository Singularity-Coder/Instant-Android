package com.singularitycoder.localbroadcastkotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.singularitycoder.localbroadcastkotlin.AppConstants.INTENT_ACTION_DATE
import com.singularitycoder.localbroadcastkotlin.AppConstants.INTENT_DATA_DATE
import com.singularitycoder.localbroadcastkotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            binding.tvDate.text = intent.getStringExtra(INTENT_DATA_DATE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.btnStartService.setOnClickListener { MyJobIntentService.enqueueMyWork(this, 1, Intent(this, MyJobIntentService::class.java)) }
    }

    override fun onResume() {
        super.onResume()
        // Register local broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter(INTENT_ACTION_DATE))
    }

    override fun onPause() {
        super.onPause()
        // Unregister local broadcast
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }
}