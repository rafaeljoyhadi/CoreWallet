package com.example.corewallet.coresavings

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.RetrofitClient
import com.example.corewallet.models.BudgetAdapter
import com.example.corewallet.models.Budget
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoreBudget : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var BudgetAdapter: BudgetAdapter
    private lateinit var budgetList: List<Budget>


    private fun showCustomPopup(anchorView: View, plan: Budget) {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.popup_menu_core_budget, null)
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.elevation = 30f
        popupWindow.showAsDropDown(anchorView, -178, -5, Gravity.START)

        popupView.findViewById<View>(R.id.itemEdit).setOnClickListener {
            val data = HashMap<String, Any>()
            data["id_budget"] = plan.id_budget
            data["planName"] = plan.plan_name
            data["category"] = plan.category_number
            data["amount"] = "Rp. ${plan.amount_limit}"
            data["progress"] = ((plan.spent_amount.toFloat() / plan.amount_limit) * 100).toInt()

            // Kirim tanggal mulai dan akhir ke halaman edit
            data["startDate"] = plan.start_date
            data["endDate"] = plan.end_date

            navigateToCoreBudgetEdit(this, CoreBudgetEdit::class.java, data)
        }

        popupView.findViewById<View>(R.id.itemDelete).setOnClickListener {
            Toast.makeText(this, "Delete clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
    }

    //Delete ini setelah menggunakan API
    private fun navigateToCoreBudgetEdit(
        activity: AppCompatActivity,
        nextActivityClass: Class<*>,
        data: HashMap<String, Any>
    ) {
        val intent = Intent(activity, nextActivityClass)
        for ((key, value) in data) {
            when (value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Float -> intent.putExtra(key, value)
                is Double -> intent.putExtra(key, value)
                else -> throw IllegalArgumentException("Unsupported data type for key: $key")
            }
        }
        activity.startActivity(intent)
    }

    private fun navigateToCoreBudgetDetail(budget: Budget) {
        val intent = Intent(this, CoreBudgetDetail::class.java)
        intent.putExtra("id_budget", budget.id_budget)
        intent.putExtra("planName", budget.plan_name)
        intent.putExtra("category", budget.category_number)
        intent.putExtra("amount", budget.amount_limit)
        intent.putExtra("spentAmount", budget.spent_amount)
        intent.putExtra("startDate", budget.start_date)
        intent.putExtra("endDate", budget.end_date)
        startActivity(intent)
    }

    private fun getBudgetPlans() {
        val apiService = ApiClient.getApiService()
        val userId = 1 // Ganti dengan singleton user jika sudah tersedia

        lifecycleScope.launch {
            try {
                val response = apiService.getBudgetPlans(userId)
                if (response.isSuccessful && response.body() != null) {
                    // Ambil daftar budget langsung dari respons body
                    val budgetList = response.body()!!.map { budget ->
                        Budget(
                            id_budget = budget.id_budget,
                            plan_name = budget.plan_name,
                            amount_limit = budget.amount_limit,
                            spent_amount = budget.spent_amount,
                            start_date = budget.start_date,
                            end_date = budget.end_date,
                            category_number = budget.category_number // Mapping dari id_category
                        )
                    }

                    // Log data untuk debugging
                    Log.d("API_RESPONSE", "Data diterima: $budgetList")

                    // Inisialisasi adapter dengan callback untuk popup dan navigasi
                    BudgetAdapter = BudgetAdapter(
                        budgetPlans = budgetList,
                        onMoreOptionsClick = { anchorView, plan ->
                            showCustomPopup(anchorView, plan)
                        },
                        onItemClicked = { plan ->
                            navigateToCoreBudgetDetail(plan)
                        }
                    )

                    recyclerView.adapter = BudgetAdapter
                } else {
                    Toast.makeText(this@CoreBudget, "Gagal mengambil data", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Exception: ${e.message}")
                Toast.makeText(this@CoreBudget, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget)
        window.statusBarColor = Color.parseColor("#0d5892")

        // Inisialisasi RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCoreBudget)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Dummy Data untuk testing tampilan
        val dummyList = listOf(
            Budget(
                id_budget = 1,
                plan_name = "March Spendings",
                amount_limit = 1000000,
                spent_amount = 600000,
                start_date = "2025-03-01",
                end_date = "2025-03-31",
                category_number = 1
            ),
            Budget(
                id_budget = 2,
                plan_name = "April Fun",
                amount_limit = 500000,
                spent_amount = 200000,
                start_date = "2025-04-01",
                end_date = "2025-04-30",
                category_number = 2
            ),
            Budget(
                id_budget = 3,
                plan_name = "May Savings",
                amount_limit = 2000000,
                spent_amount = 1500000,
                start_date = "2025-05-01",
                end_date = "2025-05-31",
                category_number = 3
            ),
            Budget(
                id_budget = 4,
                plan_name = "June Vacation",
                amount_limit = 3000000,
                spent_amount = 2500000,
                start_date = "2025-06-01",
                end_date = "2025-06-30",
                category_number = 4
            ),
            Budget(
                id_budget = 5,
                plan_name = "July Investments",
                amount_limit = 1500000,
                spent_amount = 1000000,
                start_date = "2025-07-01",
                end_date = "2025-07-31",
                category_number = 5
            )
        )

        // Inisialisasi adapter dengan dummyList
        BudgetAdapter = BudgetAdapter(
            budgetPlans = dummyList, // Gunakan dummyList sebagai sumber data
            onMoreOptionsClick = { view, budget ->
                showCustomPopup(view, budget) // Callback untuk popup menu
            },
            onItemClicked = { budget ->
                navigateToCoreBudgetDetail(budget) // Callback untuk navigasi ke detail
            }
        )

        // Set adapter ke RecyclerView
        recyclerView.adapter = BudgetAdapter



////         NOTE: Sementara ini jangan panggil API
//         getBudgetPlans()

        // Tombol back
        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            onBackPressed() // Simulasikan tombol back
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // Button Add Budget
        val btnCoreGoal: Button = findViewById(R.id.btnAddGoal)
        btnCoreGoal.setOnClickListener {
            val moveIntent = Intent(this@CoreBudget, CoreBudgetInput::class.java)
            startActivity(moveIntent)
        }
    }
}