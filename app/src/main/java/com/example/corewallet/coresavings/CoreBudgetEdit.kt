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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.corewallet.R
import com.example.corewallet.api.RetrofitClient
import com.example.corewallet.models.Budget
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

    //Fungsi pendek untuk memastikan tanggal tidak ber value 0
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
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    startDateYear = year
                    startDateMonth = month
                    startDateDay = dayOfMonth

                    // Format tanggal ke string dan input di TextView
                    val formattedDate = "$dayOfMonth/${month + 1}/$year"
                    tvSelectedDateStart.text = "Tanggal Awal: $formattedDate"
                    tvSelectedDateStart.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                },
                startDateYear.ifZero { Calendar.getInstance().get(Calendar.YEAR) },
                startDateMonth.ifZero { Calendar.getInstance().get(Calendar.MONTH) },
                startDateDay.ifZero { Calendar.getInstance().get(Calendar.DAY_OF_MONTH) }
            )
            datePickerDialog.show()
        }

        // Listener untuk End Date
        buttonPickDateEnd.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
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
                        val formattedDate = "$dayOfMonth/${month + 1}/$year"
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
        val id = intent.getIntExtra("id_budget", 0)
        val budgetName = intent.getStringExtra("budgetName")
        val category = intent.getIntExtra("category", 0)
        val amount = intent.getStringExtra("amount")
        val startDateString = intent.getStringExtra("startDate")
        val endDateString = intent.getStringExtra("endDate")

        // Isi data ke TextView
        val tvGoalPlanName = findViewById<TextView>(R.id.etBudgetPlanNameEdit)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val tvAmount = findViewById<TextView>(R.id.etBudgetAmountEdit)

        // Set nilai untuk TextView
        tvGoalPlanName.text = budgetName
        tvAmount.text = amount
        tvSelectedDateStart.text = startDateString
        tvSelectedDateEnd.text = endDateString

        val categoryIndex = category

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

        if (categoryIndex in 0 until categories.size) {
            spinnerCategory.setSelection(categoryIndex)
        } else {
            spinnerCategory.setSelection(0)
        }

        //Parsing Tanggal
        startDateString?.let {
            val parts = it.split("-")
            if (parts.size == 3) {
                startDateYear = parts[0].toInt()
                startDateMonth = parts[1].toInt() - 1
                startDateDay = parts[2].toInt()
                tvSelectedDateStart.text = "Tanggal Awal: ${startDateDay}/${startDateMonth + 1}/$startDateYear"
                tvSelectedDateStart.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            }
        }

        endDateString?.let {
            val parts = it.split("-")
            if (parts.size == 3) {
                endDateYear = parts[0].toInt()
                endDateMonth = parts[1].toInt() - 1
                endDateDay = parts[2].toInt()
                tvSelectedDateEnd.text = "Tanggal Akhir: ${endDateDay}/${endDateMonth + 1}/$endDateYear"
                tvSelectedDateEnd.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            }
        }

        // Tombol back
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        val btnSave = findViewById<Button>(R.id.btnSave_CoreBudgetEdit)
        btnSave.setOnClickListener {
            // Ambil nilai dari input user
            val updatedPlanName = tvGoalPlanName.text.toString()
            val selectedCategoryIndex = spinnerCategory.selectedItemPosition
            val updatedAmount = tvAmount.text.toString().toLongOrNull() ?: 0L

            // Parsing tanggal awal dan akhir
            val updatedStartDate = "$startDateYear-${String.format("%02d", startDateMonth + 1)}-${String.format("%02d", startDateDay)}"
            val updatedEndDate = "$endDateYear-${String.format("%02d", endDateMonth + 1)}-${String.format("%02d", endDateDay)}"

            // Panggil fungsi PUT API dengan value dibawah ini
            updateBudgetToServer(
                id = id,
                budgetName = updatedPlanName,
                category = selectedCategoryIndex,
                amount = updatedAmount,
                startDate = updatedStartDate,
                endDate = updatedEndDate
            )

            // TODO : check if success
            MotionToast.createColorToast(
                this, "Upload Berhasil!", "Data berhasil disimpan",
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


    // Fungsi untuk nembak API PUT setelah Save Button
    private fun updateBudgetToServer(id: Int, budgetName: String, category: Int, amount: Long, startDate: String, endDate: String) {
        val budgetUpdateRequest = Budget(
            id_budget = id,
            budget_name = budgetName,
            category_number = category,
            amount_limit = amount,
            spent_amount = 0, // Silahkan diganti pas ngambil dr DB untuk jumlah yang udah dipake user
            start_date = startDate,
            end_date = endDate
        )

        RetrofitClient.instance.updateBudget(id, budgetUpdateRequest).enqueue(object : retrofit2.Callback<Void> {
            override fun onResponse(call: retrofit2.Call<Void>, response: retrofit2.Response<Void>) {
                if (response.isSuccessful) {
                    // Jika berhasil
                    MotionToast.createColorToast(
                        this@CoreBudgetEdit,
                        "Upload Berhasil!",
                        "Data berhasil disimpan",
                        MotionToastStyle.SUCCESS,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this@CoreBudgetEdit, www.sanju.motiontoast.R.font.helvetica_regular)
                    )

                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@CoreBudgetEdit, CoreBudget::class.java)
                        startActivity(intent)
                    }, 2000)
                } else {
                    // Jika gagal
                    MotionToast.createColorToast(
                        this@CoreBudgetEdit,
                        "Gagal!",
                        "Gagal menyimpan data",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this@CoreBudgetEdit, www.sanju.motiontoast.R.font.helvetica_regular)
                    )
                }
            }

            override fun onFailure(call: retrofit2.Call<Void>, t: Throwable) {
                // Handle failure
                MotionToast.createColorToast(
                    this@CoreBudgetEdit,
                    "Error!",
                    "Terjadi kesalahan: ${t.message}",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.LONG_DURATION,
                    ResourcesCompat.getFont(this@CoreBudgetEdit, www.sanju.motiontoast.R.font.helvetica_regular)
                )
            }
        })
    }
}
