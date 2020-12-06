package com.singularitycoder.phpmysqlconnection;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

class RetrofitService {

    // http://YOUR_NETWORK_IP_ADDRESS:YOUR_WEBSERVER_PORT_NUMBER/YOUR_PROJECT_NAME/index.php
    private static final String URL = "http://192.168.1.103:8888/AndroidAuth/index.php/";

    private static Retrofit retrofit;
    private static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

    private static OkHttpClient.Builder getHttpClientBuilder() {
        httpClientBuilder
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.MINUTES)
                .addInterceptor(chain -> {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    requestBuilder.addHeader("Content-Type", "application/json");
                    requestBuilder.addHeader("Accept", "application/json");
                    return chain.proceed(requestBuilder.build());
                });
        return httpClientBuilder;
    }

    private static OkHttpClient httpClient = getHttpClientBuilder().build();

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit
                    .Builder()
                    .client(httpClient)
                    .baseUrl(URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}