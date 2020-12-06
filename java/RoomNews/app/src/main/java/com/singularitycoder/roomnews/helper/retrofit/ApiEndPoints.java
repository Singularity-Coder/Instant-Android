package com.singularitycoder.roomnews.helper.retrofit;

import com.singularitycoder.roomnews.model.NewsItem;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiEndPoints {

    @GET("top-headlines")
    Single<Response<NewsItem.NewsResponse>> getNewsList(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );
}