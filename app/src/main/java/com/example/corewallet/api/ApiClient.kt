package com.example.corewallet.api


import android.annotation.SuppressLint
import com.example.corewallet.view.main.App
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
      const val BASE_URL = "http://10.0.2.2:3000/"

    @SuppressLint("StaticFieldLeak")
    private lateinit var cookieJar: SessionCookieJar

    private val okHttpClient by lazy {
        cookieJar = SessionCookieJar(App.instance)
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .cookieJar(cookieJar)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService = retrofit.create(AuthService::class.java)
    val apiService: ApiService = retrofit.create(ApiService::class.java)
    fun getCookieJar(): SessionCookieJar = cookieJar
}
