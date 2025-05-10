package com.example.corewallet

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale
import androidx.appcompat.content.res.AppCompatResources

class CoreBudget : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget)
        window.statusBarColor = Color.parseColor("#0d5892")

        // Akses ImageButton Option
        val btnOverflow = findViewById<View>(R.id.btnOverflow)
        btnOverflow.setOnClickListener {
            showCustomPopup(it)
        }

        //Data Dummy
        val savedAmount = 400000
        val targetAmount = 1000000

        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID")) // Locale Indonesia

        val progressBar = findViewById<ProgressBar>(R.id.progressBar1)

        val progress = ((savedAmount.toDouble() / targetAmount) * 100).toInt()

        progressBar.progress = progress.coerceAtMost(100) // Pastikan tidak lebih dari 100

        val tvShoppingAmount = findViewById<TextView>(R.id.tvShoppingAmount)
        tvShoppingAmount.text = "${formatter.format(savedAmount)} / ${formatter.format(targetAmount)}"


        // Button Add Budget
        val btnCoreGoal: Button = findViewById(R.id.btnAddBudget)
        btnCoreGoal.setOnClickListener {
            val moveIntent = Intent(this@CoreBudget, CoreBudgetInput::class.java)
            startActivity(moveIntent)
        }

//        val progressBar: ProgressBar = findViewById(R.id.progressBar)
//        val currentProgress = 10
//        progressBar.progress = currentProgress
//
//        if (currentProgress >= 100) {
//            progressBar.progressDrawable = ContextCompat.getDrawable(this, R.drawable.progress_bar)
//        } else {
//            progressBar.progressDrawable = ContextCompat.getDrawable(this, R.drawable.progress_bar_full)
//        }

    }

// This is the button if smth crash
//    private fun showPopupMenu(view: View) {
//        // Membuat Menu
//        val popupMenu = PopupMenu(this, view)
//
//        // Menambahkan item ke menu
//        popupMenu.menu.add(Menu.NONE, 1, Menu.NONE, "Edit")
//        popupMenu.menu.add(Menu.NONE, 2, Menu.NONE, "Delete")
//
//        // Mengatur ikon untuk setiap item
//        popupMenu.menu.getItem(0).icon = AppCompatResources.getDrawable(this, R.drawable.ic_edit)
//        popupMenu.menu.getItem(1).icon = AppCompatResources.getDrawable(this, R.drawable.ic_delete)
//
//        // Menangani pilihan menu
//        popupMenu.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                1 -> {
//                    // Tangani aksi Edit
//                    Toast.makeText(this, "Edit clicked", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                2 -> {
//                    // Tangani aksi Delete
//                    Toast.makeText(this, "Delete clicked", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                else -> false
//            }
//        }
//
//        // Menampilkan popup menu
//        popupMenu.show()
//    }

    private fun showCustomPopup(anchorView: View) {
        val inflater = LayoutInflater.from(this)
        val popupView = inflater.inflate(R.layout.popup_menu_core_budget, null, false)
        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        // Setting option button
        popupWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popupWindow.elevation = 30f
        popupWindow.showAsDropDown(anchorView, -178, -5, Gravity.START)

        // Fungsi Edit Button
        popupView.findViewById<View>(R.id.itemEdit).setOnClickListener {
            val moveIntent = Intent(this@CoreBudget, CoreBudgetEdit::class.java)
            startActivity(moveIntent)
        }

        // Fungsi Delete Button
        popupView.findViewById<View>(R.id.itemDelete).setOnClickListener {
            Toast.makeText(this, "Delete clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
    }
}