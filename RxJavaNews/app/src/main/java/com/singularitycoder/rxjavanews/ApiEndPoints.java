package com.singularitycoder.rxjavanews;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiEndPoints {

    @GET("top-headlines")
    Single<NewsItemResponse> getNewsListWithSingleObservable(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

    @GET("top-headlines")
    Observable<NewsItemResponse> getNewsListWithDefaultObservable(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

    @GET("top-headlines")
    Maybe<NewsItemResponse> getNewsListWithMaybeObservable(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

    @GET("top-headlines")
    Flowable<NewsItemResponse> getNewsListWithFlowableObservable(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );

    @GET("top-headlines")
    Completable getExecutionStatusWithCompletableObservable(
            @Query("country") String country,
            @Query("category") String category,
            @Query("apiKey") String apiKey
    );
}