package com.vyarth.ellipsify.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.model.DatePicker
import com.vyarth.ellipsify.model.Emotion
import java.text.SimpleDateFormat
import java.util.Locale


class DatePickerAdapter(private val data: MutableList<DatePicker>, private val defaultSelectedPosition: Int,  private val clickListener: DatePickerClickListener) :
    RecyclerView.Adapter<DatePickerAdapter.DatePickerViewHolder>() {

    private var selectedPosition = defaultSelectedPosition

    // ViewHolder class
    class DatePickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val DateTextView: TextView = itemView.findViewById(R.id.dateTV)
        val FrequencyTextView: TextView = itemView.findViewById(R.id.numberJournalTV)
        val DateDiv:LinearLayout=itemView.findViewById(R.id.date_div)

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

        holder.DateTextView.text= SimpleDateFormat("dd", Locale.getDefault()).format(dp.date).toString()
        holder.FrequencyTextView.text=dp.frequency.toString()

        // Manually set the background color based on the selected state
        if (selectedPosition == position) {
            holder.itemView.setBackgroundResource(R.drawable.dp_card_selected)
            holder.DateDiv.setBackgroundResource(R.drawable.date_div_selected)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.dp_card)
            holder.DateDiv.setBackgroundResource(R.drawable.date_div)
        }

        // Handle item click
        holder.itemView.setOnClickListener {
            notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            // Handle item click action (e.g., open a new activity)
            // ...
            clickListener.onDatePickerItemClick(data[selectedPosition].date)
            Log.e("data", data[selectedPosition].date.toString())
        }
    }

    fun updateData(newData: List<DatePicker>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyItemChanged(position)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return data.size
    }

    interface DatePickerClickListener {
        fun onDatePickerItemClick(selectedDate: Long)
    }
}

