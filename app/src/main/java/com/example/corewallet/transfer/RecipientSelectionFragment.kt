package com.example.corewallet.transfer

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.corewallet.R
import com.example.corewallet.TransferFormFragment
import com.example.corewallet.databinding.FragmentRecipientSelectionBinding
import com.example.corewallet.models.Contact

class RecipientSelectionFragment : Fragment() {

    private lateinit var viewModel: TransferViewModel
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var allContacts: List<Contact>

    private var _binding: FragmentRecipientSelectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout manually using findViewById
        return inflater.inflate(R.layout.fragment_recipient_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(TransferViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_contacts)
        initRecyclerView(recyclerView)

        // Reference to the EditText directly
        val searchInput = view.findViewById<EditText>(R.id.search_input)
        searchInput.setOnEditorActionListener { _, _, _ ->
            val query = searchInput.text.toString().trim()
            filterContacts(query)
            true
        }

        val addNewBeneficiaryButton: Button = view.findViewById(R.id.btn_add_new_beneficiary)
        addNewBeneficiaryButton.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.transfer_content, AddRecipientFragment()) // use the correct container ID
                addToBackStack(null)
            }
        }

        val contacts = listOf(
            Contact("JOY", "9933793887"),
            Contact("IRVAN", "9933793843"),
            Contact("CAROLLYN", "9933793843"),
            Contact("MATT", "9933793887"),
            Contact("DEA", "9933793887"),
            Contact("GRACE", "9933793887"),
            Contact("JOYIII", "9933793887"),
            Contact("VAN", "9933793843"),
            Contact("OLLYN", "9933793843"),
            Contact("MATT", "9933793887"),
            Contact("DIANA", "9933793887"),
            Contact("REY", "9933793887")
        )
        allContacts = contacts
        contactAdapter.updateList(allContacts)

    }

    private fun filterContacts(query: String) {
        val filteredContacts = allContacts.filter {
            it.name.contains(query, ignoreCase = true) || it.accountNumber.contains(query)
        }
        contactAdapter.updateList(filteredContacts)
    }

    private fun initRecyclerView(recyclerView: RecyclerView) {
        val userId = arguments?.getInt("userId", -1) ?: -1
        val username = arguments?.getString("username", "") ?: ""
        val accountNumber = arguments?.getString("accountNumber", "") ?: ""
        val senderBalance = arguments?.getDouble("senderBalance", 0.0) ?: 0.0
        val receiverBalance = arguments?.getDouble("receiverBalance", 0.0) ?: 0.0

        contactAdapter = ContactAdapter(mutableListOf()) { contact ->
            // Update ViewModel with selected recipient
            viewModel.selectedRecipient.value = contact.accountNumber

            // Navigate to TransferFormFragment with contact and user data
            parentFragmentManager.commit {
                replace(
                    R.id.transfer_content,
                    TransferFormFragment.newInstance(
                        receiverName = contact.name,
                        receiverAccountNumber = contact.accountNumber,
                        receiverProfilePicture = contact.profilePicture,
                        receiverBalance = contact.contactBalance
                    )
                )
                addToBackStack(null)
                setReorderingAllowed(true)
            }
        }
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }
    }


}