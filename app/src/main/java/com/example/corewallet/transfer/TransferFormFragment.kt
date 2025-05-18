package com.example.corewallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.corewallet.databinding.FragmentTransferFormBinding
import com.example.corewallet.transfer.TransferViewModel

class TransferFormFragment : Fragment() {

    private var _binding: FragmentTransferFormBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: TransferViewModel

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
                putDouble("userBalance", receiverBalance)
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

        val contactName = arguments?.getString("contactName").orEmpty()
        val contactAccountNumber = arguments?.getString("contactAccountNumber").orEmpty()
        val contactProfilePicture = arguments?.getString("contactProfilePicture")

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

        // Populate spinner with categories
        viewModel.categories.observe(viewLifecycleOwner) { categoryList ->
            val names = categoryList.map { it.category_name }
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, names)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerTransactionCategory.adapter = adapter
        }
        viewModel.loadCategories()

        // Observe the transfer result
        viewModel.transferResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Transfer Complete", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            result.onFailure {
                Toast.makeText(requireContext(), "Transfer failed, please try again", Toast.LENGTH_SHORT).show()
            }
        }


        binding.transferButton.setOnClickListener {
            val amount = binding.amountInput.text.toString().toDoubleOrNull() ?: 0.0
            val description = binding.transferDescInput.text.toString().trim()
            val category = binding.spinnerTransactionCategory.selectedItem.toString()

            if (amount < 10000) {
                Toast.makeText(requireContext(), "Minimum transfer amount is Rp10,000", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Kick off the API call; UI reacts in the observer above
            viewModel.performTransfer(
                amount = amount,
                recipientAccountNumber = contactAccountNumber,
                description = description,
                category = category
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
