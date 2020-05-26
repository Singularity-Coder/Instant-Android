package com.singularitycoder.mvcarchitecture;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsModel {

    private static final String TAG = "Model";

    private static NewsModel newsModel;
    private ApiEndPoints apiService;

    public NewsModel() {
        apiService = RetrofitService.getInstance().create(ApiEndPoints.class);
    }

    public static NewsModel getInstance() {
        if (null == newsModel) {
            newsModel = new NewsModel();
        }
        return newsModel;
    }

    public void getNewsDataFromApi(NewsContract.ResponseResults responseResults, String country, String category) {
        Call<NewsItemResponse> call = apiService.getNewsList(country, category, "YOUR_NEWSAPI.ORG_API_KEY");
        call.enqueue(new Callback<NewsItemResponse>() {

            @Override
            public void onResponse(Call<NewsItemResponse> call, Response<NewsItemResponse> response) {
                if (response.isSuccessful()) {
                    if (null != response.body()) {
                        responseResults.onSuccess(response.body().getArticles());
                    }
                } else {
                    responseResults.onEmpty("Nothing to show :(");
                }
            }

            @Override
            public void onFailure(Call<NewsItemResponse> call, Throwable t) {
                responseResults.onFailure(t);
            }
        });
    }
}
