package com.example.corewallet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.corewallet.databinding.FragmentTransferBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var _binding: FragmentTransferBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Retrieve user details from LoginActivity
        val userId = intent.getIntExtra("userId", -1)
        val username = intent.getStringExtra("username") ?: ""
        val accountNumber = intent.getStringExtra("accountNumber") ?: ""
        val balance = intent.getDoubleExtra("balance", 0.0)

        // Create DashboardFragment with user details
        val dashboardFragment = DashboardFragment.newInstance(userId, username, accountNumber, balance)

        // Load DashboardFragment if no fragment is currently displayed
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, dashboardFragment)
                setReorderingAllowed(true)
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val dashboardFrag = DashboardFragment.newInstance(userId, username, accountNumber, balance)

                    replaceFragment(dashboardFrag)
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
                // pindah activity 
                R.id.nav_coresavings -> {
                    val intent = Intent(this, CoreSavingsActivity::class.java)
                    startActivity(intent)
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
