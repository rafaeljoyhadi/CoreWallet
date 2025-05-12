package com.example.corewallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.corewallet.ContactAdapter
import com.example.corewallet.Contact

class RecipientSelectionFragment : Fragment() {

    private lateinit var viewModel: TransferViewModel
    private lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout manually using findViewById
        return inflater.inflate(R.layout.fragment_recipient_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize ViewModel
        viewModel = ViewModelProvider(requireActivity()).get(TransferViewModel::class.java)

        // Initialize RecyclerView
        val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recycler_contacts)
        initRecyclerView(recyclerView)

        // Handle SearchView
//        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.search_view)
//        handleSearchView(searchView)

        // Add New Beneficiary Button
        val addNewBeneficiaryButton = view.findViewById<View>(R.id.btn_add_new_beneficiary)
        addNewBeneficiaryButton.setOnClickListener {
            // Navigate to Add Beneficiary Screen
            // Example: findNavController().navigate(R.id.action_recipientSelectionFragment_to_addBeneficiaryFragment)
        }

        // Fetch or simulate contacts
        val contacts = listOf(
            Contact("JOY", "9933793887"),
            Contact("IRVAN", "9933793843"),
            Contact("CAROLLYN", "9933793843")
        )
        contactAdapter.updateList(contacts) // Use updateList instead of submitList
    }

    private fun filterContacts(query: String) {
        val filteredContacts = contactAdapter.contacts.filter {
            it.name.contains(query, ignoreCase = true) || it.accountNumber.contains(query, ignoreCase = true)
        }
        contactAdapter.updateList(filteredContacts) // Update the adapter with the filtered list
    }

    private fun initRecyclerView(recyclerView: androidx.recyclerview.widget.RecyclerView) {
        contactAdapter = ContactAdapter(mutableListOf()) { contact ->
            // Handle contact selection
            viewModel.selectedRecipient.value = contact.accountNumber
            // Navigate to next fragment (e.g., AccountSelectionFragment)
            // Example: findNavController().navigate(R.id.action_recipientSelectionFragment_to_accountSelectionFragment)
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }
    }

    private fun handleSearchView(searchView: androidx.appcompat.widget.SearchView) {
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterContacts(newText.orEmpty())
                return true
            }
        })
    }

}