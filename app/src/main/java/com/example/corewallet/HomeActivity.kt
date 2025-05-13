package com.example.corewallet

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.corewallet.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var settingsIcon: ImageView
    private lateinit var logoutIcon: ImageView
    private lateinit var helloText: TextView

    private lateinit var transferButton: LinearLayout
    private lateinit var topUpButton: LinearLayout
    private lateinit var withdrawButton: LinearLayout
    private lateinit var profileButton: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initViews()
        setupListeners()

        val userName = intent.getStringExtra("username") ?: "User"
        helloText.text = "HELLO, $userName".uppercase()
    }

    private fun initViews() {
        settingsIcon = findViewById(R.id.settingsIcon)
        logoutIcon = findViewById(R.id.logoutIcon)
        helloText = findViewById(R.id.helloText)

        transferButton = findViewById(R.id.transferButton)
        topUpButton = findViewById(R.id.topUpButton)
        withdrawButton = findViewById(R.id.withdrawButton)
        profileButton = findViewById(R.id.profileButton)
    }

    private fun setupListeners() {
        settingsIcon.setOnClickListener {
            Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show()
        }

        val logoutIcon = findViewById<ImageView>(R.id.logoutIcon)

        logoutIcon.setOnClickListener {
            ApiClient.authService.logout().enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        // Clear session cookies
                        ApiClient.getCookieJar().clearCookies()

                        Toast.makeText(this@HomeActivity, "Logged out successfully", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@HomeActivity, "Logout failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(this@HomeActivity, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }



        transferButton.setOnClickListener {
            Toast.makeText(this, "Transfer clicked", Toast.LENGTH_SHORT).show()
            // Intent to TransferActivity
        }

        topUpButton.setOnClickListener {
            Toast.makeText(this, "Top Up clicked", Toast.LENGTH_SHORT).show()
            // Intent to TopUpActivity
        }

        withdrawButton.setOnClickListener {
            Toast.makeText(this, "Withdraw clicked", Toast.LENGTH_SHORT).show()
            // Intent to WithdrawActivity
        }

        profileButton.setOnClickListener {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            // Intent to ProfileActivity
        }
    }
}
