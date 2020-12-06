package com.singularitycoder.roomnews.helper.room;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.singularitycoder.roomnews.model.NewsItem;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public final class NewsArticleConverter implements Serializable {

    @TypeConverter
    public String NewsArticleListToString(@NonNull final List<NewsItem.NewsArticle> newsArticleList) {
        if (null == newsArticleList) return (null);
        final Gson gson = new Gson();
        final Type type = new TypeToken<List<NewsItem.NewsArticle>>() {
        }.getType();
        final String json = gson.toJson(newsArticleList, type);
        return json;
    }

    @TypeConverter
    public List<NewsItem.NewsArticle> stringToNewsArticleList(@NonNull final String newsArticleListString) {
        if (null == newsArticleListString) return (null);
        final Gson gson = new Gson();
        final Type type = new TypeToken<List<NewsItem.NewsArticle>>() {
        }.getType();
        final List<NewsItem.NewsArticle> newsArticleList = gson.fromJson(newsArticleListString, type);
        return newsArticleList;
    }
}
