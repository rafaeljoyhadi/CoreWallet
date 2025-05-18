package com.example.corewallet.api

import com.google.gson.annotations.SerializedName

data class TransferRequest(
    @SerializedName("recipient_account_number")
    val recipientAccountNumber: String,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("note")
    val note: String?,

    val categoryName: String
)