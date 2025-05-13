package com.example.corewallet.transfer

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TransferViewModel : ViewModel() {
    val selectedRecipient = MutableLiveData<String>()
    val selectedAccount = MutableLiveData<String>()
    val transferAmount = MutableLiveData<Double>()
    val pin = MutableLiveData<String>()
    val transferResult = MutableLiveData<Boolean>()
}