package com.singularitycoder.kotlinretrofit1.helper.api

import com.singularitycoder.kotlinretrofit1.helper.AppConstants
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApiService {

    fun getApiEndPoints(): ApiEndPoints {
        return getRetrofit()!!.create(ApiEndPoints::class.java)
    }

    @Synchronized
    fun getRetrofit(): Retrofit? {
        return Retrofit.Builder()
            .client(getHttpClientBuilder().build())
            .baseUrl(AppConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun getHttpClientBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.MINUTES)
            .addInterceptor { chain: Interceptor.Chain ->
                val requestBuilder: Request.Builder = chain.request().newBuilder()
                requestBuilder.addHeader("Content-Type", "application/json")
                requestBuilder.addHeader("Accept", "application/json")
                chain.proceed(requestBuilder.build())
            }
    }
}