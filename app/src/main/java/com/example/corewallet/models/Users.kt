package com.example.corewallet.models

data class Users(
    val id_user: Int,
    val username: String,
    val email: String,
    val name: String,
    val account_number: String,
    val balance: Double
)

