package com.ecom.app.network

import android.content.Context
import com.ecom.app.BuildConfig
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = BuildConfig.BASE_URL

    private lateinit var cookieJar: PersistentCookieJar

    fun init(context: Context) {
        cookieJar = PersistentCookieJar(
            SetCookieCache(),
            SharedPrefsCookiePersistor(context)
        )
    }

    // -----------------------------------------
    // Base URL helper (reusable)
    // -----------------------------------------
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

    // -----------------------------------------
    // OkHttp client with persistent cookies
    // -----------------------------------------
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(cookieJar)
            .build()
    }

    // -----------------------------------------
    // Retrofit instance
    // -----------------------------------------
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

    // -----------------------------------------
    // CSRF Token (used for POST)
    // -----------------------------------------
    fun getCsrfToken(): String? {
        return cookieJar.loadForRequest(baseHttpUrl())
            .firstOrNull { it.name == "csrftoken" }
            ?.value
    }

    // -----------------------------------------
    // Cookie header for WebView
    // -----------------------------------------
    fun getCookieHeader(): String? {
        val cookies = cookieJar.loadForRequest(baseHttpUrl())

        if (cookies.isEmpty()) return null

        return cookies.joinToString("; ") { cookie ->
            "${cookie.name}=${cookie.value}"
        }
    }
}