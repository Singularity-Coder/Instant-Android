package com.singularitycoder.roomnews.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.singularitycoder.roomnews.helper.AppConstants;
import com.singularitycoder.roomnews.helper.espresso.ApiIdlingResource;
import com.singularitycoder.roomnews.helper.retrofit.StateMediator;
import com.singularitycoder.roomnews.helper.retrofit.UiState;
import com.singularitycoder.roomnews.model.NewsItem;
import com.singularitycoder.roomnews.repository.NewsRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public final class NewsViewModel extends AndroidViewModel {

    @NonNull
    private final String TAG = "NewsViewModel";

    @NonNull
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    @NonNull
    private NewsRepository newsRepository = NewsRepository.getInstance();

    @Nullable
    private LiveData<List<NewsItem.NewsArticle>> newsArticleList;

    public NewsViewModel(@NonNull Application application) {
        super(application);
        newsRepository = new NewsRepository(application);
        newsArticleList = newsRepository.getAllNewsArticlesFromRoomDbThroughDao();
    }

    // ROOM START______________________________________________________________

    public final void insertIntoRoomDbThroughRepository(NewsItem.NewsArticle newsArticle) {
        newsRepository.insertNewsArticleIntoRoomDbThroughDao(newsArticle);
    }

    public final void updateInRoomDbThroughRepository(NewsItem.NewsArticle newsArticle) {
        newsRepository.updateNewsArticleIntoRoomDbThroughDao(newsArticle);
    }

    public final void deleteFromRoomDbThroughRepository(NewsItem.NewsArticle newsArticle) {
        newsRepository.deleteNewsArticleFromRoomDbThroughDao(newsArticle);
    }

    public final void getNewsArticleFromRoomDbThroughRepository(int rowId) {
        newsRepository.getNewsArticleFromRoomDbThroughDao(rowId);
    }

    public final LiveData<List<NewsItem.NewsArticle>> getAllNewsArticlesFromRoomDbThroughRepository() {
        return newsArticleList;
    }

    public final void insertAllNewsArticlesIntoRoomDbThroughRepository(@Nullable final List<NewsItem.NewsArticle> articles) {
        newsRepository.insertAllNewsArticlesIntoRoomDbThroughDao(articles);
    }

    public final void deleteAllNewsArticlesFromRoomDbThroughRepository() {
        newsRepository.deleteAllNewsArticlesFromRoomDbThroughDao();
    }

    public final void deleteAllNewsResponsesFromRoomDbThroughRepository() {
        newsRepository.deleteAllNewsResponsesFromRoomDbThroughDao();
    }

    public final void deleteAllNewsSourcesFromRoomDbThroughRepository() {
        newsRepository.deleteAllNewsSourcesFromRoomDbThroughDao();
    }

    // ROOM END______________________________________________________________

    @NonNull
    public final LiveData<StateMediator<Object, UiState, String, String>> getNewsFromRepository(
            @Nullable final String country,
            @NonNull final String category,
            @Nullable final ApiIdlingResource idlingResource) throws IllegalArgumentException {

        if (null != idlingResource) idlingResource.setIdleState(false);

        final StateMediator<Object, UiState, String, String> stateMediator = new StateMediator<>();
        final MutableLiveData<StateMediator<Object, UiState, String, String>> mutableLiveData = new MutableLiveData<>();

        stateMediator.set(null, UiState.LOADING, "Loading...", null);
        mutableLiveData.postValue(stateMediator);

        final DisposableSingleObserver disposableSingleObserver =
                newsRepository.getNewsWithRetrofit(country, category)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver() {
                            @Override
                            public void onSuccess(Object o) {
                                Log.d(TAG, "onResponse: resp: " + o);
                                if (null != o) {
                                    stateMediator.set(o, UiState.SUCCESS, "Got Data!", AppConstants.KEY_GET_NEWS_LIST_API_SUCCESS_STATE);
                                    mutableLiveData.postValue(stateMediator);
                                    if (null != idlingResource) idlingResource.setIdleState(true);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                stateMediator.set(null, UiState.ERROR, e.getMessage(), null);
                                mutableLiveData.postValue(stateMediator);
                                if (null != idlingResource) idlingResource.setIdleState(true);
                            }
                        });
        compositeDisposable.add(disposableSingleObserver);
        return mutableLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
}
