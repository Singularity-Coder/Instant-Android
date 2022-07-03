package com.example.androidstoragemadness

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.BatteryManager
import android.os.Build
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.material.snackbar.Snackbar

fun Activity?.isAppEnabled(fullyQualifiedAppId: String): Boolean {
    val appInfo = this?.packageManager?.getApplicationInfo(fullyQualifiedAppId, 0)
    return appInfo?.enabled == true
}

fun Context.batteryPercent(): Int {
    val bm = applicationContext.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
}

fun Context.isCameraPresent(): Boolean {
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

fun Context.showNotification(fileName: String) {
    val channelId = "downloadStartingChannelId"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        NotificationChannel(
            channelId,
            fileName,
            NotificationManager.IMPORTANCE_HIGH
        ).also { it: NotificationChannel ->
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(it)
        }
    }
    val notification = NotificationCompat.Builder(this, channelId)
        .setContentTitle("Downloading $fileName")
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setPriority(NotificationCompat.PRIORITY_HIGH) // For < API 26 this is a must
        .build()
    NotificationManagerCompat.from(this).notify(112233 /* We only want 1 notif at all times */, notification)
}

fun Context.getDownloadableUrlFromWebView(
    url: String,
    onDownloadableUrlReady: (url: String) -> Unit,
) {
    WebView(this).apply {
        webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, downloadableUrl: String) {
                println("downloadable url: $downloadableUrl")
                onDownloadableUrlReady.invoke(downloadableUrl)
            }
        }
        loadUrl(url)
    }
}

enum class App(val id: String) {
    DOWNLOAD_MANAGER(id = "com.android.providers.downloads")
}