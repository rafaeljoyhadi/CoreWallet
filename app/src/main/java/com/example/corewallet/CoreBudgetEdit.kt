package com.example.corewallet

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class CoreBudgetEdit : AppCompatActivity() {
    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget_edit)
        window.statusBarColor = Color.parseColor("#0d5892")

        // Mengakses container utama
        container = findViewById(R.id.container)

        // Mengakses ImageView sebagai tombol
        val buttonPickDate = findViewById<View>(R.id.buttonPickDate)

        // Listener untuk membuka DatePickerDialog
        buttonPickDate.setOnClickListener {
            // Ambil tanggal saat ini sebagai default
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Tampilkan DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Format tanggal ke string
                    val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    println("Tanggal yang dipilih: $selectedDate")
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        // Menangani klik pada tombol


        // Terima data dari Intent
        val shoppingCategory = intent.getStringExtra("shoppingCategory")
        val shoppingAmount = intent.getStringExtra("shoppingAmount")
        val shoppingProgress = intent.getIntExtra("shoppingProgress", 0)

        val transportationCategory = intent.getStringExtra("transportationCategory")
        val transportationAmount = intent.getStringExtra("transportationAmount")
        val transportationProgress = intent.getIntExtra("transportationProgress", 0)

        // Isi data ke TextView dan ProgressBar
        val tvShoppingCategory = findViewById<TextView>(R.id.etBudgetName)
        val tvShoppingAmount = findViewById<TextView>(R.id.etBudgetAmount)

        // Set nilai untuk Shopping
        tvShoppingCategory.text = shoppingCategory
        tvShoppingAmount.text = shoppingAmount


    }

    private fun addNewSection() {
        // Membuat Spinner
        val spinner = Spinner(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 5.dpToPx()) // Margin bottom 5dp
                setPadding(12,12,12,12)
            }
            adapter = ArrayAdapter.createFromResource(
                this@CoreBudgetEdit,
                R.array.categories_cb_form,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

        }

        val editText = EditText(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16.dpToPx()) // Margin bottom 16dp
            }
            hint = "Enter the budget amount"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        // Wrapper untuk Spinner dan EditText
        val wrapper = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        wrapper.addView(spinner)
        wrapper.addView(editText)
        container.addView(wrapper)
    }

    // Helper function untuk mengonversi dp ke px
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

}
