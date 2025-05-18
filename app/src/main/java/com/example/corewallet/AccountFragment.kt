package com.example.corewallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.util.Patterns
import androidx.fragment.app.Fragment
import com.example.corewallet.databinding.FragmentAccountBinding
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.UpdateProfileRequest
import com.example.corewallet.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private var isEditable = false
    private var userId: Int = -1
    private var currentUser: User? = null
    private var originalPin: String? = null
    private var originalPassword: String? = null

    companion object {
        fun newInstance(userId: Int): AccountFragment {
            val fragment = AccountFragment()
            val args = Bundle().apply {
                putInt("userId", userId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve userId from arguments
        userId = arguments?.getInt("userId", -1) ?: -1

        // Log to verify data
        println("AccountFragment: userId=$userId")

        // Get user data
        if (userId != -1) {
            loadUserData(userId)
        } else {
            Toast.makeText(requireContext(), "User ID is missing", Toast.LENGTH_SHORT).show()
        }

        // Set initial state of EditTexts
        binding.editDisplayName.isEnabled = isEditable
        binding.editPassword.isEnabled = isEditable
        binding.editEmail.isEnabled = isEditable
        binding.editPin.isEnabled = isEditable

        // Set up Edit Profile button
        binding.btnEditProfile.setOnClickListener {
            isEditable = !isEditable

            // Toggle EditText enabled state
            binding.editDisplayName.isEnabled = isEditable
            binding.editPassword.isEnabled = isEditable
            binding.editEmail.isEnabled = isEditable
            binding.editPin.isEnabled = isEditable

            if (isEditable) {
                binding.btnEditProfile.text = "Save Changes"
                binding.editPin.setText(originalPin) // Show real PIN when editing
                binding.editPassword.setText(originalPassword) // Show real password when editing
            } else {
                val nameInput = binding.editDisplayName.text.toString()
                val emailInput = binding.editEmail.text.toString()
                val pinInput = binding.editPin.text.toString()
                val passwordInput = binding.editPassword.text.toString()

                val updatedName = nameInput.takeIf { it.isNotBlank() }
                val updatedEmail = emailInput.takeIf { it.isNotBlank() }
                val updatedPin = if (pinInput == "••••••" || pinInput.isBlank()) {
                    originalPin
                } else {
                    pinInput.takeIf { it.isNotBlank() }
                }
                val updatedPassword = if (passwordInput == "••••••" || passwordInput.isBlank()) {
                    originalPassword
                } else {
                    passwordInput.takeIf { it.isNotBlank() }
                }

                // Validate Name
                if (nameInput.isBlank()) {
                    Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
                    resetUIAfterFailedValidation()
                    return@setOnClickListener
                }

                // Validate Email
                if (emailInput.isBlank()) {
                    Toast.makeText(requireContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show()
                    resetUIAfterFailedValidation()
                    return@setOnClickListener
                } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
                    Toast.makeText(requireContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                    resetUIAfterFailedValidation()
                    return@setOnClickListener
                }

                // Validate Password
                if (updatedPassword != null && updatedPassword.length < 8) {
                    Toast.makeText(requireContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                    resetUIAfterFailedValidation()
                    return@setOnClickListener
                }

                // Validate PIN
                if (updatedPin != null && !Regex("^\\d{6}$").matches(updatedPin)) {
                    Toast.makeText(requireContext(), "PIN must be exactly 6 digits", Toast.LENGTH_SHORT).show()
                    resetUIAfterFailedValidation()
                    return@setOnClickListener
                }


                val request = UpdateProfileRequest(
                    name = updatedName ?: currentUser?.name ?: "",
                    email = updatedEmail ?: currentUser?.email ?: "",
                    pin = updatedPin ?: currentUser?.pin ?: "",
                    password = updatedPassword // Will be null if unchanged
                )

                ApiClient.authService.updateUser(userId, request).enqueue(object : Callback<User> {
                    override fun onResponse(call: Call<User>, response: Response<User>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Profile updated successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            binding.btnEditProfile.text = "Edit Profile"
                            currentUser = response.body()

                            // Restore masked values
                            binding.editPin.setText("••••••")
                            binding.editPassword.setText("••••••")
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to update profile",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<User>, t: Throwable) {
                        Toast.makeText(
                            requireContext(),
                            "Network error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }

    private fun resetUIAfterFailedValidation() {
        isEditable = false
        binding.editDisplayName.isEnabled = false
        binding.editPassword.isEnabled = false
        binding.editEmail.isEnabled = false
        binding.editPin.isEnabled = false
        binding.btnEditProfile.text = "Edit Profile"

        resetFieldsToOriginal() // Reverts inputs to original values
    }

    private fun resetFieldsToOriginal() {
        binding.editDisplayName.setText(currentUser?.name)
        binding.editEmail.setText(currentUser?.email)
        binding.editPin.setText("••••••")
        binding.editPassword.setText("••••••")
    }

    private fun loadUserData(userId: Int) {
        ApiClient.authService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    currentUser = user
                    user?.let {
                        binding.editDisplayName.setText(it.name)
                        binding.editEmail.setText(it.email)

                        // Store original password
                        originalPassword = it.password
                        binding.editPassword.setText("••••••")

                        originalPin = it.pin
                        binding.editPin.setText("••••••")
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}