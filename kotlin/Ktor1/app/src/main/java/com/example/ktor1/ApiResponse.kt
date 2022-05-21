package com.example.ktor1

import io.ktor.client.statement.*

class ApiResponse(
    response: HttpResponse?,
    error: String
) {
    val httpResponse = response
    val errorMessage = error
}