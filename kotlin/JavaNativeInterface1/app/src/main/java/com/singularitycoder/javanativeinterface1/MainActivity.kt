package com.singularitycoder.javanativeinterface1

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.singularitycoder.javanativeinterface1.databinding.ActivityMainBinding

// https://www.journaldev.com/28972/android-jni-application-ndk
// https://blog.mindorks.com/getting-started-with-android-ndk-android-tutorial

// LLDB: It is used by Android Studio to debug the native code present in your project.
// NDK: Native Development Kit(NDK) is used to code in C and C++ i.e. native languages for Android.
// CMake: It is an open-source system that manages the build process in an operating system and a compiler-independent manner.
class MainActivity : AppCompatActivity() {

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Example of a call to a native method
        binding.tvSampleText.text = stringFromJNI()

        binding.btnJni.setOnClickListener {
            val result = sendYourName("Singularity", "Coder")
            Toast.makeText(this@MainActivity, "Result from JNI is $result", Toast.LENGTH_LONG).show()
        }

        binding.btnJniStringArray.setOnClickListener {
            val strings = stringArrayFromJNI()
            Toast.makeText(this@MainActivity, "First element is " + strings!![0], Toast.LENGTH_LONG).show()
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    // Get a string from the native code in our Java code using JNI.
    private external fun stringFromJNI(): String

    // computing strings in native code and returning
    private external fun sendYourName(firstName: String?, lastName: String?): String?

    // returning string array
    private external fun stringArrayFromJNI(): Array<String?>?
}