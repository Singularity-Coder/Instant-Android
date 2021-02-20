package com.singularitycoder.kotlinretrofit1.helper.api

import com.singularitycoder.kotlinretrofit1.model.RepoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiEndPoints {

    @GET("/search/repositories")
    fun searchAllRepositories(
            @Query("q") query: String,
            @Query("sort") sort: String,
            @Query("order") order: String,
    ): Call<RepoResponse>
}