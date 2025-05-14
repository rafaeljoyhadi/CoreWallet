package com.example.corewallet.api

import com.example.corewallet.models.Budget
import com.example.corewallet.models.Users
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @GET("user/{id}")
    fun getUserById(@Path("id") userId: Int): Call<Users>

    //Sementara untuk hit API PUT
    @PUT("budgets/{id}") // Ganti dengan endpoint API Anda
    fun updateBudget(
        @Path("id") id: Int,
        @Body budgetUpdateRequest: Budget
    ): Call<Void> // Menggunakan Void jika server tidak mengembalikan respons

}