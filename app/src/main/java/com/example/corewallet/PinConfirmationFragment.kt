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

        // Reference to dots
        dots = listOf(
            view.findViewById(R.id.dot_1),
            view.findViewById(R.id.dot_2),
            view.findViewById(R.id.dot_3),
            view.findViewById(R.id.dot_4),
            view.findViewById(R.id.dot_5),
            view.findViewById(R.id.dot_6)
        )

        // Show number pad
        showKeyboard(view)
    }

    private fun showKeyboard(parentView: View) {
        val inputField = EditText(requireContext())
        inputField.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
        inputField.imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
        inputField.isFocusableInTouchMode = true
        inputField.visibility = View.INVISIBLE
        (parentView as ViewGroup).addView(inputField)
        inputField.requestFocus()

        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(inputField, InputMethodManager.SHOW_IMPLICIT)

        inputField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Do nothing
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    if (it.length > pinInput.length) {
                        if (pinInput.length < 6) {
                            pinInput.append(it[it.length - 1])
                            updateDots()
                        }
                    } else if (it.length < pinInput.length) {
                        pinInput.deleteAt(pinInput.length - 1)
                        updateDots()
                    }

                    // Clear input field content to allow continuous input
                    inputField.text?.clear()

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
