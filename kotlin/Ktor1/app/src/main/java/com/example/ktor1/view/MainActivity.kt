package com.example.ktor1.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.ktor1.apiservice.GithubApiEndPointsService
import com.example.ktor1.apiservice.ReqResApiEndPointsService
import com.example.ktor1.databinding.ActivityMainBinding
import com.example.ktor1.model.*
import com.example.ktor1.utils.isOnline
import com.example.ktor1.utils.onFailure
import com.example.ktor1.utils.onOffline
import com.example.ktor1.utils.onSuccess
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
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
    lateinit var githubApiService: GithubApiEndPointsService

    @Inject
    lateinit var reqResApiService: ReqResApiEndPointsService

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGetGithubUsers.setOnClickListener {
            CoroutineScope(IO).launch { getGithubUserList() }
        }
        binding.btnGetGithubUsersOffline.setOnClickListener {
            CoroutineScope(IO).launch { getGithubUserListWithOfflineFeature() }
        }
        binding.btnGetReqresUsers.setOnClickListener {
            CoroutineScope(IO).launch { getReqResUserList() }
        }
        binding.btnGetReqresUserNotFound.setOnClickListener {
            CoroutineScope(IO).launch { getReqResUserNotFound() }
        }
        binding.btnCreateReqresUser.setOnClickListener {
            CoroutineScope(IO).launch { createReqResUser() }
        }
    }

    private suspend fun getGithubUserList() {
        if (!isOnline()) {
            withContext(Main) { Snackbar.make(binding.root, "Device is offline", Snackbar.LENGTH_LONG).show() }
            return
        }

        val httpResponse = try {
            githubApiService.getGithubUserList(userCount = 135)
        } catch (e: ResponseException) {
            println("Error: ${e.response.status.description}")
            e.response
        } catch (e: Exception) {
            println(e.message)
            null
        }

        httpResponse?.onSuccess { statusCode: Int ->
            val githubUserList: List<GithubUser> = httpResponse.body()
            withContext(Main) { binding.tvUsers.text = githubUserList.toString() }
            println("status code: $statusCode, response: ${gson.toJson(githubUserList)}")
        }

        httpResponse?.onFailure { statusCode: Int ->
            val errorBody = try {
                httpResponse.body<ApiError>()
            } catch (e: Exception) {
                println(e.message)
                null
            }
            println("status code: $statusCode, error: ${errorBody?.message}")
        }
    }

    private suspend fun getGithubUserListWithOfflineFeature() {
        val apiResponse = githubApiService.getGithubUserListWithOfflineFeature(userCount = 135)

        apiResponse onSuccess { statusCode: Int ->
            val githubUserList: List<GithubUser> = apiResponse.httpResponse?.body() ?: emptyList()
            withContext(Main) { binding.tvUsers.text = githubUserList.toString() }
            println("status code: $statusCode, response: ${gson.toJson(githubUserList)}")
        }

        apiResponse onFailure { statusCode: Int ->
            val errorBody = try {
                apiResponse.httpResponse?.body<ApiError>()
            } catch (e: Exception) {
                println(e.message)
                null
            }
            println("status code: $statusCode, error: ${errorBody?.message}")
        }

        apiResponse onOffline {
            withContext(Main) { Snackbar.make(binding.root, "Device is offline", Snackbar.LENGTH_LONG).show() }
        }
    }

    private suspend fun getReqResUserList() {
        if (!isOnline()) {
            withContext(Main) { Snackbar.make(binding.root, "Device is offline", Snackbar.LENGTH_LONG).show() }
            return
        }

        val httpResponse = try {
            reqResApiService.getUserList(pageCount = 2)
        } catch (e: ResponseException) {
            println("Error: ${e.response.status.description}")
            e.response
        } catch (e: Exception) {
            println(e.message)
            null
        }

        httpResponse?.onSuccess { statusCode: Int ->
            val reqResUserList: ReqResUserList = httpResponse.body()
            withContext(Main) { binding.tvUsers.text = reqResUserList.toString() }
            println("status code: $statusCode, response: ${gson.toJson(reqResUserList)}")
        }

        httpResponse?.onFailure { statusCode: Int ->
            val errorBody = try {
                httpResponse.body<ApiError>()
            } catch (e: Exception) {
                println(e.message)
                null
            }
            println("status code: $statusCode, error: ${errorBody?.message}")
        }
    }

    private suspend fun getReqResUserNotFound() {
        if (!isOnline()) {
            withContext(Main) { Snackbar.make(binding.root, "Device is offline", Snackbar.LENGTH_LONG).show() }
            return
        }

        val httpResponse = try {
            reqResApiService.getUser(userId = 23)
        } catch (e: ResponseException) {
            println("Error: ${e.response.status.description}")
            e.response
        } catch (e: Exception) {
            println(e.message)
            null
        }

        httpResponse?.onFailure { statusCode: Int ->
            val errorBody = try {
                httpResponse.body<ApiError>()
            } catch (e: Exception) {
                println(e.message)
                null
            }
            withContext(Main) { binding.tvUsers.text = "status code: $statusCode, error: ${errorBody?.message}" }
            println("status code: $statusCode, error: ${errorBody?.message}")
        }
    }

    private suspend fun createReqResUser() {
        if (!isOnline()) {
            withContext(Main) { Snackbar.make(binding.root, "Device is offline", Snackbar.LENGTH_LONG).show() }
            return
        }

        val requestPayload = ReqResRequest(name = "Hithesh", job = "Android Dev")
        val httpResponse = try {
            reqResApiService.createUser(requestPayload = requestPayload)
        } catch (e: ResponseException) {
            println("Error: ${e.response.status.description}")
            e.response
        } catch (e: Exception) {
            println(e.message)
            null
        }

        httpResponse?.onSuccess { statusCode: Int ->
            val createdUserResponse: ReqResResponse = httpResponse.body()
            withContext(Main) { binding.tvUsers.text = createdUserResponse.toString() }
            println("status code: $statusCode, response: ${gson.toJson(createdUserResponse)}")
        }

        httpResponse?.onFailure { statusCode: Int ->
            val errorBody = try {
                httpResponse.body<ApiError>()
            } catch (e: Exception) {
                println(e.message)
                null
            }
            withContext(Main) { binding.tvUsers.text = "status code: $statusCode, error: ${errorBody?.message}" }
            println("status code: $statusCode, error: ${errorBody?.message}")
        }
    }

    private suspend fun uploadFileForReqResUser() {
        if (!isOnline()) {
            withContext(Main) { Snackbar.make(binding.root, "Device is offline", Snackbar.LENGTH_LONG).show() }
            return
        }

        val fileToSend = File("my_profile_pic")

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(name = "profile_pic", filename = "hithesh123", body = fileToSend.asRequestBody())
            .addFormDataPart(name = "random_name", value = "random_value")
            .build()

        val formBuilder: FormBuilder.() -> Unit = {
            append("random_name", "random_value")
            append("profile_pic", fileToSend.readBytes(), Headers.build {
                append(HttpHeaders.ContentType, "multipart/form-data") // Mime type required
                append(HttpHeaders.ContentDisposition, "filename=hithesh123") // Filename in content disposition required
            })
        }

        val httpResponse = try {
            reqResApiService.uploadFile(formBuilder = formBuilder, multipartBody = multipartBody)
        } catch (e: ResponseException) {
            println("Error: ${e.response.status.description}")
            e.response
        } catch (e: Exception) {
            println(e.message)
            null
        }
    }
}