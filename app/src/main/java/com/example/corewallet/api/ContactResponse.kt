package com.example.corewallet.api

import com.google.gson.annotations.SerializedName

data class ContactResponse(
    @SerializedName("saved_id_user") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("account_number") val accountNumber: String,
    @SerializedName("profile_picture") val profilePicture: String?,   // Optional
    @SerializedName("balance") val balance: Double                    // Optional
)