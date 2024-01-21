package com.vyarth.ellipsify.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.model.Emotion
import com.vyarth.ellipsify.model.Journal

class JournalAdapter(private val journal: List<Journal>,
                     private val activityClasses: List<Class<out AppCompatActivity>>,
                     private val dailyJournalCount: String,
                     private val moodJournalCount: String):
    RecyclerView.Adapter<JournalAdapter.JournalViewHolder>(){

    // ViewHolder class
    class JournalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val journalTitle: TextView = itemView.findViewById(R.id.journalTitle)
        val cardView: CardView = itemView.findViewById(R.id.cardView) // Replace with the actual ID of your CardView
        val journalImageView: ImageView = itemView.findViewById(R.id.journalImageView) // Replace with the actual ID of your ImageView
        val journalDesc: TextView = itemView.findViewById(R.id.journalDesc)
        val journalCount: TextView = itemView.findViewById(R.id.journalCount)


    }

    // Create new ViewHolders (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.journal_item, parent, false)
        return JournalViewHolder(itemView)
    }


    // Replace the contents of a ViewHolder (invoked by the layout manager)
    override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
        val jrnl = journal[position]
        val activityClass = activityClasses[position]

        when (jrnl.title) {
            "Daily Journal" -> holder.journalCount.text = dailyJournalCount
            "Mood Journal" -> holder.journalCount.text = moodJournalCount
            // Add more cases as needed
        }

        holder.journalTitle.text = jrnl.title
        holder.journalDesc.text=jrnl.desc

        // Set background color
        holder.cardView.setBackgroundResource(jrnl.backgroundColor);

        // Set image
        holder.journalImageView.setImageResource(jrnl.imageResId)

        holder.journalCount.text = jrnl.count

        holder.journalCount.setTextColor(ContextCompat.getColor(holder.itemView.context, jrnl.countColor))

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
        holder.journalCount.typeface = fontCount
        holder.journalTitle.typeface= fontTitle
        holder.journalDesc.typeface= fontDesc

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, activityClass)
            intent.putExtra("journal_title", jrnl.title)
            holder.itemView.context.startActivity(intent)
        }

    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return journal.size
    }
}