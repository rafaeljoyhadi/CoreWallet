package com.example.corewallet.view.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.corewallet.api.UserResponse
import com.example.corewallet.databinding.FragmentDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.example.corewallet.R
import com.example.corewallet.TransferFragment
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.User
import com.example.corewallet.view.account.AccountFragment
import com.example.corewallet.view.login.LoginActivity
import com.example.corewallet.view.topup.TopUpFragment
import com.example.corewallet.view.transactions.PinConfirmationFragment
import com.example.corewallet.view.withdraw.WithdrawFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(userId: Int): DashboardFragment {
            val fragment = DashboardFragment()
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
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = arguments?.getInt("userId", -1) ?: -1
        println("DashboardFragment: Loading user with ID = $userId")
        if (userId != -1) {
            loadUserData(userId)
            println("DashboardFragment: Loading user with ID = $userId")
        } else {
            Toast.makeText(requireContext(), "User ID is missing", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadUserData(userId: Int) {
        ApiClient.authService.getUserById(userId).enqueue(object : retrofit2.Callback<User> {
            override fun onResponse(call: retrofit2.Call<User>, response: retrofit2.Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        binding.helloText.text = "Hello, ${user.name}"
                        binding.accountNumber.text = user.account_number
                        binding.balanceAmount.text = "Rp. ${String.format("%,.2f", user.balance)}"

                        // Also update listener values with live data
                        setupListeners(
                            userId = user.id_user,
                            username = user.name,
                            accountNumber = user.account_number,
                            balance = user.balance.toDouble()
                        )
                    }
                } else {
                    // Log the actual HTTP status code and error message
                    val errorCode = response.code()
                    val errorBody = response.errorBody()?.string() ?: "No error body"

                    println("API Error: Code $errorCode, Body: $errorBody")

                    Toast.makeText(requireContext(), "Failed to load user data (Code $errorCode)", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<User>, t: Throwable) {
                Toast.makeText(requireContext(), "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.nav_account
        }

        // Top-up button
        binding.btnTopUp.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.fragment_container,
                    TopUpFragment(),
                    TopUpFragment::class.java.simpleName
                )
                addToBackStack(null)
                setReorderingAllowed(true)
            }
        }

        // Withdraw button
        binding.btnWithdraw.setOnClickListener {
            parentFragmentManager.commit {
                replace(
                    R.id.fragment_container,
                    WithdrawFragment(),
                    WithdrawFragment::class.java.simpleName
                )
                addToBackStack(null)
                setReorderingAllowed(true)
            }
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