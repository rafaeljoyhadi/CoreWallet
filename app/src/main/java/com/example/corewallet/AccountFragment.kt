package com.example.corewallet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText

class AccountFragment : Fragment() {

    private lateinit var editDisplayName: EditText
    private lateinit var editPassword: EditText
    private lateinit var editEmail: EditText
    private lateinit var editPin: EditText
    private lateinit var btnEditProfile: Button

    private var isEditable = false

    private var userId: Int = -1 // You'll get this from login intent or session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.account_menu, container, false)
    }


}