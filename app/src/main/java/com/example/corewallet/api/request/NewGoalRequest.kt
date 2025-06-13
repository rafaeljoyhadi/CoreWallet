package com.example.corewallet.api.request

import com.google.gson.annotations.SerializedName

data class NewGoalRequest(
    @SerializedName("goal_name")
    val goalName: String,
    @SerializedName("target_amount")
    val targetAmount: Long,
    val deadline: String
)