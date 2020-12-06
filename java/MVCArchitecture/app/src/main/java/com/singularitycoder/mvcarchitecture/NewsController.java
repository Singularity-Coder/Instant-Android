package com.singularitycoder.mvcarchitecture;

import java.util.List;

public class NewsController {

    private static final String TAG = "Controller";

    private static NewsController newsController;
    private NewsModel newsModel;
    private NewsContract.ResponseResults responseResults;
    private NewsContract.SetViews setViews;

    public NewsController(NewsContract.SetViews setViews) {
        this.newsModel = NewsModel.getInstance();
        this.setViews = setViews;
    }

    public static NewsController getInstance(NewsContract.SetViews setViews) {
        if (null == newsController) {
            newsController = new NewsController(setViews);
        }
        return newsController;
    }

    public NewsContract.ResponseResults implementResponseResults() {
        responseResults = new NewsContract.ResponseResults() {
            @Override
            public void onSuccess(List<NewsSubItemArticle> newsList) {
                if (null != setViews) setViews.onFinishedLoading();

                if (null != newsList) {
                    setViews.onResponseSuccess(newsList);
                } else {
                    setViews.onResponseEmpty("Nothing to show :(");
                }
            }

            @Override
            public void onEmpty(String message) {
                if (null != setViews) setViews.onResponseEmpty("Nothing to show :(");
            }

            @Override
            public void onFailure(Throwable throwable) {
                setViews.onResponseFailure(throwable);
            }
        };
        return responseResults;
    }

    public void getNewsList(String country, String category) {
        newsModel.getNewsDataFromApi(implementResponseResults(), country, category);
    }
}
