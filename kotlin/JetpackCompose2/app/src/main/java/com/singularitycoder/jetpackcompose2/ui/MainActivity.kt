package com.singularitycoder.jetpackcompose2.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.singularitycoder.jetpackcompose2.R
import com.singularitycoder.jetpackcompose2.model.Recipe
import com.singularitycoder.jetpackcompose2.ui.MainActivity.Companion.defaultRecipes
import com.singularitycoder.jetpackcompose2.ui.MainActivity.Companion.recipe
import com.singularitycoder.jetpackcompose2.ui.theme.JetpackCompose2Theme

class MainActivity : AppCompatActivity() {
    companion object {
        val recipe: Recipe = Recipe(imageResource = R.drawable.header, title = "Random Recipie", ingredients = listOf("Potato", "Cheese", "Cake", "Fruits", "Spinach", "Salt"))
        val defaultRecipes = mutableListOf<Recipe>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // JetpackCompose2Theme looks great in dark mode as well
        setContent { JetpackCompose2Theme { UI() } }
    }
}

@Composable
fun UI() {
    // A surface container using the 'background' color from the theme
    Surface(color = MaterialTheme.colors.background) {
        for (i in 0..10) defaultRecipes.add(index = i, element = recipe)
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(title = { Text("Fav Recipes") })
            RecipeList(recipes = defaultRecipes)
        }
    }
}

@Preview(showBackground = false)
@Composable
fun DefaultPreview() = JetpackCompose2Theme { UI() }