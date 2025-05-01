package com.example.corewallet.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/transactions/topup")
    fun topUp(@Body request: TopupRequest): Call<TopupResponse>


}
