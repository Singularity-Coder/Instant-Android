package com.example.ktor1

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.example.ktor1.databinding.ActivityMainBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

// https://ktor.io/
// https://ktor.io/docs/response.html
// https://ktor.io/docs/request.html
// https://github.com/Kotlin/kotlinx.serialization
// https://barros9.medium.com/no-more-retrofit-move-to-ktor-on-android-957058819b67
// Philip Lackner - https://www.youtube.com/watch?v=3KTXD_ckAX0
// https://medium.com/default-to-open/experimenting-with-ktor-5c8c1bf78d72
// https://medium.com/google-developer-experts/how-to-use-ktor-client-on-android-dcdeddc066b9

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var gson: Gson

    @Inject
    lateinit var apiService: ApiEndPointsService

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnGetUsers.setOnClickListener {
            CoroutineScope(IO).launch {
                getGithubUsers()
            }
        }
    }

    // 3xx responses - RedirectResponseException
    // 4xx responses - ClientRequestException
    // 5xx responses - ServerResponseException
    private suspend fun getGithubUsers() {
        val httpResponse = try {
            apiService.getGithubUsers(userCount = 135)
        } catch (e: ResponseException) {
            println("Error: ${e.response.status.description}")
            e.response
        }

        httpResponse onSuccess { statusCode: Int ->
            val userList: List<User> = httpResponse.body()
            withContext(Main) { binding.tvUsers.text = userList.toString() }
            println("status code: $statusCode, response: ${gson.toJson(userList)}")
        }

        httpResponse onFailure { statusCode: Int ->
            val errorBody = httpResponse.body<KtorError>()
            println("status code: $statusCode, error: ${errorBody.message}")
        }

        httpResponse onOffline {
            println("You are offline")
        }
    }

    private suspend infix fun HttpResponse.onSuccess(doTask: suspend (statusCode: Int) -> Unit): HttpResponse {
        if (this.status.value in 200..299) {
            doTask.invoke(this.status.value)
        }
        return this
    }

    private suspend infix fun HttpResponse.onFailure(doTask: suspend (statusCode: Int) -> Unit): HttpResponse {
        if (this.status.value !in 200..299) {
            doTask.invoke(this.status.value)
        }
        return this
    }

    private suspend infix fun HttpResponse.onOffline(doTask: suspend () -> Unit): HttpResponse {
        if (!isOnline()) {
            doTask.invoke()
        }
        return this
    }

    @RequiresPermission(android.Manifest.permission.ACCESS_NETWORK_STATE)
    private fun isOnline(): Boolean {
        val conMan = getSystemService(CONNECTIVITY_SERVICE) as? ConnectivityManager

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
}