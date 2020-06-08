package com.singularitycoder.retrofitpostwithgson;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

class RetrofitService {

    private static Retrofit retrofit;

    private static OkHttpClient.Builder getHttpClientBuilder() {
        return new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(chain -> {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    requestBuilder.addHeader("Content-Type", "application/json");
                    requestBuilder.addHeader("Accept", "application/json");
                    return chain.proceed(requestBuilder.build());
                });
    }

    static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit
                    .Builder()
                    .client(getHttpClientBuilder().build())
                    .baseUrl("https://reqres.in/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                    .addConverterFactory(GsonConverterFactory.create())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}