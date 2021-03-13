package com.singularitycoder.simpleforegroundservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.singularitycoder.simpleforegroundservice.AppUtils.INTENT_TASKS
import com.singularitycoder.simpleforegroundservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var myService: MyService? = null
    private var connection = object : ServiceConnection {
        override fun onServiceDisconnected(componentName: ComponentName?) {
            myService = null
        }

        override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
            myService = (service as MyService.MyBinder).getService()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setUpClickListeners(binding)
    }

    override fun onResume() {
        super.onResume()
        bindMyService()
    }

    override fun onPause() {
        super.onPause()
        unbindMyService()
    }

    private fun bindMyService() {
        Intent(this@MainActivity, MyService::class.java).also { intent ->
            this@MainActivity.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun unbindMyService() {
        myService?.let { this@MainActivity.unbindService(connection) }
        myService = null
    }

    private fun setUpClickListeners(binding: ActivityMainBinding) {
        binding.btnStartService.setOnClickListener {
            binding.btnStartService.visibility = View.GONE
            val intent = Intent(this@MainActivity, MyService::class.java).apply {
                putExtra(INTENT_TASKS, 50)
                action = AppUtils.ACTION_START_FOREGROUND
            }
            this@MainActivity.startService(intent)
            Toast.makeText(this@MainActivity, "My Service Started!", Toast.LENGTH_SHORT).show()
        }
        binding.btnStopService.setOnClickListener {
            binding.btnStartService.visibility = View.VISIBLE
            val intent = Intent(this@MainActivity, MyService::class.java).setAction(AppUtils.ACTION_STOP_FOREGROUND)
            this@MainActivity.stopService(intent)
            Toast.makeText(this@MainActivity, "My Service Stopped!", Toast.LENGTH_SHORT).show()
        }
    }
}