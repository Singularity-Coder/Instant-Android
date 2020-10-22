package com.singularitycoder.retrofitresponseobject.view;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.singularitycoder.retrofitresponseobject.R;
import com.singularitycoder.retrofitresponseobject.adapter.NewsAdapter;
import com.singularitycoder.retrofitresponseobject.databinding.ActivityMainBinding;
import com.singularitycoder.retrofitresponseobject.helper.AppUtils;
import com.singularitycoder.retrofitresponseobject.helper.StateMediator;
import com.singularitycoder.retrofitresponseobject.helper.UiState;
import com.singularitycoder.retrofitresponseobject.model.NewsItem;
import com.singularitycoder.retrofitresponseobject.viewmodel.NewsViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static java.lang.String.valueOf;

public final class MainActivity extends AppCompatActivity {

    @NonNull
    private final String TAG = "MainActivity";

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @NonNull
    private final List<NewsItem.NewsArticle> newsList = new ArrayList<>();

    @Nullable
    private NewsViewModel newsViewModel;

    @Nullable
    private NewsItem.NewsResponse newsResponse;

    @Nullable
    private NewsAdapter newsAdapter;

    @Nullable
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appUtils.setStatusBarColor(this, R.color.colorPrimaryDark);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialise();
        setUpRecyclerView();
        getNewsFromApi();
        binding.swipeRefreshLayout.setOnRefreshListener(this::getNewsFromApi);
    }

    private void initialise() {
        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
    }

    private void setUpRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerNews.setLayoutManager(layoutManager);
        newsAdapter = new NewsAdapter(newsList, this);
        binding.recyclerNews.setAdapter(newsAdapter);
        binding.recyclerNews.setItemAnimator(new DefaultItemAnimator());
    }

    private void getNewsFromApi() {
        if (appUtils.hasInternet(this)) showOnlineState();
        else showOfflineState();
    }

    private void showOnlineState() {
        final String country = "in";
        final String category = "technology";
        newsViewModel.getNewsFromRepository(country, category).observe(MainActivity.this, observeLiveData());
    }

    private void showOfflineState() {
        binding.tvNoInternet.setVisibility(View.VISIBLE);
        hideLoading();
        newsList.clear();
    }

    @Nullable
    private Observer<StateMediator<Object, UiState, String, String>> observeLiveData() {
        Observer<StateMediator<Object, UiState, String, String>> observer = null;
        if (appUtils.hasInternet(this)) {
            observer = stateMediator -> {
                if (UiState.LOADING == stateMediator.getStatus()) showLoadingState(stateMediator);

                if (UiState.SUCCESS == stateMediator.getStatus()) showSuccessState(stateMediator);

                if (UiState.EMPTY == stateMediator.getStatus()) showEmptyState(stateMediator);

                if (UiState.ERROR == stateMediator.getStatus()) showErrorState(stateMediator);
            };
        }
        return observer;
    }

    private void showLoadingState(StateMediator<Object, UiState, String, String> stateMediator) {
        runOnUiThread(() -> showLoading());
    }

    private void showSuccessState(StateMediator<Object, UiState, String, String> stateMediator) {
        runOnUiThread(() -> {
            if (("NEWS").equals(stateMediator.getKey())) {
                showNewsListSuccessState(stateMediator);
            }
        });
    }

    private void showNewsListSuccessState(StateMediator<Object, UiState, String, String> stateMediator) {
        newsList.clear();
        hideLoading();
        binding.tvNoInternet.setVisibility(View.GONE);

        final Response<NewsItem.NewsResponse> response = (Response<NewsItem.NewsResponse>) stateMediator.getData();

        if (HttpURLConnection.HTTP_OK == response.code()) {
            if (null == response.body()) return;
            newsResponse = response.body();
            final List<NewsItem.NewsArticle> newsArticles = newsResponse.getArticles();
            newsList.addAll(newsArticles);
            newsAdapter.notifyDataSetChanged();
            Toast.makeText(MainActivity.this, valueOf(stateMediator.getData()), Toast.LENGTH_SHORT).show();
        }

        if (HttpURLConnection.HTTP_BAD_REQUEST == response.code()) {
            if (null == response.errorBody()) return;
            try {
                JSONObject jsonErrorObject = null;
                try {
                    jsonErrorObject = new JSONObject(response.errorBody().string());
                } catch (IOException ignored) {
                }
                String errors = "";
                final JSONObject jsonObject = jsonErrorObject.getJSONObject("error");
                final JSONArray jsonArray = jsonObject.getJSONArray("errors");
                if (null == jsonArray) return;
                for (int i = 0; i < jsonArray.length(); i++) {
                    errors += jsonArray.getJSONObject(i).getString("field") + " " + jsonArray.getJSONObject(i).getString("message") + "\n\n";
                }
                System.out.println(errors);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (HttpURLConnection.HTTP_INTERNAL_ERROR == response.code()) {
            Toast.makeText(this, "Something is wrong. Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEmptyState(StateMediator<Object, UiState, String, String> stateMediator) {
        runOnUiThread(() -> {
            binding.tvNothing.setVisibility(View.VISIBLE);
            binding.tvNothing.setText("Nothing to show :(");
            hideLoading();
            binding.tvNoInternet.setVisibility(View.GONE);
            Toast.makeText(this, valueOf(stateMediator.getMessage()), Toast.LENGTH_LONG).show();
        });
    }

    private void showErrorState(StateMediator<Object, UiState, String, String> stateMediator) {
        runOnUiThread(() -> {
            binding.tvNothing.setVisibility(View.GONE);
            hideLoading();
            binding.tvNoInternet.setVisibility(View.GONE);
            Toast.makeText(this, valueOf(stateMediator.getMessage()), Toast.LENGTH_LONG).show();
            Log.d(TAG, "liveDataObserver: error: " + stateMediator.getMessage());
        });
    }

    private void showLoading() {
        binding.swipeRefreshLayout.setRefreshing(true);
    }

    private void hideLoading() {
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}