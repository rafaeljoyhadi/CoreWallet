package com.example.corewallet.api

import SessionCookieJar
import android.annotation.SuppressLint
import android.content.Context
import com.example.corewallet.App
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:3000"

    @SuppressLint("StaticFieldLeak")
    private lateinit var cookieJar: SessionCookieJar

    private val okHttpClient: OkHttpClient by lazy {
        cookieJar = SessionCookieJar(App.instance) // Store reference
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .cookieJar(cookieJar)
            .build()
    }

    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }

    fun getCookieJar(): SessionCookieJar = cookieJar

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://yourapi.com/api/") // Ganti sesuai domain
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun getApiService(): ApiService = retrofit.create(ApiService::class.java)
}
