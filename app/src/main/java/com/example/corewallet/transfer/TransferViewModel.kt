package com.example.corewallet.transfer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransferViewModel : ViewModel() {
    val selectedRecipient = MutableLiveData<String>()
    val selectedAccount = MutableLiveData<String>()
    val transferAmount = MutableLiveData<Double>()
    val pin = MutableLiveData<String>()
    val transferResult = MutableLiveData<Boolean>()

    fun performTransfer(
        amount: Double,
        recipientAccountNumber: String,
        description: String,
        category: String
    ) {
        // TODO: Implement backend API call to process transfer
        println("Transfer: amount=$amount, recipient=$recipientAccountNumber, description=$description, category=$category")
        // Example: Call Retrofit service to send transfer request
    }
}