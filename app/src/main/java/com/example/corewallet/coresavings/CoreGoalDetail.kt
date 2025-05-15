package com.example.corewallet.coresavings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.corewallet.R
import java.text.NumberFormat
import java.util.Locale

class CoreGoalDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_goal_detail) // Pastikan nama layout sesuai dengan file XML Anda
        window.statusBarColor = Color.parseColor("#0d5892")

        // Terima data dari Intent
        val idGoal = intent.getIntExtra("id_goal", 0)
        val goalName = intent.getStringExtra("goalName").orEmpty()
        val targetAmount = intent.getLongExtra("targetAmount", 0L)
        val savedAmount = intent.getLongExtra("savedAmount", 0L)
        val deadline = intent.getStringExtra("deadline").orEmpty()
        val status = intent.getIntExtra("status", 0)

        // Inisialisasi elemen UI
        val tvTitle = findViewById<TextView>(R.id.tvTitle)
        val tvDateRange = findViewById<TextView>(R.id.tvDeadline)
        val tvAmount = findViewById<TextView>(R.id.tvSavedAmount)
        val tvTargetAmount = findViewById<TextView>(R.id.tvTargetAmount)
        val progressBar = findViewById<android.widget.ProgressBar>(R.id.progressBar)

        // Format tanggal
        val formattedDeadline = formatDate(deadline)

        // Format jumlah uang
        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
        val formattedTargetAmount = formatter.format(targetAmount)
        val formattedSavedAmount = formatter.format(savedAmount)

        // Set nilai ke elemen UI
        tvTitle.text = goalName.uppercase()
        tvDateRange.text = "$formattedDeadline"

        tvAmount.text = "IDR $formattedSavedAmount"
        tvTargetAmount.text = "IDR $formattedTargetAmount"

        // Hitung progress bar
        val progress = if (targetAmount == 0L) 0 else
            ((savedAmount.toDouble() / targetAmount.toDouble()) * 100).toInt()
        progressBar.progress = progress.coerceAtMost(100)

        // Ubah warna progress bar jika tujuan selesai
        if (status == 1 || savedAmount >= targetAmount) {
            progressBar.progressDrawable =
                resources.getDrawable(R.drawable.progress_bar, null)
            tvAmount.setTextColor(Color.parseColor("#008000")) // Hijau untuk selesai
        } else {
            tvAmount.setTextColor(Color.BLACK)
        }

        // Tombol back
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed() // Simulasikan tombol back
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // Tombol edit
        val btnEdit = findViewById<ImageView>(R.id.btnEdit)
        btnEdit.setOnClickListener {
            val intent = Intent(this, CoreGoalEdit::class.java)
            intent.putExtra("id_goal", idGoal)
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