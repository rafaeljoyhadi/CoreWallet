package com.example.corewallet.api

import com.example.corewallet.models.Users
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface UserApi {
    @GET("user/{id}")
    fun getUserById(@Path("id") userId: Int): Call<Users>
}