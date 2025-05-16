package com.example.corewallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.example.corewallet.databinding.FragmentTransferBinding
import com.example.corewallet.transfer.RecipientSelectionFragment

class TransferFragment : Fragment() {

    private var _binding: FragmentTransferBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(userId: Int, username: String, accountNumber: String, balance: Double): TransferFragment {
            val fragment = TransferFragment()
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve user details from arguments
        val userId = arguments?.getInt("userId", -1) ?: -1
        val username = arguments?.getString("username", "") ?: ""
        val accountNumber = arguments?.getString("accountNumber", "") ?: ""
        val balance = arguments?.getDouble("balance", 0.0) ?: 0.0

        // Log to verify data
        println("TransferFragment: userId=$userId, username=$username, accountNumber=$accountNumber, balance=$balance")

        // Load RecipientSelectionFragment into the FrameLayout, passing user data
        if (savedInstanceState == null) {
            childFragmentManager.commit {
                replace(
                    R.id.transfer_content,
                    RecipientSelectionFragment()
                )
                setReorderingAllowed(true)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}