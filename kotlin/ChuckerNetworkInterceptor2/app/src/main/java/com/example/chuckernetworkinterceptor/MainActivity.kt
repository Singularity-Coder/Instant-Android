package com.example.chuckernetworkinterceptor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.chuckernetworkinterceptor.ui.theme.ChuckerNetworkInterceptorTheme
import dagger.hilt.android.AndroidEntryPoint
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import okhttp3.*
import javax.inject.Inject

// TODO Module

// https://github.com/ChuckerTeam/chucker
// https://www.section.io/engineering-education/debugging-with-chucker/
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var httpClient: HttpClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChuckerNetworkInterceptorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    UI(httpClient)
                }
            }
        }
    }
}

@Composable
fun UI(httpClient: HttpClient) {
    val responseStringState = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                coroutineScope.launch {
                    responseStringState.value = httpClient.get("elephants").bodyAsText()
                }
            }) {
            Text(text = "Call API")
        }
        Text(text = responseStringState.value)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ChuckerNetworkInterceptorTheme {
        UI(HttpClient())
    }
}

const val BASE_HOST = "elephant-api.herokuapp.com"