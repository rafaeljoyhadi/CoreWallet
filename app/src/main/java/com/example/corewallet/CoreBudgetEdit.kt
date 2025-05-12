package com.example.corewallet

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class CoreBudgetEdit : AppCompatActivity() {
    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget_edit)
        window.statusBarColor = Color.parseColor("#0d5892")

        // Inisialisasi kalender untuk tanggal default
        val calendar = Calendar.getInstance()
        var selectedYear = calendar.get(Calendar.YEAR)
        var selectedMonth = calendar.get(Calendar.MONTH)
        var selectedDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Mengakses elemen UI
        val buttonPickDateStart = findViewById<View>(R.id.buttonPickDateStart)
        val buttonPickDateEnd = findViewById<View>(R.id.buttonPickDateEnd)
        val tvSelectedDateStart = findViewById<TextView>(R.id.tvSelectedDateStart)
        val tvSelectedDateEnd = findViewById<TextView>(R.id.tvSelectedDateEnd)

        // Listener Start Date
        buttonPickDateStart.setOnClickListener {
            // Tampilkan DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Simpan tanggal yang dipilih
                    selectedYear = year
                    selectedMonth = month
                    selectedDay = dayOfMonth

                    // Format tanggal ke string
                    val formattedDate = "$dayOfMonth/${month + 1}/$year"

                    // Tampilkan tanggal di TextView
                    tvSelectedDateStart.text = "Tanggal terpilih: $formattedDate"
                    tvSelectedDateStart.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                },
                selectedYear,
                selectedMonth,
                selectedDay
            )
            datePickerDialog.show()
        }

        // Listener End Date
        buttonPickDateEnd.setOnClickListener {
            // Tampilkan DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Simpan tanggal yang dipilih
                    selectedYear = year
                    selectedMonth = month
                    selectedDay = dayOfMonth

                    // Format tanggal ke string
                    val formattedDate = "$dayOfMonth/${month + 1}/$year"

                    // Tampilkan tanggal di TextView
                    tvSelectedDateEnd.text = "Tanggal terpilih: $formattedDate"
                    tvSelectedDateEnd.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                },
                selectedYear,
                selectedMonth,
                selectedDay
            )
            datePickerDialog.show()
        }

        // Terima data dari Server
        val shoppingCategory = intent.getStringExtra("shoppingCategory")
        val shoppingAmount = intent.getStringExtra("shoppingAmount")
        val shoppingProgress = intent.getIntExtra("shoppingProgress", 0)

        val transportationCategory = intent.getStringExtra("transportationCategory")
        val transportationAmount = intent.getStringExtra("transportationAmount")
        val transportationProgress = intent.getIntExtra("transportationProgress", 0)

        // Isi data ke TextView dan ProgressBar
        val tvGoalPlanName = findViewById<TextView>(R.id.etGoalPlanName)
        val tvShoppingAmount = findViewById<TextView>(R.id.etBudgetAmount)

        // Set nilai untuk Shopping
        tvGoalPlanName.text = shoppingCategory
        tvShoppingAmount.text = shoppingAmount
    }

}
