package com.example.corewallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.corewallet.databinding.FragmentAddRecipientBinding

class AddRecipientFragment : Fragment() {

    private var _binding: FragmentAddRecipientBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddRecipientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.backButton.setOnClickListener {
//           // Navigate back to RecipientSelectionFragment
//        }

        binding.buttonAddBeneficiary.setOnClickListener {
            val accountNumber = binding.editTextAccountNumber.text.toString().trim()

            if (accountNumber.isEmpty()) {
                binding.editTextAccountNumber.error = "Please enter a valid account number."
                return@setOnClickListener
            }

            val isSuccess = accountNumber.length == 10 // example condition
            val name = "Recipient Name" // You could also simulate name fetching here

            if (isSuccess) {
                showResultDialog(
                    title = "Recipient Added",
                    message = "Successfully added:\n$name\n$accountNumber"
                )
            } else {
                showResultDialog(
                    title = "Failed",
                    message = "Could not add recipient. Please check the account number."
                )
            }

        }
    }
    private fun showResultDialog(title: String, message: String) {
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_result, null)

        val dialog = android.app.Dialog(requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
        dialog.setContentView(dialogView)
        dialog.setCancelable(true) // Allow dismiss by tapping outside

        dialogView.findViewById<TextView>(R.id.tv_popup_title).text = title
        dialogView.findViewById<TextView>(R.id.tv_popup_message).text = message

        dialogView.findViewById<ImageView>(R.id.btn_popup_close).setOnClickListener {
            dialog.dismiss()
        }

        // Optional: tap outside popup container to dismiss
        dialogView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}