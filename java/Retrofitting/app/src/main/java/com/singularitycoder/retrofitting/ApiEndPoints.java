package com.singularitycoder.retrofitting;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiEndPoints {

    @GET("top-headlines")
    Call<String> getNewsList(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );
}
