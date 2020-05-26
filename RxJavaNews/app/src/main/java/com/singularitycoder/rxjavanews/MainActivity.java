package com.singularitycoder.rxjavanews;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noFeedText;
    private TextView noInternetText;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ArrayList<NewsSubItemArticle> newsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setStatusBarColor(this, R.color.colorPrimaryDark);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        setUpRecyclerView();
        getNewsData();
        swipeRefreshLayout.setOnRefreshListener(this::getNewsDataFromApiWithSingleObservable);
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
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private void getNewsData() {
        if (hasInternet(this)) {
            AsyncTask.execute(this::getNewsDataFromApiWithSingleObservable);      // Emits a single response
//            AsyncTask.execute(this::getNewsDataFromApiWithDefaultObservable);     // Alternate way
//            AsyncTask.execute(this::getNewsDataFromApiWithMaybeObservable);     // Alternate way when u r not sure if data exists or not
//            AsyncTask.execute(this::getNewsDataFromApiWithFlowableObservable);     // Alternate way when there is back pressure or too much data is being emitted by observable n observer is not able to handle that load.
//            AsyncTask.execute(this::getExecutionStatusWithCompletableObservable);     // Alternate way but only gives status to check whether api call is working or not.
        } else {
            noInternetText.setVisibility(View.VISIBLE);
        }
    }

    public boolean hasInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null;
    }

    private void getNewsDataFromApiWithSingleObservable() {
        ApiEndPoints apiService = RetrofitService.getInstance().create(ApiEndPoints.class);
        compositeDisposable.add(
                apiService
                        .getNewsListWithSingleObservable("in", "technology", "7f04855731a54f77bff41dcfa5b7a4a4")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<NewsItemResponse>() {
                            @Override
                            public void onSuccess(NewsItemResponse newsObject) {
                                if (null != newsObject && newsObject.getArticles().size() > 0) {
                                    newsList.clear();
                                    newsList.addAll(newsObject.getArticles());
                                    newsAdapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                    progressBar.setVisibility(View.GONE);
                                    noInternetText.setVisibility(View.GONE);
                                    noFeedText.setVisibility(View.GONE);
                                } else {
                                    swipeRefreshLayout.setRefreshing(false);
                                    progressBar.setVisibility(View.GONE);
                                    noInternetText.setVisibility(View.GONE);
                                    noFeedText.setVisibility(View.VISIBLE);
                                    noFeedText.setText("Nothing to show :(");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                progressBar.setVisibility(View.GONE);
                                noInternetText.setVisibility(View.GONE);
                                swipeRefreshLayout.setRefreshing(false);
                                noFeedText.setVisibility(View.VISIBLE);
                                noFeedText.setText("Something is wrong.\nTry again!");
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        })
        );
    }

    @SuppressLint("CheckResult")
    private void getNewsDataFromApiWithDefaultObservable() {
        ApiEndPoints apiService = RetrofitService.getInstance().create(ApiEndPoints.class);
        Observable<NewsItemResponse> newsObservable = apiService.getNewsListWithDefaultObservable("in", "technology", "7f04855731a54f77bff41dcfa5b7a4a4");
        compositeDisposable.add(
                newsObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(newsItemResponse -> newsItemResponse.getArticles())
                        .subscribe(newsSubItemArticles -> handleResults(newsSubItemArticles), t -> handleError(t))
        );
    }

    private void getNewsDataFromApiWithMaybeObservable() {
        ApiEndPoints apiService = RetrofitService.getInstance().create(ApiEndPoints.class);
        apiService
                .getNewsListWithMaybeObservable("in", "technology", "7f04855731a54f77bff41dcfa5b7a4a4")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(newsItemResponse -> newsItemResponse.getArticles())
                .subscribe(observerMaybe());
    }

    private void getNewsDataFromApiWithFlowableObservable() {
        ApiEndPoints apiService = RetrofitService.getInstance().create(ApiEndPoints.class);
        Flowable<NewsItemResponse> newsObservable = apiService.getNewsListWithFlowableObservable("in", "technology", "7f04855731a54f77bff41dcfa5b7a4a4");
        compositeDisposable.add(
                newsObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(newsItemResponse -> newsItemResponse.getArticles())
                        .subscribe(newsSubItemArticles -> handleResults(newsSubItemArticles), t -> handleError(t))
        );
    }

    @SuppressLint("CheckResult")
    private void getExecutionStatusWithCompletableObservable() {
        ApiEndPoints apiService = RetrofitService.getInstance().create(ApiEndPoints.class);
        Completable newsObservable = apiService.getExecutionStatusWithCompletableObservable("in", "technology", "7f04855731a54f77bff41dcfa5b7a4a4");
        newsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(MainActivity.this, "Status: Success", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                        progressBar.setVisibility(View.GONE);
                        noInternetText.setVisibility(View.GONE);
                        noFeedText.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                        noInternetText.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        noFeedText.setVisibility(View.VISIBLE);
                        noFeedText.setText("Something is wrong.\nTry again!");
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private MaybeObserver<List<NewsSubItemArticle>> observerMaybe() {
        return new MaybeObserver<List<NewsSubItemArticle>>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onSuccess(List<NewsSubItemArticle> newsSubItemArticles) {
                if (null != newsSubItemArticles && newsSubItemArticles.size() > 0) {
                    newsList.clear();
                    newsList.addAll(newsSubItemArticles);
                    newsAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    noInternetText.setVisibility(View.GONE);
                    noFeedText.setVisibility(View.GONE);
                } else {
                    swipeRefreshLayout.setRefreshing(false);
                    progressBar.setVisibility(View.GONE);
                    noInternetText.setVisibility(View.GONE);
                    noFeedText.setVisibility(View.VISIBLE);
                    noFeedText.setText("Nothing to show :(");
                }
            }

            @Override
            public void onError(Throwable e) {
                progressBar.setVisibility(View.GONE);
                noInternetText.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                noFeedText.setVisibility(View.VISIBLE);
                noFeedText.setText("Something is wrong.\nTry again!");
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void handleResults(List<NewsSubItemArticle> newsSubItemArticles) {
        if (null != newsSubItemArticles && newsSubItemArticles.size() > 0) {
            newsList.clear();
            newsList.addAll(newsSubItemArticles);
            newsAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            noInternetText.setVisibility(View.GONE);
            noFeedText.setVisibility(View.GONE);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            progressBar.setVisibility(View.GONE);
            noInternetText.setVisibility(View.GONE);
            noFeedText.setVisibility(View.VISIBLE);
            noFeedText.setText("Nothing to show :(");
        }
    }

    private void handleError(Throwable t) {
        progressBar.setVisibility(View.GONE);
        noInternetText.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        noFeedText.setVisibility(View.VISIBLE);
        noFeedText.setText("Something is wrong.\nTry again!");
        Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}