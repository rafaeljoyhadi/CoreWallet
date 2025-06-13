package com.example.corewallet.api.request

import com.google.gson.annotations.SerializedName

data class UpdateGoalRequest(
    @SerializedName("goal_name")
    val goalName: String?,

    @SerializedName("target_amount")
    val targetAmount: Long?,

    @SerializedName("deadline")
    val deadline: String?
)