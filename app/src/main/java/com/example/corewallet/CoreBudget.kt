package com.example.corewallet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import java.text.NumberFormat
import java.util.Locale

class CoreBudget : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget)
        window.statusBarColor = Color.parseColor("#0d5892")

        //Data Dummy
        val savedAmount = 400000
        val targetAmount = 1000000

        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID")) // Locale Indonesia

        val progressBar = findViewById<ProgressBar>(R.id.progressBar1)

        val progress = ((savedAmount.toDouble() / targetAmount) * 100).toInt()

        progressBar.progress = progress.coerceAtMost(100) // Pastikan tidak lebih dari 100

        val tvShoppingAmount = findViewById<TextView>(R.id.tvShoppingAmount)
        tvShoppingAmount.text = "${formatter.format(savedAmount)} / ${formatter.format(targetAmount)}"

//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

//        val btnAddBudget = findViewById<Button>(R.id.btnAddBudget)
//
//        btnAddBudget.setOnClickListener {
//            val intent = Intent(this@CoreBudget, CoreBudgetForm::class.java)
//            startActivity(intent)
//        }

        val btnCoreGoal: Button = findViewById(R.id.btnAddBudget)
        btnCoreGoal.setOnClickListener {
            val moveIntent = Intent(this@CoreBudget, CoreBudgetForm::class.java)
            startActivity(moveIntent)
        }

//        val progressBar: ProgressBar = findViewById(R.id.progressBar)
//        val currentProgress = 10
//        progressBar.progress = currentProgress
//
//        if (currentProgress >= 100) {
//            progressBar.progressDrawable = ContextCompat.getDrawable(this, R.drawable.progress_bar)
//        } else {
//            progressBar.progressDrawable = ContextCompat.getDrawable(this, R.drawable.progress_bar_full)
//        }



    }
}