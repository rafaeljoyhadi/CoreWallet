package com.example.corewallet.coresavings

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.corewallet.R
import java.text.NumberFormat
import java.util.Locale

class CoreBudgetDetail : AppCompatActivity() {

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
        setContentView(R.layout.core_budget_detail)
        window.statusBarColor = Color.parseColor("#0d5892")

        //Button ke CoreBudgetEdit & Export Data Dummy
        val btnNavigateToCoreBudgetEdit = findViewById<View>(R.id.btnEdit)
        btnNavigateToCoreBudgetEdit.setOnClickListener {
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
        }

        val savedAmount = 400000
        val targetAmount = 1000000

        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID")) // Locale Indonesia

        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        val progress = ((savedAmount.toDouble() / targetAmount) * 100).toInt()

        progressBar.progress = progress.coerceAtMost(100)

        val tvShoppingAmount = findViewById<TextView>(R.id.tvShoppingAmount)
        tvShoppingAmount.text = "${formatter.format(savedAmount)} / ${formatter.format(targetAmount)}"

        if (savedAmount > targetAmount) {
            progressBar.progressDrawable = ContextCompat.getDrawable(this,
                R.drawable.progress_bar_full
            )
            tvShoppingAmount.setTextColor(Color.parseColor("#FF0000"))
            tvShoppingAmount.setTypeface(null, Typeface.BOLD)
        } else {
            tvShoppingAmount.setTextColor(Color.BLACK)
            tvShoppingAmount.setTypeface(null, Typeface.NORMAL)
        }

    }

}