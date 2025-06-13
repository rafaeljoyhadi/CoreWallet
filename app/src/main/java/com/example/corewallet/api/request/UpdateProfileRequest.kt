package com.example.corewallet.api.request

data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val pin: String,
    val password: String? = null,
    val image_path: String? = null
)