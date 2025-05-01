package com.example.corewallet.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val username: String, val password: String)

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String,
    val name: String,
    val pin: String
)

data class UserResponse(val message: String, val user: User? = null)

data class User(
    val id_user: Int,
    val username: String,
    val name: String,
    val account_number: String
)

interface AuthService {
    @POST("/users/login")
    fun login(@Body request: LoginRequest): Call<UserResponse>

    @POST("/users")
    fun register(@Body request: RegisterRequest): Call<UserResponse>
}
