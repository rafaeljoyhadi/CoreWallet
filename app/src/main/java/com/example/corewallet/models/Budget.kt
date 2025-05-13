package com.example.corewallet.models

data class Budget(
    val title: String,
    val date: String,
    val category: String,
    val amountSpent: Int,
    val amountTarget: Int
)

data class ApiResponse(
    val budgets: List<Budget>
)