package com.ecom.app.network

import android.content.Context
import android.util.Log
import com.ecom.app.BuildConfig
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = BuildConfig.BASE_URL

    private lateinit var cookieJar: PersistentCookieJar

    fun init(context: Context) {
        cookieJar = PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(context)
        )
    }

    private fun baseHttpUrl(): HttpUrl {
        return HttpUrl.Builder()
            .scheme(if (BASE_URL.startsWith("https")) "https" else "http")
            .host(
                BASE_URL
                    .removePrefix("https://")
                    .removePrefix("http://")
                    .trimEnd('/')
            )
            .build()
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    class GetRetryInterceptor(
        private val maxRetries: Int = 3,
        private val retryDelayMs: Long = 5_000L
    ) : Interceptor {

        private val retryStatusCodes = setOf(
            408, // Request Timeout
            429, // Too Many Requests
            500,
            502,
            503,
            504
        )

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            if (request.method != "GET") {
                return chain.proceed(request)
            }

            var attempt = 0
            var lastException: IOException? = null

            while (attempt <= maxRetries) {
                try {
                    val response = chain.proceed(request)

                    if (
                        response.isSuccessful ||
                        response.code !in retryStatusCodes
                    ) {
                        return response
                    }

                    response.close()

                } catch (e: SocketTimeoutException) {
                    lastException = e

                    Log.e(
                        "API_RETRY",
                        "Socket timeout for ${request.url}",
                        e
                    )

                } catch (e: IOException) {
                    lastException = e

                    Log.e(
                        "API_RETRY",
                        "IO failure for ${request.url}",
                        e
                    )
                }

                attempt++

                if (attempt <= maxRetries) {
                    Log.w(
                        "API_RETRY",
                        "Retry ${attempt}/$maxRetries for ${request.url}"
                    )

                    Thread.sleep(retryDelayMs)
                }
            }

            throw lastException ?: IOException("GET request failed after retries")
        }
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(cookieJar)

            // This decides when a request is considered failed.
            // Slow images are not affected here; this is only Retrofit API calls.
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)

            .addInterceptor(GetRetryInterceptor(
                maxRetries = 3,
                retryDelayMs = 5_000L
            ))
            .addInterceptor(loggingInterceptor)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    fun getCsrfToken(): String? {
        return cookieJar.loadForRequest(baseHttpUrl())
            .firstOrNull { it.name == "csrftoken" }
            ?.value
    }

    fun getCookieHeader(): String? {
        val cookies = cookieJar.loadForRequest(baseHttpUrl())

        if (cookies.isEmpty()) return null

        return cookies.joinToString("; ") { cookie ->
            "${cookie.name}=${cookie.value}"
        }
    }
}