package com.example.corewallet.view.account

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.User
import com.example.corewallet.api.request.UpdateProfileRequest
import com.example.corewallet.databinding.FragmentAccountBinding
import com.example.corewallet.view.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import www.sanju.motiontoast.MotionToast
import www.sanju.motiontoast.MotionToastStyle
import java.io.File
import java.io.FileOutputStream

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private var isEditable = false
    private var userId: Int = -1
    private var currentUser: User? = null
    private var originalPin: String? = null
    private var originalPassword: String? = null
    private var tempImageFile: File? = null

    companion object {
        fun newInstance(userId: Int): AccountFragment {
            val fragment = AccountFragment()
            val args = Bundle().apply {
                putInt("userId", userId)
            }
            fragment.arguments = args
            return fragment
        }
        private const val STORAGE_PERMISSION_CODE = 100
    }

    private val pickImageLegacy = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            try {
                tempImageFile = File(requireContext().cacheDir, "profile_image_${System.currentTimeMillis()}.jpg")
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val outputStream = FileOutputStream(tempImageFile)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()
                println("File size: ${tempImageFile?.length()} bytes")
                if (tempImageFile?.length() == 0L) {
                    Toast.makeText(requireContext(), "Selected file is empty", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }
                uploadProfilePicture(tempImageFile!!)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error selecting image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
        setupView()
    }

    private fun setupView() {
        userId = arguments?.getInt("userId", -1) ?: -1
        println("AccountFragment: userId=$userId")

        if (userId != -1) {
            loadUserData(userId)
        } else {
            Toast.makeText(requireContext(), "User ID is missing", Toast.LENGTH_SHORT).show()
        }

        binding.editDisplayName.isEnabled = isEditable
        binding.editPassword.isEnabled = isEditable
        binding.editEmail.isEnabled = isEditable
        binding.editPin.isEnabled = isEditable
        binding.profilePict.isEnabled = isEditable

        binding.profilePict.setOnClickListener {
            if (isEditable) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                        pickImageLegacy.launch("image/*")
                    } else {
                        requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), STORAGE_PERMISSION_CODE)
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        pickImageLegacy.launch("image/*")
                    } else {
                        requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
                    }
                }
            }
        }

        binding.btnEditProfile.setOnClickListener {
            isEditable = !isEditable
            binding.editDisplayName.isEnabled = isEditable
            binding.editPassword.isEnabled = isEditable
            binding.editEmail.isEnabled = isEditable
            binding.editPin.isEnabled = isEditable
            binding.profilePict.isEnabled = isEditable

            if (isEditable) {
                binding.btnEditProfile.text = "Save Changes"
                binding.editPin.setText(originalPin)
                binding.editPassword.setText(originalPassword)
            } else {
                saveProfileChanges()
            }
        }

        binding.logoBtn.setOnClickListener {
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.nav_home
        }
    }

    private fun saveProfileChanges() {
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

        if (nameInput.isBlank()) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            resetUIAfterFailedValidation()
            return
        }

        if (emailInput.isBlank()) {
            Toast.makeText(requireContext(), "Email cannot be empty", Toast.LENGTH_SHORT).show()
            resetUIAfterFailedValidation()
            return
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            Toast.makeText(requireContext(), "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            resetUIAfterFailedValidation()
            return
        }

        if (updatedPassword != null && updatedPassword.length < 8) {
            Toast.makeText(requireContext(), "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            resetUIAfterFailedValidation()
            return
        }

        if (updatedPin != null && !Regex("^\\d{6}$").matches(updatedPin)) {
            Toast.makeText(requireContext(), "PIN must be exactly 6 digits", Toast.LENGTH_SHORT).show()
            resetUIAfterFailedValidation()
            return
        }

        val request = UpdateProfileRequest(
            name = updatedName ?: currentUser?.name ?: "",
            email = updatedEmail ?: currentUser?.email ?: "",
            pin = updatedPin ?: currentUser?.pin ?: "",
            password = updatedPassword,
            image_path = currentUser?.profile_picture
        )

        ApiClient.authService.updateUser(userId, request).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    binding.btnEditProfile.text = "Edit Profile"
                    currentUser = response.body()
                    binding.editPin.setText("••••••")
                    binding.editPassword.setText("••••••")
                    currentUser?.profile_picture?.let { loadImage(it) }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Toast.makeText(requireContext(), "Failed to update profile: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun uploadProfilePicture(imageFile: File) {
        // Check session validity
        Log.d("AccountFragment", "Uploading for userId: $userId")
        ApiClient.authService.getUserById(userId).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    performUpload(imageFile)
                } else {
                    ApiClient.getCookieJar().clearCookies()
                    Log.d("AccountFragment", "Session expired, clearing cookies")
                    Toast.makeText(requireContext(), "Session expired, please log in again", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error checking session: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun performUpload(imageFile: File) {
        val requestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("profile_picture", imageFile.name, requestBody)
        Log.d("AccountFragment", "Uploading 2 for userId: $userId")
        ApiClient.authService.uploadProfilePicture(userId, part).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    currentUser = response.body()
                    val newImagePath = currentUser?.profile_picture
                    currentUser?.profile_picture?.let { url ->
                        loadImage(url)
                        Toast.makeText(requireContext(), "Profile picture updated", Toast.LENGTH_SHORT).show()
                    }

                    val request = UpdateProfileRequest(
                        name = currentUser?.name ?: "",
                        email = currentUser?.email ?: "",
                        pin = currentUser?.pin ?: "",
                        password = null,
                        image_path = newImagePath
                    )

                    ApiClient.authService.updateUser(userId, request).enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            if (!response.isSuccessful) {
                                val error = response.errorBody()?.string()
                                Log.e("UpdateAfterUpload", "Failed to update image_path: $error")
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Log.e("UpdateAfterUpload", "Network error updating image_path: ${t.message}")
                        }
                    })
                }
                else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e("AccountFragment", "Failed to upload profile picture: ${response.code()} - $errorBody")
                    Toast.makeText(requireContext(), "Failed to upload profile picture: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {

                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun loadImage(url: String) {
        Glide.with(this)
            .load("${ApiClient.BASE_URL}$url")
            .placeholder(R.drawable.img_default_profile_pict)
            .error(R.drawable.img_default_profile_pict)
            .into(binding.profilePict)
    }

    private fun resetUIAfterFailedValidation() {
        isEditable = false
        binding.editDisplayName.isEnabled = false
        binding.editPassword.isEnabled = false
        binding.editEmail.isEnabled = false
        binding.editPin.isEnabled = false
        binding.profilePict.isEnabled = false
        binding.btnEditProfile.text = "Edit Profile"
        resetFieldsToOriginal()
    }

    private fun resetFieldsToOriginal() {
        binding.editDisplayName.setText(currentUser?.name)
        binding.editEmail.setText(currentUser?.email)
        binding.editPin.setText("••••••••")
        binding.editPassword.setText("••••••••")
        currentUser?.profile_picture?.let { loadImage(it) }
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
                        originalPassword = it.password
                        binding.editPassword.setText("••••••••")
                        originalPin = it.pin
                        binding.editPin.setText("••••••••")
                        it.profile_picture?.takeIf { url -> url.isNotBlank() }?.let { url -> loadImage(url) }
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Toast.makeText(requireContext(), "Failed to load user data: ${response.code()} - $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("AccountFragment", "Failed to load user data: ${t.message}")
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImageLegacy.launch("image/*")
        } else {
            Toast.makeText(requireContext(), "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        tempImageFile?.delete()
    }
}