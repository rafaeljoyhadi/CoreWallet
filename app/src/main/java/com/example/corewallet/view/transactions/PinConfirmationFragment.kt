package com.example.corewallet.view.transactions

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.example.corewallet.R

class PinConfirmationFragment : Fragment() {

    private lateinit var pinInputs: List<EditText>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pin_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pinInputs = listOf(
            view.findViewById(R.id.pin1),
            view.findViewById(R.id.pin2),
            view.findViewById(R.id.pin3),
            view.findViewById(R.id.pin4),
            view.findViewById(R.id.pin5),
            view.findViewById(R.id.pin6)
        )

        for (editText in pinInputs) {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
        }

        pinInputs.first().requestFocus()

        showKeyboard(pinInputs.first())

        for (i in 0 until pinInputs.size - 1) {
            pinInputs[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        pinInputs[i + 1].requestFocus()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        for (i in 1 until pinInputs.size) {
            pinInputs[i].setOnKeyListener { _, keyCode, event ->
                if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                    if (pinInputs[i].text.isEmpty()) {
                        pinInputs[i - 1].requestFocus()
                        pinInputs[i - 1].text.clear() // Hapus isi kolom sebelumnya saat backspace
                    }
                }
                false
            }
        }

        pinInputs.last().addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.isNotEmpty()) {
                    val pin = pinInputs.map { it.text.toString() }.joinToString(separator = "")
                    sendApiRequest(pin)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        for ((index, editText) in pinInputs.withIndex()) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // Gunakan EditText itu sendiri sebagai bullet point
                    editText.setBackgroundResource(
                        if (s?.isNotEmpty() == true) R.drawable.circle_filled else R.drawable.circle_border
                    )
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun showKeyboard(view: View) {
        view.post {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun sendApiRequest(pin: String) {
        Toast.makeText(requireContext(), "PIN Entered: $pin", Toast.LENGTH_SHORT).show()
        resetPinInputs()
        // Di sini tambahin API

    }

    private fun resetPinInputs() {
        Handler(Looper.getMainLooper()).postDelayed({
            for (editText in pinInputs) {
                editText.text.clear()
                editText.setBackgroundResource(R.drawable.circle_border)
            }

            pinInputs.first().requestFocus()
            showKeyboard(pinInputs.first())
        }, 500)
    }
}