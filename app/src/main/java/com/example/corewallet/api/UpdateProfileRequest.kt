package com.example.corewallet.api

data class UpdateProfileRequest(
    val name: String?,
    val email: String?,
    val password: String?,
    val pin: String?,
    val username: String? = null,
    val image_path: String? = null
)
