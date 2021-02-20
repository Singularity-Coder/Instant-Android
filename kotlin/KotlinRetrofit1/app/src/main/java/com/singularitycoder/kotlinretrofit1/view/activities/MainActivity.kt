package com.singularitycoder.kotlinretrofit1.view.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.singularitycoder.kotlinretrofit1.R
import com.singularitycoder.kotlinretrofit1.databinding.ActivityMainBinding
import com.singularitycoder.kotlinretrofit1.helper.AppUtils
import com.singularitycoder.kotlinretrofit1.helper.api.ApiService
import com.singularitycoder.kotlinretrofit1.model.Item
import com.singularitycoder.kotlinretrofit1.model.RepoError
import com.singularitycoder.kotlinretrofit1.model.RepoResponse
import com.singularitycoder.kotlinretrofit1.view.adapters.RepoAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {

    // todo higher order func not working

    private val TAG: String = "MainActivity"
    private val repoList: MutableList<Item> = ArrayList()
    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private val handler: Handler = Handler(Looper.getMainLooper())

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtils.setStatusBarColor(activity = this, statusBarColor = R.color.purple_700)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpRecyclerView()
        getRepos()
        setUpSwipeRefreshLayout()
        binding.root.setOnClickListener { }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun setUpRecyclerView() {
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        binding.recyclerView.adapter = RepoAdapter(
            repoList = repoList,
            context = this@MainActivity
        )
    }

    private fun getRepos() {
        executor.execute {
            if (AppUtils.hasInternet(context = this)) showReposOnlineState()
            else showReposOfflineState()
        }
    }

    private fun showReposOnlineState() {
        handler.post {
            binding.tvNoInternet.visibility = View.GONE
            showLoading()
        }

        val callback: Callback<RepoResponse> = object : Callback<RepoResponse> {
            override fun onResponse(call: Call<RepoResponse>?, response: Response<RepoResponse>?) {
                handler.post {
                    hideLoading()
                    binding.tvNothing.visibility = View.GONE
                }

                if (HttpURLConnection.HTTP_OK == response?.code()) showRepoHttpOkState(response)
                else showRepoErrorState(response!!)
            }

            override fun onFailure(call: Call<RepoResponse>?, t: Throwable?) {
                Log.e(TAG, "Github API Error: ${t?.message}")
            }
        }

        ApiService.getApiEndPoints().searchAllRepositories("language:kotlin", "stars", "desc").enqueue(
            callback
        )
    }

    private fun showRepoHttpOkState(response: Response<RepoResponse>) {
        try {
            val repoResponse: RepoResponse = RepoResponse(response.body()?.itemsList ?: emptyList())
            repoList.clear()
            repoList.addAll(repoResponse.itemsList)
            handler.post { binding.recyclerView.adapter?.notifyDataSetChanged() }
        } catch (e: Exception) {
            Log.e(TAG, "error: ${e.message}")
        }

        handler.post { if (0 == repoList.size) binding.tvNothing.visibility = View.VISIBLE }
    }

    private fun showRepoErrorState(response: Response<RepoResponse>) {
        val error: RepoError = AppUtils.parseError(response = response)
        handler.post {
            AppUtils.showSnack(
                view = binding.root,
                message = error.message!!,
                actionButtonText = "OK",
                voidFunction = { AppUtils.dummy() })
        }
    }

    private fun showReposOfflineState() {
        handler.post {
            binding.tvNoInternet.visibility = View.VISIBLE
            hideLoading()
        }
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setColorSchemeColors(resources.getColor(R.color.purple_500))
        binding.swipeRefreshLayout.setOnRefreshListener { getRepos() }
    }

    private fun showLoading() {
        binding.shimmerLoading.root.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.shimmerLoading.root.visibility = View.GONE
        binding.swipeRefreshLayout.isRefreshing = false
    }
}
