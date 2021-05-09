package com.singularitycoder.simpleroomdb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TodoDao {

    // CREATE --------------------------------------------------------------------------------------------------------------------------------------------------------

    @Insert
    void createTodo(Todo todo);

    @Insert
    void createMultipleTodos(List<Todo> todoList);

    // READ --------------------------------------------------------------------------------------------------------------------------------------------------------

    @Query("SELECT * FROM todo_table WHERE todo_uid LIKE :uid")
    Todo readTodoById(int uid);

    @Query("SELECT * FROM todo_table")
    List<Todo> readAllTodos();

    @Query("SELECT * FROM todo_table WHERE todo_completed LIKE :todoState")
    List<Todo> readAllCompletedTodos(int todoState);

    // UPDATE --------------------------------------------------------------------------------------------------------------------------------------------------------

    @Update
    void updateTodoByObject(Todo todo);

    @Query("UPDATE todo_table SET todo_completed = :todoState")
    void updateAllTodosState(int todoState);

    @Query("UPDATE todo_table SET todo_task_name = :todoName WHERE todo_completed LIKE 1")
    void updateAllCompletedTodoNames(String todoName);

    // DELETE --------------------------------------------------------------------------------------------------------------------------------------------------------

    @Delete
    void deleteTodoByObject(Todo todo);

    @Query("DELETE FROM todo_table WHERE todo_completed = :todoState")
    void deleteAllTodosByTodoState(int todoState);

    @Query("DELETE FROM todo_table")
    void deleteAllTodos();
}
