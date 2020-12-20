package com.singularitycoder.kotlinrecyclerview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        val userList: ArrayList<User> = ArrayList()
        userList.add(User("Hithesh Vurjana", "Android Developer"))
        userList.add(User("Michael Bewets", "Doctor"))
        userList.add(User("Hema Hew", "iOS Developer"))
        userList.add(User("Jack Conner", "Node JS Developer"))
        userList.add(User("Marissa Williams", "Marketing Specialist"))
        userList.add(User("Priya Rao", "Android Developer"))
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val adapter: UsersAdapter = UsersAdapter(userList)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }
}