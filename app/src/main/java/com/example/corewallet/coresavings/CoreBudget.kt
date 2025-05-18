package com.example.corewallet.coresavings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.models.BudgetAdapter
import Budget
import com.google.gson.internal.bind.TypeAdapters.SHORT
import kotlinx.coroutines.launch

class CoreBudget : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var budgetAdapter: BudgetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget)
        window.statusBarColor = Color.parseColor("#0d5892")

        recyclerView = findViewById(R.id.recyclerViewCoreBudget)
        recyclerView.layoutManager = LinearLayoutManager(this)

        getBudgetPlans()

        findViewById<View>(R.id.btnBack).setOnClickListener {
            onBackPressed()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        findViewById<View>(R.id.btnAddBudget).setOnClickListener {
            startActivity(Intent(this@CoreBudget, CoreBudgetInput::class.java))
        }
    }

    private fun getBudgetPlans() {
        lifecycleScope.launch {
            try {
                val resp = ApiClient.apiService.getBudgetPlans()
                if (resp.isSuccessful) {
                    val budgets = resp.body() ?: emptyList()
                    setupAdapter(budgets)
                } else {
                    Toast.makeText(this@CoreBudget, "Failed to load budgets", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@CoreBudget, "Network error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupAdapter(budgets: List<Budget>) {
        Log.d("CoreBudget", "setupAdapter() called with ${budgets.size} items")

        budgetAdapter = BudgetAdapter(
            budgetPlans = budgets,
            onMoreOptionsClick = { view, plan -> showCustomPopup(view, plan) },
            onItemClicked = { plan -> navigateToCoreBudgetDetail(plan) }
        )
        recyclerView.adapter = budgetAdapter
    }


    private fun showCustomPopup(anchorView: View, plan: Budget) {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.popup_menu_core_savings, null)
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
            val data = hashMapOf<String, Any>(
                "id_budget" to plan.id_budget,
                "budgetName" to (plan.budget_name ?: "Unnamed"),
                "category" to plan.id_category,
                "amount" to plan.amount_limit,
                "progress" to if (plan.amount_limit > 0)
                    ((plan.spent_amount.toFloat() / plan.amount_limit) * 100).toInt()
                else 0,
                "startDate" to plan.start_date,
                "endDate" to plan.end_date
            )

            navigateToCoreBudgetEdit(this, CoreBudgetEdit::class.java, data)
        }

        popupView.findViewById<View>(R.id.itemDelete).setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.deleteBudget(plan.id_budget)
                    if (response.isSuccessful) {
                        Toast.makeText(this@CoreBudget, "Budget deleted", Toast.LENGTH_SHORT).show()
                        getBudgetPlans() // Refresh the list
                    } else {
                        Toast.makeText(this@CoreBudget, "Failed to delete budget", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@CoreBudget, "Network error", Toast.LENGTH_SHORT).show()
                }
            }
            popupWindow.dismiss()
        }
    }

    private fun navigateToCoreBudgetEdit(
        activity: AppCompatActivity,
        nextActivityClass: Class<*>,
        data: HashMap<String, Any>
    ) {
        val intent = Intent(activity, nextActivityClass)
        data.forEach { (key, value) ->
            when (value) {
                is String  -> intent.putExtra(key, value)
                is Int     -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Float   -> intent.putExtra(key, value)
                is Double  -> intent.putExtra(key, value)
                is Long    -> intent.putExtra(key, value)
                else       -> throw IllegalArgumentException("Unsupported data type for key: $key")
            }
        }
        activity.startActivity(intent)
    }


    private fun navigateToCoreBudgetDetail(budget: Budget) {
        Intent(this, CoreBudgetDetail::class.java).apply {
            putExtra("id_budget", budget.id_budget)
            putExtra("budgetName", budget.budget_name)
            putExtra("category", budget.id_category)
            putExtra("amount", budget.amount_limit)
            putExtra("spentAmount", budget.spent_amount)
            putExtra("startDate", budget.start_date)
            putExtra("endDate", budget.end_date)
            startActivity(this)
        }
    }
}
