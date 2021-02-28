package com.singularitycoder.jetpackcompose2.ui

import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.singularitycoder.jetpackcompose2.model.Recipe

@Composable
fun RecipeList(recipes: List<Recipe>) {
    val padding: Modifier = Modifier.absolutePadding(left = 16.dp, top = 16.dp, right = 16.dp, bottom = 0.dp)
    val paddingLastCard: Modifier = Modifier.absolutePadding(left = 16.dp, top = 16.dp, right = 16.dp, bottom = 16.dp)

    LazyColumn {
        // Single Item
//        item { RecipeCard(recipes[0], padding) }

        // Lists with objects
//        for (i in 0..recipes.lastIndex) items(i) { RecipeCard(recipes[i], padding) }

        // Lists with objects
        itemsIndexed(recipes) { index, recipe ->
            println("Recipe at index $index is $recipe")
            // Add bottom padding if last card
            if (index == recipes.lastIndex) RecipeCard(recipes[index], paddingLastCard)
            else RecipeCard(recipes[index], padding)
        }
    }
}