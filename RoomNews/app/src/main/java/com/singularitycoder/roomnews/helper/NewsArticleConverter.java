package com.singularitycoder.roomnews.helper;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.singularitycoder.roomnews.model.NewsItem;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

public class NewsArticleConverter implements Serializable {

    @TypeConverter
    public String NewsArticleListToString(@NonNull final List<NewsItem.NewsArticle> deviceInfoList) {
        if (null == deviceInfoList) return (null);
        final Gson gson = new Gson();
        final Type type = new TypeToken<List<NewsItem.NewsArticle>>() {
        }.getType();
        String json = gson.toJson(deviceInfoList, type);
        return json;
    }

    @TypeConverter
    public List<NewsItem.NewsArticle> stringToNewsArticleList(@NonNull final String deviceInfoString) {
        if (null == deviceInfoString) return (null);
        final Gson gson = new Gson();
        final Type type = new TypeToken<List<NewsItem.NewsArticle>>() {
        }.getType();
        List<NewsItem.NewsArticle> deviceInfoList = gson.fromJson(deviceInfoString, type);
        return deviceInfoList;
    }
}
