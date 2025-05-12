package com.example.corewallet

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.graphics.Color
import android.util.Log
import android.view.View
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

        // Load DashboardFragment if no fragment is currently displayed
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                replace(R.id.fragment_container, DashboardFragment())
                setReorderingAllowed(true)
            }
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(DashboardFragment())
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
