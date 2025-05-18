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
import android.widget.DatePicker
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.TransactionCategory
import com.example.corewallet.api.CreateBudgetRequest
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget_input)
        window.statusBarColor = Color.parseColor("#0d5892")

        // initialize to today
        val cal = Calendar.getInstance()
        startYear = cal.get(Calendar.YEAR)
        startMonth = cal.get(Calendar.MONTH)
        startDay = cal.get(Calendar.DAY_OF_MONTH)
        endYear = startYear
        endMonth = startMonth
        endDay = startDay

        // UI references
        val btnPickStart = findViewById<View>(R.id.buttonPickDateStart)
        val btnPickEnd   = findViewById<View>(R.id.buttonPickDateEnd)
        val tvStart      = findViewById<TextView>(R.id.tvSelectedDateStart)
        val tvEnd        = findViewById<TextView>(R.id.tvSelectedDateEnd)
        val etName       = findViewById<TextView>(R.id.etGoalPlanName)
        val etAmount     = findViewById<TextView>(R.id.etBudgetAmount)
        val btnSave      = findViewById<Button>(R.id.btnSave_CoreBudgetInput)
        val btnBack      = findViewById<ImageView>(R.id.btnBack)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val initialCategoryId = intent.getIntExtra("category", -1)

        // fetch live categories
        ApiClient.apiService.getTransactionCategories()
            .enqueue(object : Callback<List<TransactionCategory>> {
                override fun onResponse(
                    call: Call<List<TransactionCategory>>,
                    response: Response<List<TransactionCategory>>
                ) {
                    categories = response.body() ?: emptyList()

                    // Build a list of names from the fetched categories
                    val names = categories.map { it.category_name }

                    // Create & assign spinner adapter
                    val adapter = ArrayAdapter(
                        this@CoreBudgetInput,
                        android.R.layout.simple_spinner_item,
                        names
                    ).also {
                        it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    }
                    spinnerCategory.adapter = adapter

                    // Preselect the correct item by matching the incoming category ID
                    val preselect = categories.indexOfFirst { it.id_category == initialCategoryId }
                    if (preselect >= 0) {
                        spinnerCategory.setSelection(preselect)
                    }
                }

                override fun onFailure(call: Call<List<TransactionCategory>>, t: Throwable) {
                    Toast.makeText(this@CoreBudgetInput, "Failed to load categories", Toast.LENGTH_SHORT).show()
                }
            })

        // date pickers
        btnPickStart.setOnClickListener {
            DatePickerDialog(
                this,
                { _: DatePicker, y, m, d ->
                    startYear = y; startMonth = m; startDay = d
                    tvStart.text = "Tanggal terpilih: $d/${m + 1}/$y"
                    tvStart.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                },
                startYear, startMonth, startDay
            ).show()
        }
        btnPickEnd.setOnClickListener {
            DatePickerDialog(
                this,
                { _: DatePicker, y, m, d ->
                    if (startYear == 0) {
                        Toast.makeText(this, "Pilih Tanggal Awal terlebih dahulu", Toast.LENGTH_SHORT).show()
                        return@DatePickerDialog
                    }
                    val ok = when {
                        y > startYear -> true
                        y == startYear && m > startMonth -> true
                        y == startYear && m == startMonth && d > startDay -> true
                        else -> false
                    }
                    if (!ok) {
                        Toast.makeText(this, "Tanggal Akhir harus setelah Awal", Toast.LENGTH_SHORT).show()
                        return@DatePickerDialog
                    }
                    endYear = y; endMonth = m; endDay = d
                    tvEnd.text = "Tanggal terpilih: $d/${m + 1}/$y"
                    tvEnd.setTextColor(resources.getColor(android.R.color.holo_green_dark))
                },
                endYear, endMonth, endDay
            ).show()
        }

        btnBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val amount = etAmount.text.toString().toLongOrNull() ?: 0L
            val catPos = spinnerCategory.selectedItemPosition

            if (name.isBlank()) {
                Toast.makeText(this, "Nama anggaran harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (amount < 10000) {
                Toast.makeText(this, "Jumlah limit minimal 10000", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sDate = "%04d-%02d-%02d".format(startYear, startMonth + 1, startDay)
            val eDate = "%04d-%02d-%02d".format(endYear, endMonth + 1, endDay)

            val request = CreateBudgetRequest(
                planName    = name,
                idCategory  = categories[catPos].id_category,
                amountLimit = amount,
                startDate   = sDate,
                endDate     = eDate
            )

            ApiClient.apiService.createBudgetPlan(request)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            MotionToast.createColorToast(
                                this@CoreBudgetInput,
                                "Berhasil!",
                                "Anggaran baru dibuat",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(
                                    this@CoreBudgetInput,
                                    www.sanju.motiontoast.R.font.helvetica_regular
                                )
                            )
                            Handler(Looper.getMainLooper()).postDelayed({
                                startActivity(Intent(this@CoreBudgetInput, CoreBudget::class.java))
                                finish()
                            }, 1500)
                        } else {
                            Toast.makeText(this@CoreBudgetInput, "Server error", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@CoreBudgetInput, "Network error", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
