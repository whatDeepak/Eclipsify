package com.vyarth.ellipsify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.model.DatePicker
import com.vyarth.ellipsify.model.Emotion

class DatePickerAdapter(private val data: List<DatePicker>) :
    RecyclerView.Adapter<DatePickerAdapter.DatePickerViewHolder>() {

    // ViewHolder class
    class DatePickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val DateTextView: TextView = itemView.findViewById(R.id.dateTV)
        val FrequencyTextView: TextView = itemView.findViewById(R.id.numberJournalTV)

    }

    // Create new ViewHolders (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatePickerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.date_picker_item, parent, false)
        return DatePickerViewHolder(itemView)
    }


    // Replace the contents of a ViewHolder (invoked by the layout manager)
    override fun onBindViewHolder(holder: DatePickerViewHolder, position: Int) {
        val dp = data[position]

        holder.DateTextView.text= dp.date.toString()
        holder.FrequencyTextView.text=dp.frequency.toString()

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return data.size
    }
}
