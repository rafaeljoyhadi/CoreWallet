package com.example.corewallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.corewallet.databinding.FragmentTransferFormBinding
import com.example.corewallet.transfer.TransferViewModel
import java.text.NumberFormat
import java.util.Locale

class TransferFormFragment : Fragment() {

    private var _binding: FragmentTransferFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TransferViewModel

    //    TODO : GET SENDER BALANCE
    companion object {
        fun newInstance(
            receiverName: String,
            receiverAccountNumber: String,
            receiverProfilePicture: String?,
            receiverBalance: Double
        ): TransferFormFragment {
            val fragment = TransferFormFragment()
            val args = Bundle().apply {
                putString("contactName", receiverName)
                putString("contactAccountNumber", receiverAccountNumber)
                putString("contactProfilePicture", receiverProfilePicture)
                putDouble("receiverBalance", receiverBalance)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TransferViewModel::class.java)

        val contactName = arguments?.getString("contactName", "") ?: ""
        val contactAccountNumber = arguments?.getString("contactAccountNumber", "") ?: ""
        val contactProfilePicture = arguments?.getString("contactProfilePicture")
        val userBalance = arguments?.getDouble("userBalance", 0.0) ?: 0.0

        println("TransferFormFragment: contactName=$contactName, contactAccountNumber=$contactAccountNumber, contactProfilePicture=$contactProfilePicture, userBalance=$userBalance")

        binding.recipientName.text = contactName
        binding.recipientAccNum.text = contactAccountNumber
        if (!contactProfilePicture.isNullOrEmpty()) {
            Glide.with(this)
                .load(contactProfilePicture)
                .placeholder(R.drawable.img_default_profile_pict)
                .error(R.drawable.img_default_profile_pict)
                .into(binding.recipientProfilePhoto)
        } else {
            binding.recipientProfilePhoto.setImageResource(R.drawable.img_default_profile_pict)
        }

        val categories = arrayOf("Personal", "Business", "Gift", "Other")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerTransactionCategory.adapter = adapter

        binding.transferButton.setOnClickListener {
            val amount = binding.amountInput.text.toString().toDoubleOrNull() ?: 0.0
            val description = binding.transferDescInput.text.toString().trim()
            val category = binding.spinnerTransactionCategory.selectedItem.toString()

            if (amount <= 0) {
                Toast.makeText(requireContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (amount > userBalance) {
                Toast.makeText(requireContext(), "Insufficient balance", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.performTransfer(
                amount = amount,
                recipientAccountNumber = contactAccountNumber,
                description = description,
                category = category
            )

            Toast.makeText(requireContext(), "Transfer initiated: $amount to $contactName", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}