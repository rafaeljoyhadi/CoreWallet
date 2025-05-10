package com.example.corewallet

import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.ComponentActivity

class CoreBudgetInput : ComponentActivity() {

    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget_edit)
        window.statusBarColor = Color.parseColor("#0d5892")

        // Mengakses container utama
        container = findViewById(R.id.container)

        // Mengakses ImageView sebagai tombol
        val btnAddMoreCategories = findViewById<ImageView>(R.id.btnAddMoreCategories)

        // Menangani klik pada tombol
        btnAddMoreCategories.setOnClickListener {
            addNewSection()
        }

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
                this@CoreBudgetInput,
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
