package com.singularitycoder.phpmysqlconnection;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiEndPoints {

    @POST(".")
    Call<String> signUpEndPoint(
            @Body RequestBody jsonObject
    );

    @POST(".")
    Call<String> signInEndPoint(
            @Body RequestBody jsonObject
    );
}