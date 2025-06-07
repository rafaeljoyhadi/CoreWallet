package com.example.corewallet.view.main

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.corewallet.view.transactions.QrisFragment
import com.example.corewallet.R
import com.example.corewallet.view.transactions.TransactionHistoryFragment
import com.example.corewallet.view.coresavings.CoreSavingsFragment
import com.example.corewallet.databinding.ActivityMainBinding
import com.example.corewallet.view.account.AccountFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve user details from Intent
        val userId = intent.getIntExtra("userId", -1)
        val accountNumber = intent.getStringExtra("accountNumber") ?: ""

        // Persist the current user's account number for global access
        getSharedPreferences("coreWalletPrefs", Context.MODE_PRIVATE)
            .edit()
            .putString("accountNumber", accountNumber)
            .apply()

        // Check for fragment selection flags
        val showTransactionHistory = intent.getBooleanExtra("showTransactionHistory", false)
        val showQris = intent.getBooleanExtra("showQris", false)
        val showAccount = intent.getBooleanExtra("showAccount", false)

        // Determine initial fragment
        val initialFragment: Fragment = when {
            showTransactionHistory -> TransactionHistoryFragment()
            showQris -> QrisFragment()
            showAccount -> AccountFragment.newInstance(userId)
            else -> DashboardFragment.newInstance(userId)
        }

        // Load initial fragment only once
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, initialFragment)
                setReorderingAllowed(true)
            }
        }

        // Setup BottomNavigationView
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = when {
            showTransactionHistory -> R.id.nav_transaction_history
            showQris -> R.id.nav_qris
            showAccount -> R.id.nav_account
            else -> R.id.nav_home
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(DashboardFragment.newInstance(userId))
                    true
                }
                R.id.nav_transaction_history -> {
                    replaceFragment(TransactionHistoryFragment())
                    true
                }
                R.id.nav_qris -> {
                    replaceFragment(QrisFragment())
                    true
                }
                R.id.nav_coresavings -> {
                    replaceFragment(CoreSavingsFragment())
                    true
                }
                R.id.nav_account -> {
                    replaceFragment(AccountFragment.newInstance(userId))
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
