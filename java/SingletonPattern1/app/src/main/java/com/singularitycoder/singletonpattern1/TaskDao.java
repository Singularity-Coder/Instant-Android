package com.singularitycoder.singletonpattern1;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface TaskDao {

    @Insert
    void createTask(Task task);

    @Query("SELECT * FROM task_table WHERE task_uid LIKE :uid")
    Task readTaskById(int uid);

    @Update
    void updateTaskByObject(Task task);

    @Delete
    void deleteTaskByObject(Task task);
}
