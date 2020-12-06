package com.singularitycoder.viperarchi1.NewsHome;

import android.app.Activity;

import androidx.fragment.app.FragmentManager;

import com.singularitycoder.viperarchi1.NewsHome.Entity.NewsSubItemArticle;

import java.util.List;

public class NewsContract {

    public interface View {
        void showProgress();
        void hideProgress();
        void setResponseToViews(List<NewsSubItemArticle> data);
        void ifResponseEmpty();
        void ifResponseFailed(Throwable throwable);
    }

    public interface Interactor {
        interface OnFinishedListener {
            void onResponseSuccess(List<NewsSubItemArticle> data);
            void onResponseEmpty();
            void onResponseFailure(Throwable throwable);
        }
        void getNewsFromApi(OnFinishedListener onFinishedListener, String country, String category);
    }

    public interface Presenter {
        void showNews(String country, String category);
        void showNewsDetail(int position, FragmentManager getSupportFragmentManager);
    }

    public interface Router {
        void goToNewsDetailScreen(Activity activity, FragmentManager getSupportFragmentManager, NewsSubItemArticle newsSubItemArticle);
    }
}
