package com.vyarth.ellipsify.adapters

import android.content.Intent
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.BedtimeStory
import com.vyarth.ellipsify.model.Journal

class BedtimeStoryAdapter(private val story: List<BedtimeStory>,
                     private val activityClasses: List<Class<out AppCompatActivity>>):
    RecyclerView.Adapter<BedtimeStoryAdapter.BedtimeStoryHolder>(){

    // ViewHolder class
    class BedtimeStoryHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val journalTitle: TextView = itemView.findViewById(R.id.cardTitle)
        val cardView: CardView = itemView.findViewById(R.id.cardView) // Replace with the actual ID of your CardView
        val journalDesc: TextView = itemView.findViewById(R.id.cardDesc)
        val journalCount: TextView = itemView.findViewById(R.id.cardTime)
    }

    // Create new ViewHolders (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BedtimeStoryHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.bedtime_item, parent, false)
        return BedtimeStoryHolder(itemView)
    }


    // Replace the contents of a ViewHolder (invoked by the layout manager)
    override fun onBindViewHolder(holder: BedtimeStoryHolder, position: Int) {
        val data = story[position]
        val activityClass = activityClasses[position]


        holder.journalTitle.text = data.title
        holder.journalDesc.text=data.desc

        // Set background color
        holder.cardView.setBackgroundResource(data.backgroundColor);

        // Set image

        holder.journalCount.text = data.time

        holder.journalCount.setTextColor(ContextCompat.getColor(holder.itemView.context, data.timeColor))

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
            intent.putExtra("story_title", data.title)
            intent.putExtra("story_refer",data.refer)
            intent.putExtra("story_bg",data.backgroundColor)
            intent.putExtra("story_image",data.timeColor)
            holder.itemView.context.startActivity(intent)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return story.size
    }
}