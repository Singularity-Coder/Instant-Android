package com.singularitycoder.singletonpattern1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.singularitycoder.singletonpattern1.databinding.ActivityMainBinding;

// https://www.javatpoint.com/singleton-design-pattern-in-java
// define a class that has only one instance and provides a global point of access to it
// a class must ensure that only single instance should be created and single object can be used by all other classes.

// 2 forms of singleton
// 1. Early Instantiation: creation of instance at load time.
// 2. Lazy Instantiation: creation of instance when required.

// Advantages
// Saves memory because object is not created at each request. Only single instance is reused again and again.

// Uses
// Singleton pattern is mostly used in multi-threaded and database applications. It is used in logging, caching, thread pools, configuration settings etc.

// To create the singleton class, we need to have static member of class, private constructor and static factory method:
// Static member: It gets memory only once because of static, itcontains the instance of the Singleton class.
// Private constructor: It will prevent to instantiate the Singleton class from outside the class.
// Static factory method: This provides the global point of access to the Singleton object and returns the instance to the caller.

// Check read operations in logs
// Check write operations in Database Inspector Live

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TaskDao dao;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dao = TaskRoomDatabase.getInstance(getApplicationContext()).taskDao();
        setUpClickListeners();
    }

    private void setUpClickListeners() {
        binding.btnCreateTodo.setOnClickListener(v -> createATodo());
        binding.btnReadTodoById.setOnClickListener(v -> readTodoById());
        binding.btnUpdateTodoByObject.setOnClickListener(v -> updateATodo());
        binding.btnDeleteTodoByObject.setOnClickListener(v -> deleteATodo());
    }

    private void createATodo() {
        final Task task = new Task("First Task");
        new Thread(() -> dao.createTask(task)).start();
    }

    private void readTodoById() {
        new Thread(() -> {
            try {
                final Task thirdTask = dao.readTaskById(3);
                Log.d(TAG, "Third Task: " + thirdTask.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateATodo() {
        new Thread(() -> {
            final Task task = dao.readTaskById(2);
            if (task != null) {
                task.setTaskName("Second Task");
                dao.updateTaskByObject(task);
                Log.d(TAG, "Task is updated");
            }
        }).start();
    }

    private void deleteATodo() {
        new Thread(() -> {
            final Task task = dao.readTaskById(2);
            if (task != null) {
                Log.d(TAG, "Delete Task: " + task.toString());
                dao.deleteTaskByObject(task);
                Log.d(TAG, "Task has been deleted");
            }
        }).start();
    }
}