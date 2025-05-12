package com.example.corewallet.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("/transactions/topup")
    fun topUp(@Body request: TopupRequest): Call<TopupResponse>

    @PUT("/api/users/{id}")
    fun updateProfile(
        @Path("id") id: Int,
        @Body request: UpdateProfileRequest
    ): Call<ResponseBody> // using ResponseBody so you don’t need BasicResponse.kt

}
