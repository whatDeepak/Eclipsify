package com.vyarth.ellipsify.adapters

import android.view.LayoutInflater
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.model.Emotion
import com.vyarth.ellipsify.model.Explore

class ExploreAdapter(private val explore: List<Explore>):
    RecyclerView.Adapter<ExploreAdapter.ExploreViewHolder>(){

    // ViewHolder class
    class ExploreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val exploreTitle: TextView = itemView.findViewById(R.id.exploreTitle)
        val cardView: CardView = itemView.findViewById(R.id.cardView) // Replace with the actual ID of your CardView
        val exploreImageView: ImageView = itemView.findViewById(R.id.exploreImageView) // Replace with the actual ID of your ImageView
        val exploreDesc: TextView = itemView.findViewById(R.id.exploreDesc)
        val exploreBtn: TextView = itemView.findViewById(R.id.exploreBtn)
        val exploreArrow: ImageView =itemView.findViewById(R.id.exploreArrow)


    }

    // Create new ViewHolders (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.explore_item, parent, false)
        return ExploreViewHolder(itemView)
    }


    // Replace the contents of a ViewHolder (invoked by the layout manager)
    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        val xplr = explore[position]


        holder.exploreTitle.text = xplr.title
        holder.exploreDesc.text=xplr.desc

        // Set background color
        holder.cardView.setBackgroundResource(xplr.backgroundColor);

        holder.exploreArrow.setImageResource(xplr.btnImage)
        holder.exploreArrow.setColorFilter(ContextCompat.getColor(holder.itemView.context, xplr.btnColor))



        // Set image
        holder.exploreImageView.setImageResource(xplr.imageResId)

        holder.exploreBtn.text = xplr.btn

        holder.exploreBtn.setTextColor(ContextCompat.getColor(holder.itemView.context, xplr.btnColor))

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
        holder.exploreBtn.typeface = fontCount
        holder.exploreTitle.typeface= fontTitle
        holder.exploreDesc.typeface= fontDesc


    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return explore.size
    }
}