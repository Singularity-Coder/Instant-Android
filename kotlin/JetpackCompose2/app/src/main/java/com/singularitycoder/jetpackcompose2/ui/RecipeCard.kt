package com.singularitycoder.jetpackcompose2.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.singularitycoder.jetpackcompose2.model.Recipe
import com.singularitycoder.jetpackcompose2.ui.MainActivity.Companion.defaultRecipes

@Composable
fun RecipeCard(recipe: Recipe, modifier: Modifier) {
    Surface(shape = RoundedCornerShape(8.dp), elevation = 8.dp, modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(recipe.imageResource),
                contentDescription = null,
                modifier = Modifier.height(144.dp).fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.absolutePadding(left = 16.dp, top = 8.dp, right = 16.dp, bottom = 16.dp)) {
                Text(text = recipe.title, style = MaterialTheme.typography.h4)
                Spacer(Modifier.height(8.dp))
                for (ingredient in recipe.ingredients) {
                    Text(text = "• $ingredient")
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun DefaultRecipeCard() {
    RecipeCard(defaultRecipes[0], Modifier.padding(16.dp))
}
