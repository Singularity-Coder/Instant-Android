package com.singularitycoder.volleynetworkinggson;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noFeedText;
    private TextView noInternetText;
    private ArrayList<NewsSubItemArticle> newsList = new ArrayList<>();
    private String BASE_URL = "http://newsapi.org/v2/";
    private RequestQueue requestQueue;

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
        requestQueue = VolleyRequestQueue.getInstance(this).getRequestQueue();
        getNewsWithJsonObjectRequest("in", "science", "YOUR_NEWSAPI.ORG_API_KEY");
        requestQueue.addRequestFinishedListener(request -> {
            if (("application/json").equals(request.getBodyContentType())) {
                runOnUiThread(() -> Toast.makeText(this, "Got Science News", Toast.LENGTH_SHORT).show());
                getNewsWithStringRequest("in", "technology", "YOUR_NEWSAPI.ORG_API_KEY");
            } else {
                runOnUiThread(() -> Toast.makeText(this, "Got Tech News", Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void getNewsWithJsonObjectRequest(String country, String category, String apiKey) {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

        final String url = BASE_URL + "top-headlines?country=" + country + "&category=" + category + "&apiKey=" + apiKey;
        requestQueue.add(
                new JsonObjectRequest(
                        url,
                        null,
                        response -> {
                            runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                            Log.d("Response: ", String.valueOf(response));
                            if (!("").equals(response)) {
                                GsonBuilder builder = new GsonBuilder();
                                Gson gson = builder.create();
                                NewsItemResponse newsItemResponse = gson.fromJson(String.valueOf(response), NewsItemResponse.class);
                                newsList.addAll(newsItemResponse.getArticles());
                                newsAdapter.notifyDataSetChanged();
                                runOnUiThread(() -> {
                                    swipeRefreshLayout.setRefreshing(false);
                                    progressBar.setVisibility(View.GONE);
                                    noFeedText.setVisibility(View.GONE);
                                });
                            } else {
                                runOnUiThread(() -> {
                                    newsList.clear();
                                    newsAdapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                    progressBar.setVisibility(View.GONE);
                                    noFeedText.setVisibility(View.VISIBLE);
                                    noFeedText.setText("Nothing to show :(");
                                });
                            }
                        },
                        error -> {
                            if (error instanceof NetworkError) {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show());
                            } else {
                                runOnUiThread(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    noInternetText.setVisibility(View.GONE);
                                    noFeedText.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setRefreshing(false);
                                    noFeedText.setText("Something is wrong.\nTry again!");
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                });
                            }
                        }
                ) {
                    @Override
                    public Priority getPriority() {
                        return Priority.IMMEDIATE;  // Used when networks are bad. Sets which API must load first or last based on the priority labels.
                    }

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        // Used in POST String Request to enter data to send to servers
//                        Map<String, String> params = new HashMap<>();
//                        params.put("type", "All");
//                        params.put("id", "22");
//                        return params;
                        return super.getParams();
                    }

                    @Override
                    protected String getParamsEncoding() {
                        return super.getParamsEncoding();
                    }

                    @Override
                    public byte[] getBody() {
                        return super.getBody();
                    }

                    @Override
                    public Cache.Entry getCacheEntry() {
                        return super.getCacheEntry();
                    }

                    @Nullable
                    @Override
                    public Response.ErrorListener getErrorListener() {
                        return super.getErrorListener();
                    }

                    @Override
                    public int getMethod() {
                        return super.getMethod();
                    }

                    @Override
                    public int getTrafficStatsTag() {
                        return super.getTrafficStatsTag();
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        // If you have any authorization token etc, to access the API put them here
//                        HashMap<String, String> headers = new HashMap<>();
//                        headers.put("AuthKey", apiKey);
//                        return headers;
                        return super.getHeaders();
                    }

                    @Override
                    public Object getTag() {
                        return super.getTag();
                    }

                    @Override
                    public RetryPolicy getRetryPolicy() {
                        return super.getRetryPolicy();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }

                    @Override
                    public String getCacheKey() {
                        return super.getCacheKey();
                    }

                    @Override
                    public String getUrl() {
                        return super.getUrl();
                    }
                }
        );
    }

    private void getNewsWithStringRequest(String country, String category, String apiKey) {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

        final String url = BASE_URL + "top-headlines?country=" + country + "&category=" + category + "&apiKey=" + apiKey;
        requestQueue.add(
                new StringRequest(
                        Request.Method.GET,
                        url,
                        response -> {
                            runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                            Log.d("Response: ", String.valueOf(response));
                            if (!("").equals(response)) {
                                GsonBuilder builder = new GsonBuilder();
                                Gson gson = builder.create();
                                NewsItemResponse newsItemResponse = gson.fromJson(response, NewsItemResponse.class);
                                newsList.addAll(newsItemResponse.getArticles());
                                newsAdapter.notifyDataSetChanged();
                                runOnUiThread(() -> {
                                    swipeRefreshLayout.setRefreshing(false);
                                    progressBar.setVisibility(View.GONE);
                                    noFeedText.setVisibility(View.GONE);
                                });
                            } else {
                                runOnUiThread(() -> {
                                    newsList.clear();
                                    newsAdapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                    progressBar.setVisibility(View.GONE);
                                    noFeedText.setVisibility(View.VISIBLE);
                                    noFeedText.setText("Nothing to show :(");
                                });
                            }
                        },
                        error -> {
                            if (error instanceof NetworkError) {
                                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "No Internet", Toast.LENGTH_LONG).show());
                            } else {
                                runOnUiThread(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    noInternetText.setVisibility(View.GONE);
                                    noFeedText.setVisibility(View.VISIBLE);
                                    swipeRefreshLayout.setRefreshing(false);
                                    noFeedText.setText("Something is wrong.\nTry again!");
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                                });
                            }
                        }
                ) {
                    @Override
                    public Priority getPriority() {
                        return Priority.LOW;
                    }
                }
        );
    }
}
