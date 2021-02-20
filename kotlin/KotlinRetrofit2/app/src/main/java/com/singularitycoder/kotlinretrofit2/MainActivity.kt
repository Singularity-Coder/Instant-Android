package com.singularitycoder.kotlinretrofit2

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.singularitycoder.kotlinretrofit2.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.net.HttpURLConnection

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private var _binding: ActivityMainBinding? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_binding!!.root)
        binding = _binding!!
        getRepos()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun getRepos() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                getReposFromRetrofit()
            } catch (e: Exception) {
                binding.tvRepo.text = e.message
            }
        }
    }

    private fun getReposFromRetrofit() {
        val apiService: ApiService = Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)

        apiService.getAllRepos().enqueue(object : Callback<List<Repo>> {
            override fun onResponse(
                    call: Call<List<Repo>>,
                    reposResponse: retrofit2.Response<List<Repo>>
            ) {
                if (HttpURLConnection.HTTP_OK == reposResponse.code()) {
                    val repoList: List<Repo> = reposResponse.body() as List<Repo>
                    var reposString: String = ""
                    for (element in repoList) {
                        reposString += "${element.owner.html_url} \n\n"
                        binding.tvRepo.text = reposString
                    }
                    binding.progressBar.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }
}

interface ApiService {
    @GET("/repositories")
    fun getAllRepos(): Call<List<Repo>>
}

data class Repo(val owner: Owner)

data class Owner(val html_url: String)