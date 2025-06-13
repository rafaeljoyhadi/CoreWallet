package com.example.corewallet.view.topup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.request.TopUpRequest
import com.example.corewallet.api.response.TopUpResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopUpViewModel : ViewModel() {

    // LiveData for top-up result, using Result to match TransferViewModel
    private val _topUpResult = MutableLiveData<Result<TopUpResponse?>>()
    val topUpResult: LiveData<Result<TopUpResponse?>> get() = _topUpResult

    fun performTopUp(amountText: String) {
        // Validate input
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _topUpResult.postValue(Result.failure(RuntimeException("Please enter a valid amount")))
            return
        }

        println("=== performTopUp CALLED ===")
        val request = TopUpRequest(amount)

        ApiClient.apiService.topUp(request).enqueue(object : Callback<TopUpResponse> {
            override fun onResponse(call: Call<TopUpResponse>, response: Response<TopUpResponse>) {
                println("=== TopUp Response Debug ===")
                println("Code: ${response.code()}")
                println("isSuccessful: ${response.isSuccessful}")
                println("Body: ${response.body()}")
                println("ErrorBody: ${response.errorBody()?.string()}")
                println("Message: ${response.message()}")
                println("===============================")

                if (response.isSuccessful && response.body() != null) {
                    _topUpResult.postValue(Result.success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                        ?: "Failed with status ${response.code()}"
                    _topUpResult.postValue(Result.failure(RuntimeException(errorMessage)))
                }
            }

            override fun onFailure(call: Call<TopUpResponse>, t: Throwable) {
                println("=== TopUp FAILURE ===")
                println("Reason: ${t.message}")
                println("========================")
                _topUpResult.postValue(Result.failure(RuntimeException("Network error: ${t.message}")))
            }
        })
    }
}