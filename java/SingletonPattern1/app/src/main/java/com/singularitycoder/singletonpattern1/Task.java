package com.singularitycoder.singletonpattern1;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "task_uid")
    private int uid;

    @ColumnInfo(name = "task_name")
    private String taskName;

    public Task(String taskName) {
        this.taskName = taskName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String toString() {
        return "\nTask{" + "uid=" + uid + ", text='" + taskName + '\'' + '}';
    }
}
