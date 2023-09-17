package com.mtcdb.stem.mathtrix.calculator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mtcdb.stem.mathtrix.R

class CalculationOptionAdapter(
    private val calculationOptions: List<CalculationOption>,
    private val onItemClick: (CalculationOption) -> Unit
) : RecyclerView.Adapter<CalculationOptionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.tVCalculationOption)
        val textViewDescription: TextView = itemView.findViewById(R.id.tVCalculationOptionDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_calculation_option, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = calculationOptions[position]
        holder.textViewName.text = option.name
        holder.textViewDescription.text = option.description

        // Handle item click
        holder.itemView.setOnClickListener {
            onItemClick(option)
        }
    }

    override fun getItemCount(): Int {
        return calculationOptions.size
    }
}