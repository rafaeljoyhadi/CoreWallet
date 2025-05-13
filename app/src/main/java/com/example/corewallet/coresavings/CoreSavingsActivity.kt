package com.example.corewallet.coresavings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.corewallet.CoreSavingsActivity
import com.example.corewallet.DashboardFragment
import com.example.corewallet.QrisFragment
import com.example.corewallet.R
import com.example.corewallet.TransactionHistoryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Data class sesuai JSON
data class SavingsResponse(
    val id: Int,
    val id_user: Int,
    val goal_name: String,
    val target_amount: Int,
    val saved_amount: Int,
    val deadline: String,
    val status: Int
)

// API Service
interface ApiService {
    @GET("your_endpoint_here")
    fun getCoreSavings(): Call<SavingsResponse>
}

// Retrofit Instance
object RetrofitInstance {
    private const val BASE_URL = "https://your_api_base_url_here/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}


class CoreSavings : AppCompatActivity() {
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
            val moveIntent = Intent(this@CoreSavings, CoreBudget::class.java)
            startActivity(moveIntent)
        }

        val btnCoreGoal: Button = findViewById(R.id.btn_core_goal)
        btnCoreGoal.setOnClickListener {
            val moveIntent = Intent(this@CoreSavings, CoreGoal::class.java)
            startActivity(moveIntent)
            RetrofitInstance.api.getCoreSavings().enqueue(object : Callback<SavingsResponse> {
                override fun onResponse(
                    call: Call<SavingsResponse>,
                    response: Response<SavingsResponse>
                ) {
                    if (response.isSuccessful) {
                        val data = response.body()
                        data?.let {
                            val intent = Intent(this@CoreSavings, CoreBudget::class.java).apply {
                                putExtra("goal_name", it.goal_name)
                                putExtra("target_amount", it.target_amount)
                                putExtra("saved_amount", it.saved_amount)
                                putExtra("deadline", it.deadline)
                                putExtra("status", it.status)
                            }
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this@CoreSavings, "Gagal mendapatkan data", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<SavingsResponse>, t: Throwable) {
                    Toast.makeText(this@CoreSavings, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }
}