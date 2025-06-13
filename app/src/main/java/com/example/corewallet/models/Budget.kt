import com.google.gson.annotations.SerializedName

data class Budget(
    val id_budget: Int,

    @SerializedName(value = "plan_name", alternate = ["budget_name"])
    val budget_name: String,

    val amount_limit: Long,
    val spent_amount: Long,
    val start_date: String,
    val end_date: String,
    val id_category: Int,
    val category_name: String? = null,
    val status: Int
)