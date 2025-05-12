package com.example.corewallet

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
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
import androidx.core.content.ContextCompat

class CoreBudget : AppCompatActivity() {

     fun showCustomPopup(anchorView: View) {
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

            val shoppingCategory = "Shopping"
            val shoppingAmount = "Rp. 400.000"
            val shoppingProgress = 40

            val transportationCategory = "Transportation"
            val transportationAmount = "Rp. 600.000"
            val transportationProgress = 60

            //Membawa Value ke CoreBudgetEdit
            findViewById<View>(R.id.budget1).setOnClickListener {
                // Siapkan data dalam HashMap
                val data = HashMap<String, Any>()
                data["shoppingCategory"] = shoppingCategory
                data["shoppingAmount"] = shoppingAmount
                data["shoppingProgress"] = shoppingProgress

                data["transportationCategory"] = transportationCategory
                data["transportationAmount"] = transportationAmount
                data["transportationProgress"] = transportationProgress

                // Panggil fungsi untuk membuka halaman berikutnya
                navigateToCoreBudgetEdit(this, CoreBudgetEdit::class.java, data)
            }
//            val moveIntent = Intent(this@CoreBudget, CoreBudgetEdit::class.java)
//            startActivity(moveIntent)
        }

        // Fungsi Delete Button
        popupView.findViewById<View>(R.id.itemDelete).setOnClickListener {
            Toast.makeText(this, "Delete clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
    }

    private fun navigateToCoreBudgetEdit(activity: AppCompatActivity, nextActivityClass: Class<*>, data: HashMap<String, Any>) {
        // Membuat Intent untuk membuka aktivitas berikutnya
        val intent = Intent(activity, nextActivityClass)

        // Menambahkan data ke Intent
        for ((key, value) in data) {
            when (value) {
                is String -> intent.putExtra(key, value)
                is Int -> intent.putExtra(key, value)
                is Boolean -> intent.putExtra(key, value)
                is Float -> intent.putExtra(key, value)
                is Double -> intent.putExtra(key, value)
                else -> throw IllegalArgumentException("Unsupported data type for key: $key")
            }
        }

        // Memulai aktivitas baru
        activity.startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget)
        window.statusBarColor = Color.parseColor("#0d5892")

        // Akses ImageButton Option
        val btnOverflow = findViewById<View>(R.id.btnOverflow)
        btnOverflow.setOnClickListener {
            showCustomPopup(it)
        }

        val list1 = findViewById<View>(R.id.budget1)
        list1.setOnClickListener {
            val moveIntent = Intent(this@CoreBudget, CoreBudgetDetail::class.java)
            startActivity(moveIntent)
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

        if (savedAmount > targetAmount) {
            // Ganti drawable progress bar dengan progress_bar_full.xml
            progressBar.progressDrawable = ContextCompat.getDrawable(this, R.drawable.progress_bar_full)

            // Ubah warna teks menjadi merah (#FF0000)
            tvShoppingAmount.setTextColor(Color.parseColor("#FF0000"))

            // Tambahkan boldness ke teks
            tvShoppingAmount.setTypeface(null, Typeface.BOLD)
        } else {
            // Kembalikan warna teks ke default (misalnya hitam)
            tvShoppingAmount.setTextColor(Color.BLACK)

            // Hapus boldness (kembalikan ke normal)
            tvShoppingAmount.setTypeface(null, Typeface.NORMAL)
        }




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


}