package com.example.ktor1.apiservice

import com.example.ktor1.utils.ApiResponse
import com.example.ktor1.utils.getOrNull
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

class GithubApiEndPointsService(private val client: HttpClient) {

    suspend fun getGithubUserList(userCount: Int): HttpResponse {
        return client.get("users?since=$userCount")
    }

    suspend fun getGithubUserListWithOfflineFeature(userCount: Int): ApiResponse {
        return client.getOrNull("users?since=$userCount")
    }
}