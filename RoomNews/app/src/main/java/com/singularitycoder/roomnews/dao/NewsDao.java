package com.singularitycoder.roomnews.dao;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.singularitycoder.roomnews.model.NewsItem;

import java.util.List;

@Dao
public interface NewsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNewsArticle(NewsItem.NewsArticle newsArticle);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateNewsArticle(NewsItem.NewsArticle newsArticle);

    @Delete
    void deleteNewsArticle(NewsItem.NewsArticle newsArticle);

    @Query("SELECT * FROM table_news_article WHERE roomIdNewsArticle=:rowId")
    NewsItem.NewsArticle getNewsArticle(int rowId);

    @Query("SELECT * FROM table_news_article ORDER BY roomIdNewsArticle ASC")
    LiveData<List<NewsItem.NewsArticle>> getAllNewsArticles();

    // All of the parameters of the Insert method must either be classes annotated with Entity or collections/array of it.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllNewsArticles(@Nullable final List<NewsItem.NewsArticle> articles);

    @Query("DELETE FROM table_news_response")
    void deleteAllNewsResponses();

    @Query("DELETE FROM table_news_article")
    void deleteAllNewsArticles();

    @Query("DELETE FROM table_news_source")
    void deleteAllNewsSources();
}