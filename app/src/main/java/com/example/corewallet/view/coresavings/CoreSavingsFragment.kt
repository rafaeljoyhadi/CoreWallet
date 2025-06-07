package com.example.corewallet.view.coresavings

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.corewallet.R
import com.example.corewallet.view.coresavings.corebudget.CoreBudget
import com.example.corewallet.view.coresavings.coregoal.CoreGoal
import com.example.corewallet.databinding.FragmentCoreSavingsBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class CoreSavingsFragment : Fragment() {

    private var _binding: FragmentCoreSavingsBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(userId: Int, username: String, accountNumber: String, balance: Double): CoreSavingsFragment {
            val fragment = CoreSavingsFragment()
            val args = Bundle()
            args.putInt("userId", userId)
            args.putString("username", username)
            args.putString("accountNumber", accountNumber)
            args.putDouble("balance", balance)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoreSavingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve user details from LoginActivity
        val userId = arguments?.getInt("userId", -1) ?: -1
        val username = arguments?.getString("username", "") ?: ""
        val accountNumber = arguments?.getString("accountNumber", "") ?: ""
        val balance = arguments?.getDouble("balance", 0.0) ?: 0.0
        binding.btnCoreBudget.setOnClickListener {
            // move to coreBudgetActivity with data from LoginActivity
            val intent = Intent(requireContext(), CoreBudget::class.java).apply {
                putExtra("userId", userId)
                putExtra("username", username)
                putExtra("accountNumber", accountNumber)
                putExtra("balance", balance)
            }
            startActivity(intent)
        }

        binding.btnCoreGoal.setOnClickListener {
            // move to coregoalActivity with data from LoginActivity
            val intent = Intent(requireContext(), CoreGoal::class.java).apply {
                putExtra("userId", userId)
                putExtra("username", username)
                putExtra("accountNumber", accountNumber)
                putExtra("balance", balance)
            }
            startActivity(intent)
        }

        binding.logoBtn.setOnClickListener {
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.selectedItemId = R.id.nav_home
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }
}