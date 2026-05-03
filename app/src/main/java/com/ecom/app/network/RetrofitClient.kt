package com.ecom.app.network

import com.ecom.app.BuildConfig
import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.CookieManager
import java.net.CookiePolicy

object RetrofitClient {

    private const val BASE_URL = BuildConfig.BASE_URL

    // Cookie manager (stores session + CSRF)
    private val cookieManager = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    // OkHttp with cookie support
    private val okHttpClient = OkHttpClient.Builder()
        .cookieJar(JavaNetCookieJar(cookieManager))
        .build()

    // Retrofit instance
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

    // Extract CSRF token from cookies
    fun getCsrfToken(): String? {
        return cookieManager.cookieStore.cookies
            .firstOrNull { it.name == "csrftoken" }
            ?.value
    }
}