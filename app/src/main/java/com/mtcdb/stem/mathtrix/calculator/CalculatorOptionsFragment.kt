package com.mtcdb.stem.mathtrix.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mtcdb.stem.mathtrix.R
import com.mtcdb.stem.mathtrix.calculator.options.SimpleInterestFragment

class CalculatorOptionsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CalculationOptionAdapter
    private lateinit var dictionarySearchView: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calculator_options, container, false)

        dictionarySearchView = view.findViewById(R.id.calculatorSearchView)
        dictionarySearchView.isIconifiedByDefault = false
        dictionarySearchView.queryHint = "Search calculations here..."

        recyclerView = view.findViewById(R.id.recyclerViewCalculationOptions)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val calculationOptions = listOf(
            CalculationOption("Simple Interest", "Calculate simple interest"),
            CalculationOption("Compound Interest", "Calculate compound interest"),
        )

        // Initialize the adapter
        adapter = CalculationOptionAdapter(calculationOptions) { selectedOption ->
            val calculatorFragment = when (selectedOption.name) {
                "Simple Interest" -> SimpleInterestFragment()
                // Add more calculation fragments based on your options
                else -> null
            }

            if (calculatorFragment != null) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, calculatorFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        // Set the adapter to the RecyclerView
        recyclerView.adapter = adapter

        return view
    }
}
