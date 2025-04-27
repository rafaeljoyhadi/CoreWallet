package com.example.corewallet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

class CoreBudget : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget)
        window.statusBarColor = Color.parseColor("#0d5892")
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