package com.example.corewallet.view.coresavings

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.corewallet.R
import com.example.corewallet.view.coresavings.corebudget.CoreBudget
import com.example.corewallet.view.coresavings.coregoal.CoreGoal


class CoreSavingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_savings)
        window.statusBarColor = Color.parseColor("#0d5892")

        val btnCoreBudget: Button = findViewById(R.id.btn_core_budget)
        btnCoreBudget.setOnClickListener {
            val moveIntent = Intent(this@CoreSavingsActivity, CoreBudget::class.java)
            startActivity(moveIntent)
        }

        val btnCoreGoal: Button = findViewById(R.id.btn_core_goal)
        btnCoreGoal.setOnClickListener {
            val moveIntent = Intent(this@CoreSavingsActivity, CoreGoal::class.java)
            startActivity(moveIntent)
        }

    }
}