package com.singularitycoder.retrofitresponseobject.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.singularitycoder.retrofitresponseobject.helper.ApiEndPoints;
import com.singularitycoder.retrofitresponseobject.helper.AppConstants;
import com.singularitycoder.retrofitresponseobject.helper.RetrofitService;
import com.singularitycoder.retrofitresponseobject.model.NewsItem;

import io.reactivex.Single;
import retrofit2.Response;

public final class NewsRepository {

    @Nullable
    private static NewsRepository _instance;

    private NewsRepository() {
    }

    @NonNull
    public static synchronized NewsRepository getInstance() {
        if (null == _instance) _instance = new NewsRepository();
        return _instance;
    }

    @Nullable
    public final Single<Response<NewsItem.NewsResponse>> getNewsWithRetrofit(
            @Nullable final String country,
            @NonNull final String category) {
        final ApiEndPoints apiService = RetrofitService.getInstance().create(ApiEndPoints.class);
        return apiService.getNewsList(country, category, AppConstants.API_KEY);
    }
}
