package com.example.ktor1

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.net.UnknownHostException

// Random experiment
suspend fun HttpClient.getOrNull(
    urlString: String,
    block: HttpRequestBuilder.() -> Unit = {}
): ApiResponse {
    var errorMessage = "Something went wrong. Try again!"
    val httpResponse = try {
        this.get(urlString, block)
    } catch (e: RedirectResponseException) {
        // 3xx responses
        errorMessage = e.message
        e.response
    } catch (e: ClientRequestException) {
        // 4xx responses
        errorMessage = e.message
        e.response
    } catch (e: ServerResponseException) {
        // 5xx responses
        errorMessage = e.message
        e.response
    } catch (e: UnknownHostException) {
        errorMessage = "offline"
        null
    } catch (e: Exception) {
        errorMessage = e.message ?: "Something went wrong. Try again!"
        null
    }
    return ApiResponse(
        response = httpResponse,
        error = errorMessage
    )
}

suspend infix fun ApiResponse.onSuccess(doTask: suspend (statusCode: Int) -> Unit): ApiResponse {
    this.httpResponse ?: return this
    if (this.httpResponse.status.value in 200..299) {
        doTask.invoke(this.httpResponse.status.value)
    }
    return this
}

suspend infix fun ApiResponse.onFailure(doTask: suspend (statusCode: Int) -> Unit): ApiResponse {
    this.httpResponse ?: return this
    if (this.httpResponse.status.value !in 200..299) {
        doTask.invoke(this.httpResponse.status.value)
    }
    return this
}

suspend infix fun ApiResponse.onOffline(doTask: suspend () -> Unit): ApiResponse {
    if (null == this.httpResponse && this.errorMessage == "offline") doTask.invoke()
    return this
}

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

suspend infix fun HttpResponse.onSuccess(doTask: suspend (statusCode: Int) -> Unit): HttpResponse {
    if (this.status.value in 200..299) {
        doTask.invoke(this.status.value)
    }
    return this
}

suspend infix fun HttpResponse.onFailure(doTask: suspend (statusCode: Int) -> Unit): HttpResponse {
    if (this.status.value !in 200..299) {
        doTask.invoke(this.status.value)
    }
    return this
}