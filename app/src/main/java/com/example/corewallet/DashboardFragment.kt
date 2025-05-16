package com.example.corewallet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.example.corewallet.api.UserResponse
import com.example.corewallet.databinding.FragmentDashboardBinding
import com.example.corewallet.PinConfirmationFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.example.corewallet.api.ApiClient

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(userId: Int, username: String, accountNumber: String, balance: Double): DashboardFragment {
            val fragment = DashboardFragment()
            val args = Bundle().apply {
                putInt("userId", userId)
                putString("username", username)
                putString("accountNumber", accountNumber)
                putDouble("balance", balance)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve user details from arguments
        val userId = arguments?.getInt("userId", -1) ?: -1
        val username = arguments?.getString("username", "") ?: ""
        val accountNumber = arguments?.getString("accountNumber", "") ?: ""
        val balance = arguments?.getDouble("balance", 0.0) ?: 0.0

        // Log to verify data
        println("DashboardFragment: userId=$userId, username=$username, accountNumber=$accountNumber, balance=$balance")

        // Display user details
        binding.helloText.text = "Hello, $username"
        binding.accountNumber.text = accountNumber
        binding.balanceAmount.text = "Rp. ${String.format("%.2f", balance)}"

        setupListeners(userId, username, accountNumber, balance)
    }

    private fun setupListeners(userId: Int, username: String, accountNumber: String, balance: Double) {
        // Settings button
        binding.settingsIcon.setOnClickListener {
            Toast.makeText(requireContext(), "Settings Clicked", Toast.LENGTH_SHORT).show()
        }

        // Copy account number
        binding.copyIcon.setOnClickListener {
            copyToClipboard(binding.accountNumber.text.toString())
            Toast.makeText(requireContext(), "Account number copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        // Logout button
        binding.logoutIcon.setOnClickListener {
            ApiClient.authService.logout().enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        ApiClient.getCookieJar().clearCookies()
                        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "Logout failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Transfer button
        binding.btnTransfer.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.fragment_container,
                    TransferFragment.newInstance(userId, username, accountNumber, balance)
                )
                addToBackStack(null) // Allow back navigation to DashboardFragment
                setReorderingAllowed(true)
            }
        }

        // Profile button
        binding.btnProfile.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.fragment_container,
                    AccountFragment.newInstance(userId),
                    AccountFragment::class.java.simpleName
                )
                addToBackStack(null)
                setReorderingAllowed(true)
            }
        }

        // Top-up button
        binding.btnTopUp.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.fragment_container,
                    PinConfirmationFragment(),
                    PinConfirmationFragment::class.java.simpleName
                )
                addToBackStack(null)
                setReorderingAllowed(true)
            }
        }

        // Withdraw button
        binding.btnWithdraw.setOnClickListener {
            Toast.makeText(requireContext(), "Withdraw clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Account Number", text)
        clipboardManager.setPrimaryClip(clipData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}