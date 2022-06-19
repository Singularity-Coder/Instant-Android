package com.example.epoxy1.viewmodel

import com.airbnb.epoxy.EpoxyController
import com.example.epoxy1.getFoodItems
import com.example.epoxy1.model.Food
import com.example.epoxy1.view.foodViewItem

class FoodViewItemController : EpoxyController() {

    var foodItems: List<Food> = getFoodItems(50)

    override fun buildModels() {
        var i: Byte = 1
        foodItems.forEach { it: Food ->
            foodViewItem {
                id("food_item_${i++}")
                image(it.image)
                title(it.title)
                desc(it.description)
            }
        }
    }
}