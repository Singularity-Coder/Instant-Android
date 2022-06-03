package com.example.androidstoragemadness

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.view.View
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

fun Activity?.isAppEnabled(fullyQualifiedAppId: String): Boolean {
    val appInfo = this?.packageManager?.getApplicationInfo(fullyQualifiedAppId, 0)
    return appInfo?.enabled == true
}

fun Context.batteryPercent(): Int {
    val bm = applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
}

fun Context.isCameraPresentOnDevice(): Boolean {
    return packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
}

fun View.showSnackBar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
) = Snackbar.make(this, message, duration).show()

@RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
fun Context.isOnline(): Boolean {
    val conMan = getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE) as? ConnectivityManager

    @Suppress("DEPRECATION")
    fun checkOldWay(): Boolean {
        val oldActiveNet = conMan?.activeNetworkInfo
        val oldWifi = conMan?.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        val oldMobile = conMan?.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        val oldEthernet = conMan?.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET)
        val hasOldWifi = null != oldWifi && oldWifi.isConnected
        val hasOldCellular = null != oldMobile && oldMobile.isConnected
        val hasOldEthernet = null != oldEthernet && oldEthernet.isConnected

        if (oldActiveNet?.isConnected == false) return false
        return hasOldWifi || hasOldCellular || hasOldEthernet
    }

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val activeNet = conMan?.activeNetwork
        val netCap = conMan?.getNetworkCapabilities(activeNet)
        val hasWifi = netCap?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ?: false
        val hasCellular = netCap?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ?: false
        val hasEthernet = netCap?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ?: false

        hasWifi || hasCellular || hasEthernet
    } else checkOldWay()
}

enum class App(val id: String) {
    DOWNLOAD_MANAGER(id = "com.android.providers.downloads")
}