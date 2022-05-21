package com.example.ktor1

import android.content.Context
import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import okhttp3.CipherSuite.Companion.TLS_DHE_RSA_WITH_AES_128_CBC_SHA
import okhttp3.CipherSuite.Companion.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
import okhttp3.CipherSuite.Companion.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256
import okhttp3.CipherSuite.Companion.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
import okhttp3.CipherSuite.Companion.TLS_RSA_WITH_3DES_EDE_CBC_SHA
import okhttp3.CipherSuite.Companion.TLS_RSA_WITH_AES_128_CBC_SHA
import okhttp3.ConnectionSpec
import okhttp3.TlsVersion
import java.text.DateFormat
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun injectGson(): Gson = Gson()

    @Singleton
    @Provides
    fun injectApiService(client: HttpClient): ApiEndPointsService {
        return ApiEndPointsService(client)
    }

    @Singleton
    @Provides
    fun injectKtorOkHttpClient(@ApplicationContext context: Context): HttpClient {
        // To handle this exception - javax.net.ssl.SSLHandshakeException - https://stackoverflow.com/questions/41821569/javax-net-ssl-sslhandshakeexception-connection-closed-by-peer-at-com-android-or
        val spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
            .tlsVersions(TlsVersion.TLS_1_0)
            .cipherSuites(
                TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
                TLS_DHE_RSA_WITH_AES_128_CBC_SHA,
                TLS_RSA_WITH_AES_128_CBC_SHA,
                TLS_RSA_WITH_3DES_EDE_CBC_SHA
            )
            .build()
        return HttpClient(OkHttp) {
            engine {
                config {
                    followRedirects(true)
                    readTimeout(1, TimeUnit.MINUTES)
                    writeTimeout(3, TimeUnit.MINUTES)
                    connectTimeout(2, TimeUnit.MINUTES)
//                    sslSocketFactory(SslSettings.getSslContext()!!.socketFactory, SslSettings.getTrustManager())
//                    connectionSpecs(Collections.singletonList(spec))
//                    protocols(listOf(Protocol.HTTP_1_1))
//                    authenticator(Authenticator { route, response -> Request.Builder().build() })
                }

//                addInterceptor(Interceptor { Response.Builder().build() })
                addNetworkInterceptor(StethoInterceptor())
            }
            install(ContentNegotiation) {
                gson {
                    setLenient()
                    setPrettyPrinting()
                    setDateFormat(DateFormat.LONG)
                    addDeserializationExclusionStrategy(DeserializationExclusionStrategy())
                    addSerializationExclusionStrategy(SerializationExclusionStrategy())
                }
            }
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.v("Logger Ktor =>", message)
                    }
                }
                level = LogLevel.ALL
            }
            install(ResponseObserver) {
                onResponse { response ->
                    Log.d("HTTP status:", "${response.status.value}")
                }
            }
            install(DefaultRequest) {
                host = "api.github.com"
                url {
                    protocol = URLProtocol.HTTPS
                }
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                header(HttpHeaders.Accept, ContentType.Application.Json)
            }
            HttpResponseValidator {
                validateResponse { response ->
                    if (response.status.value !in 200..299) {
                        val error: KtorError = response.body()
                        if (error.code != 0) {
                            throw CustomResponseException(response, "Code: ${error.code}, message: ${error.message}")
                        }
                    }
                }
                handleResponseExceptionWithRequest { exception, request ->
                    val clientException = exception as? ClientRequestException ?: return@handleResponseExceptionWithRequest
                    val exceptionResponse = exception.response
                    if (exceptionResponse.status == HttpStatusCode.NotFound) {
                        val exceptionResponseText = exceptionResponse.bodyAsText()
                        throw MissingPageException(exceptionResponse, exceptionResponseText)
                    }
                }
            }
        }
    }
}
