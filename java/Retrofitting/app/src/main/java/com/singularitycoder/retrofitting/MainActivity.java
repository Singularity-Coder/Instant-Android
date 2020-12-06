package com.singularitycoder.retrofitting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noFeedText;
    private TextView noInternetText;
    private ArrayList<NewsItem> newsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(this, R.color.colorPrimaryDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        setUpRecyclerView();
        getNewsData();
        swipeRefreshLayout.setOnRefreshListener(this::getNewsDataFromApi);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void setStatusBarColor(Activity activity, int statusBarColor) {
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, statusBarColor));
        window.requestFeature(window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private void initializeViews() {
        progressBar = findViewById(R.id.progress_circular);
        recyclerView = findViewById(R.id.recycler_news);
        noFeedText = findViewById(R.id.tv_nothing_to_show);
        noInternetText = findViewById(R.id.tv_no_internet);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
    }

    private void setUpRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        newsAdapter = new NewsAdapter(newsList, this);
        recyclerView.setAdapter(newsAdapter);
    }

    private void getNewsData() {
        if (hasInternet(this)) {
            AsyncTask.execute(this::getNewsDataFromApi);
        } else {
            noInternetText.setVisibility(View.VISIBLE);
        }
    }

    public boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    private void getNewsDataFromApi() {
        ApiEndPoints apiService = RetrofitService.getRetrofitInstance().create(ApiEndPoints.class);
        Call<String> call = apiService.getNewsList("in", "technology", "7058c10f192f48d7bf026d1209cc8dcb");
        call.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Log.d("Response: ", String.valueOf(response.body()));

                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());

                                if (("ok").equals(jsonObject.getString("status"))) {
                                    if (!newsList.isEmpty()) {
                                        newsList.clear();
                                    }
                                    JSONArray jsonArray = jsonObject.getJSONArray("articles");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObjectSource = jsonArray.getJSONObject(i).getJSONObject("source");
                                        JSONObject arrayObject = jsonArray.getJSONObject(i);

                                        try {
                                            newsList.add(new NewsItem(
                                                    jsonObjectSource.getString("name"),
                                                    arrayObject.getString("author"),
                                                    arrayObject.getString("title"),
                                                    arrayObject.getString("description"),
                                                    arrayObject.getString("urlToImage"),
                                                    arrayObject.getString("publishedAt")
                                            ));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    runOnUiThread(() -> {
                                        if (newsList.size() == 0) {
                                            noInternetText.setVisibility(View.GONE);
                                            noFeedText.setVisibility(View.VISIBLE);
                                        } else {
                                            noInternetText.setVisibility(View.GONE);
                                            noFeedText.setVisibility(View.GONE);
                                        }
                                        swipeRefreshLayout.setRefreshing(false);
                                        newsAdapter.notifyDataSetChanged();
                                    });
                                }

                                if (jsonObject.getString("error").equals("true")) {
                                    Toast.makeText(MainActivity.this, "Error fetching data!", Toast.LENGTH_SHORT).show();
                                    swipeRefreshLayout.setRefreshing(false);
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        noFeedText.setVisibility(View.VISIBLE);
                        noFeedText.setText("Something is wrong.\nTry again!");
                    }
                });
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    noInternetText.setVisibility(View.GONE);
                    noFeedText.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    noFeedText.setText("Nothing to show :(");
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
