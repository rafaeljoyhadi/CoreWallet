package com.example.corewallet.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class LoginRequest(val username: String, val password: String)

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val name: String,
    val pin: String
)

data class UserResponse(
    val message: String,
    val user: User?,
)

data class User(
    val id_user: Int,
    val username: String,
    val name: String,
    val account_number: String,
    // added
    val balance: Int,
    val email: String
)

interface AuthService {
    @POST("/users/login")
    fun login(@Body request: LoginRequest): Call<UserResponse>

    @POST("/users")
    fun register(@Body request: RegisterRequest): Call<UserResponse>

    @POST("/users/logout")
    fun logout(): Call<UserResponse>

    @PUT("/api/users/profile")
    fun updateProfile(@Body request: UpdateProfileRequest): Call<ResponseBody>

    @GET("user/{id}")
    fun getUserById(@Path("id") userId: Int): Call<User>

}
