package com.singularitycoder.rebootbroadcast

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.singularitycoder.rebootbroadcast.AppUtils.KEY_INTENT_REBOOT
import com.singularitycoder.rebootbroadcast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.deviceState = intent.getStringExtra(KEY_INTENT_REBOOT)
    }
}