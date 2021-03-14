package com.singularitycoder.systembroadcasts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import com.singularitycoder.systembroadcasts.AppUtils.KEY_INTENT_SYSTEM_EVENT
import com.singularitycoder.systembroadcasts.AppUtils.permissions
import com.singularitycoder.systembroadcasts.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ActivityCompat.requestPermissions(this, permissions, 1111)
        binding.systemEvent = intent.getStringExtra(KEY_INTENT_SYSTEM_EVENT)
    }
}