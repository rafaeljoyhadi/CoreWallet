package com.example.corewallet.transfer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.corewallet.R
import com.example.corewallet.TransferFormFragment
import com.example.corewallet.api.ContactResponse
import com.example.corewallet.databinding.FragmentRecipientSelectionBinding

class RecipientSelectionFragment : Fragment() {
    companion object {
        fun newInstance(userId: Int, username: String, accountNumber: String, balance: Double): RecipientSelectionFragment {
            val fragment = RecipientSelectionFragment()
            val args = Bundle().apply {
                putInt("userId", userId)
                putString("username", username)
                putString("accountNumber", accountNumber)
                putDouble("balance", balance)
            }
            fragment.arguments = args
            return fragment
        }
    }


    private var _binding: FragmentRecipientSelectionBinding? = null
    private val binding get() = _binding!!
    private lateinit var vm: TransferViewModel
    private lateinit var adapter: ContactAdapter
    private var fullList: List<ContactResponse> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipientSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ViewModel
        vm = ViewModelProvider(requireActivity())[TransferViewModel::class.java]

        // RecyclerView setup
        adapter = ContactAdapter { contact ->
            val fragment = TransferFormFragment.newInstance(
                receiverName = contact.name,
                receiverAccountNumber = contact.accountNumber,
                receiverProfilePicture = contact.profilePicture,
                receiverBalance = contact.balance
            )

            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        binding.recyclerContacts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RecipientSelectionFragment.adapter
        }

        binding.btnAddNewBeneficiary.setOnClickListener {
            val fragment = AddRecipientFragment()

            (activity as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }



        // Observe contacts
        vm.contacts.observe(viewLifecycleOwner) { list ->
            val nonNull = list ?: emptyList()
            fullList = nonNull
            adapter.updateList(nonNull)
        }

        // Load contacts
        vm.loadContacts()

        // Search filter
        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim().orEmpty().lowercase()
                val filtered = fullList.filter {
                    it.name.lowercase().contains(query)
                }
                adapter.updateList(filtered)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
