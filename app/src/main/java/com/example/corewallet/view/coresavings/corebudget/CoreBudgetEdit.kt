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

    private var startYear = 0
    private var startMonth = 0
    private var startDay = 0

    private var endYear = 0
    private var endMonth = 0
    private var endDay = 0

    private var categories: List<TransactionCategory> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget_edit)
        window.statusBarColor = Color.parseColor("#0d5892")

        val idBudget = intent.getIntExtra("id_budget", 0)
        val initialName = intent.getStringExtra("budgetName") ?: ""
        val initialCategory = intent.getIntExtra("category", -1)
        val initialAmount = intent.getLongExtra("amount", 0L)
        val initialStart = intent.getStringExtra("startDate") ?: ""
        val initialEnd = intent.getStringExtra("endDate") ?: ""

        val etName = findViewById<EditText>(R.id.etBudgetPlanNameEdit)
        val etAmount = findViewById<EditText>(R.id.etBudgetAmountEdit)
        val spinnerCategory = findViewById<Spinner>(R.id.spinnerCategory)
        val tvStart = findViewById<TextView>(R.id.tvSelectedDateStart)
        val tvEnd = findViewById<TextView>(R.id.tvSelectedDateEnd)
        val btnPickStart = findViewById<View>(R.id.buttonPickDateStart)
        val btnPickEnd = findViewById<View>(R.id.buttonPickDateEnd)
        val btnSave = findViewById<Button>(R.id.btnSave_CoreBudgetEdit)
        val btnBack = findViewById<ImageView>(R.id.btnBack)

        etName.setText(initialName)
        etAmount.setText(initialAmount.toString())
        tvStart.text = formatDisplayDate(initialStart)
        tvEnd.text = formatDisplayDate(initialEnd)

        initialStart.substringBefore("T").split("-").let {
            if (it.size == 3) {
                startYear = it[0].toInt()
                startMonth = it[1].toInt() - 1
                startDay = it[2].toInt()
            }
        }
        initialEnd.substringBefore("T").split("-").let {
            if (it.size == 3) {
                endYear = it[0].toInt()
                endMonth = it[1].toInt() - 1
                endDay = it[2].toInt()
            }
        }

        ApiClient.apiService.getTransactionCategories()
            .enqueue(object : Callback<List<TransactionCategory>> {
                override fun onResponse(
                    call: Call<List<TransactionCategory>>,
                    response: Response<List<TransactionCategory>>
                ) {
                    categories = response.body() ?: emptyList()
                    val names = categories.map { it.category_name }
                    val adapter = ArrayAdapter(
                        this@CoreBudgetEdit,
                        android.R.layout.simple_spinner_item,
                        names
                    ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
                    spinnerCategory.adapter = adapter

                    val idx = categories.indexOfFirst { it.id_category == initialCategory }
                    if (idx >= 0) spinnerCategory.setSelection(idx)
                }

                override fun onFailure(call: Call<List<TransactionCategory>>, t: Throwable) {
                    MotionToast.createColorToast(
                        this@CoreBudgetEdit,
                        "Error",
                        "Failed to load categories",
                        MotionToastStyle.ERROR,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.LONG_DURATION,
                        ResourcesCompat.getFont(this@CoreBudgetEdit, www.sanju.motiontoast.R.font.helvetica_regular)
                    )
                }
            })

        btnPickStart.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, y, m, d ->
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
                MotionToast.createColorToast(
                    this,
                    "Warning",
                    "Pilih Tanggal Awal dulu",
                    MotionToastStyle.WARNING,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this@CoreBudgetEdit, www.sanju.motiontoast.R.font.helvetica_regular)
                )
                return@setOnClickListener
            }
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    val ok = when {
                        y > startYear -> true
                        y == startYear && m > startMonth -> true
                        y == startYear && m == startMonth && d > startDay -> true
                        else -> false
                    }
                    if (!ok) {
                        MotionToast.createColorToast(
                            this,
                            "Invalid",
                            "Tanggal Akhir harus setelah Awal",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.SHORT_DURATION,
                            ResourcesCompat.getFont(this@CoreBudgetEdit, www.sanju.motiontoast.R.font.helvetica_regular)
                        )
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

        btnBack.setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val amount = etAmount.text.toString().toLongOrNull() ?: 0L
            val pos = spinnerCategory.selectedItemPosition

            if (name.isBlank()) {
                MotionToast.createColorToast(
                    this,
                    "Error",
                    "Nama anggaran harus diisi",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this@CoreBudgetEdit, www.sanju.motiontoast.R.font.helvetica_regular)
                )
                return@setOnClickListener
            }
            if (amount < 10000) {
                MotionToast.createColorToast(
                    this,
                    "Error",
                    "Limit harus minimal 10000",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this@CoreBudgetEdit, www.sanju.motiontoast.R.font.helvetica_regular)
                )
                return@setOnClickListener
            }
            if (pos < 0 || pos >= categories.size) {
                MotionToast.createColorToast(
                    this,
                    "Error",
                    "Pilih kategori",
                    MotionToastStyle.ERROR,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(this@CoreBudgetEdit, www.sanju.motiontoast.R.font.helvetica_regular)
                )
                return@setOnClickListener
            }

            val sDate = "%04d-%02d-%02d".format(startYear, startMonth + 1, startDay)
            val eDate = "%04d-%02d-%02d".format(endYear, endMonth + 1, endDay)

            val req = UpdateBudgetRequest(
                planName = name,
                idCategory = categories[pos].id_category,
                amountLimit = amount,
                startDate = sDate,
                endDate = eDate
            )

            ApiClient.apiService.updateBudget(idBudget, req)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, resp: Response<Void>) {
                        if (resp.isSuccessful) {
                            MotionToast.createColorToast(
                                this@CoreBudgetEdit,
                                "Success",
                                "Anggaran diperbarui",
                                MotionToastStyle.SUCCESS,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(this@CoreBudgetEdit, www.sanju.motiontoast.R.font.helvetica_regular)
                            )
                            Handler(Looper.getMainLooper()).postDelayed({
                                startActivity(Intent(this@CoreBudgetEdit, CoreBudget::class.java))
                                finish()
                            }, 500)
                        } else {
                            MotionToast.createColorToast(
                                this@CoreBudgetEdit,
                                "Error",
                                "Server error",
                                MotionToastStyle.ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.LONG_DURATION,
                                ResourcesCompat.getFont(this@CoreBudgetEdit, www.sanju.motiontoast.R.font.helvetica_regular)
                            )
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        MotionToast.createColorToast(
                            this@CoreBudgetEdit,
                            "Error",
                            "Network error",
                            MotionToastStyle.ERROR,
                            MotionToast.GRAVITY_BOTTOM,
                            MotionToast.LONG_DURATION,
                            ResourcesCompat.getFont(this@CoreBudgetEdit, www.sanju.motiontoast.R.font.helvetica_regular)
                        )
                    }
                })
        }
    }

    private fun formatDisplayDate(iso: String): String = iso.substringBefore("T").split("-").let {
        if (it.size == 3) "%02d/%02d/%04d".format(it[2].toInt(), it[1].toInt(), it[0].toInt()) else iso
    }
    private fun formatDisplayDate(day: Int, month: Int, year: Int): String = "%02d/%02d/%04d".format(day, month+1, year)

    private fun Int.ifZero(default: () -> Int): Int = if (this == 0) default() else this
}


