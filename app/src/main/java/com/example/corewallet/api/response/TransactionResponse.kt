package com.example.corewallet.api.response

data class TransactionResponse(
    val id_transaction: String,
    val datetime: String,            // ISO 8601
    val transaction_type: String,
    val amount: Double,
    val status: String,
    val note: String?,

    val category_name: String,

    val sender_username: String,
    val sender_account_number: String,

    val receiver_username: String,
    val receiver_account_number: String,

    val is_credit: Int
)
