package com.example.corewallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.UpdateProfileRequest
import com.example.corewallet.databinding.FragmentAccountBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private var isEditable = false
    private var userId: Int = -1

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

        //TODO get user data

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
            } else {
                // Save changes to backend
                val request = UpdateProfileRequest(
                    name = binding.editDisplayName.text.toString().takeIf { it.isNotBlank() },
                    email = binding.editEmail.text.toString().takeIf { it.isNotBlank() },
                    password = binding.editPassword.text.toString().takeIf { it.isNotBlank() },
                    pin = binding.editPin.text.toString().takeIf { it.isNotBlank() }
                )

                ApiClient.authService.updateProfile(request).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Profile updated successfully!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Failed to update profile: ${response.code()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        binding.btnEditProfile.text = "Edit Profile"
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(
                            requireContext(),
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.btnEditProfile.text = "Edit Profile"
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}