package com.singularitycoder.mvvmarchitecture;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class NewsViewModel extends ViewModel {

    private static final String TAG = "NewsViewModel";

    private MutableLiveData<NewsItemResponse> mutableLiveData;
    private NewsRepository newsRepository;

    public LiveData<NewsItemResponse> getNewsFromRepository(String country, String category) {
        newsRepository = NewsRepository.getInstance();
        mutableLiveData = newsRepository.getNewsDataFromApi(country, category);
        return mutableLiveData;
    }
}