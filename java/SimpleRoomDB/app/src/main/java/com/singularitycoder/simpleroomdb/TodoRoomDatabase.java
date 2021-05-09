package com.singularitycoder.simpleroomdb;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Todo.class}, version = 1)
public abstract class TodoRoomDatabase extends RoomDatabase {

    public abstract TodoDao todoDao();

    private static volatile TodoRoomDatabase INSTANCE;

    static synchronized TodoRoomDatabase getInstance(Context context) {
        if (INSTANCE == null) INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TodoRoomDatabase.class, "Todo_Database").fallbackToDestructiveMigration().build();
        return INSTANCE;
    }
}
