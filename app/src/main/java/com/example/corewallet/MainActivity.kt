package com.example.corewallet

import android.os.Bundle
import androidx.activity.ComponentActivity
import android.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_savings)
        window.statusBarColor = Color.parseColor("#0d5892")

    }
}
