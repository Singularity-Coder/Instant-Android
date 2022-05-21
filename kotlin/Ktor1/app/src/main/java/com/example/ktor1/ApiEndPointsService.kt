package com.example.ktor1

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class ApiEndPointsService(private val client: HttpClient) {

    suspend fun getGithubUsers(userCount: Int): HttpResponse {
        return client.get("users?since=$userCount")
    }
}
