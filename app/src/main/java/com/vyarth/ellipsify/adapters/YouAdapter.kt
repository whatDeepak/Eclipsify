package com.vyarth.ellipsify.adapters

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.model.Journal
import com.vyarth.ellipsify.model.Profile

class YouAdapter(private val profiles: List<Profile>):
    RecyclerView.Adapter<YouAdapter.YouViewHolder>(){

    // ViewHolder class
    class YouViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val youTitle: TextView = itemView.findViewById(R.id.youTitle)
        val cardView: CardView = itemView.findViewById(R.id.cardView) // Replace with the actual ID of your CardView
        val youDesc: TextView = itemView.findViewById(R.id.youDesc)
        val youDescCount: TextView = itemView.findViewById(R.id.youDescCount)
        val youCount: TextView = itemView.findViewById(R.id.youCount)


    }

    // Create new ViewHolders (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YouViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.you_item, parent, false)
        return YouViewHolder(itemView)
    }


    // Replace the contents of a ViewHolder (invoked by the layout manager)
    override fun onBindViewHolder(holder: YouViewHolder, position: Int) {
        val profile = profiles[position]


        holder.youTitle.text = profile.title
        holder.youDesc.text=profile.desc
        holder.youDescCount.text=profile.descCount

        // Set background color
        holder.cardView.setBackgroundResource(profile.backgroundColor);


        // Set image

        holder.youCount.text = profile.count


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
        holder.youCount.typeface = fontCount
        holder.youTitle.typeface= fontTitle
        holder.youDesc.typeface= fontDesc
        holder.youDescCount.typeface= fontCount


    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return profiles.size
    }
}