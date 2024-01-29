package com.vyarth.ellipsify.adapters.explore

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
import com.vyarth.ellipsify.model.Affirmations
import com.vyarth.ellipsify.model.Breathing

class AffirmationAdapter(private val item: List<Affirmations>):
    RecyclerView.Adapter<AffirmationAdapter.AffirmationHolder>(){

    // ViewHolder class
    class AffirmationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.affirmTitle)
        val cardView: CardView = itemView.findViewById(R.id.affirmCardView) // Replace with the actual ID of your CardView
    }

    // Create new ViewHolders (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AffirmationHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.affirmation_item, parent, false)
        return AffirmationHolder(itemView)
    }


    // Replace the contents of a ViewHolder (invoked by the layout manager)
    override fun onBindViewHolder(holder: AffirmationHolder, position: Int) {
        val data = item[position]

        holder.title.text = data.title

        // Set background color
        holder.cardView.setBackgroundResource(data.backgroundColor);

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
        holder.title.typeface= fontTitle


    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return item.size
    }
}