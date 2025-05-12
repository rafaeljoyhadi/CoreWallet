package com.example.corewallet

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast

class PinConfirmationFragment : Fragment() {

    private lateinit var pinInput: StringBuilder
    private lateinit var dots: List<View>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_pin_confirmation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pinInput = StringBuilder()

        dots = listOf(
            view.findViewById(R.id.dot_1),
            view.findViewById(R.id.dot_2),
            view.findViewById(R.id.dot_3),
            view.findViewById(R.id.dot_4),
            view.findViewById(R.id.dot_5),
            view.findViewById(R.id.dot_6)
        )

        val hiddenEditText = view.findViewById<EditText>(R.id.hiddenEditText)
        setupKeyboard(hiddenEditText)
    }

    private fun setupKeyboard(editText: EditText) {
        editText.requestFocus()
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.length > pinInput.length && pinInput.length < 6) {
                        pinInput.append(it.last())
                    } else if (it.length < pinInput.length && pinInput.isNotEmpty()) {
                        pinInput.deleteAt(pinInput.length - 1)
                    }

                    editText.text?.clear() // Reset to keep capturing single digit
                    updateDots()

                    if (pinInput.length == 6) {
                        onPinEntered(pinInput.toString())
                    }
                }
            }
        })
    }

    private fun updateDots() {
        for (i in dots.indices) {
            dots[i].setBackgroundResource(if (i < pinInput.length) R.drawable.circle_filled else R.drawable.circle_border)
        }
    }

    private fun onPinEntered(pin: String) {
        Toast.makeText(requireContext(), "PIN Entered: $pin", Toast.LENGTH_SHORT).show()
        // Proceed with verification or navigation
    }
}
