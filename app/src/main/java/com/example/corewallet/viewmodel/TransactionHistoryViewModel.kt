package com.example.corewallet.viewmodel.transactions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.response.TransactionResponse
import kotlinx.coroutines.launch

class TransactionHistoryViewModel : ViewModel() {

    // LiveData to hold the list of transactions
    private val _transactions = MutableLiveData<List<TransactionResponse>>()
    val transactions: LiveData<List<TransactionResponse>> get() = _transactions

    // LiveData to hold error messages
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    // Function to fetch transaction history
    fun fetchTransactionHistory() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getTransactionHistory()
                if (response.isSuccessful) {
                    _transactions.value = response.body() ?: emptyList()
                    _error.value = null
                } else {
                    _error.value = "Failed to load transactions"
                }
            } catch (e: Exception) {
                _error.value = "Network error: ${e.message}"
            }
        }
    }

    // Helper to convert month number to text label
    fun monthLabel(mm: String): String = when (mm) {
        "01" -> "JAN"; "02" -> "FEB"; "03" -> "MAR"; "04" -> "APR"
        "05" -> "MAY"; "06" -> "JUN"; "07" -> "JUL"; "08" -> "AUG"
        "09" -> "SEP"; "10" -> "OCT"; "11" -> "NOV"; "12" -> "DEC"
        else -> "UNK"
    }
}