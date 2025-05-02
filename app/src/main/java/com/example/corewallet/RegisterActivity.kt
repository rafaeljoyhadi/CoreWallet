package com.example.corewallet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.RegisterRequest
import com.example.corewallet.api.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var fullNameInput: EditText
    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var pinInput: EditText
    private lateinit var registerButton: Button
    private lateinit var goToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        fullNameInput = findViewById(R.id.fullNameInput)
        usernameInput = findViewById(R.id.usernameInput)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput)
        pinInput = findViewById(R.id.pinInput)
        registerButton = findViewById(R.id.registerButton)
        goToLogin = findViewById(R.id.goToLogin)
    }

    private fun setupListeners() {
        registerButton.setOnClickListener {
            registerUser()
        }

        goToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val fullName = fullNameInput.text.toString().trim()
        val username = usernameInput.text.toString().trim()
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()
        val pin = pinInput.text.toString()

        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || pin.isEmpty()) {
            showToast("Please fill in all fields")
            return
        }

        if (password != confirmPassword) {
            showToast("Passwords do not match")
            return
        }

        val registerRequest = RegisterRequest(
            username = username,
            password = password,
            email = email,
            name = fullName,
            pin = pin
        )

        val api = ApiClient.authService
        api.register(registerRequest).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Register Complete! Please log in.",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Registration failed"
                    showToast(errorMsg)
                    Log.e("RegisterActivity", "Error: $errorMsg")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                showToast("Registration error: ${t.message}")
                Log.e("RegisterActivity", "Network error", t)
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
