package com.example.corewallet.coresavings

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.corewallet.R
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.Calendar

class CoreBudgetInput : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget_input)
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
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedYear = year
                    selectedMonth = month
                    selectedDay = dayOfMonth

                    // Format tanggal ke string dan input ke TextView
                    val formattedDate = "$dayOfMonth/${month + 1}/$year"
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
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    selectedYear = year
                    selectedMonth = month
                    selectedDay = dayOfMonth

                    // Format tanggal ke string dan input ke TextView
                    val formattedDate = "$dayOfMonth/${month + 1}/$year"
                    tvSelectedDateEnd.text = "Tanggal terpilih: $formattedDate"
                    tvSelectedDateEnd.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                },
                selectedYear,
                selectedMonth,
                selectedDay
            )
            datePickerDialog.show()
        }

        //Untuk Spinner Category
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val categories = listOf(
            "Top-Up",
            "Food",
            "Shopping",
            "Entertainment",
            "Bills",
            "Transfer",
            "Other/Miscellaneous"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        // Tombol back
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed() // Simulasikan tombol back
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        //Button Save dan Kembali ke CoreBudget
        val btnSave = findViewById<Button>(R.id.btnSave_CoreBudgetInput)
        btnSave.setOnClickListener {
            MotionToast.createColorToast(
                this, "Upload Berhasil!", "Data berhasil disimpan",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)

                //Tambahin API buat nembak ja
                //Ambil id_budget, budget_name, amount_limit, start_date, end_date
            )

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, CoreBudget::class.java)
                startActivity(intent)
            }, 2000)
        }
    }
}
