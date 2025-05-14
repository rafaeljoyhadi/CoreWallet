package com.example.corewallet.models

data class Budget(
    val id_budget: Int,
    val plan_name: String,
    val amount_limit: Long,
    val spent_amount: Long,
    val start_date: String,
    val end_date: String,
    val category_number: Int
)