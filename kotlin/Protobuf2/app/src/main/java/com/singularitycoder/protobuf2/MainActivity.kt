package com.singularitycoder.protobuf2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.singularitycoder.protobuf2.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
        private const val TIMEOUT_IN_20_SECONDS = 20000
        private const val TIMEOUT_IN_30_SECONDS = 30000
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getRepos()
    }

    private fun getRepos() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
//                getUsersFromHttpUrlConnection()
                getUsersFromRetrofit()
            } catch (e: Exception) {
                launch(Dispatchers.Main) { binding.tvRepo.text = e.message }
            }
        }
    }

    private fun getUsersFromRetrofit() {
        val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        httpClient.addInterceptor(object : Interceptor {
            override fun intercept(chain: Interceptor.Chain): Response {
                val original: Request = chain.request()

                val request: Request = original.newBuilder()
                    .header("Content-Type", "application/x-protobuf")
                    .method(original.method(), original.body())
                    .build()

                return chain.proceed(request)
            }
        })

        val client: OkHttpClient = httpClient.build()
        val apiService: ApiService = Retrofit.Builder()
            .baseUrl("https://api.github.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)

        apiService.getAllRepos().enqueue(object : Callback<List<GithubUserProtos.User>> {
            override fun onResponse(call: Call<List<GithubUserProtos.User>>, response: retrofit2.Response<List<GithubUserProtos.User>>) {
                if (HttpURLConnection.HTTP_OK == response.code()) {
//                    val userList = GithubUserProtos.GithubUsers.parseFrom(response.body().toString().toByteArray())

                    GlobalScope.launch(Dispatchers.Main) {
                        var reposString: String = ""
                        (response.body() as List<GithubUserProtos.User>).forEach { user: GithubUserProtos.User ->
                            reposString += "${user.htmlUrl} \n\n"
                            binding.tvRepo.text = reposString
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<List<GithubUserProtos.User>>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
                GlobalScope.launch(Dispatchers.Main) { binding.tvRepo.text = t.message }
            }
        })
    }

    private fun getUsersFromHttpUrlConnection() {
        try {
//            val jsonStr = URL("https://api.github.com/users").readText()
            val url = URL("https://api.github.com/users")
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")
                readTimeout = TIMEOUT_IN_20_SECONDS
                connectTimeout = TIMEOUT_IN_30_SECONDS
                connect()
            }
            val responseCode = connection.responseCode
            if (HttpURLConnection.HTTP_OK == responseCode) {
                try {
                    val inputStream: InputStream = BufferedInputStream(connection.inputStream)
//                    BufferedReader(InputStreamReader(inputStream)).use {
//                        val response = StringBuffer()
//                        var inputLine = it.readLine()
//                        while (inputLine != null) {
//                            response.append(inputLine)
//                            inputLine = it.readLine()
//                        }
//                        it.close()
//                        println("Response : $response")
//
//                        val userList: List<GithubUserProtos.User> = GithubUserProtos.GithubUsers.parseFrom(response.toString().toByteArray()).usersList
//                    }
                    val userList: List<GithubUserProtos.User> = GithubUserProtos.GithubUsers.parseFrom(inputStream).usersList
                    GlobalScope.launch(Dispatchers.Main) {
                        var reposString: String = ""
                        userList.forEach { user: GithubUserProtos.User ->
                            Log.d(TAG, "onResponse: ${user.htmlUrl}")
                            reposString += "${user.htmlUrl} \n\n"
                            binding.tvRepo.text = reposString
                        }
                        binding.progressBar.visibility = View.GONE
                    }
                } catch (e: IOException) {
                    GlobalScope.launch(Dispatchers.Main) { binding.tvRepo.text = e.message }
                }
            } else {
                val errorStream: InputStream = connection.errorStream
            }
        } catch (e: java.lang.Exception) {
            GlobalScope.launch(Dispatchers.Main) { binding.tvRepo.text = e.message }
        }
    }
}

interface ApiService {
    @Headers("Cache-Control: max-age=640000")
    @GET("/users")
    fun getAllRepos(): Call<List<GithubUserProtos.User>>
}