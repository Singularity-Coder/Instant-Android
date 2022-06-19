package com.example.epoxy1.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.epoxy1.R
import com.example.epoxy1.viewmodel.FoodViewItemController

// https://medium.com/android-news/simplifying-recycler-view-with-epoxy-in-kotlin-nachos-tutorial-series-946d22116d57
class MainActivity : AppCompatActivity() {
    private val recyclerView: RecyclerView by lazy { findViewById<RecyclerView>(R.id.recycler_view) }
    private val foodViewItemController: FoodViewItemController by lazy { FoodViewItemController() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecycler()
    }

    private fun initRecycler() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
            adapter = foodViewItemController.adapter
        }

        // This statement builds model and adds it to the recycler view
        foodViewItemController.requestModelBuild()
    }
}