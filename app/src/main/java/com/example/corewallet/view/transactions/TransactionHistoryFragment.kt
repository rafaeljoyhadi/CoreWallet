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
import androidx.lifecycle.lifecycleScope
import com.example.corewallet.R
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.response.TransactionResponse
import com.example.corewallet.databinding.FragmentTransactionHistoryBinding
import kotlinx.coroutines.launch

class TransactionHistoryFragment : Fragment() {

    private lateinit var binding: FragmentTransactionHistoryBinding

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
        fetchTransactions()
    }

    private fun fetchTransactions() {
        lifecycleScope.launch {
            try {
                val response = ApiClient.apiService.getTransactionHistory()
                if (response.isSuccessful) {
                    val transactions = response.body() ?: emptyList()
                    populateTransactionItems(transactions)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Failed to load transactions",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Network error: ${e.message}", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun populateTransactionItems(transactions: List<TransactionResponse>) {
        // ① Read current user’s account number from SharedPreferences
        val prefs = requireContext()
            .getSharedPreferences("coreWalletPrefs", Context.MODE_PRIVATE)
        val currentUserAcct = prefs.getString("accountNumber", "") ?: ""

        val container = binding.containerTransactions
        container.removeAllViews()

        for (tx in transactions) {
            val item = layoutInflater.inflate(R.layout.item_history, container, false)

            // — Parse date —
            val datePart = tx.datetime.substringBefore("T")
            val parts = datePart.split("-")
            val year = parts[0]
            val month = monthLabel(parts[1])
            val day = parts[2]

            item.findViewById<TextView>(R.id.tvDay).text = day
            item.findViewById<TextView>(R.id.tvMonth).text = month
            item.findViewById<TextView>(R.id.tvYear).text = year

            val isIncome = tx.transaction_type == "Top-Up"
                    || tx.receiver_account_number == currentUserAcct

            // — Other party’s name —
            val other = when {
                tx.transaction_type == "Top-Up" -> "CoreWallet"
                isIncome                       -> tx.sender_username
                tx.receiver_username != null   -> tx.receiver_username
                else                           -> "CoreWallet"
            }

            item.findViewById<TextView>(R.id.tvName).text = other

            // — Format amount —
            val formatted = "IDR ${"%,.2f".format(tx.amount).replace(",", ".")}"
            val tvAmount = item.findViewById<TextView>(R.id.tvAmount)
            tvAmount.text = formatted

            // — Icon & color based on isIncome —
            val iconRes = if (isIncome) R.drawable.ic_income else R.drawable.ic_expense
            item.findViewById<View>(R.id.iconBg).setBackgroundResource(iconRes)

            val colorId = if (isIncome) R.color.primary_green else R.color.primary_red
            tvAmount.setTextColor(ContextCompat.getColor(requireContext(), colorId))

            // — Note —
            item.findViewById<TextView>(R.id.tvNote).text =
                if (!tx.note.isNullOrBlank()) "${tx.category_name} - ${tx.note}" else tx.category_name


            container.addView(item)
        }
    }


    // Helper to convert month number to text label
    private fun monthLabel(mm: String): String = when (mm) {
        "01" -> "JAN"; "02" -> "FEB"; "03" -> "MAR"; "04" -> "APR"
        "05" -> "MAY"; "06" -> "JUN"; "07" -> "JUL"; "08" -> "AUG"
        "09" -> "SEP"; "10" -> "OCT"; "11" -> "NOV"; "12" -> "DEC"
        else -> "UNK"
    }
}