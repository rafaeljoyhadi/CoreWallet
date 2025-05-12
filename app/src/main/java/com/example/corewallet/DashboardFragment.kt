package com.example.corewallet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import com.example.corewallet.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//  TODO: Replace with actual data from the API
        binding.balanceAmount.text = "Rp. 100.000"
        // Set up click listener for the transfer button
        binding.btnTransfer.setOnClickListener {
            val mCategoryFragment = TransferFragment()
            val mFragmentManager = parentFragmentManager as FragmentManager
            mFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mCategoryFragment, TransferFragment::class.java.simpleName)
                .addToBackStack(null)
                .commit()
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}