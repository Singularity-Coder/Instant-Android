package com.example.ktor1

import io.ktor.client.plugins.*
import io.ktor.client.statement.*
import kotlinx.serialization.Serializable

@Serializable
data class KtorError(
    val code: Int,
    val message: String
)

class CustomResponseException(
    response: HttpResponse,
    cachedResponseText: String
) : ResponseException(response, cachedResponseText) {
    override val message: String = """
        Custom server error: ${response.call.request.url}
        Status: ${response.status}
        Text: $cachedResponseText
    """.trimIndent()
}

class MissingPageException(
    response: HttpResponse,
    cachedResponseText: String
) : ResponseException(response, cachedResponseText) {
    override val message: String = """
        Missing page: ${response.call.request.url}
        Status: ${response.status}
    """.trimIndent()
}
