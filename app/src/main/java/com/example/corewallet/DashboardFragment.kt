package com.example.corewallet

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.example.corewallet.api.RetrofitClient
import com.example.corewallet.databinding.FragmentDashboardBinding
import com.example.corewallet.models.Users
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//  TODO: Replace with actual data from the API
        binding.accountNumber.text = "251 230 5099"
        binding.balanceAmount.text = "Rp. 100.000"

        val userId = 1 // Replace with logged-in user's ID (store from login)

        RetrofitClient.instance.getUserById(userId).enqueue(object : Callback<Users> {
            override fun onResponse(call: Call<Users>, response: Response<Users>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    user?.let {
                        binding.accountNumber.text = it.account_number
                        binding.balanceAmount.text = "Rp. ${it.balance.toInt()}"
                    }
                } else {
                    Log.e("DashboardFragment", "Failed to fetch user: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<Users>, t: Throwable) {
                Log.e("DashboardFragment", "API call failed: ${t.message}")
            }
        })

        // Set up click listener for the transfer button
        binding.btnTransfer.setOnClickListener {
            val mTFFragment = TransferFragment()
            val mFragmentManager = parentFragmentManager as FragmentManager
            mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mTFFragment, TransferFragment::class.java.simpleName)
                .addToBackStack(null)
                .commit()
        }

        //  onclick listener for profile menu
        binding.btnProfile.setOnClickListener {
            val mProfileFragment = AccountFragment()
            val mFragmentManager = parentFragmentManager as FragmentManager
            mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mProfileFragment, AccountFragment::class.java.simpleName)
                .addToBackStack(null)
                .commit()
        }
        binding.btnTopUp.setOnClickListener {
            val mPin = PinConfirmationFragment()
            val mFragmentManager = parentFragmentManager as FragmentManager
            mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mPin, PinConfirmationFragment::class.java.simpleName)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}