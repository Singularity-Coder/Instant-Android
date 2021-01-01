package com.singularitycoder.kotlinretrofit1.helper

import android.R
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar

class AppUtils {

    fun setStatusBarColor(activity: Activity, statusBarColor: Int) {
        val window: Window = activity.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(activity, statusBarColor)
    }

    fun hasInternet(context: Context): Boolean {
        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun glideImage(context: Context?, imgUrl: String?, imageView: ImageView?) {
        Glide.with(context!!).load(imgUrl)
            .placeholder(R.color.holo_purple)
            .error(R.color.holo_red_light)
            .encodeQuality(40)
            .circleCrop()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .into(imageView!!)
    }

    fun showSnack(
        view: View,
        message: String,
        actionButtonText: String,
        voidFunction: (Unit) -> Unit,
    ) = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(actionButtonText) { voidFunction }
        .show()

    fun dummy() {

    }
}