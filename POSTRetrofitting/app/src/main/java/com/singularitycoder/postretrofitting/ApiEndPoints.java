package com.singularitycoder.postretrofitting;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiEndPoints {

    @POST("/create_account")
    Call<String> setUserData(
            @Header("Authorization") String authKey,    // This is for demo purpose. U generally won't have an Auth key during Signup.
            @Body RequestBody jsonObject
    );
}