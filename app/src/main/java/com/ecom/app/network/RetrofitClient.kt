package com.ecom.app.network

import android.content.Context
import com.ecom.app.BuildConfig
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
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

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cookieJar(cookieJar)
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
        val url = okhttp3.HttpUrl.Builder()
            .scheme(if (BASE_URL.startsWith("https")) "https" else "http")
            .host(
                BASE_URL
                    .removePrefix("https://")
                    .removePrefix("http://")
                    .trimEnd('/')
            )
            .build()

        return cookieJar.loadForRequest(url)
            .firstOrNull { it.name == "csrftoken" }
            ?.value
    }
}