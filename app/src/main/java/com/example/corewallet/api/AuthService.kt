package com.example.corewallet.api

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.*
import com.google.gson.annotations.Expose

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
    @Expose
    @SerializedName("id_user") val id_user: Int,

    @Expose
    @SerializedName("username") val username: String,

    @Expose
    @SerializedName("name") val name: String,

    @Expose
    @SerializedName("account_number") val account_number: String,

    @Expose
    @SerializedName("balance") val balance: Double,

    @Expose
    @SerializedName("email") val email: String,

    @Expose
    @SerializedName("pin") val pin: String,

    @SerializedName("password") val password: String = ""

) {
    override fun toString(): String {
        return "User(id_user=$id_user, username='$username', name='$name', account_number='$account_number', balance=$balance, email='$email', pin='*****')"
    }
}

interface AuthService {
    @POST("/users/login")
    fun login(@Body request: LoginRequest): Call<UserResponse>

    @POST("/users")
    fun register(@Body request: RegisterRequest): Call<UserResponse>

    @POST("/users/logout")
    fun logout(): Call<UserResponse>

    @GET("users/{id}")
    fun getUserById(@Path("id") userId: Int): Call<User>

    @PUT("users/{id}")
    fun updateUser(@Path("id") userId: Int, @Body request: UpdateProfileRequest): Call<User>
}
