package com.singularitycoder.multitouchy

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.singularitycoder.multitouchy.databinding.ActivityMainBinding

// https://www.youtube.com/watch?v=tcfyiZ5avkU
// https://www.youtube.com/watch?v=JYMuRIqxzwg
// When user puts finger on the screen we receive an event touch_down
// When the user lifts the finger up we receive another event touch_up
// With multiple fingers we receive pointer - touch pointers

// To implement gestures u must implement View.OnTouchListener interface , GestureDetector.OnGestureListener , GestureDetector.OnDoubleTapListener
class MainActivity : AppCompatActivity(),
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var gestureDetector: GestureDetector

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        gestureDetector = GestureDetector(/* context = */ this, /* listener = */ this)
        setupGestureEventsOnView()
        setUpTouchEventListeners()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGestureEventsOnView() {
        // Detect all events on image view
        binding.ivImage.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                // Set gestures with gestureDetector against this motionEvent
                gestureDetector.onTouchEvent(event)
                gestureDetector.onTouchEvent(event)
                println("gesture onTouch")
                return true
            }
        })
    }

    // Detect view touch and release
    @SuppressLint("ClickableViewAccessibility")
    private fun setUpTouchEventListeners() {
        binding.root.setOnTouchListener { view, motionEvent ->
            println(
                """
                    action: ${motionEvent.action}
                    downTime: ${motionEvent.downTime}
                    orientation: ${motionEvent.orientation}
                    pointerCount: ${motionEvent.pointerCount}
                    pressure: ${motionEvent.pressure}
                    rawX: ${motionEvent.rawX}
                    rawY: ${motionEvent.rawY}
                    xPrecision: ${motionEvent.xPrecision}
                    yPrecision: ${motionEvent.yPrecision}
                """.trimIndent()
            )

            val eventType = motionEvent.actionMasked

            when (eventType) {
                MotionEvent.ACTION_DOWN -> {
                    // User touched the screen
                    println("Action: User touched the screen")
                }
                MotionEvent.ACTION_UP -> {
                    // User lifted his finger up
                    println("Action: User lifted his finger up")

                }
                MotionEvent.ACTION_MOVE -> {
                    // User moved his finger on the screen
                    println("Action: User moved his finger on the screen")
                }
                MotionEvent.ACTION_HOVER_MOVE -> {

                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    println("Action: Pointer down with pointer count ${motionEvent.pointerCount}")
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    println("Action: Pointer up with pointer count ${motionEvent.pointerCount}")
                }
            }

            return@setOnTouchListener true // This will forward the event to appropriate listeners
        }
    }

    //------------------------------------------------------------------------------------------

    override fun onDown(e: MotionEvent): Boolean {
        println("gesture onDown")
        return false
    }

    override fun onShowPress(e: MotionEvent) {
        println("gesture onShowPress")
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        println("gesture onSingleTapUp")
        return false
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        println("gesture onScroll")
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        println("gesture onLongPress")
    }

    override fun onFling(e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        println("gesture onFling")
        return false
    }

    //------------------------------------------------------------------------------------------

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        println("gesture onSingleTapConfirmed")
        return false
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        println("gesture onDoubleTap")
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        println("gesture onDoubleTapEvent")
        return false
    }
}