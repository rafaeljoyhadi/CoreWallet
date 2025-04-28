package com.example.corewallet

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = Color.parseColor("#0d5892")

        val transferButton = findViewById<View>(R.id.transfer_btn)

        // Set a click listener to navigate to TransferActivity
        transferButton.setOnClickListener {
            val intent = Intent(this, TransferActivity::class.java)
            startActivity(intent) // Start the TransferActivity
        }

        // This code is temporary just to redirect
        val btnMoveActivity: Button = findViewById(R.id.btn_core_savings)
        btnMoveActivity.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, CoreSavings::class.java)
            startActivity(moveIntent)
        }


    }
}
