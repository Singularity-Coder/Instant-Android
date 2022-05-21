package com.example.ktor1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ktor1.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.call.*
import io.ktor.client.plugins.*
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
            CoroutineScope(IO).launch { getGithubUsers() }
        }
        binding.btnGetUsersOffline.setOnClickListener {
            CoroutineScope(IO).launch { getGithubUsersWithOfflineFeature() }
        }
    }

    // 3xx responses - RedirectResponseException
    // 4xx responses - ClientRequestException
    // 5xx responses - ServerResponseException
    private suspend fun getGithubUsers() {
        if (!this.isOnline()) {
            withContext(Main) { Snackbar.make(binding.root, "Device is offline", Snackbar.LENGTH_LONG).show() }
            return
        }

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
    }

    private suspend fun getGithubUsersWithOfflineFeature() {
        val apiResponse = apiService.getGithubUsers2(userCount = 135)

        apiResponse onSuccess { statusCode: Int ->
            val userList: List<User> = apiResponse.httpResponse?.body() ?: emptyList()
            withContext(Main) { binding.tvUsers.text = userList.toString() }
            println("status code: $statusCode, response: ${gson.toJson(userList)}")
        }

        apiResponse onFailure { statusCode: Int ->
            val errorBody = apiResponse.httpResponse?.body<KtorError>()
            println("status code: $statusCode, error: ${errorBody?.message}")
        }

        apiResponse onOffline {
            withContext(Main) { Snackbar.make(binding.root, "Device is offline", Snackbar.LENGTH_LONG).show() }
        }
    }
}