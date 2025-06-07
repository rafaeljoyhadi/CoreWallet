package com.example.corewallet.api.request

import com.google.gson.annotations.SerializedName

data class AddContactRequest(
    @SerializedName("account_number")
    val accountNumber: String
)