package com.singularitycoder.pagination;

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
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String FIRST_PAGE = "1";
    private static final String ITEMS_PER_PAGE = "10";  // Set this to more items on long phones to scroll.

    private final ArrayList<UsersSubItemData> userList = new ArrayList<>();

    private RecyclerView recyclerView;
    private UsersAdapter usersAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvNoFeedText, tvNoInternetText;
    private TextView tvAdUrl, tvAdCompany, tvAdDescription;
    private LinearLayout linLayLoadMore;
    private ImageView ivAd;

    private boolean isScrolling = false;
    private int lazyLoadPageNumber = 1;
    private int totalPages = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(this, R.color.colorPrimaryDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        setUpRecyclerView();
        getNewsData();
        swipeRefreshLayout.setOnRefreshListener(() -> getNewsDataFromApi(FIRST_PAGE));
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
        tvNoFeedText = findViewById(R.id.tv_nothing_to_show);
        tvNoInternetText = findViewById(R.id.tv_no_internet);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        linLayLoadMore = findViewById(R.id.lin_lay_load_more);
        tvAdCompany = findViewById(R.id.tv_ad_company);
        tvAdDescription = findViewById(R.id.tv_ad_description);
        tvAdUrl = findViewById(R.id.tv_ad_url);
        ivAd = findViewById(R.id.iv_ad);
    }

    private void setUpRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        usersAdapter = new UsersAdapter(userList, this);
        recyclerView.setAdapter(usersAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        /** Since this has only 12 items only 2 more will load in this API.
         * Since phones are long these days there is a chance this might not scroll on all phones.
         * Try with an API that has a lot of items. */
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                final int currentItems = linearLayoutManager.getChildCount();
                final int totalItems = linearLayoutManager.getItemCount();
                final int scrolledOutItems = linearLayoutManager.findFirstVisibleItemPosition();
                Log.d(TAG, "onScrolled: scrolledOutItems: " + scrolledOutItems);
                Log.d(TAG, "onScrolled: totalItems: " + totalItems);
                Log.d(TAG, "onScrolled: currentItems: " + currentItems);

                if (totalPages > 0) {
                    if (lazyLoadPageNumber <= totalPages) {
                        if (isScrolling && (totalItems == currentItems + scrolledOutItems)) {
                            // Load new data
                            linLayLoadMore.setVisibility(View.VISIBLE);
                            lazyLoadPageNumber++;
                            AsyncTask.execute(() -> getNewsDataFromApi(valueOf(lazyLoadPageNumber)));
                        }
                    } else {
                        linLayLoadMore.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void getNewsData() {
        if (hasInternet(this)) {
            AsyncTask.execute(() -> getNewsDataFromApi(FIRST_PAGE));
        } else {
            tvNoInternetText.setVisibility(View.VISIBLE);
        }
    }

    public boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    private void getNewsDataFromApi(String pageNumber) {
        if (("1").equals(pageNumber)) runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
        ApiEndPoints apiService = RetrofitService.getInstance().create(ApiEndPoints.class);
        Call<UsersResponse> call = apiService.getUsersList(pageNumber, ITEMS_PER_PAGE);
        call.enqueue(new Callback<UsersResponse>() {

            @Override
            public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
                Log.d("Response: ", String.valueOf(response.body()));

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (("1").equals(pageNumber)) {
                            userList.clear();
                            lazyLoadPageNumber = 1;
                        }
                        List<UsersSubItemData> usersSubItemData = response.body().getData();
                        Glide.with(MainActivity.this).load("https://cdn.pixabay.com/photo/2020/01/29/20/24/architecture-4803602_960_720.jpg").into(ivAd);
                        tvAdUrl.setText(response.body().getAd().getUrl());
                        tvAdCompany.setText(response.body().getAd().getCompany());
                        tvAdDescription.setText(response.body().getAd().getText());
                        totalPages = response.body().getTotal();
                        userList.addAll(usersSubItemData);
                        usersAdapter.notifyDataSetChanged();
                        runOnUiThread(() -> {
                            linLayLoadMore.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            tvNoInternetText.setVisibility(View.GONE);
                            tvNoFeedText.setVisibility(View.GONE);
                        });
                    } else {
                        runOnUiThread(() -> {
                            userList.clear();
                            linLayLoadMore.setVisibility(View.GONE);
                            swipeRefreshLayout.setRefreshing(false);
                            progressBar.setVisibility(View.GONE);
                            tvNoInternetText.setVisibility(View.GONE);
                            tvNoFeedText.setVisibility(View.VISIBLE);
                            tvNoFeedText.setText("Nothing to show :(");
                        });
                    }
                } else {
                    runOnUiThread(() -> {
                        userList.clear();
                        linLayLoadMore.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        tvNoInternetText.setVisibility(View.GONE);
                        tvNoFeedText.setVisibility(View.VISIBLE);
                        tvNoFeedText.setText("Something is wrong.\nTry again!");
                        Toast.makeText(MainActivity.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                    });

                }
            }

            @Override
            public void onFailure(Call<UsersResponse> call, Throwable t) {
                runOnUiThread(() -> {
                    userList.clear();
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    tvNoInternetText.setVisibility(View.GONE);
                    tvNoFeedText.setVisibility(View.VISIBLE);
                    tvNoFeedText.setText("Something is wrong.\nTry again!");
                    Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}