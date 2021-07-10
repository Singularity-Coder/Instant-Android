package com.singularitycoder.daggerhilt1

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.singularitycoder.daggerhilt1.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.HttpURLConnection

// https://www.youtube.com/watch?v=B56oV3IHMxg
// https://stackoverflow.com/questions/63077578/dagger-hilt-viewmodelinject-sharedviewmodel-not-injecting-into-fragment
// https://stackoverflow.com/questions/58106707/how-does-kotlin-use-this-by-delegate-to-instantiate-the-viewmodel
// Basic idea is that class should not construct its own dependencies. Pass them from outside through constructors

// 1. @HiltAndroidApp - Application class
// 2. @AndroidEntryPoint - on Activities. Allows Hilt to inject dependencies
// 3. @Inject - on a variable like viewmodel etc and it injects at the declaration. No need to initialise
// 4. @InstallIn

// 5. @Module - U need this when Hilt doesnt know how to create dependencies. Like Interface or a class with context
// 6. @Provides
// 7. @ApplicationContext
// 8. @Singleton - Scope Annotation

// 9. @EntryPoint - Use on stuff like content provider. Hilt doesnt offer injection here readily

// 10. @ViewModelInject
// 11. @Assisted

// SCOPES
// 1. @Singleton - Application Component
// 2. @ActivityRetained - ActivityRetainedComponent
// 3. @ActivityScoped - ActivityComponent
// 4. @FragmentScoped - FragmentComponent
// 5. @ViewScoped - ViewWithFragmentComponent
// 6. @ServiceScoped - ServiceComponent

// Component knows how to create our dependencies

// TODO
// 1. Navigation Components Fragment
// 2. Hilt
// 3. Room
// 4. Tests
// 5. Gradle Changes
// 6. Click Listener Higher Order Function

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG: String = "MainActivity"
    }

    private val repoList = ArrayList<Item>()

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppUtils.setStatusBarColor(activity = this, statusBarColor = R.color.purple_700)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpRecyclerView()
        getRepos()
        setUpSwipeRefreshLayout()
        binding.root.setOnClickListener { }
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            itemAnimator = DefaultItemAnimator()
            adapter = RepoAdapter(repoList = repoList, context = this@MainActivity)
        }
    }

    private fun getRepos() {
        CoroutineScope(IO).launch {
            if (AppUtils.hasInternet(context = this@MainActivity)) showReposOnlineState()
            else showReposOfflineState()
        }
    }

    private suspend fun showReposOnlineState() {
        withContext(Main) {
            binding.tvNoInternet.visibility = View.GONE
            showLoading()
        }
        val callback: Callback<RepoResponse> = object : Callback<RepoResponse> {
            override fun onResponse(call: Call<RepoResponse>?, response: Response<RepoResponse>?) {
                runOnUiThread {
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

        ApiService.getApiEndPoints().searchAllRepositories("language:kotlin", "stars", "desc").enqueue(callback)
    }

    private fun showRepoHttpOkState(response: Response<RepoResponse>) {
        try {
            val repoResponse = RepoResponse(response.body()?.itemsList ?: emptyList())
            repoList.clear()
            repoList.addAll(repoResponse.itemsList)
            runOnUiThread {
                binding.recyclerView.adapter?.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            Log.e(TAG, "error: ${e.message}")
        }

        runOnUiThread {
            if (0 == repoList.size) binding.tvNothing.visibility = View.VISIBLE
        }
    }

    private fun showRepoErrorState(response: Response<RepoResponse>) {
        val error: RepoError = AppUtils.parseError(response = response)
        runOnUiThread {
            AppUtils.showSnack(
                view = binding.root,
                message = error.message ?: "NA",
                actionButtonText = "OK",
                voidFunction = { })
        }
    }

    private suspend fun showReposOfflineState() {
        withContext(Main) {
            binding.tvNoInternet.visibility = View.VISIBLE
            hideLoading()
        }
    }

    private fun setUpSwipeRefreshLayout() {
        binding.swipeRefreshLayout.apply {
            setColorSchemeColors(ContextCompat.getColor(this@MainActivity, R.color.purple_500))
            setOnRefreshListener { getRepos() }
        }
    }

    private fun showLoading() {
        binding.shimmerLoading.root.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.apply {
            shimmerLoading.root.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
        }
    }
}