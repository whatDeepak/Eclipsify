package com.vyarth.ellipsify.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.model.JournalList

class JournalListAdapter(private val journalList: MutableList<JournalList>,
                         private val activityClasses: List<Class<out AppCompatActivity>>,
                         private val onItemClick: (JournalList) -> Unit):
    RecyclerView.Adapter<JournalListAdapter.JournalListViewHolder>(){

    // ViewHolder class
    class JournalListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val journalTitle: TextView = itemView.findViewById(R.id.journalListTitle)
        val cardView: CardView = itemView.findViewById(R.id.cardView) // Replace with the actual ID of your CardView
        val journalDesc: TextView = itemView.findViewById(R.id.journalListDesc)
        val journalDate: TextView = itemView.findViewById(R.id.journalListDate)


    }

    // Create new ViewHolders (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.journal_list_item, parent, false)
        return JournalListViewHolder(itemView)
    }


    // Replace the contents of a ViewHolder (invoked by the layout manager)
    override fun onBindViewHolder(holder: JournalListViewHolder, position: Int) {
        val jrnl = journalList[position]


        holder.journalTitle.text = jrnl.title


        val maxCharsToShow = 50 // You can adjust this number based on your preference
        val truncatedText = if (jrnl.text?.length ?: 0 > maxCharsToShow) {
            "${jrnl.text?.substring(0, maxCharsToShow)}..."
        } else {
            jrnl.text
        }
        holder.journalDesc.text = truncatedText


        // Set background color
        holder.cardView.setBackgroundResource(R.drawable.bg_journal_list);


//        holder.journalDate.text = jrnl.timestamp.toString()
//        Log.e("hehe",jrnl.timestamp.toString())


        val fontCount = Typeface.createFromAsset(
            holder.itemView.context.assets,
            "epilogue_semibold.ttf"
        )
        val fontTitle = Typeface.createFromAsset(
            holder.itemView.context.assets,
            "epilogue_bold.ttf"
        )
        val fontDesc= Typeface.createFromAsset(
            holder.itemView.context.assets,
            "poppins_regular.ttf"
        )
        holder.journalDate.typeface = fontCount
        holder.journalTitle.typeface= fontTitle
        holder.journalDesc.typeface= fontDesc

        holder.itemView.setOnClickListener {
            // Call the onItemClick listener to handle item click
            onItemClick.invoke(jrnl)
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    fun updateData(newData: List<JournalList>) {
        journalList.clear()
        journalList.addAll(newData)
        notifyDataSetChanged()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return journalList.size
    }
}