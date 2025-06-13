package com.example.corewallet.viewmodel

import com.example.corewallet.api.response.TransferResponse
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.corewallet.api.*
import com.example.corewallet.api.request.TransferRequest
import com.example.corewallet.api.response.ContactResponse
import com.example.corewallet.models.TransactionCategory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransferViewModel : ViewModel() {

    // Now nullable list
    private val _contacts = MutableLiveData<List<ContactResponse>?>(null)
    val contacts: LiveData<List<ContactResponse>?> = _contacts

    private val _categories = MutableLiveData<List<TransactionCategory>>(emptyList())
    val categories: LiveData<List<TransactionCategory>> = _categories

    private val _transferResult = MutableLiveData<Result<TransferResponse?>>()
    val transferResult: LiveData<Result<TransferResponse?>> = _transferResult

    fun loadContacts() {
        ApiClient.apiService.getContacts().enqueue(object : Callback<List<ContactResponse>> {
            override fun onResponse(
                call: Call<List<ContactResponse>>,
                response: Response<List<ContactResponse>>
            ) {
                // response.body() is List<ContactResponse>?
                _contacts.value = response.body()
            }

            override fun onFailure(call: Call<List<ContactResponse>>, t: Throwable) {
                _contacts.value = null
            }
        })
    }

    fun loadCategories() {
        ApiClient.apiService.getTransactionCategories()
            .enqueue(object : Callback<List<TransactionCategory>> {
                override fun onResponse(
                    call: Call<List<TransactionCategory>>,
                    response: Response<List<TransactionCategory>>
                ) {
                    _categories.value = response.body() ?: emptyList()
                }

                override fun onFailure(call: Call<List<TransactionCategory>>, t: Throwable) {
                    _categories.value = emptyList()
                }
            })
    }

    fun performTransfer(
        amount: Double,
        recipientAccountNumber: String,
        description: String,
        category: String
    ) {
        println("=== performTransfer CALLED ===")
        val note = if (description.isNotBlank()) description else "No notes"
        val request = TransferRequest(
            recipientAccountNumber = recipientAccountNumber,
            amount = amount,
            note = note,
            categoryName = category
        )

        ApiClient.apiService.transferToUser(request)
            .enqueue(object : Callback<TransferResponse> {
                override fun onResponse(
                    call: Call<TransferResponse>,
                    response: Response<TransferResponse>
                ) {
                    println("=== Transfer Response Debug ===")
                    println("Code: ${response.code()}")
                    println("isSuccessful: ${response.isSuccessful}")
                    println("Body: ${response.body()}")
                    println("ErrorBody: ${response.errorBody()?.string()}")
                    println("Message: ${response.message()}")
                    println("===============================")

                    if (response.isSuccessful && response.body() != null) {
                        _transferResult.postValue(Result.success(response.body()!!))
                    } else {
                        val errorMessage = response.errorBody()?.string()
                            ?: "Failed with status ${response.code()}"
                        _transferResult.postValue(Result.failure(RuntimeException(errorMessage)))
                    }
                }

                override fun onFailure(call: Call<TransferResponse>, t: Throwable) {
                    println("=== Transfer FAILURE ===")
                    println("Reason: ${t.message}")
                    println("========================")
                    _transferResult.postValue(
                        Result.failure(RuntimeException("Network error: ${t.message}"))
                    )
                }

            })
    }




}
