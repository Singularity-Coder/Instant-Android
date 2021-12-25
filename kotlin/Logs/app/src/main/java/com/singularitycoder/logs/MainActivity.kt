package com.singularitycoder.logs

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import java.lang.NumberFormatException
import javax.security.auth.login.LoginException

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i(TAG, "onCreate: This is an info log")
        Log.d(TAG, "onCreate: This is a debug log")
        Log.w(TAG, "onCreate: This is a warning log")
        try {
            val myInt = "0x".toInt()
        } catch (e: NumberFormatException) {
            Log.e(TAG, "onCreate: number format exception: $e")
        }

        Log.d(TAG, "onCreate() called with: savedInstanceState = $savedInstanceState")
        add(5, 19)
    }

    private fun add(num1: Int, num2: Int): Int {
        val result = num1 + num2
        Log.d(TAG, "add() returned: $result")
        return result
    }
 }