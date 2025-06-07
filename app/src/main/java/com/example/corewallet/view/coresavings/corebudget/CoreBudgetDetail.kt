package com.example.corewallet.view.coresavings.corebudget

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.models.TransactionCategory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class CoreBudgetDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget_detail)
        window.statusBarColor = Color.parseColor("#0d5892")

        val idBudget = intent.getIntExtra("id_budget", 0)
        val budgetName = intent.getStringExtra("budgetName").orEmpty()
        val categoryId = intent.getIntExtra("category", -1)
        val amountLimit = intent.getLongExtra("amount", 0L)
        val spentAmount = intent.getLongExtra("spentAmount", 0L)
        val startDate = intent.getStringExtra("startDate").orEmpty()
        val endDate = intent.getStringExtra("endDate").orEmpty()

        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvDateRange = findViewById<TextView>(R.id.tvDateRange)
        val tvCategory = findViewById<TextView>(R.id.tvCategory)
        val tvAmount = findViewById<TextView>(R.id.tvAmount)
        val tvTargetAmount = findViewById<TextView>(R.id.tvTargetAmount)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnEdit = findViewById<ImageView>(R.id.btnEditBudget)

        // Set static fields
        tvTitle.text = budgetName.uppercase(Locale.getDefault())
        tvDateRange.text = formatDate(startDate) + " - " + formatDate(endDate)

        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        tvAmount.text = "IDR ${formatter.format(spentAmount)}"
        tvTargetAmount.text = "IDR ${formatter.format(amountLimit)}"

        val progress = if (amountLimit == 0L) 0 else ((spentAmount.toDouble() / amountLimit.toDouble()) * 100).toInt().coerceAtMost(100)
        progressBar.progress = progress

        if (progress >= 100) {
            progressBar.progressDrawable = ContextCompat.getDrawable(this, R.drawable.progress_bar_full)
            tvAmount.setTextColor(Color.RED)
        } else {
            progressBar.progressDrawable = ContextCompat.getDrawable(this, R.drawable.progress_bar)
            tvAmount.setTextColor(Color.BLACK)
        }

        // Fetch categories and map ID to name
        ApiClient.apiService.getTransactionCategories()
            .enqueue(object : Callback<List<TransactionCategory>> {
                override fun onResponse(call: Call<List<TransactionCategory>>, response: Response<List<TransactionCategory>>) {
                    val categories = response.body() ?: emptyList()
                    val matched = categories.find { it.id_category == categoryId }
                    tvCategory.text = matched?.category_name ?: "Unknown"
                }
                override fun onFailure(call: Call<List<TransactionCategory>>, t: Throwable) {
                    tvCategory.text = "Unknown"
                }
            })

        btnBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        btnEdit.setOnClickListener {
            Intent(this, CoreBudgetEdit::class.java).apply {
                putExtra("id_budget", idBudget)
                putExtra("budgetName", budgetName)
                putExtra("category", categoryId)
                putExtra("amount", amountLimit)
                putExtra("spentAmount", spentAmount)
                putExtra("startDate", startDate)
                putExtra("endDate", endDate)
                startActivity(this)
            }
        }
    }

    private fun formatDate(date: String): String {
        val parts = date.split("-")
        return if (parts.size == 3) {
            val month = parts[1].toIntOrNull() ?: return date
            val monthNames = arrayOf("JANUARY","FEBRUARY","MARCH","APRIL","MAY","JUNE","JULY","AUGUST","SEPTEMBER","OCTOBER","NOVEMBER","DECEMBER")
            "${parts[2]} ${monthNames[month - 1]} ${parts[0]}"
        } else date
    }
}
