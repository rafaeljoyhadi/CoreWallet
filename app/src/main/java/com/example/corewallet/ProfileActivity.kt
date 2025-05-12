package com.example.corewallet

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.UpdateProfileRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var editDisplayName: EditText
    private lateinit var editPassword: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPin: EditText
    private lateinit var btnEditProfile: Button

    private var isEditable = false

    private var userId: Int = -1 // You'll get this from login intent or session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // UI References
        editDisplayName = findViewById(R.id.editDisplayName)
        editPassword = findViewById(R.id.editPassword)
        editEmail = findViewById(R.id.editEmail)
        editPin = findViewById(R.id.editPin)
        btnEditProfile = findViewById(R.id.btnEditProfile)

        // Get user ID from intent or session (must match login)
        userId = intent.getIntExtra("userId", -1) // You can change to SessionManager if needed

        btnEditProfile.setOnClickListener {
            isEditable = !isEditable

            editDisplayName.isEnabled = isEditable
            editPassword.isEnabled = isEditable
            editEmail.isEnabled = isEditable
            editPin.isEnabled = isEditable

            if (isEditable) {
                btnEditProfile.text = "Save Changes"
            } else {
                val request = UpdateProfileRequest(
                    name = editDisplayName.text.toString().takeIf { it.isNotBlank() },
                    email = editEmail.text.toString().takeIf { it.isNotBlank() },
                    password = editPassword.text.toString().takeIf { it.isNotBlank() },
                    pin = editPin.text.toString().takeIf { it.isNotBlank() }
                )

                ApiClient.authService.updateProfile(request)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@ProfileActivity,
                                    "Profile updated successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@ProfileActivity,
                                    "Failed to update profile",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            btnEditProfile.text = "Edit Profile"
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(
                                this@ProfileActivity,
                                "Error: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                            btnEditProfile.text = "Edit Profile"
                        }
                    })
            }
        }
    }
}
