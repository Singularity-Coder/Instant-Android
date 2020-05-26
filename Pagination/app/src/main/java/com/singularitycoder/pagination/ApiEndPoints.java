package com.singularitycoder.pagination;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiEndPoints {

    @GET("/api/users")
    Call<UsersResponse> getUsersList(
            @Query("page") String pageNumber,
            @Query("per_page") String numOfItemsPerPage
    );
}