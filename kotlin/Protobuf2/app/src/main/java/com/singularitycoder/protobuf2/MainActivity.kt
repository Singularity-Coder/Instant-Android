package com.singularitycoder.protobuf2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.singularitycoder.protobuf2.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
                getReposFromHttpUrlConnection()
            } catch (e: Exception) {
                launch(Dispatchers.Main) { binding.tvRepo.text = e.message }
            }
        }
    }

    private fun getReposFromHttpUrlConnection() {
        try {
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