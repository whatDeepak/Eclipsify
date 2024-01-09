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
import com.vyarth.ellipsify.model.Home
import com.vyarth.ellipsify.model.Journal


class HomeAdapter(private val list: List<Home>):
    RecyclerView.Adapter<HomeAdapter.HomeViewHolder>(){

    // ViewHolder class
    class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val homeTitle: TextView = itemView.findViewById(R.id.homeTitle)
        val cardView: CardView = itemView.findViewById(R.id.cardView) // Replace with the actual ID of your CardView
        val homeImageView: ImageView = itemView.findViewById(R.id.homeImageView) // Replace with the actual ID of your ImageView
        val homeDesc: TextView = itemView.findViewById(R.id.homeDesc)
        val homeCount: TextView = itemView.findViewById(R.id.homeBtn)
        val btnImage: ImageView =itemView.findViewById(R.id.homeBtnImage)


    }

    // Create new ViewHolders (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.home_item, parent, false)
        return HomeViewHolder(itemView)
    }


    // Replace the contents of a ViewHolder (invoked by the layout manager)
    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val item = list[position]


        holder.homeTitle.text = item.title
        holder.homeDesc.text=item.desc

        // Set background color
        holder.cardView.setBackgroundResource(item.backgroundColor);


        // Set image
        holder.homeImageView.setImageResource(item.imageResId)

        holder.btnImage.setImageResource(item.btnImage)

        holder.homeCount.text = item.btn

        holder.homeCount.setTextColor(ContextCompat.getColor(holder.itemView.context, item.btnColor))

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
        holder.homeCount.typeface = fontCount
        holder.homeTitle.typeface= fontTitle
        holder.homeDesc.typeface= fontDesc


    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return list.size
    }
}