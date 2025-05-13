package com.example.corewallet

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.content.Intent
import android.util.Log
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
    }

    private fun setupListeners() {
        loginButton.setOnClickListener { performLogin() }
        goToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
            finish()
        }
    }

    private fun performLogin() {
        Log.d("LoginDebug", "Login button clicked")
        val email = emailInput.text.toString().trim()
        val password = passwordInput.text.toString().trim()
        Log.d("LoginDebug", "Email: $email, Password: $password")

        if (email.isBlank() || password.isBlank()) {
            showToast("Please fill in all fields")
            return
        }

        val request = LoginRequest(email, password)

        ApiClient.authService.login(request).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val user = response.body()?.user
                    if (user != null) {
                        Toast.makeText(
                            this@LoginActivity,
                            "Welcome, ${user.name}",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("userId", user.id_user)
                        intent.putExtra("accountNumber", user.account_number)
                        intent.putExtra("balance", user.balance)
                        intent.putExtra("username", user.name)
                        startActivity(intent)
                        finish()
                    } else {
                        showToast("Login failed: Empty user data")
                        Log.e("LoginDebug", "Login failed: Empty user object")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginDebug", "Login failed with code ${response.code()}")
                    Log.e("LoginDebug", "Error body: $errorBody")
                    showToast("Login failed: ${response.code()} - ${errorBody ?: "Unknown error"}")
                }
            }


            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                showToast("Network error: ${t.message}")
            }
        }

        )

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
