package com.example.corewallet

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.Calendar

class CoreBudgetEdit : AppCompatActivity() {
    // Variabel untuk menyimpan Start Date
    private var startDateYear = 0
    private var startDateMonth = 0
    private var startDateDay = 0

    // Variabel untuk menyimpan End Date
    private var endDateYear = 0
    private var endDateMonth = 0
    private var endDateDay = 0

    // Fungsi untuk membandingkan tanggal
    private fun isEndDateValid(
        startYear: Int,
        startMonth: Int,
        startDay: Int,
        endYear: Int,
        endMonth: Int,
        endDay: Int
    ): Boolean {
        return when {
            endYear > startYear -> true
            endYear == startYear && endMonth > startMonth -> true
            endYear == startYear && endMonth == startMonth && endDay > startDay -> true
            else -> false
        }
    }

    fun Int.ifZero(defaultValue: () -> Int): Int {
        return if (this == 0) defaultValue() else this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget_edit)
        window.statusBarColor = Color.parseColor("#0d5892")

        // Mengakses elemen UI
        val buttonPickDateStart = findViewById<View>(R.id.buttonPickDateStart)
        val buttonPickDateEnd = findViewById<View>(R.id.buttonPickDateEnd)
        val tvSelectedDateStart = findViewById<TextView>(R.id.tvSelectedDateStart)
        val tvSelectedDateEnd = findViewById<TextView>(R.id.tvSelectedDateEnd)

        // Listener untuk Start Date
        buttonPickDateStart.setOnClickListener {
            // Tampilkan DatePickerDialog untuk Start Date
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Simpan tanggal yang dipilih untuk Start Date
                    startDateYear = year
                    startDateMonth = month
                    startDateDay = dayOfMonth

                    // Format tanggal ke string
                    val formattedDate = "$dayOfMonth/${month + 1}/$year"

                    // Tampilkan tanggal di TextView untuk Start Date
                    tvSelectedDateStart.text = "Tanggal Awal: $formattedDate"
                    tvSelectedDateStart.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                },
                startDateYear.ifZero { Calendar.getInstance().get(Calendar.YEAR) }, // Default tahun saat ini
                startDateMonth.ifZero { Calendar.getInstance().get(Calendar.MONTH) }, // Default bulan saat ini
                startDateDay.ifZero { Calendar.getInstance().get(Calendar.DAY_OF_MONTH) } // Default hari saat ini
            )
            datePickerDialog.show()
        }

// Listener untuk End Date
        buttonPickDateEnd.setOnClickListener {
            // Tampilkan DatePickerDialog untuk End Date
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Simpan tanggal yang dipilih untuk End Date
                    endDateYear = year
                    endDateMonth = month
                    endDateDay = dayOfMonth

                    // Validasi: Pastikan Start Date sudah dipilih
                    if (startDateYear == 0 || startDateMonth == 0 || startDateDay == 0) {
                        Toast.makeText(this, "Pilih Tanggal Awal terlebih dahulu", Toast.LENGTH_SHORT).show()
                        return@DatePickerDialog
                    }

                    // Bandingkan tanggal
                    if (isEndDateValid(startDateYear, startDateMonth, startDateDay, year, month, dayOfMonth)) {
                        // Format tanggal ke string
                        val formattedDate = "$dayOfMonth/${month + 1}/$year"

                        // Tampilkan tanggal di TextView untuk End Date
                        tvSelectedDateEnd.text = "Tanggal Akhir: $formattedDate"
                        tvSelectedDateEnd.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                    } else {
                        MotionToast.createColorToast(this,
                            "Failed ☹️",
                            "Tanggal tidak boleh sebelum tanggal mulai!",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular))
                    }
                },
                endDateYear.ifZero { Calendar.getInstance().get(Calendar.YEAR) }, // Default tahun saat ini
                endDateMonth.ifZero { Calendar.getInstance().get(Calendar.MONTH) }, // Default bulan saat ini
                endDateDay.ifZero { Calendar.getInstance().get(Calendar.DAY_OF_MONTH) } // Default hari saat ini
            )
            datePickerDialog.show()
        }

        // Terima data dari Server
        val shoppingCategory = intent.getStringExtra("shoppingCategory")
        val shoppingAmount = intent.getStringExtra("shoppingAmount")
        val shoppingProgress = intent.getIntExtra("shoppingProgress", 0)

        // Isi data ke TextView dan ProgressBar
        val tvGoalPlanName = findViewById<TextView>(R.id.etGoalPlanName)
        val tvShoppingAmount = findViewById<TextView>(R.id.etBudgetAmount)

        // Set nilai untuk Shopping
        tvGoalPlanName.text = shoppingCategory
        tvShoppingAmount.text = shoppingAmount

        //Button Save dan Kembali ke CoreBudget
        val btnSave = findViewById<Button>(R.id.btnSave_CoreBudgetEdit)
        btnSave.setOnClickListener {
            MotionToast.createColorToast(this, "Upload Berhasil!", "Data berhasil disimpan",
                MotionToastStyle.SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.LONG_DURATION,
                ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
            )

            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, CoreBudget::class.java)
                startActivity(intent)
            }, 2000)
        }
    }
}
