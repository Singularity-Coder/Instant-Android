package com.singularitycoder.custtoast

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

// Why custom toast: too many varities of android toast styles from different phone manufacturers
class MainActivity : AppCompatActivity() {

    // Step 1: Create the custom layout
    // Step 2: Add the custom layout to the Toast instance
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_show_custom_toast).setOnClickListener {
            showCustomToast(
                message = "Image Saved",
                image = R.drawable.pic1,
                duration = Toast.LENGTH_LONG,
                textImage = R.drawable.ic_baseline_save_24
            )
        }

        findViewById<Button>(R.id.btn_show_single_instance_toast).setOnClickListener {
            showSingleToastInstance(
                message = "Image Saved",
                duration = Toast.LENGTH_LONG
            )
        }
    }

    private fun showCustomToast(
        message: String,
        @DrawableRes image: Int,
        duration: Int,
        @DrawableRes textImage: Int,
        isCenter: Boolean = false
    ) {
        val layout = LayoutInflater.from(this).inflate(R.layout.layout_custom_toast, null)

        // ImageView
        layout.findViewById<ImageView>(R.id.iv_custom_toast_image).apply {
            setImageResource(image)
        }

        // TextView
        layout.findViewById<TextView>(R.id.tv_custom_toast_text).apply {
            text = message
            setCompoundDrawablesWithIntrinsicBounds(textImage, 0, 0, 0)
            compoundDrawablePadding = 12
        }

        Toast(applicationContext).apply {
            view = layout
            this.duration = duration
            if (isCenter) setGravity(Gravity.CENTER, 0, 0) else setGravity(Gravity.BOTTOM, 32, 32)  // offset is the deviation from the norm.
            show()
        }
    }

    // toasts that only has a single instance. So no matter how many times u click, it disappears after the fixed duration
    private fun showSingleToastInstance(
        message: String,
        duration: Int = Toast.LENGTH_LONG
    ) {
        try {
            if (this.isFinishing) return
            if (message.isBlank()) return
            if (null != toast) toast?.cancel()
            toast = Toast.makeText(this, message, duration)
            toast?.show()
        } catch (t: Throwable) {
            t.printStackTrace()
        }
    }

    // https://stackoverflow.com/questions/11288475/custom-toast-on-android-a-simple-example
    // Not working
    private fun showToast2() {
        val toast = Toast.makeText(this, "I am custom Toast!", Toast.LENGTH_LONG)
        val toastView = toast.view // Returns the default view of the Toast.

        // get text view from the default view of the toast
        toastView?.findViewById<TextView>(R.id.message).apply {
            this?.textSize = 25f
            this?.setTextColor(ContextCompat.getColor(context, R.color.purple_700))
            this?.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_notification_clear_all, 0, 0, 0)
            this?.gravity = Gravity.CENTER
            this?.compoundDrawablePadding = 16
            this?.setBackgroundColor(Color.CYAN)
        }

        toast.show()
    }
}