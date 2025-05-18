package com.example.corewallet.api

data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val pin: String,
    val password: String? = null
)