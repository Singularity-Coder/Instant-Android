package com.singularitycoder.mvparchitecture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// MODEL: This is the "M" in MVP
public class NewsModel {

    private static final String TAG = "NewsModel";

    public NewsContract.Model newsModel;

    public NewsModel() {
        newsModel = new NewsContract.Model() {
            @Override
            public void getNewsFromApi(OnFinishedListener onFinishedListener, String country, String category) {
                ApiEndPoints apiService = RetrofitService.getRetrofitInstance().create(ApiEndPoints.class);
                Call<NewsItemResponse> call = apiService.getNewsList(country, category, "YOUR_NEWSAPI.ORG_API_KEY");
                call.enqueue(new Callback<NewsItemResponse>() {
                    @Override
                    public void onResponse(Call<NewsItemResponse> call, Response<NewsItemResponse> response) {
                        if (response.body() != null) {
                            if (response.body().getArticles() != null) {
                                onFinishedListener.onResponseSuccess(response.body().getArticles());  // U r setting the interface data here
                            }
                        } else {
                            onFinishedListener.onResponseEmpty();
                        }
                    }

                    @Override
                    public void onFailure(Call<NewsItemResponse> call, Throwable t) {
                        onFinishedListener.onResponseFailure(t);
                    }
                });
            }
        };
    }
}
