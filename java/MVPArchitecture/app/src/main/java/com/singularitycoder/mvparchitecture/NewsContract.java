package com.singularitycoder.mvparchitecture;

import java.util.List;

public class NewsContract {

    interface Model {
        interface OnFinishedListener {
            void onResponseSuccess(List<NewsSubItemArticle> data);
            void onResponseEmpty();
            void onResponseFailure(Throwable throwable);
        }
        void getNewsFromApi(OnFinishedListener onFinishedListener, String country, String category);
    }

    interface View {
        void showProgress();
        void hideProgress();
        void setResponseToViews(List<NewsSubItemArticle> data);
        void ifResponseEmpty();
        void ifResponseFailed(Throwable throwable);
    }

    interface Presenter {
        void showNews(String country, String category);
    }
}
