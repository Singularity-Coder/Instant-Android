package com.singularitycoder.roomnews.helper.room;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.singularitycoder.roomnews.dao.NewsDao;
import com.singularitycoder.roomnews.model.NewsItem;

@Database(entities = {
        NewsItem.NewsResponse.class,
        NewsItem.NewsArticle.class,
        NewsItem.NewsSource.class
}, version = 1, exportSchema = false)
@TypeConverters({
        NewsArticleConverter.class
})
public abstract class NewsRoomDatabase extends RoomDatabase {

    @Nullable
    private static NewsRoomDatabase _instance;

    @Nullable
    public abstract NewsDao newsDao();

    @NonNull
    public static synchronized NewsRoomDatabase getInstance(Context context) {
        if (null == _instance) {
            _instance = Room
                    .databaseBuilder(
                            context.getApplicationContext(),
                            NewsRoomDatabase.class,
                            "news_room_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return _instance;
    }
}