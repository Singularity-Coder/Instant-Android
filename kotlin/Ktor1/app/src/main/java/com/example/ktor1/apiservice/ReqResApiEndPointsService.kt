package com.example.ktor1.apiservice

import com.example.ktor1.model.ReqResRequest
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import okhttp3.MultipartBody

class ReqResApiEndPointsService(private val client: HttpClient) {

    suspend fun getUserList(pageCount: Int): HttpResponse {
        return client.get("https://reqres.in/api/users") {
            url {
                parameters.append("page", pageCount.toString())
            }
        }
    }

    suspend fun getUser(userId: Int): HttpResponse {
        return client.get("https://reqres.in/api/users/$userId")
    }

    suspend fun createUser(requestPayload: ReqResRequest): HttpResponse {
        return client.post("https://reqres.in/api/users") {
            contentType(ContentType.Application.Json)
            setBody(requestPayload)
        }
    }

    suspend fun updateUser(
        requestPayload: ReqResRequest,
        userId: Int,
    ): HttpResponse {
        return client.put("https://reqres.in/api/users/$userId") {
            contentType(ContentType.Application.Json)
            setBody(requestPayload)
        }
    }

    suspend fun deleteUser(userId: Int): HttpResponse {
        return client.delete("https://reqres.in/api/users/$userId")
    }

    suspend fun uploadFile(
        formBuilder: FormBuilder.() -> Unit,
        multipartBody: MultipartBody
    ): HttpResponse {
        return client.post("https://reqres.in/api/users") {
            setBody(multipartBody) // This makes the call but its sending data in byte stream instead of file even though i am sending file
            // setBody(MultiPartFormDataContent(formData(formBuilder))) // This doesnt work probably because the engine is OkHttp
            onUpload { bytesSentTotal, contentLength ->
                println("Sent $bytesSentTotal bytes, content length $contentLength")
            }
        }
    }
}