package com.example.corewallet

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.Fragment

class CoreBudgetForm : Fragment() {

    private lateinit var categoryContainer: LinearLayout
    private lateinit var btnAddCategory: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout Fragment
        return inflater.inflate(R.layout.core_budget_form, container, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set warna status bar
        requireActivity().window.statusBarColor = Color.parseColor("#0d5892")

        categoryContainer = view.findViewById(R.id.categoryContainer)
        btnAddCategory = view.findViewById(R.id.btnAddCategory)

        btnAddCategory.setOnClickListener {
            addCategoryView()
        }

        // Tambahkan 1 default
        addCategoryView()
    }

    private fun addCategoryView() {
        val inflater = LayoutInflater.from(requireContext())
        val newItem = inflater.inflate(R.layout.core_budget_form, categoryContainer, false)

        val spinner = newItem.findViewById<Spinner>(R.id.spinnerCategory)
        val editBudget = newItem.findViewById<EditText>(R.id.etBudgetAmount)

        categoryContainer.addView(newItem)
    }
}
