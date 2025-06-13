package com.example.corewallet.view.transactions

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.corewallet.R
import com.example.corewallet.api.response.TransactionResponse
import com.example.corewallet.databinding.FragmentTransactionHistoryBinding
import com.example.corewallet.viewmodel.transactions.TransactionHistoryViewModel

class TransactionHistoryFragment : Fragment() {

    private lateinit var binding: FragmentTransactionHistoryBinding
    private val viewModel: TransactionHistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.fetchTransactionHistory()
    }

    private fun setupObservers() {
        // Observe transactions LiveData
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            populateTransactionItems(transactions)
        }

        // Observe error LiveData
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun populateTransactionItems(transactions: List<TransactionResponse>) {
        // Read current user's account number from SharedPreferences
        val prefs = requireContext().getSharedPreferences("coreWalletPrefs", Context.MODE_PRIVATE)
        val currentUserAcct = prefs.getString("accountNumber", "") ?: ""

        val container = binding.containerTransactions
        container.removeAllViews()

        for (tx in transactions) {
            val item = layoutInflater.inflate(R.layout.item_history, container, false)

            // Parse date
            val datePart = tx.datetime.substringBefore("T")
            val parts = datePart.split("-")
            val year = parts[0]
            val month = viewModel.monthLabel(parts[1]) // Use ViewModel's monthLabel
            val day = parts[2]

            item.findViewById<TextView>(R.id.tvDay).text = day
            item.findViewById<TextView>(R.id.tvMonth).text = month
            item.findViewById<TextView>(R.id.tvYear).text = year

            val isIncome = tx.transaction_type == "Top-Up" || tx.receiver_account_number == currentUserAcct

            // Other party's name
            val other = when {
                tx.transaction_type == "Top-Up" -> "CoreWallet"
                isIncome -> tx.sender_username
                tx.receiver_username != null -> tx.receiver_username
                else -> "CoreWallet"
            }

            item.findViewById<TextView>(R.id.tvName).text = other

            // Format amount
            val formatted = "IDR ${"%,.2f".format(tx.amount).replace(",", ".")}"
            val tvAmount = item.findViewById<TextView>(R.id.tvAmount)
            tvAmount.text = formatted

            // Icon & color based on isIncome
            val iconRes = if (isIncome) R.drawable.ic_income else R.drawable.ic_expense
            item.findViewById<View>(R.id.iconBg).setBackgroundResource(iconRes)

            val colorId = if (isIncome) R.color.primary_green else R.color.primary_red
            tvAmount.setTextColor(ContextCompat.getColor(requireContext(), colorId))

            // Note
            item.findViewById<TextView>(R.id.tvNote).text =
                if (!tx.note.isNullOrBlank()) "${tx.category_name} - ${tx.note}" else tx.category_name

            container.addView(item)
        }
    }
}