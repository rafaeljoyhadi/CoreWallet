package com.example.corewallet

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CoreSavings : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.core_savings)

        val btnCoreBudget: Button = findViewById(R.id.btn_core_goal)
        btnCoreBudget.setOnClickListener {
            val moveIntent = Intent(this@CoreSavings, MainActivity::class.java)
            startActivity(moveIntent)
        }

        val btnCoreGoal: Button = findViewById(R.id.btn_core_budget)
        btnCoreGoal.setOnClickListener {
            val moveIntent = Intent(this@CoreSavings, MainActivity::class.java)
            startActivity(moveIntent)
        }

    }
}