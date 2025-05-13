package com.example.corewallet.coresavings

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.corewallet.R
import java.util.Calendar


class CoreGoalInput : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_goal_input)
        window.statusBarColor = Color.parseColor("#0d5892")

        // Inisialisasi kalender untuk tanggal default
        val calendar = Calendar.getInstance()
        var selectedYear = calendar.get(Calendar.YEAR)
        var selectedMonth = calendar.get(Calendar.MONTH)
        var selectedDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Mengakses elemen UI
        val targetDate = findViewById<View>(R.id.buttonTargetInputDate)
        val tvTargetDate = findViewById<TextView>(R.id.tvTargetInputDate)

        // Listener Start Date
        targetDate.setOnClickListener {
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
                    tvTargetDate.text = "Tanggal terpilih: $formattedDate"
                    tvTargetDate.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                },
                selectedYear,
                selectedMonth,
                selectedDay
            )
            datePickerDialog.show()
        }
    }
}