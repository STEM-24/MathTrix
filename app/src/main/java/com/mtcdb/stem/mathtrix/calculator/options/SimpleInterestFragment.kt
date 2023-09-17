package com.mtcdb.stem.mathtrix.calculator.options

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mtcdb.stem.mathtrix.R

class SimpleInterestFragment : Fragment() {

    private lateinit var principalEditText: EditText
    private lateinit var rateEditText: EditText
    private lateinit var timeEditText: EditText
    private lateinit var calculateButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView = inflater.inflate(R.layout.fragment_simple_interest, container, false)

        // Initialize UI elements
        principalEditText = rootView.findViewById(R.id.editTextPrincipal)
        rateEditText = rootView.findViewById(R.id.editTextRate)
        timeEditText = rootView.findViewById(R.id.editTextTime)
        calculateButton = rootView.findViewById(R.id.buttonCalculate)
        resultTextView = rootView.findViewById(R.id.textViewResult)

        // Handle calculate button click
        calculateButton.setOnClickListener {
            calculateSimpleInterest()
        }

        return rootView
    }

    @SuppressLint("StringFormatInvalid")
    private fun calculateSimpleInterest() {
        val principal = principalEditText.text.toString().toDoubleOrNull() ?: 0.0
        val rate = rateEditText.text.toString().toDoubleOrNull() ?: 0.0
        val time = timeEditText.text.toString().toDoubleOrNull() ?: 0.0

        val simpleInterest = (principal * rate * time) / 100.0

        resultTextView.text = simpleInterest.toString()
    }
}
