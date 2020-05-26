package com.singularitycoder.mvvmarchitecture;

import androidx.lifecycle.MutableLiveData;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {

    private static final String TAG = "NewsRepository";

    private static NewsRepository newsRepository;
    private ApiEndPoints apiService;

    public NewsRepository() {
        apiService = RetrofitService.getRetrofitInstance().create(ApiEndPoints.class);
    }

    public static NewsRepository getInstance() {
        if (newsRepository == null) {
            newsRepository = new NewsRepository();
        }
        return newsRepository;
    }

    public MutableLiveData<NewsItemResponse> getNewsDataFromApi(String country, String category) {
        final MutableLiveData<NewsItemResponse> newsLiveData = new MutableLiveData<>();
        NewsItemResponse newsItemResponse = new NewsItemResponse();
        Call<NewsItemResponse> call = apiService.getNewsList(country, category, "YOUR_NEWSAPI.ORG_API_KEY");
        call.enqueue(new Callback<NewsItemResponse>() {

            @Override
            public void onResponse(Call<NewsItemResponse> call, Response<NewsItemResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        response.body().setResponseStatus("SUCCESS");
                        newsLiveData.postValue(response.body());
                    }
                } else {
                    response.body().setResponseStatus("EMPTY");
                    newsLiveData.postValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<NewsItemResponse> call, Throwable t) {
                newsItemResponse.setResponseStatus("FAILURE");
                newsItemResponse.setResponseMessage(t.getMessage());
                newsLiveData.postValue(newsItemResponse);
            }
        });
        newsItemResponse.setResponseStatus("STARTED");
        newsLiveData.postValue(newsItemResponse);
        return newsLiveData;
    }
}