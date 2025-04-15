package com.example.corewallet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.graphics.Color
import android.widget.Button

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = Color.parseColor("#0d5892")

        // This code is temporary just to redirect
        val btnMoveActivity: Button = findViewById(R.id.btn_core_savings)
        btnMoveActivity.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, CoreSavings::class.java)
            startActivity(moveIntent)
        }

    }
}
