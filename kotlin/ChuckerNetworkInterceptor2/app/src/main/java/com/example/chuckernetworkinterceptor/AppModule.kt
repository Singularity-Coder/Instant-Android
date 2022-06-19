package com.example.chuckernetworkinterceptor

import android.content.Context
import android.util.Log
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
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
import okhttp3.Interceptor
import okhttp3.TlsVersion
import java.text.DateFormat
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun injectChuckerCollector(@ApplicationContext context: Context): ChuckerCollector {
        return ChuckerCollector(
            context = context,
            showNotification = true,
            retentionPeriod = RetentionManager.Period.ONE_WEEK // Period taken to retain the collected data
        )
    }

    @Singleton
    @Provides
    fun injectChuckerInterceptor(
        @ApplicationContext context: Context,
        chuckerCollector: ChuckerCollector
    ): Interceptor {
        return ChuckerInterceptor.Builder(context)
            .collector(chuckerCollector)
            .maxContentLength(250_000L) // The maximum body content length in bytes, after this responses will be truncated.
            .redactHeaders("Auth-Token", "Bearer") // List of headers to replace with ** in the Chucker UI
            .alwaysReadResponseBody(true) // Read the whole response body even when the client does not consume the response completely. This is useful in case of parsing errors or when the response body is closed before being read like in Retrofit with Void and Unit types.
            .build()
    }

    @Singleton
    @Provides
    fun injectKtorOkHttpClient(
        @ApplicationContext context: Context,
        chuckerInterceptor: Interceptor
    ): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                addInterceptor(chuckerInterceptor)
            }
            install(ContentNegotiation) {
                gson()
            }
            install(DefaultRequest) {
                host = BASE_HOST
                url {
                    protocol = URLProtocol.HTTPS
                }
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }
    }
}