package com.singularitycoder.singletonpattern1;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Task.class}, version = 1)
public abstract class TaskRoomDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    private static volatile TaskRoomDatabase INSTANCE;

    static TaskRoomDatabase getInstance(Context context) {
        synchronized (TaskRoomDatabase.class) {
            if (INSTANCE == null) INSTANCE = Room.databaseBuilder(context.getApplicationContext(), TaskRoomDatabase.class, "task_database").build();
        }
        return INSTANCE;
    }
}
