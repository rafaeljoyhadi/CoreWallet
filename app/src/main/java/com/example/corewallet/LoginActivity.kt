package com.example.corewallet

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.corewallet.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var topUpTestButton: Button
    private lateinit var goToRegister: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        emailInput = findViewById(R.id.loginUsernameInput)
        passwordInput = findViewById(R.id.loginPasswordInput)
        loginButton = findViewById(R.id.loginButton)
        goToRegister = findViewById(R.id.goToRegister)
        topUpTestButton = findViewById(R.id.topUpTestButton)
    }

    private fun setupListeners() {
        loginButton.setOnClickListener { performLogin() }
        topUpTestButton.setOnClickListener { performTopUp() }
        goToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun performLogin() {
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()

        if (email.isBlank() || password.isBlank()) {
            showToast("Please fill in all fields")
            return
        }

        val request = LoginRequest(email, password)

        ApiClient.authService.login(request).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()?.user
                    showToast("Welcome, ${user?.name ?: "User"}")
                    Log.d("LoginActivity", "Login successful: $user")
                } else {
                    val error = response.errorBody()?.string() ?: "Login failed"
                    showToast(error)
                    Log.e("LoginActivity", "Login error: $error")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                showToast("Login failed: ${t.message}")
                Log.e("LoginActivity", "Login failed", t)
            }
        })
    }

    private fun performTopUp() {
        val topUpRequest = TopupRequest(500.0)

        ApiClient.apiService.topUp(topUpRequest).enqueue(object : Callback<TopupResponse> {
            override fun onResponse(call: Call<TopupResponse>, response: Response<TopupResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    showToast("Top-Up Success: New Balance = ${result?.new_balance}")
                    Log.d("LoginActivity", "Top-Up Response: $result")
                } else {
                    showToast("Unauthorized or Session expired")
                    Log.e(
                        "LoginActivity",
                        "Top-Up failed: ${response.code()} ${response.message()}"
                    )
                }
            }

            override fun onFailure(call: Call<TopupResponse>, t: Throwable) {
                showToast("Top-Up error: ${t.message}")
                Log.e("LoginActivity", "Top-Up error", t)
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
