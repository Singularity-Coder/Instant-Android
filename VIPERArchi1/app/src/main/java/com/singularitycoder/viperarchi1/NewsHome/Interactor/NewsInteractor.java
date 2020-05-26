package com.singularitycoder.viperarchi1.NewsHome.Interactor;

import com.singularitycoder.viperarchi1.ApiEndPoints;
import com.singularitycoder.viperarchi1.NewsHome.Entity.NewsItemResponse;
import com.singularitycoder.viperarchi1.NewsHome.NewsContract;
import com.singularitycoder.viperarchi1.RetrofitService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsInteractor {

    private static final String TAG = "NewsInteractor";

    public NewsContract.Interactor newsInteractor;

    public NewsInteractor() {
        newsInteractor = new NewsContract.Interactor() {
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
