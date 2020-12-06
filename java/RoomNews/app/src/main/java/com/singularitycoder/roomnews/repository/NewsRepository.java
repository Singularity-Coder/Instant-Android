package com.singularitycoder.roomnews.repository;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.singularitycoder.roomnews.dao.NewsDao;
import com.singularitycoder.roomnews.helper.retrofit.ApiEndPoints;
import com.singularitycoder.roomnews.helper.AppConstants;
import com.singularitycoder.roomnews.helper.room.NewsRoomDatabase;
import com.singularitycoder.roomnews.helper.retrofit.RetrofitService;
import com.singularitycoder.roomnews.model.NewsItem;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Response;

public final class NewsRepository {

    @Nullable
    private static NewsRepository _instance;

    @Nullable
    private NewsDao newsDao;

    @Nullable
    private LiveData<List<NewsItem.NewsArticle>> newsArticleList;

    private NewsRepository() {
    }

    public NewsRepository(Application application) {
        final NewsRoomDatabase database = NewsRoomDatabase.getInstance(application);
        newsDao = database.newsDao();
        newsArticleList = newsDao.getAllNewsArticles();
    }

    @NonNull
    public static synchronized NewsRepository getInstance() {
        if (null == _instance) _instance = new NewsRepository();
        return _instance;
    }

    // ROOM START______________________________________________________________

    public final void insertNewsArticleIntoRoomDbThroughDao(NewsItem.NewsArticle newsArticle) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> newsDao.insertNewsArticle(newsArticle));
    }

    public final void updateNewsArticleIntoRoomDbThroughDao(NewsItem.NewsArticle newsArticle) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> newsDao.updateNewsArticle(newsArticle));
    }

    public final void deleteNewsArticleFromRoomDbThroughDao(NewsItem.NewsArticle newsArticle) {
        AsyncTask.SERIAL_EXECUTOR.execute(() -> newsDao.deleteNewsArticle(newsArticle));
    }

    public final void getNewsArticleFromRoomDbThroughDao(int rowId) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> newsDao.getNewsArticle(rowId));
    }

    public final LiveData<List<NewsItem.NewsArticle>> getAllNewsArticlesFromRoomDbThroughDao() {
        return newsArticleList;
    }

    public final void insertAllNewsArticlesIntoRoomDbThroughDao(@Nullable final List<NewsItem.NewsArticle> articles) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> newsDao.insertAllNewsArticles(articles));
    }

    public final void deleteAllNewsArticlesFromRoomDbThroughDao() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> newsDao.deleteAllNewsArticles());
    }

    public final void deleteAllNewsResponsesFromRoomDbThroughDao() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> newsDao.deleteAllNewsResponses());
    }

    public final void deleteAllNewsSourcesFromRoomDbThroughDao() {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> newsDao.deleteAllNewsSources());
    }

    // ROOM END______________________________________________________________

    @Nullable
    public final Single<Response<NewsItem.NewsResponse>> getNewsWithRetrofit(
            @Nullable final String country,
            @NonNull final String category) {
        final ApiEndPoints apiService = RetrofitService.getInstance().create(ApiEndPoints.class);
        return apiService.getNewsList(country, category, AppConstants.API_KEY);
    }
}
