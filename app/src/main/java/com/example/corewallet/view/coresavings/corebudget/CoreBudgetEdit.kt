package com.example.corewallet.view.coresavings.corebudget

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.models.TransactionCategory
import com.example.corewallet.api.request.UpdateBudgetRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.util.Calendar

class CoreBudgetEdit : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etAmount: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var tvStart: TextView
    private lateinit var tvEnd: TextView
    private lateinit var btnPickStart: View
    private lateinit var btnPickEnd: View
    private lateinit var btnSave: Button
    private lateinit var btnBack: ImageView

    private var startYear = 0
    private var startMonth = 0
    private var startDay = 0
    private var endYear = 0
    private var endMonth = 0
    private var endDay = 0

    private var categories: List<TransactionCategory> = emptyList()
    private var idBudget = 0
    private var initialCategory = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget_edit)
        window.statusBarColor = Color.parseColor("#0d5892")

        initViews()
        initIntentData()
        setupInitialData()
        setupCategorySpinner()
        setupDatePickers()
        setupListeners()
    }

    private fun initViews() {
        etName = findViewById(R.id.etBudgetPlanNameEdit)
        etAmount = findViewById(R.id.etBudgetAmountEdit)
        spinnerCategory = findViewById(R.id.spinnerCategory)
        tvStart = findViewById(R.id.tvSelectedDateStart)
        tvEnd = findViewById(R.id.tvSelectedDateEnd)
        btnPickStart = findViewById(R.id.buttonPickDateStart)
        btnPickEnd = findViewById(R.id.buttonPickDateEnd)
        btnSave = findViewById(R.id.btnSave_CoreBudgetEdit)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun initIntentData() {
        idBudget = intent.getIntExtra("id_budget", 0)
        initialCategory = intent.getIntExtra("category", -1)

        val initialName = intent.getStringExtra("budgetName") ?: ""
        val initialAmount = intent.getLongExtra("amount", 0L)
        val initialStart = intent.getStringExtra("startDate") ?: ""
        val initialEnd = intent.getStringExtra("endDate") ?: ""

        etName.setText(initialName)
        etAmount.setText(initialAmount.toString())
        tvStart.text = formatDisplayDate(initialStart)
        tvEnd.text = formatDisplayDate(initialEnd)

        initialStart.extractDate()?.let { (y, m, d) ->
            startYear = y; startMonth = m; startDay = d
        }
        initialEnd.extractDate()?.let { (y, m, d) ->
            endYear = y; endMonth = m; endDay = d
        }
    }

    private fun setupInitialData() {
        // Placeholder jika ingin inisialisasi tambahan
    }

    private fun setupCategorySpinner() {
        ApiClient.apiService.getTransactionCategories()
            .enqueue(object : Callback<List<TransactionCategory>> {
                override fun onResponse(
                    call: Call<List<TransactionCategory>>,
                    response: Response<List<TransactionCategory>>
                ) {
                    categories = response.body() ?: emptyList()
                    val names = categories.map { it.category_name }
                    val adapter = ArrayAdapter(this@CoreBudgetEdit, android.R.layout.simple_spinner_item, names)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerCategory.adapter = adapter

                    val idx = categories.indexOfFirst { it.id_category == initialCategory }
                    if (idx >= 0) spinnerCategory.setSelection(idx)
                }

                override fun onFailure(call: Call<List<TransactionCategory>>, t: Throwable) {
                    showToast("Error", "Failed to load categories", MotionToastStyle.ERROR)
                }
            })
    }

    private fun setupDatePickers() {
        val cal = Calendar.getInstance()

        btnPickStart.setOnClickListener {
            DatePickerDialog(this, { _, y, m, d ->
                startYear = y; startMonth = m; startDay = d
                tvStart.text = formatDisplayDate(d, m, y)
            },
                startYear.ifZero { cal.get(Calendar.YEAR) },
                startMonth.ifZero { cal.get(Calendar.MONTH) },
                startDay.ifZero { cal.get(Calendar.DAY_OF_MONTH) }
            ).show()
        }

        btnPickEnd.setOnClickListener {
            if (startYear == 0) {
                showToast("Warning", "Pilih Tanggal Awal dulu", MotionToastStyle.WARNING)
                return@setOnClickListener
            }
            DatePickerDialog(this, { _, y, m, d ->
                val valid = y > startYear || (y == startYear && (m > startMonth || (m == startMonth && d > startDay)))
                if (!valid) {
                    showToast("Invalid", "Tanggal Akhir harus setelah Awal", MotionToastStyle.ERROR)
                    return@DatePickerDialog
                }
                endYear = y; endMonth = m; endDay = d
                tvEnd.text = formatDisplayDate(d, m, y)
            },
                endYear.ifZero { cal.get(Calendar.YEAR) },
                endMonth.ifZero { cal.get(Calendar.MONTH) },
                endDay.ifZero { cal.get(Calendar.DAY_OF_MONTH) }
            ).show()
        }
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        btnSave.setOnClickListener {
            validateAndSaveBudget()
        }
    }

    private fun validateAndSaveBudget() {
        val name = etName.text.toString().trim()
        val amount = etAmount.text.toString().toLongOrNull() ?: 0L
        val pos = spinnerCategory.selectedItemPosition

        when {
            name.isBlank() -> showToast("Error", "Nama anggaran harus diisi", MotionToastStyle.ERROR)
            amount < 10000 -> showToast("Error", "Limit harus minimal 10000", MotionToastStyle.ERROR)
            pos !in categories.indices -> showToast("Error", "Pilih kategori", MotionToastStyle.ERROR)
            else -> {
                val sDate = "%04d-%02d-%02d".format(startYear, startMonth + 1, startDay)
                val eDate = "%04d-%02d-%02d".format(endYear, endMonth + 1, endDay)
                val req = UpdateBudgetRequest(name, categories[pos].id_category, amount, sDate, eDate)

                ApiClient.apiService.updateBudget(idBudget, req)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, resp: Response<Void>) {
                            if (resp.isSuccessful) {
                                showToast("Success", "Anggaran diperbarui", MotionToastStyle.SUCCESS)
                                Handler(Looper.getMainLooper()).postDelayed({
                                    startActivity(Intent(this@CoreBudgetEdit, CoreBudget::class.java))
                                    finish()
                                }, 500)
                            } else showToast("Error", "Server error", MotionToastStyle.ERROR)
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            showToast("Error", "Network error", MotionToastStyle.ERROR)
                        }
                    })
            }
        }
    }

    private fun showToast(title: String, msg: String, style: MotionToastStyle) {
        MotionToast.createColorToast(
            this, title, msg, style,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(this, www.sanju.motiontoast.R.font.helvetica_regular)
        )
    }

    private fun formatDisplayDate(iso: String): String =
        iso.substringBefore("T").split("-").let {
            if (it.size == 3) "%02d/%02d/%04d".format(it[2].toInt(), it[1].toInt(), it[0].toInt()) else iso
        }

    private fun formatDisplayDate(day: Int, month: Int, year: Int): String =
        "%02d/%02d/%04d".format(day, month + 1, year)

    private fun Int.ifZero(default: () -> Int): Int = if (this == 0) default() else this

    private fun String.extractDate(): Triple<Int, Int, Int>? =
        this.substringBefore("T").split("-").let {
            if (it.size == 3) Triple(it[0].toInt(), it[1].toInt() - 1, it[2].toInt()) else null
        }
}
