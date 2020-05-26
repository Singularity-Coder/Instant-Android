package com.singularitycoder.mvparchitecture;

import java.util.List;

// PRESENTER: This is the "P" in MVP
public class NewsPresenter {

    private static final String TAG = "NewsPresenter";

    private NewsContract.View newsView;
    private NewsContract.Model newsModel;
    private NewsContract.Model.OnFinishedListener newsListener;
    private NewsContract.Presenter newsPresenter;

    public NewsPresenter(NewsContract.View newsView) {
        this.newsView = newsView;
        this.newsModel = new NewsModel().newsModel;
        implementContractOnFinishedListener();
        implementContractPresenter();
    }

    public NewsContract.Model.OnFinishedListener implementContractOnFinishedListener() {
        newsListener = new NewsContract.Model.OnFinishedListener() {
            @Override
            public void onResponseSuccess(List<NewsSubItemArticle> data) {
                if (null != newsView) newsView.hideProgress();

                if (null != data) {
                    newsView.setResponseToViews(data);
                } else {
                    newsView.ifResponseEmpty();
                }
            }

            @Override
            public void onResponseEmpty() {
                if (null != newsView) newsView.ifResponseEmpty();
            }

            @Override
            public void onResponseFailure(Throwable throwable) {
                newsView.ifResponseFailed(throwable);
            }
        };
        return newsListener;
    }

    public NewsContract.Presenter implementContractPresenter() {
        newsPresenter = new NewsContract.Presenter() {
            @Override
            public void showNews(String country, String category) {
                if (null != newsView) newsView.showProgress();
                newsModel.getNewsFromApi(newsListener, country, category);
            }
        };
        return newsPresenter;
    }
}