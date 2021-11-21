package com.singularitycoder.buildstuff

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.singularitycoder.buildstuff.ui.theme.BuildStuffTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { BuildStuffTheme { Surface(color = MaterialTheme.colors.background) { UI() } } }
    }

    @Composable
    private fun UI() = Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (BuildConfig.DEBUG) Greeting1(isDebug = true)
        when (BuildConfig.BUILD_TYPE) {
            "debug" -> Greeting2(buildType = "Debug")
            "release" -> Greeting2(buildType = "Release")
        }
        when (BuildConfig.FLAVOR) {
            "rnd" -> Greeting3(flavor = "Rnd")   // ./gradlew installRndDebug
            "dev" -> Greeting3(flavor = "Dev")   // ./gradlew installDevDebug
            "qa" -> Greeting3(flavor = "Qa")   // ./gradlew installQaDebug
            "prod" -> Greeting3(flavor = "Prod")   // ./gradlew installProdDebug
        }
    }
}

@Composable
fun Greeting1(isDebug: Boolean) = Text(text = "Is in debug mode: $isDebug")

@Composable
fun Greeting2(buildType: String) = Text(text = "Build Type is $buildType")

@Composable
fun Greeting3(flavor: String) = Text(text = "Build Flavor is $flavor")

@Preview(showBackground = true)
@Composable
fun DefaultPreview() = BuildStuffTheme { Greeting1(true) }