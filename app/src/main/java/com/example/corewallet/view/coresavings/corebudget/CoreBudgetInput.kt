package com.example.corewallet.view.coresavings.corebudget

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.models.TransactionCategory
import com.example.corewallet.api.request.CreateBudgetRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.Calendar

class CoreBudgetInput : AppCompatActivity() {

    private var startYear = 0
    private var startMonth = 0
    private var startDay = 0

    private var endYear = 0
    private var endMonth = 0
    private var endDay = 0

    private lateinit var categories: List<TransactionCategory>

    private lateinit var tvStart: TextView
    private lateinit var tvEnd: TextView
    private lateinit var etName: TextView
    private lateinit var etAmount: TextView
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageView
    private lateinit var spinnerCategory: Spinner
    private var initialCategoryId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget_input)
        window.statusBarColor = Color.parseColor("#0d5892")

        initDateDefaults()
        initViews()
        setupDatePickers()
        fetchCategories()
        setupListeners()
    }

    private fun initDateDefaults() {
        val cal = Calendar.getInstance()
        startYear = cal.get(Calendar.YEAR)
        startMonth = cal.get(Calendar.MONTH)
        startDay = cal.get(Calendar.DAY_OF_MONTH)
        endYear = startYear
        endMonth = startMonth
        endDay = startDay
    }

    private fun initViews() {
        tvStart = findViewById(R.id.tvSelectedDateStart)
        tvEnd = findViewById(R.id.tvSelectedDateEnd)
        etName = findViewById(R.id.etGoalPlanName)
        etAmount = findViewById(R.id.etBudgetAmount)
        btnSave = findViewById(R.id.btnSave_CoreBudgetInput)
        btnBack = findViewById(R.id.btnBack)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        initialCategoryId = intent.getIntExtra("category", -1)
    }

    private fun setupDatePickers() {
        findViewById<View>(R.id.buttonPickDateStart).setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                startYear = y; startMonth = m; startDay = d
                tvStart.text = "Tanggal terpilih: $d/${m + 1}/$y"
                tvStart.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            }, startYear, startMonth, startDay).show()
        }

        findViewById<View>(R.id.buttonPickDateEnd).setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                if (!isValidEndDate(y, m, d)) return@DatePickerDialog
                endYear = y; endMonth = m; endDay = d
                tvEnd.text = "Tanggal terpilih: $d/${m + 1}/$y"
                tvEnd.setTextColor(resources.getColor(android.R.color.holo_green_dark))
            }, endYear, endMonth, endDay).show()
        }
    }

    private fun isValidEndDate(y: Int, m: Int, d: Int): Boolean {
        if (startYear == 0) {
            showToast("Warning!", "Pilih Tanggal Awal terlebih dahulu!", MotionToastStyle.WARNING)
            return false
        }
        val valid = y > startYear || (y == startYear && m > startMonth) ||
                (y == startYear && m == startMonth && d > startDay)
        if (!valid) {
            showToast("Warning!", "Tanggal Akhir harus setelah Awal!", MotionToastStyle.WARNING)
        }
        return valid
    }

    private fun fetchCategories() {
        ApiClient.apiService.getTransactionCategories().enqueue(object : Callback<List<TransactionCategory>> {
            override fun onResponse(call: Call<List<TransactionCategory>>, response: Response<List<TransactionCategory>>) {
                categories = response.body() ?: emptyList()
                val names = categories.map { it.category_name }
                val adapter = ArrayAdapter(this@CoreBudgetInput, android.R.layout.simple_spinner_item, names)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategory.adapter = adapter

                val preselect = categories.indexOfFirst { it.id_category == initialCategoryId }
                if (preselect >= 0) spinnerCategory.setSelection(preselect)
            }

            override fun onFailure(call: Call<List<TransactionCategory>>, t: Throwable) {
                showToast("Error!", "Failed to load categories!", MotionToastStyle.ERROR)
            }
        })
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        btnSave.setOnClickListener {
            saveBudget()
        }
    }

    private fun saveBudget() {
        val name = etName.text.toString().trim()
        val amount = etAmount.text.toString().toLongOrNull() ?: 0L
        val catPos = spinnerCategory.selectedItemPosition

        if (name.isBlank()) {
            showToast("Warning!", "Nama Anggaran harus diisi!", MotionToastStyle.WARNING)
            return
        }
        if (amount < 10000) {
            showToast("Warning!", "Jumlah limit minimal 10000!", MotionToastStyle.WARNING)
            return
        }
        if (catPos < 0 || catPos >= categories.size) {
            showToast("Error", "Pilih kategori", MotionToastStyle.ERROR)
            return
        }

        val sDate = "%04d-%02d-%02d".format(startYear, startMonth + 1, startDay)
        val eDate = "%04d-%02d-%02d".format(endYear, endMonth + 1, endDay)

        val request = CreateBudgetRequest(
            planName = name,
            idCategory = categories[catPos].id_category,
            amountLimit = amount,
            startDate = sDate,
            endDate = eDate
        )

        ApiClient.apiService.createBudgetPlan(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    showToast("Berhasil!", "Anggaran baru dibuat", MotionToastStyle.SUCCESS)
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this@CoreBudgetInput, CoreBudget::class.java))
                        finish()
                    }, 1500)
                } else {
                    showToast("Error!", "Server Error!", MotionToastStyle.ERROR)
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                showToast("Info!", "Network Error!", MotionToastStyle.INFO)
            }
        })
    }

    private fun showToast(title: String, message: String, style: MotionToastStyle) {
        MotionToast.createColorToast(
            this,
            title,
            message,
            style,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.LONG_DURATION,
            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
        )
    }
}
