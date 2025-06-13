package com.example.corewallet.view.topup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.corewallet.api.ApiClient
import com.example.corewallet.api.request.WithdrawRequest
import com.example.corewallet.api.response.WithdrawResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WithdrawViewModel : ViewModel() {

    // LiveData for top-up result, using Result to match TransferViewModel
    private val _withdrawResult = MutableLiveData<Result<WithdrawResponse>>()
    val withdrawResult: LiveData<Result<WithdrawResponse>> get() = _withdrawResult

    fun performWithdraw(amountText: String) {
        // Validate input
        val amount = amountText.toDoubleOrNull()
        if (amount == null || amount <= 0 || amount < 10000) {
            _withdrawResult.postValue(Result.failure(RuntimeException("Please enter a valid amount. Minimum withdraw is IDR 10.000")))
            return
        }

        println("=== performWithdraw CALLED ===")
        val request = WithdrawRequest(amount)

        ApiClient.apiService.withdraw(request).enqueue(object : Callback<WithdrawResponse> {
            override fun onResponse(call: Call<WithdrawResponse>, response: Response<WithdrawResponse>) {
                println("=== Withdraw Response Debug ===")
                println("Code: ${response.code()}")
                println("isSuccessful: ${response.isSuccessful}")
                println("Body: ${response.body()}")
                println("ErrorBody: ${response.errorBody()?.string()}")
                println("Message: ${response.message()}")
                println("===============================")

                if (response.isSuccessful && response.body() != null) {
                    _withdrawResult.postValue(Result.success(response.body()!!))
                } else {
                    val errorMessage = response.errorBody()?.string()
                        ?: "Failed with status ${response.code()}"
                    _withdrawResult.postValue(Result.failure(RuntimeException(errorMessage)))
                }
            }

            override fun onFailure(call: Call<WithdrawResponse>, t: Throwable) {
                println("=== Withdraw FAILURE ===")
                println("Reason: ${t.message}")
                println("========================")
                _withdrawResult.postValue(Result.failure(RuntimeException("Network error: ${t.message}")))
            }
        })
    }
}