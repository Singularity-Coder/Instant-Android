package com.example.ktor1

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class ApiEndPointsService(private val client: HttpClient) {

    suspend fun getGithubUsers(userCount: Int): HttpResponse {
        return client.get("users?since=$userCount")
    }

    suspend fun getGithubUsers2(userCount: Int): ApiResponse {
        return client.getOrNull("users?since=$userCount")
    }
}