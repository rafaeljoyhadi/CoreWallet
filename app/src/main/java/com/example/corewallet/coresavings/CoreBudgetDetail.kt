package com.example.corewallet.coresavings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.corewallet.R
import com.example.corewallet.models.Budget
import java.text.NumberFormat
import java.util.Locale

class CoreBudgetDetail : AppCompatActivity() {

    private lateinit var budget: Budget

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget_detail) // Pastikan nama layout sesuai dengan file XML Anda
        window.statusBarColor = Color.parseColor("#0d5892")

        // Terima data dari Intent
        val idBudget = intent.getIntExtra("id_budget", 0)
        val budgetName = intent.getStringExtra("budgetName").orEmpty()
        val categoryNumber = intent.getIntExtra("category", 0)
        val amountLimit = intent.getLongExtra("amount", 0L)
        val spentAmount = intent.getLongExtra("spentAmount", 0L)
        val startDate = intent.getStringExtra("startDate").orEmpty()
        val endDate = intent.getStringExtra("endDate").orEmpty()

        // Inisialisasi elemen UI
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvDateRange = findViewById<TextView>(R.id.tvDateRange)
        val tvCategory = findViewById<TextView>(R.id.tvCategory)
        val tvAmount = findViewById<TextView>(R.id.tvAmount)
        val tvTargetAmount = findViewById<TextView>(R.id.tvTargetAmount)
        val progressBar = findViewById<android.widget.ProgressBar>(R.id.progressBar)

        // Format tanggal
        val formattedStartDate = formatDate(startDate)
        val formattedEndDate = formatDate(endDate)

        // Format jumlah uang
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedAmountLimit = formatter.format(amountLimit)
        val formattedSpentAmount = formatter.format(spentAmount)

        // Set nilai ke elemen UI
        tvTitle.text = budgetName.uppercase()
        tvDateRange.text = "$formattedStartDate - $formattedEndDate"

        //TODO : get category from DB
        // Mapping kategori
        val categories = listOf(
            "Top-Up",
            "Food",
            "Shopping",
            "Entertainment",
            "Bills",
            "Transfer",
            "Other/Miscellaneous"
        )
        val categoryName = if (categoryNumber in categories.indices) {
            categories[categoryNumber]
        } else {
            "Unknown"
        }
        tvCategory.text = categoryName

        tvAmount.text = "IDR $formattedSpentAmount"
        tvTargetAmount.text = "IDR $formattedAmountLimit"

        // Hitung progress bar
        val progress = if (amountLimit == 0L) 0 else
            ((spentAmount.toDouble() / amountLimit.toDouble()) * 100).toInt()
        progressBar.progress = progress.coerceAtMost(100)

        // Ubah warna progress bar jika sudah mencapai 100%
        if (progress >= 100) {
            progressBar.progressDrawable =
                ContextCompat.getDrawable(this, R.drawable.progress_bar_full)
            tvAmount.setTextColor(Color.parseColor("#FF0000"))
        } else {
            progressBar.progressDrawable =
                ContextCompat.getDrawable(this, R.drawable.progress_bar)
            tvAmount.setTextColor(Color.BLACK)
        }

        // Tombol back
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // Tombol edit
        val btnEdit = findViewById<ImageView>(R.id.btnEditBudget)
        btnEdit.setOnClickListener {
            val intent = Intent(this, CoreBudgetEdit::class.java)
            intent.putExtra("id_budget", idBudget)
            intent.putExtra("budgetName", budgetName)
            intent.putExtra("category", categoryName)
            intent.putExtra("amount", amountLimit)
            intent.putExtra("spentAmount", spentAmount)
            intent.putExtra("startDate", startDate)
            intent.putExtra("endDate", endDate)
            startActivity(intent)
        }

    }

    // Fungsi untuk memformat tanggal
    private fun formatDate(date: String): String {
        val parts = date.split("-")
        return if (parts.size == 3) {
            "${parts[2]} ${getMonthName(parts[1])} ${parts[0]}"
        } else {
            date
        }
    }

    // Fungsi untuk mendapatkan nama bulan dari angka
    private fun getMonthName(month: String): String {
        return when (month) {
            "01" -> "JANUARY"
            "02" -> "FEBRUARY"
            "03" -> "MARCH"
            "04" -> "APRIL"
            "05" -> "MAY"
            "06" -> "JUNE"
            "07" -> "JULY"
            "08" -> "AUGUST"
            "09" -> "SEPTEMBER"
            "10" -> "OCTOBER"
            "11" -> "NOVEMBER"
            "12" -> "DECEMBER"
            else -> ""
        }
    }
}