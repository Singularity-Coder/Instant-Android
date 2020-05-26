package com.singularitycoder.mvcarchitecture;

import java.util.List;

public class NewsContract {

    interface ResponseResults {
        void onSuccess(List<NewsSubItemArticle> newsList);
        void onEmpty(String message);
        void onFailure(Throwable throwable);
    }

    interface SetViews {
        void onResponseLoading();
        void onFinishedLoading();
        void onResponseSuccess(List<NewsSubItemArticle> newsList);
        void onResponseEmpty(String message);
        void onResponseFailure(Throwable throwable);
    }
}
