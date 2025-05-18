import com.google.gson.annotations.SerializedName

data class TransferResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("sender_new_balance")
    val senderNewBalance: Double,

    @SerializedName("recipient_new_balance")
    val recipientNewBalance: Double,

    @SerializedName("transaction_id")
    val transactionId: Long,

    @SerializedName("transaction")
    val transaction: TransactionDetail
)

data class TransactionDetail(
    @SerializedName("id_transaction")
    val idTransaction: Int,

    @SerializedName("datetime")
    val datetime: String,

    @SerializedName("source_id_user")
    val sourceIdUser: Int,

    @SerializedName("target_id_user")
    val targetIdUser: Int,

    @SerializedName("id_category")
    val idCategory: Int,

    @SerializedName("transaction_category_name")
    val transactionCategoryName: String,

    @SerializedName("is_credit")
    val isCredit: Int,

    @SerializedName("status")
    val status: Int,

    @SerializedName("amount")
    val amount: Double,

    @SerializedName("note")
    val note: String
)
