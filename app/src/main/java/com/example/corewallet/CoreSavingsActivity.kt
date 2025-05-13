package com.example.corewallet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.corewallet.coresavings.CoreBudget
import com.example.corewallet.coresavings.CoreGoal
import com.example.corewallet.coresavings.RetrofitInstance
import com.example.corewallet.coresavings.SavingsResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CoreSavingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_savings)
        window.statusBarColor = Color.parseColor("#0d5892")

        // Retrieve user details from LoginActivity
        val userId = intent.getIntExtra("userId", -1)
        val username = intent.getStringExtra("username") ?: ""
        val accountNumber = intent.getStringExtra("accountNumber") ?: ""
        val balance = intent.getDoubleExtra("balance", 0.0)

        val btnCoreBudget: Button = findViewById(R.id.btn_core_budget)
        btnCoreBudget.setOnClickListener {
            val moveIntent = Intent(this@CoreSavingsActivity, CoreBudget::class.java)
            startActivity(moveIntent)
        }

        val btnCoreGoal: Button = findViewById(R.id.btn_core_goal)
        btnCoreGoal.setOnClickListener {
            val moveIntent = Intent(this@CoreSavingsActivity, CoreGoal::class.java)
            startActivity(moveIntent)
            RetrofitInstance.api.getCoreSavings().enqueue(object : Callback<SavingsResponse> {
                override fun onResponse(
                    call: Call<SavingsResponse>,
                    response: Response<SavingsResponse>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        data?.let {
                            val intent = Intent(this@CoreSavingsActivity, CoreBudget::class.java).apply {
                                putExtra("goal_name", it.goal_name)
                                putExtra("target_amount", it.target_amount)
                                putExtra("saved_amount", it.saved_amount)
                                putExtra("deadline", it.deadline)
                                putExtra("status", it.status)
                            }
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this@CoreSavingsActivity, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SavingsResponse>, t: Throwable) {
                    Toast.makeText(this@CoreSavingsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    navigateToMainActivity("DashboardFragment")
                    true
                }
                R.id.nav_transaction_history -> {
                    navigateToMainActivity("TransactionHistoryFragment")
                    true
                }
                R.id.nav_qris -> {
                    navigateToMainActivity("QrisFragment")
                    true
                }
                R.id.nav_coresavings -> {
                    // Stay in CoreSavingsActivity
                    true
                }
                R.id.nav_account -> {
                    navigateToMainActivity("AccountFragment")
                    true
                }
                else -> false
            }
        }


    }
    private fun navigateToMainActivity(fragmentName: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("targetFragment", fragmentName) // Pass the target fragment name
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP // Ensure MainActivity is reused
        }
        startActivity(intent)
        finish() // Close CoreSavingsActivity
    }
}