package com.example.corewallet.models

data class Goal(
    val id_goal: Int,
    val goal_name: String,
    val target_amount: Long,
    val saved_amount: Long,
    val deadline: String,
    val status: Int
)