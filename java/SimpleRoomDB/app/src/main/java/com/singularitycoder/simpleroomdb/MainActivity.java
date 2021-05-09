package com.singularitycoder.simpleroomdb;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.singularitycoder.simpleroomdb.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

// https://www.sqlite.org/lang.html
// https://www.youtube.com/watch?v=qO56SL856xc&list=PLdHg5T0SNpN3CMNtsd5KGaiBtzhTGIwtC
// Room ORM - Abstraction layer over SQLite

// 3 steps to work with Room ORM
// 1. DB class
// 2. Entity - POJOs that act as tables
// 3. Dao Interface - has methods for accessing DB for performing DB operations


// 6 components of DB class
// 1. Must be annotated with @Database(entities = {x.class}, version = 1)
// 2. Must be an abstract class
// 3. Must extends RoomDatabase
// 4. Must include all entities
// 5. Must contain 0 arg abstract method & return an interface - annotated with @Dao - we use this to communicate with DB
// 6. Must contain synchronized DB instance a.k.a singleton which contains the DB name


// Components of Entity
// 1. @Entity annotation above class
// 2. @PrimaryKey(autoGenerate = true) for auto-incrementing column row unique ids
// 3. @ColumnInfo(name = "some_column_name") for creating a column with name mentioned
// 4. Empty constructor


// Components of Dao
// 1. Its an interface
// 2. @Insert for inserting row
// 3. @Update for updating row
// 4. @Delete for deleting row
// 5. @Query for custom DB operations
// 6. @Ignore for informing DB to not consider it as a DB column


// DB Operations
// 1. Create single item
// 2. Create multiple items at once

// 3. Read an item by some key
// 4. Read all items
// 5. Read all items with the key "x"

// 6. Update an item
// 7. Update all items at once
// 8. Update all items with the key "x"

// 9. Delete an item
// 10. Delete all items at once
// 11. Delete all items with the key "x"


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TodoDao dao;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dao = TodoRoomDatabase.getInstance(getApplicationContext()).todoDao();
        setUpClickListeners();
    }

    private void setUpClickListeners() {
        binding.btnCreateTodo.setOnClickListener(v -> createATodo());
        binding.btnCreateMultipleTodos.setOnClickListener(v -> createMultipleTodos());

        binding.btnReadTodoById.setOnClickListener(v -> readTodoById());
        binding.btnReadAllTodos.setOnClickListener(v -> readAllTodos());
        binding.btnReadAllCompletedTodos.setOnClickListener(v -> readAllCompletedTodos());

        binding.btnUpdateTodoByObject.setOnClickListener(v -> updateATodo());
        binding.btnUpdateAllTodosState.setOnClickListener(v -> updateAllTodosIncomplete());
        binding.btnUpdateAllCompletedTodoNames.setOnClickListener(v -> updateAllCompletedTodoNames());

        binding.btnDeleteTodoByObject.setOnClickListener(v -> deleteATodo());
        binding.btnDeleteAllTodosByState.setOnClickListener(v -> deleteAllTodosByState());
        binding.btnDeleteAllTodos.setOnClickListener(v -> deleteAllTodos());
    }

    private void createATodo() {
        final Todo todo = new Todo("watch some anime ...", false);
        new Thread(() -> dao.createTodo(todo)).start();
    }

    private void createMultipleTodos() {
        new Thread(() -> {
            final List<Todo> todoList = new ArrayList<>();
            todoList.add(new Todo("watch code geass", false));
            todoList.add(new Todo("watch death note", true));
            todoList.add(new Todo("watch marvel movies series", true));
            todoList.add(new Todo("watch your name movie", false));
            dao.createMultipleTodos(todoList);
            Log.d(TAG, "Todos created.");
        }).start();
    }

    private void readTodoById() {
        new Thread(() -> {
            try {
                final Todo thirdTodo = dao.readTodoById(3);
                Log.d(TAG, "Third Todo: " + thirdTodo.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void readAllTodos() {
        new Thread(() -> {
            final List<Todo> todoList = dao.readAllTodos();
            Log.d(TAG, "All Todos: " + todoList.toString());
        }).start();
    }

    private void readAllCompletedTodos() {
        new Thread(() -> {
            final List<Todo> todoList = dao.readAllCompletedTodos(1);
            Log.d(TAG, "All Completed Todos: " + todoList.toString());
        }).start();
    }

    private void updateATodo() {
        new Thread(() -> {
            final Todo todo = dao.readTodoById(2);
            if (todo != null) {
                todo.setCompleted(true);
                dao.updateTodoByObject(todo);
                Log.d(TAG, "Todo is updated");
            }
        }).start();
    }

    private void updateAllTodosIncomplete() {
        new Thread(() -> {
            dao.updateAllTodosState(0);
            Log.d(TAG, "All todos are incomplete");
        }).start();
    }

    private void updateAllCompletedTodoNames() {
        new Thread(() -> {
            dao.updateAllCompletedTodoNames("Omae wa mo shinderu!");
            Log.d(TAG, "All completed todo names changed!");
        }).start();
    }

    private void deleteATodo() {
        new Thread(() -> {
            final Todo todo = dao.readTodoById(2);
            if (todo != null) {
                Log.d(TAG, "Delete Todo: " + todo.toString());
                dao.deleteTodoByObject(todo);
                Log.d(TAG, "Todo has been deleted");
            }
        }).start();
    }

    private void deleteAllTodosByState() {
        new Thread(() -> {
            dao.deleteAllTodosByTodoState(1);
            Log.d(TAG, "Deleting all completed todos.");
        }).start();
    }

    private void deleteAllTodos() {
        new Thread(() -> {
            dao.deleteAllTodos();
            Log.d(TAG, "Deleting all todos.");
        }).start();
    }
}