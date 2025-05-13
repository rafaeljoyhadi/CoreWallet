package com.example.corewallet.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.corewallet.R

class TransferFormFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_transfer_form, container, false)

        // Reference to the Spinner
        val spinner = view.findViewById<Spinner>(R.id.spinnerTransactionCategory)

        // Sample data
        val categories = arrayOf("Food", "Transport", "Entertainment", "Other")

        // Create ArrayAdapter using default layout for spinner item
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item, // Custom layout
            R.id.category_inp,     // ID of TextView in the layout
            categories
        )

        // Set adapter
        spinner.adapter = adapter

        // Handle item selection
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                Toast.makeText(
                    requireContext(),
                    "Selected: ${categories[position]}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optional
            }
        }

        return view
    }
}