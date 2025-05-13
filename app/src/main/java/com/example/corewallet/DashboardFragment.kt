package com.example.corewallet

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.UserResponse
import com.example.corewallet.databinding.FragmentDashboardBinding
import com.example.corewallet.transfer.TransferFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(userId: Int, username: String, accountNumber: String, balance: Double): DashboardFragment {
            val fragment = DashboardFragment()
            val args = Bundle()
            args.putInt("userId", userId)
            args.putString("username", username)
            args.putString("accountNumber", accountNumber)
            args.putDouble("balance", balance)
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
        val username = arguments?.getString("username", "") ?: ""
        val accountNumber = arguments?.getString("accountNumber", "") ?: ""
        val balance = arguments?.getDouble("balance", 0.0) ?: 0.0

        // Display user details
        binding.helloText.text = "Hello, $username"
        binding.accountNumber.text = accountNumber
        binding.balanceAmount.text = "Rp. ${String.format("%.2f", balance)}"

        // copy account number
        binding.copyIcon

        setupListeners()
    }

    private fun setupListeners() {
        binding.settingsIcon.setOnClickListener {
            Toast.makeText(requireContext(), "Settings Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.copyIcon.setOnClickListener {
            copyToClipboard(binding.accountNumber.text.toString())
            Toast.makeText(requireContext(), "Account number copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        // logout
        binding.logoutIcon.setOnClickListener {
            ApiClient.authService.logout().enqueue(object : Callback<UserResponse> {
                override fun onResponse(
                    call: Call<UserResponse>,
                    response: Response<UserResponse>
                ) {
                    if (response.isSuccessful) {
                        ApiClient.getCookieJar().clearCookies()
                        Toast.makeText(
                            requireContext(),
                            "Logged out successfully",
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        Toast.makeText(requireContext(), "Logout failed", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        // Handle transfer button click
        binding.btnTransfer.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    TransferFragment(),
                    TransferFragment::class.java.simpleName
                )
                .addToBackStack(null)
                .commit()
        }

        //  onclick listener for profile menu
        binding.btnProfile.setOnClickListener {
            val mProfileFragment = AccountFragment()
            val mFragmentManager = parentFragmentManager as FragmentManager
            mFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    mProfileFragment,
                    AccountFragment::class.java.simpleName
                )
                .addToBackStack(null)
                .commit()
        }

        binding.btnTopUp.setOnClickListener {
            val mPin = PinConfirmationFragment()
            val mFragmentManager = parentFragmentManager as FragmentManager
            mFragmentManager.beginTransaction()
                .replace(
                    R.id.fragment_container,
                    mPin,
                    PinConfirmationFragment::class.java.simpleName
                )
                .addToBackStack(null)
                .commit()

            //Handle withdraw button click
            binding.btnWithdraw.setOnClickListener {
                Toast.makeText(requireContext(), "Withdraw clicked", Toast.LENGTH_SHORT).show()
            }

            //handle profile button click
            binding.btnProfile.setOnClickListener {
                Toast.makeText(requireContext(), "Profile clicked", Toast.LENGTH_SHORT).show()

            }
        }
    }

    // copy account number
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
