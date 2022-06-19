package com.example.ktor1

import android.content.Context
import com.example.ktor1.apiservice.GithubApiEndPointsService
import com.example.ktor1.model.ApiError
import com.example.ktor1.model.GithubUser
import com.example.ktor1.utils.isOnline
import com.example.ktor1.utils.onFailure
import com.example.ktor1.utils.onSuccess
import com.google.gson.Gson
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class KtorRepository @Inject constructor(
    private val githubApiService: GithubApiEndPointsService,
    private val gson: Gson,
) {
    suspend fun getGithubUserList(context: Context): Flow<List<GithubUser>> = flow<List<GithubUser>> {
        if (!context.isOnline()) {
            println("Device is offline")
            return@flow
        }

        val httpResponse = try {
            githubApiService.getGithubUserList(userCount = 135)
        } catch (e: ResponseException) {
            println("Error: ${e.response.status.description}")
            e.response
        } catch (e: Exception) {
            println(e.message)
            null
        }

        httpResponse?.onSuccess { statusCode: Int ->
            val githubUserList: List<GithubUser> = httpResponse.body()
            emit(githubUserList)
            println("status code: $statusCode, response: ${gson.toJson(githubUserList)}")
        }

        httpResponse?.onFailure { statusCode: Int ->
            println("status code: $statusCode, error: ${httpResponse.bodyAsText()}")
        }
    }.flowOn(Dispatchers.IO)
}