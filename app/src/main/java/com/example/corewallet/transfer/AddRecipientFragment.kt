package com.example.corewallet.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.AddContactRequest
import com.example.corewallet.api.AddContactResponse
import com.example.corewallet.databinding.FragmentAddRecipientBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddRecipientFragment : Fragment() {
    private var _binding: FragmentAddRecipientBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRecipientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonAddBeneficiary.setOnClickListener {
            val accountNumber = binding.editTextAccountNumber.text.toString().trim()
            if (accountNumber.isEmpty()) {
                binding.editTextAccountNumber.error = "Please enter a valid account number."
                return@setOnClickListener
            }

            binding.buttonAddBeneficiary.isEnabled = false
            val request = AddContactRequest(accountNumber = accountNumber)

            ApiClient.apiService.addContact(request)
                .enqueue(object : Callback<AddContactResponse> {
                    override fun onResponse(
                        call: Call<AddContactResponse>,
                        response: Response<AddContactResponse>
                    ) {
                        binding.buttonAddBeneficiary.isEnabled = true

                        when {
                            response.isSuccessful -> {
                                val msg = response.body()?.message
                                    ?: "Recipient added successfully"
                                showResultDialog(
                                    title = "Success",
                                    message = msg
                                )
                            }

                            response.code() == 404 -> showResultDialog(
                                title = "Failed",
                                message = "Account not found."
                            )

                            response.code() == 409 -> showResultDialog(
                                title = "Failed",
                                message = "Contact already exists."
                            )

                            response.code() == 400 -> showResultDialog(
                                title = "Failed",
                                message = "Cannot add yourself as contact."
                            )

                            else -> showResultDialog(
                                title = "Error",
                                message = "Server error: ${response.code()}"
                            )
                        }
                    }

                    override fun onFailure(call: Call<AddContactResponse>, t: Throwable) {
                        binding.buttonAddBeneficiary.isEnabled = true
                        showResultDialog(
                            title = "Error",
                            message = "Network error: ${t.localizedMessage}"
                        )
                    }
                })
        }
    }

    private fun showResultDialog(
        title: String,
        message: String,
        onClose: (() -> Unit)? = null
    ) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_result, null)

        val dialog = android.app.Dialog(
            requireContext(),
            android.R.style.Theme_Translucent_NoTitleBar
        ).apply {
            setContentView(dialogView)
            setCancelable(true)
            setCanceledOnTouchOutside(true)

            // 1) Expand to full screen
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // 2) Let the content draw behind system bars
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            )
            window?.decorView?.fitsSystemWindows = false

            // 3) Hide status + navigation bars with WindowInsetsControllerCompat
            window?.decorView?.let { decorView ->
                val controller = WindowInsetsControllerCompat(window!!, decorView)
                controller.hide(
                    WindowInsetsCompat.Type.statusBars() or
                            WindowInsetsCompat.Type.navigationBars()
                )
                // allow swipe‐in to show them temporarily
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        dialogView.findViewById<TextView>(R.id.tv_popup_title).text = title
        dialogView.findViewById<TextView>(R.id.tv_popup_message).text = message

        dialogView.findViewById<ImageView>(R.id.btn_popup_close)
            .setOnClickListener {
                dialog.dismiss()
                onClose?.invoke()
            }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
