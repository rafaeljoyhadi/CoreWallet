package com.example.corewallet

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.graphics.Color
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.corewallet.ui.theme.CoreWalletTheme

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
    }
}
