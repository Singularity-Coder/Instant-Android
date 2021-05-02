package com.singularitycoder.kotlinactivitylifecycle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        println("View State: Activity 2 onCreate")
    }

    override fun onStart() {
        super.onStart()
        println("View State: Activity 2 onStart")
    }

    override fun onResume() {
        super.onResume()
        println("View State: Activity 2 onResume")
    }

    override fun onPause() {
        super.onPause()
        println("View State: Activity 2 onPause")
    }

    override fun onStop() {
        super.onStop()
        println("View State: Activity 2 onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("View State: Activity 2 onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        println("View State: Activity 2 onRestart")
    }
}