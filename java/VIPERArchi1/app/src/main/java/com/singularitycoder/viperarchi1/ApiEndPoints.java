package com.singularitycoder.viperarchi1;

import com.singularitycoder.viperarchi1.NewsHome.Entity.NewsItemResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiEndPoints {

    @GET("top-headlines")
    Call<NewsItemResponse> getNewsList(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );
}