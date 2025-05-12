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


    //Pop Up Setings
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

            val data = HashMap<String, Any>()
            data["shoppingCategory"] = shoppingCategory
            data["shoppingAmount"] = shoppingAmount
            data["shoppingProgress"] = shoppingProgress

            data["transportationCategory"] = transportationCategory
            data["transportationAmount"] = transportationAmount
            data["transportationProgress"] = transportationProgress

            // Panggil fungsi untuk membuka halaman berikutnya
            navigateToCoreBudgetEdit(this, CoreBudgetEdit::class.java, data)

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

        progressBar.progress = progress.coerceAtMost(100)

        val tvShoppingAmount = findViewById<TextView>(R.id.tvShoppingAmount)
        tvShoppingAmount.text =
            "${formatter.format(savedAmount)} / ${formatter.format(targetAmount)}"

        if (savedAmount > targetAmount) {
            progressBar.progressDrawable =
                ContextCompat.getDrawable(this, R.drawable.progress_bar_full)
            tvShoppingAmount.setTextColor(Color.parseColor("#FF0000"))
            tvShoppingAmount.setTypeface(null, Typeface.BOLD)
        } else {
            tvShoppingAmount.setTextColor(Color.BLACK)
            tvShoppingAmount.setTypeface(null, Typeface.NORMAL)
        }

        // Button Add Budget
        val btnCoreGoal: Button = findViewById(R.id.btnAddGoal)
        btnCoreGoal.setOnClickListener {
            val moveIntent = Intent(this@CoreBudget, CoreBudgetInput::class.java)
            startActivity(moveIntent)
        }
    }
}