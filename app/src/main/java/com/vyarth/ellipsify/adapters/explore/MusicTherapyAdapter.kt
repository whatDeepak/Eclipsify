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
import com.vyarth.ellipsify.model.BedtimeStory
import com.vyarth.ellipsify.model.MusicTherapy

class MusicTherapyAdapter(private val data: List<MusicTherapy>,
                          private val activityClasses: List<Class<out AppCompatActivity>>):
    RecyclerView.Adapter<MusicTherapyAdapter.MusicTherapyHolder>(){

    // ViewHolder class
    class MusicTherapyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.soundTextView)
        val cardView: CardView = itemView.findViewById(R.id.soundCardView) // Replace with the actual ID of your CardView
        val soundImageView: ImageView = itemView.findViewById(R.id.soundImageView) // Replace with the actual ID of your ImageView
    }

    // Create new ViewHolders (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicTherapyHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.sound_item, parent, false)
        return MusicTherapyHolder(itemView)
    }


    // Replace the contents of a ViewHolder (invoked by the layout manager)
    override fun onBindViewHolder(holder: MusicTherapyHolder, position: Int) {
        val data = data[position]
        val activityClass = activityClasses[position]


        holder.title.text = data.title

        // Set background color
        holder.cardView.setBackgroundResource(data.backgroundColor);

        // Set image
        holder.soundImageView.setImageResource(data.image)

        val fontTitle = Typeface.createFromAsset(
            holder.itemView.context.assets,
            "epilogue_bold.ttf"
        )

        holder.title.typeface= fontTitle

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, activityClass)
            intent.putExtra("story_title", data.title)
            intent.putExtra("story_refer",data.refer)
            intent.putExtra("story_bg",data.image)
            holder.itemView.context.startActivity(intent)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return data.size
    }
}