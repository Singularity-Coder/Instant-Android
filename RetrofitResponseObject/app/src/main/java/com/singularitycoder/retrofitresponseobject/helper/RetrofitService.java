package com.singularitycoder.retrofitresponseobject.helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RetrofitService {

    @Nullable
    private static Retrofit _instance;

    private RetrofitService() {
    }

    @NonNull
    public static synchronized Retrofit getInstance() {
        if (null == _instance) {
            _instance = new Retrofit
                    .Builder()
                    .client(getHttpClientBuilder().build())
                    .baseUrl(AppConstants.BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return _instance;
    }

    @NonNull
    private static OkHttpClient.Builder getHttpClientBuilder() {
        return new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(chain -> {
                    final Request.Builder requestBuilder = chain.request().newBuilder();
                    requestBuilder.addHeader("Content-Type", "application/json");
                    requestBuilder.addHeader("Accept", "application/json");
                    return chain.proceed(requestBuilder.build());
                });
    }
}