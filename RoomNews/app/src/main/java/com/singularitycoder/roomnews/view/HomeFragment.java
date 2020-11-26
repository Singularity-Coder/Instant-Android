package com.singularitycoder.roomnews.view;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.test.espresso.IdlingResource;

import com.singularitycoder.roomnews.R;
import com.singularitycoder.roomnews.adapter.NewsAdapter;
import com.singularitycoder.roomnews.databinding.FragmentHomeBinding;
import com.singularitycoder.roomnews.helper.AppConstants;
import com.singularitycoder.roomnews.helper.AppUtils;
import com.singularitycoder.roomnews.helper.NetworkStateListenerBuilder;
import com.singularitycoder.roomnews.helper.espresso.ApiIdlingResource;
import com.singularitycoder.roomnews.helper.retrofit.StateMediator;
import com.singularitycoder.roomnews.helper.retrofit.UiState;
import com.singularitycoder.roomnews.model.NewsItem;
import com.singularitycoder.roomnews.viewmodel.NewsViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

import static java.lang.String.valueOf;

public final class HomeFragment extends Fragment {

    @NonNull
    private final String TAG = "MainActivity";

    @NonNull
    private final AppUtils appUtils = AppUtils.getInstance();

    @NonNull
    private final List<NewsItem.NewsArticle> newsList = new ArrayList<>();

    @Nullable
    private NewsAdapter newsAdapter;

    @Nullable
    private NewsViewModel newsViewModel;

    @Nullable
    private ApiIdlingResource idlingResource;

    @Nullable
    private FragmentHomeBinding binding;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initialise();
        setUpRecyclerView();
        binding.swipeRefreshLayout.setOnRefreshListener(this::getNewsFromApi);
    }

    @Override
    public void onResume() {
        super.onResume();
        getNewsFromApiFromBuilder();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Causing memory leak
        appUtils.networkStateListener(getContext(), null, null, null);
    }

    private void initialise() {
        newsViewModel = new ViewModelProvider(this).get(NewsViewModel.class);
    }

    private void setUpRecyclerView() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerNews.setLayoutManager(layoutManager);
        newsAdapter = new NewsAdapter(newsList, getContext());
        binding.recyclerNews.setAdapter(newsAdapter);
        binding.recyclerNews.setItemAnimator(new DefaultItemAnimator());
    }

    private void recyclerViewLayoutAnimation() {
        final Context context = binding.recyclerNews.getContext();
        final LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, R.anim.fade_layout_rise_up);
        binding.recyclerNews.setLayoutAnimation(layoutAnimationController);
        binding.recyclerNews.getAdapter().notifyDataSetChanged();
        binding.recyclerNews.scheduleLayoutAnimation();
    }

    private void getNewsFromApi() {
        appUtils.networkStateListener(getContext(), () -> showOnlineState(), () -> showOnlineState(), () -> showOfflineState());
    }

    private void getNewsFromApiFromBuilder() {
        // This is just a builder pattern implementation of the networkStateListener() method. Its a lot easier to read and understand.
        new NetworkStateListenerBuilder(getContext())
                .setOnlineMobileFunction(() -> showOnlineState())
                .setOnlineWifiFunction(() -> showOnlineState())
                .setOfflineFunction(() -> showOfflineState())
                .build();
    }

    private Void showOnlineState() {
        binding.tvNoInternet.setVisibility(View.GONE);
        final String country = "in";
        final String category = "technology";
        newsViewModel.getNewsFromRepository(country, category, idlingResource).observe(getViewLifecycleOwner(), observeLiveData());
        return null;
    }

    private Void showOfflineState() {
        binding.tvNoInternet.setVisibility(View.VISIBLE);
        hideLoading();

        // If offline get List from Room DB
        newsViewModel.getAllNewsArticlesFromRoomDbThroughRepository().observe(getViewLifecycleOwner(), liveDataObserverForRoomDb());
        return null;
    }

    private Observer<List<NewsItem.NewsArticle>> liveDataObserverForRoomDb() {
        Observer<List<NewsItem.NewsArticle>> observer = null;
        observer = (List<NewsItem.NewsArticle> newsArticles) -> {
            if (null != newsArticles) {
                newsList.clear();
                newsList.addAll(newsArticles);
                newsAdapter.notifyDataSetChanged();
            }
        };
        return observer;
    }

    @Nullable
    private Observer<StateMediator<Object, UiState, String, String>> observeLiveData() {
        Observer<StateMediator<Object, UiState, String, String>> observer = null;
        if (appUtils.hasInternet(getContext())) {
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
        getActivity().runOnUiThread(() -> showLoading());
    }

    private void showSuccessState(StateMediator<Object, UiState, String, String> stateMediator) {
        getActivity().runOnUiThread(() -> {
            if ((AppConstants.KEY_GET_NEWS_LIST_API_SUCCESS_STATE).equals(stateMediator.getKey())) {
                showNewsListSuccessState(stateMediator);
            }
        });
    }

    private void showNewsListSuccessState(StateMediator<Object, UiState, String, String> stateMediator) {
        newsList.clear();
        hideLoading();
        binding.tvNothing.setVisibility(View.GONE);
        binding.lottieViewNothing.setVisibility(View.GONE);

        final Response<NewsItem.NewsResponse> response = (Response<NewsItem.NewsResponse>) stateMediator.getData();

        if (HttpURLConnection.HTTP_OK == response.code()) {
            showHttpOkState(stateMediator, response);
        }

        if (HttpURLConnection.HTTP_BAD_REQUEST == response.code()) {
            showHttpBadRequestState(response);
        }

        if (HttpURLConnection.HTTP_INTERNAL_ERROR == response.code()) {
            showHttpInternalErrorState();
        }
    }

    private void showHttpOkState(StateMediator<Object, UiState, String, String> stateMediator, Response<NewsItem.NewsResponse> response) {
        if (null == response.body()) return;
        final NewsItem.NewsResponse newsResponse = response.body();
        final List<NewsItem.NewsArticle> newsArticles = newsResponse.getArticles();
        newsList.addAll(newsArticles);
        newsAdapter.notifyDataSetChanged();
        appUtils.showSnack(binding.conLayNewsHomeRoot, valueOf(stateMediator.getData()), "OK", null);

        // Insert into Room DB
        try {
            newsViewModel.deleteAllNewsResponsesFromRoomDbThroughRepository();
            newsViewModel.deleteAllNewsArticlesFromRoomDbThroughRepository();
            newsViewModel.deleteAllNewsSourcesFromRoomDbThroughRepository();
            newsViewModel.insertAllNewsArticlesIntoRoomDbThroughRepository(newsArticles);
        } catch (Exception ignored) {
        }

        if (0 == newsList.size()) {
            binding.tvNothing.setVisibility(View.VISIBLE);
            binding.lottieViewNothing.setVisibility(View.VISIBLE);
            binding.lottieViewNothing.setAnimation(R.raw.nothing);
            binding.lottieViewNothing.playAnimation();
        }
    }

    private void showHttpBadRequestState(Response<NewsItem.NewsResponse> response) {
        // Just for reference. Not the actual bad request body of this API
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

    private void showHttpInternalErrorState() {
        appUtils.showSnack(binding.conLayNewsHomeRoot, "Something is wrong. Try again!", "OK", null);
    }

    private void showEmptyState(StateMediator<Object, UiState, String, String> stateMediator) {
        getActivity().runOnUiThread(() -> {
            binding.tvNothing.setVisibility(View.VISIBLE);
            binding.tvNothing.setText("Nothing to show :(");
            hideLoading();
            binding.tvNoInternet.setVisibility(View.GONE);
            appUtils.showSnack(binding.conLayNewsHomeRoot, valueOf(stateMediator.getMessage()), "OK", null);
        });
    }

    private void showErrorState(StateMediator<Object, UiState, String, String> stateMediator) {
        getActivity().runOnUiThread(() -> {
            binding.tvNothing.setVisibility(View.GONE);
            hideLoading();
            binding.tvNoInternet.setVisibility(View.GONE);
            appUtils.showSnack(binding.conLayNewsHomeRoot, valueOf(stateMediator.getMessage()), "OK", null);
            Log.d(TAG, "liveDataObserver: error: " + stateMediator.getMessage());
        });
    }

    private void showLoading() {
        binding.shimmerLoading.getRoot().setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        binding.shimmerLoading.getRoot().setVisibility(View.GONE);
        binding.swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Only called from test, creates and returns a new WaitingStateResource
    @VisibleForTesting
    @NonNull
    public IdlingResource getWaitingState() {
        if (null == idlingResource) idlingResource = new ApiIdlingResource();
        return idlingResource;
    }

    @VisibleForTesting
    public void showSuccessToast(StateMediator<Object, UiState, String, String> stateMediator) {
        appUtils.showSnack(binding.conLayNewsHomeRoot, valueOf(stateMediator.getMessage()), "OK", null);

    }
}