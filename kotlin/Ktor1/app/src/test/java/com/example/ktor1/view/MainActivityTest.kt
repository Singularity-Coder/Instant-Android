package com.example.ktor1.view

import com.example.ktor1.model.ReqResUser
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.utils.io.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

// Not working. Looks like engine issue again.
class MainActivityTest {

    @Test
    fun sampleClientTest() {
        runBlocking {
            val mockEngine = MockEngine { request ->
                respond(
                    content = ByteReadChannel("""{ "data": { "id": 2, "email": "janet.weaver@reqres.in", "first_name": "Janet", "last_name": "Weaver", "avatar": "https://reqres.in/img/faces/2-image.jpg" }, "support": { "url": "https://reqres.in/#support-heading", "text": "To keep ReqRes free, contributions towards server costs are appreciated!" } }"""),
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/json")
                )
            }
            val apiClient = ApiClient(mockEngine)

            assertEquals("janet.weaver@reqres.in", apiClient.getUser(userId = 2).email)
        }
    }

    class ApiClient(engine: HttpClientEngine) {
        private val httpClient = HttpClient(engine) {
            install(ContentNegotiation) {
                gson()
            }
        }

        suspend fun getUser(userId: Int): ReqResUser = httpClient.get("https://reqres.in/api/users/$userId").body()
    }
}