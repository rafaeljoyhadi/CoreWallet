package com.example.corewallet

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class CoreBudgetForm : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.core_budget_form)
        window.statusBarColor = Color.parseColor("#0d5892")
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
    }

        private lateinit var categoryContainer: LinearLayout
        private lateinit var btnAddCategory: ImageView

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            categoryContainer = view.findViewById(R.id.categoryContainer)
            btnAddCategory = view.findViewById(R.id.btnAddCategory)

            btnAddCategory.setOnClickListener {
                addCategoryView()
            }

            // Tambahkan satu default pertama
            addCategoryView()
        }

        private fun addCategoryView() {
            val inflater = LayoutInflater.from(requireContext())
            val newItem = inflater.inflate(R.layout.item_category_budget, categoryContainer, false)

            val spinner = newItem.findViewById<Spinner>(R.id.spinnerCategory)
            val editBudget = newItem.findViewById<EditText>(R.id.editBudget)

            // Optional: Set adapter untuk spinner jika perlu
            // spinner.adapter = ...

            categoryContainer.addView(newItem)
        }
    }






