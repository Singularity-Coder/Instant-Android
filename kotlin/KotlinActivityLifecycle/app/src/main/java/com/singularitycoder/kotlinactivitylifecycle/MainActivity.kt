package com.singularitycoder.kotlinactivitylifecycle

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.singularitycoder.kotlinactivitylifecycle.databinding.ActivityMainBinding

// 1. Activity Lifecycle
// 2. Fragment Lifecycle
// 3. Dialog Fragment Lifecycle

// FOR CURRENT ACTIVITY
// 0. On App Start - onCreate, onStart, onResume
// 1. On Phone restart - onPause, onStop, onDestroy
// 2. On Home button press - onPause, onStop
// 3. On App switcher press - onPause, onStop
// 4. On Selecting the App from App Switcher - onRestart, onStart, onResume
// 5. On Back press - onPause, onStop, onDestroy
// 6. On new activity launch (New Activity in foreground) - new Activity state and old Activity state - onPause, onStop
// 7. On Fragment launch (Fragment in foreground) - Fragment state and Activity state
// 8. On App destroyed/closed - onDestroy
// 9. On New App launched (New App in foreground) - onPause, onStop
// 10. On Notification drawer swiped from top - Nothing gets called. This is strange. So Notification drawer is a special existence.
// 11. On Fragment Launched - (New Fragment on top of old Fragment) - Nothing gets called as it is simply inflating a view
// 12. On Activity Launched - (New Activity on top of old Fragment)


// ACTIVITY LIFECYCLE GIST
// 1. Activity in foreground first time - onCreate, onStart, onResume
// 2. Activity in foreground later times - onRestart, onStart, onResume
// 3. Activity in background - onPause, onStop
// 4. Activity destroyed (Back Press/App clear) - onPause, onStop, onDestroy

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnLaunchActivity2.setOnClickListener {
            AppUtils.launchActivity(context = this, activity = MainActivity2())
        }
        binding.btnLaunchFragment1.setOnClickListener {
            AppUtils.addFragment(
                activity = this@MainActivity,
                bundle = null,
                parentLayout = R.id.container_activity_1,
                fragment = MainFragment.newInstance("", ""),
                addOrReplace = "ADD"
            )
        }
        println("View State: Activity 1 onCreate")
    }

    override fun onStart() {
        super.onStart()
        println("View State: Activity 1 onStart")
    }

    override fun onResume() {
        super.onResume()
        println("View State: Activity 1 onResume")
    }

    override fun onPause() {
        super.onPause()
        println("View State: Activity 1 onPause")
    }

    override fun onStop() {
        super.onStop()
        println("View State: Activity 1 onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        println("View State: Activity 1 onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        println("View State: Activity 1 onRestart")
    }
}