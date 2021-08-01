package com.singularitycoder.composable1

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.singularitycoder.composable1.ui.theme.Composable1Theme

// https://developer.android.com/jetpack/compose/tutorial

/**
 * Fragment
 * Constraint Layout
 * Dialog
 * Add Padding
 * Add Margin
 **/

// Extends ComponentActivity instead of AppCompatActivity
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Composable1Theme {
                // Complex UI is built through nested composable functions
                Conversation(SampleData.conversationSample)
            }
        }
    }
}

// Composable functions can only be called from other composable functions. Like suspend functions in coroutines
// Composable functions start with capital letters. Convention
// Modifiers are like fine controls of views. Like padding, margin, size, appearance, adding click actions, etc
@Composable
fun MessageCard(msg: Message) {
    // Add padding around our message
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.profile_picture),
            contentDescription = "Contact profile picture",
            modifier = Modifier
                .size(size = 40.dp)    // Set image size to 40 dp
                .clip(shape = CircleShape)  // Clip image to be shaped as a circle
                .border(width = 1.5.dp, color = MaterialTheme.colors.secondary, shape = CircleShape)
        )

        // Add a horizontal space between the image and the column. You can also add padding to each of the Texts
        Spacer(modifier = Modifier.width(8.dp))

        // Composable functions can store local state in memory by using remember, and track changes to the value passed to mutableStateOf. Composables (and its children) using this state will get redrawn automatically when the value is updated. We call this recomposition.
        // We keep track if the message is expanded or not in this variable. This has to be within Row. remember & mutableStateOf functions keep track of the local state changes. They are part of compose state APIs
        // any changes to state automatically update the UI
        var isExpanded by remember { mutableStateOf(false) }
        // surfaceColor will be updated gradually from one color to the other
        val surfaceColor: Color by animateColorAsState(
            if (isExpanded) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
        )
        // We toggle the isExpanded variable when we click on this Column
        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(text = msg.author, color = MaterialTheme.colors.secondaryVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = MaterialTheme.shapes.medium,
                elevation = 1.dp,
                // surfaceColor color will be changing gradually from primary to surface
                color = surfaceColor,
                // animateContentSize will change the Surface size gradually
                modifier = Modifier
                    .animateContentSize()
                    .padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    // If the message is expanded, we display all its content otherwise we only display the first line
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.body2
                )
            }
        }
    }
}

@Composable
fun Conversation(messages: List<Message>) {
    LazyColumn {
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@Preview
@Composable
fun PreviewConversation() {
    Composable1Theme {
        Conversation(SampleData.conversationSample)
    }
}

// Preview functions cannot have params
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Composable1Theme {
        MessageCard(msg = Message("Colleague", "Hey, take a look at Jetpack Compose, it's great!"))
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Dark Mode"
)
@Composable
fun DarkModePreview() {
    Composable1Theme {
        MessageCard(msg = Message("Colleague", "Hey, take a look at Jetpack Compose, it's great!"))
    }
}

data class Message(val author: String, val body: String)





















