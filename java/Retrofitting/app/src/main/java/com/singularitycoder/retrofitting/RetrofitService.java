package com.singularitycoder.retrofitting;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

class RetrofitService {

    private static final String URL = "https://newsapi.org/v2/";

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

    static Retrofit getRetrofitInstance() {
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
