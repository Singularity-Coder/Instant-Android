package com.singularitycoder.viperarchi1.NewsHome.Presenter;

import android.app.Activity;
import android.util.Log;

import androidx.fragment.app.FragmentManager;

import com.singularitycoder.viperarchi1.NewsHome.Interactor.NewsInteractor;
import com.singularitycoder.viperarchi1.NewsHome.NewsContract;
import com.singularitycoder.viperarchi1.NewsHome.Entity.NewsSubItemArticle;
import com.singularitycoder.viperarchi1.NewsHome.Router.NewsRouter;

import java.util.ArrayList;
import java.util.List;

public class NewsPresenter {

    private static final String TAG = "NewsPresenter";

    private Activity activity;
    private NewsContract.View newsView;
    private NewsContract.Interactor newsInteractor;
    private NewsContract.Interactor.OnFinishedListener newsListener;
    private NewsContract.Presenter newsPresenter;
    private NewsRouter newsRouter;
    private List<NewsSubItemArticle> newsList = new ArrayList<>();

    public NewsPresenter(Activity activity, NewsContract.View newsView) {
        this.activity = activity;
        this.newsView = newsView;
        this.newsInteractor = new NewsInteractor().newsInteractor;
        this.newsRouter = new NewsRouter();
        implementContractOnFinishedListener();
        implementContractPresenter();
    }

    public NewsContract.Interactor.OnFinishedListener implementContractOnFinishedListener() {
        newsListener = new NewsContract.Interactor.OnFinishedListener() {
            @Override
            public void onResponseSuccess(List<NewsSubItemArticle> data) {
                if (null != newsView) newsView.hideProgress();

                if (null != data) {
                    newsView.setResponseToViews(data);
                    if (null != newsList) newsList.clear();
                    newsList.addAll(data);
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
                newsInteractor.getNewsFromApi(newsListener, country, category);
            }

            @Override
            public void showNewsDetail(int position, FragmentManager getSupportFragmentManager) {
                newsRouter.implementContractRouter().goToNewsDetailScreen(activity, getSupportFragmentManager, newsList.get(position));
            }
        };
        return newsPresenter;
    }
}