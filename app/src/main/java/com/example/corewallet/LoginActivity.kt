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
        topUpTestButton = findViewById(R.id.topUpTestButton)
    }

    private fun setupListeners() {
        loginButton.setOnClickListener { performLogin() }
//        topUpTestButton.setOnClickListener { performTopUp() }
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

                        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                        intent.putExtra("username", user.name)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    showToast("Login failed")
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                showToast("Network error: ${t.message}")
            }
        })

    }

//    private fun performTopUp() {
//        val topUpRequest = TopupRequest(500.0)
//
//        ApiClient.apiService.topUp(topUpRequest).enqueue(object : Callback<TopupResponse> {
//            override fun onResponse(call: Call<TopupResponse>, response: Response<TopupResponse>) {
//                if (response.isSuccessful) {
//                    val topupMessage = response.body()?.message
//                    Toast.makeText(
//                        this@LoginActivity,
//                        "Topup Success: $topupMessage",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    showToast("Topup failed or unauthorized")
//                }
//            }
//
//            override fun onFailure(call: Call<TopupResponse>, t: Throwable) {
//                showToast("Topup error: ${t.message}")
//            }
//        })
//
//    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
