package com.example.corewallet.api


import com.example.corewallet.api.response.TransferResponse
import Budget
import com.example.corewallet.api.request.AddContactRequest
import com.example.corewallet.api.request.AmountRequest
import com.example.corewallet.api.request.CreateBudgetRequest
import com.example.corewallet.api.request.NewGoalRequest
import com.example.corewallet.api.request.TopupRequest
import com.example.corewallet.api.request.TransferRequest
import com.example.corewallet.api.request.UpdateBudgetRequest
import com.example.corewallet.api.request.UpdateGoalRequest
import com.example.corewallet.api.response.AddContactResponse
import com.example.corewallet.api.response.ContactResponse
import com.example.corewallet.api.response.TopupResponse
import com.example.corewallet.api.response.TransactionResponse
import com.example.corewallet.models.Goal
import com.example.corewallet.models.TransactionCategory
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    // Transactions API
    @POST("/transactions/topup")
    fun topUp(@Body request: TopupRequest): Call<TopupResponse>

    @GET("/transactions/history")
    suspend fun getTransactionHistory(): Response<List<TransactionResponse>>

    @GET("/transactions/categories")
    fun getTransactionCategories(
    ): Call<List<TransactionCategory>>

    @POST("/transactions/transfer")
    fun transferToUser(@Body request: TransferRequest): Call<TransferResponse>

    // Contacts API
    @GET("contact")
    fun getContacts(): Call<List<ContactResponse>>

    @POST("contact")
    fun addContact(
        @Body req: AddContactRequest
    ): Call<AddContactResponse>

    // CoreBudget
    @GET("/budget")
    suspend fun getBudgetPlans(): Response<List<Budget>>

    @PUT("/budget/{id}")
    fun updateBudget(
        @Path("id") id: Int,
        @Body request: UpdateBudgetRequest
    ): Call<Void>

    @DELETE("/budget/{id}")
    suspend fun deleteBudget(@Path("id") id: Int): Response<Unit>

    @POST("/budget")
    fun createBudgetPlan(@Body request: CreateBudgetRequest): Call<Void>

    // CoreGoal
    @GET("/goal")
    suspend fun getGoalPlans(): Response<List<Goal>>

    @POST("/goal")
    suspend fun createGoal(
        @Body body: NewGoalRequest
    ): Response<Goal>

    @PUT("/goal/{id}")
    suspend fun updateGoal(
        @Path("id") id: Int,
        @Body body: UpdateGoalRequest
    ): Response<Goal>

    @DELETE("/goal/{id}")
    suspend fun deleteGoal(
        @Path("id") id: Int
    ): Response<Unit>

    @POST("/goal/{id}/deposit")
    suspend fun depositToGoal(
        @Path("id") id: Int,
        @Body body: AmountRequest
    ): Response<Goal>

    @POST("/goal/{id}/withdraw")
    suspend fun withdrawFromGoal(
        @Path("id") id: Int,
        @Body body: AmountRequest
    ): Response<Goal>
}
