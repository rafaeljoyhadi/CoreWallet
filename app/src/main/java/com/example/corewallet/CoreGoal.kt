package com.example.corewallet

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.NumberFormat
import java.util.Locale


class CoreGoal : AppCompatActivity() {

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
            navigateToCoreGoalEdit(this, CoreGoalEdit::class.java, data)

//            val moveIntent = Intent(this@CoreBudget, CoreBudgetEdit::class.java)
//            startActivity(moveIntent)
        }

        // Fungsi Delete Button
        popupView.findViewById<View>(R.id.itemDelete).setOnClickListener {
            Toast.makeText(this, "Delete clicked", Toast.LENGTH_SHORT).show()
            popupWindow.dismiss()
        }
    }

    private fun navigateToCoreGoalEdit(activity: AppCompatActivity, nextActivityClass: Class<*>, data: HashMap<String, Any>) {
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
        setContentView(R.layout.core_goal)
        window.statusBarColor = Color.parseColor("#0d5892")

        // Akses Option per Goal
        val btnOverflow = findViewById<View>(R.id.btnOverflow)
        btnOverflow.setOnClickListener {
            showCustomPopup(it)
        }

        val list1 = findViewById<View>(R.id.goal1)
        list1.setOnClickListener {
            val moveIntent = Intent(this@CoreGoal, CoreGoalDetail::class.java)
            startActivity(moveIntent)
        }

        val titleGoal = "Vacation Gaming"
        val savedAmount = 4000000
        val targetAmount = 1000000

        val formatter = NumberFormat.getNumberInstance(Locale("id", "ID")) // Locale Indonesia

        val tvTitleGoal = findViewById<TextView>(R.id.tvTitle1)
        
        val progressBar = findViewById<ProgressBar>(R.id.progressBar1)

        val progress = ((savedAmount.toDouble() / targetAmount) * 100).toInt()

        progressBar.progress = progress.coerceAtMost(100)

        val goalAmount1 = findViewById<TextView>(R.id.amount1)
        goalAmount1.text = "${formatter.format(savedAmount)} / ${formatter.format(targetAmount)}"
        tvTitleGoal.text = titleGoal

        if (savedAmount > targetAmount) {
            progressBar.progressDrawable = ContextCompat.getDrawable(this, R.drawable.progress_bar_full)
            goalAmount1.setTextColor(Color.parseColor("#FF0000"))
            goalAmount1.setTypeface(null, Typeface.BOLD)
        } else {
            goalAmount1.setTextColor(Color.BLACK)
            goalAmount1.setTypeface(null, Typeface.NORMAL)
        }

        // Button Add Goal
        val btnCoreGoal: TextView = findViewById(R.id.btnAddGoal)
        btnCoreGoal.setOnClickListener {
            val moveIntent = Intent(this@CoreGoal, CoreGoalInput::class.java)
            startActivity(moveIntent)
        }
    }

}