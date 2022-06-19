package com.example.epoxy1

import com.example.epoxy1.model.Food

fun getFoodItems(count: Int): List<Food> = (1..count).map {
    Food(
        image = R.drawable.dummy_pic,
        title = "Food $it",
        description = "werty erty erty erty erty ertyui $it"
    )
}