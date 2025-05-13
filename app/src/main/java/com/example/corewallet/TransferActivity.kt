package com.example.corewallet

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.example.corewallet.R
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController

class TransferActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transfer)

        val mFragmentManager = supportFragmentManager
        val mHomeFragment  = RecipientSelectionFragment()
        val fragment = mFragmentManager.findFragmentByTag(RecipientSelectionFragment::class.java.simpleName)

        if (savedInstanceState == null) {
            mFragmentManager.commit {
                Log.d("MyFlexibleFragment", "Fragment Name :" + RecipientSelectionFragment::class.java.simpleName)
                mFragmentManager
                    .beginTransaction()
                    .add(R.id.nav_host_fragment_transfer, mHomeFragment, RecipientSelectionFragment::class.java.simpleName)
                    .commit()
            }
        }

//        // Set up the NavController with the ActionBar
//        val navController = findNavController(R.id.nav_host_fragment_transfer)
//        setupActionBarWithNavController(navController)
    }

    /**
     * Handle the Up button in the ActionBar to navigate back.
     */
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_transfer)
//        return navController.navigateUp() || super.onSupportNavigateUp()
//    }
}