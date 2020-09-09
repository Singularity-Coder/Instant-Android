package com.singularitycoder.retrofitpostwithgson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

public interface ApiEndPoints {

    @POST("/api/users")
    Call<String> setUserDataWithTypeOne(
            @Header("Authorization") String authKey,
            @Body RequestBody jsonObject
    );

    @POST("/api/users")
    Call<JSONObject> setUserDataWithTypeTwo(
            @Header("Authorization") String authKey,
            @Body CreateAccountRequest createAccountRequestObject
    );

    @POST("/api/users")
    Call<JSONObject> setUserDataWithTypeThree(
            @Header("Authorization") String authKey,
            @Body HashMap<String, String> body
    );

    @Multipart
    @POST("/api/users")
    Single<String> setUserDataWithMultiPart(
            @Header("Authorization") String authKey,
            @Part MultipartBody.Part partImage,
            @Part MultipartBody.Part partName,
            @Part MultipartBody.Part partEmail,
            @Part MultipartBody.Part partPhone,
            @Part MultipartBody.Part partPassword
    );

    @Multipart
    @POST("/api/users")
    Call<String> setUserDataWithMultiPartMap(
            @Header("Authorization") String authKey,
            @PartMap Map<String, RequestBody> partMap
    );

    @Multipart
    @POST("/api/users")
    Call<String> setUserDataWithMultiPartList(
            @Header("Authorization") String authKey,
            @Part List<Part> partList
    );
}