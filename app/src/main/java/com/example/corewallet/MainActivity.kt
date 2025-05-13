package com.example.corewallet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.example.corewallet.CoreSavingsActivity
import com.example.corewallet.coresavings.CoreSavingsFragment
import com.example.corewallet.databinding.FragmentTransferBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private var _binding: FragmentTransferBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve user details from Intent
        val userId = intent.getIntExtra("userId", -1)
        val username = intent.getStringExtra("username") ?: ""
        val accountNumber = intent.getStringExtra("accountNumber") ?: ""
        val balance = intent.getDoubleExtra("balance", 0.0)

        // Check for fragment selection flags
        val showTransactionHistory = intent.getBooleanExtra("showTransactionHistory", false)
        val showQris = intent.getBooleanExtra("showQris", false)

        // Create initial fragment
        val initialFragment = when {
            showTransactionHistory -> TransactionHistoryFragment()
            showQris -> QrisFragment()
            else -> DashboardFragment.newInstance(userId, username, accountNumber, balance)
        }

        // Load initial fragment if no fragment is currently displayed
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, initialFragment)
                setReorderingAllowed(true)
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        // Set selected item based on initial fragment
        bottomNavigationView.selectedItemId = when {
            showTransactionHistory -> R.id.nav_transaction_history
            showQris -> R.id.nav_qris
            else -> R.id.nav_home
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(DashboardFragment.newInstance(userId, username, accountNumber, balance))
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
                    replaceFragment(CoreSavingsFragment.newInstance(userId, username, accountNumber, balance))
                    true
                }
                R.id.nav_account -> {
                    replaceFragment(AccountFragment())
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