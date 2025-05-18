package com.example.corewallet.api

import com.google.gson.annotations.SerializedName

data class CreateBudgetRequest(
    @SerializedName("plan_name")    val planName: String,
    @SerializedName("id_category")  val idCategory: Int,
    @SerializedName("amount_limit") val amountLimit: Long,
    @SerializedName("start_date")   val startDate: String,
    @SerializedName("end_date")     val endDate: String
)
