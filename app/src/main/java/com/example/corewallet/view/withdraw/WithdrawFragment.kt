package com.example.corewallet.view.withdraw

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.viewModels
import com.example.corewallet.R
import com.example.corewallet.databinding.FragmentTopUpBinding
import com.example.corewallet.databinding.FragmentWithdrawBinding
import com.example.corewallet.view.topup.WithdrawViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class WithdrawFragment : Fragment() {
    private lateinit var binding: FragmentWithdrawBinding
    private val viewModel: WithdrawViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWithdrawBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.confirmBtn.setOnClickListener {
            val amountText = binding.amountET.text.toString()
            binding.confirmBtn.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            viewModel.performWithdraw(amountText)
        }
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        binding.logoBtn.setOnClickListener {
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.nav_home
        }
    }

    private fun setupObservers() {
        viewModel.withdrawResult.observe(viewLifecycleOwner) { result ->
            binding.confirmBtn.isEnabled = true // Re-enable button
            binding.progressBar.visibility = View.GONE // Hide loading
            result?.let {
                when {
                    it.isSuccess -> {
                        val response = it.getOrNull()
                        if (response?.new_balance != null) {
                            val message = "Withdraw successful! \nNew balance: IDR ${"%,.2f".format(response.new_balance).replace(",", ".")}"
                            showResultDialog(
                                title = "Success",
                                message = message,
                                onClose = { binding.amountET.text.clear() } // Clear input on dialog close
                            )
                        } else {
                            showResultDialog(
                                title = "Error",
                                message = response?.message ?: "Withdraw failed"
                            )
                        }
                    }
                    it.isFailure -> {
                        val exception = it.exceptionOrNull()
                        showResultDialog(
                            title = "Error",
                            message = exception?.message ?: "Withdraw failed"
                        )
                    }
                }
            }
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

            // Expand to full screen
            window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            // Let the content draw behind system bars
            window?.setFlags(
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            )
            window?.decorView?.fitsSystemWindows = false

            // Hide status + navigation bars with WindowInsetsControllerCompat
            window?.decorView?.let { decorView ->
                val controller = WindowInsetsControllerCompat(window!!, decorView)
                controller.hide(
                    WindowInsetsCompat.Type.statusBars() or
                            WindowInsetsCompat.Type.navigationBars()
                )
                // Allow swipe-in to show them temporarily
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
}