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

import com.vyarth.ellipsify.model.Meditation

class MeditationAdapter(private val item: List<Meditation>,
                          private val activityClasses: List<Class<out AppCompatActivity>>,
                        ):
    RecyclerView.Adapter<MeditationAdapter.MeditationHolder>(){

    // ViewHolder class
    class MeditationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.medTitle)
        val cardView: CardView = itemView.findViewById(R.id.medCardView) // Replace with the actual ID of your CardView
        val time: TextView = itemView.findViewById(R.id.medTime)
        val image: ImageView = itemView.findViewById(R.id.medImageView)
    }

    // Create new ViewHolders (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeditationHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.meditation_item, parent, false)
        return MeditationHolder(itemView)
    }


    // Replace the contents of a ViewHolder (invoked by the layout manager)
    override fun onBindViewHolder(holder: MeditationHolder, position: Int) {
        val data = item[position]
        val activityClass = activityClasses[position]

        holder.title.text = data.title

        // Set background color
        holder.cardView.setBackgroundResource(data.backgroundColor);

        // Set image
        holder.image.setImageResource(data.image)

        holder.time.text = data.time

        holder.time.setTextColor(ContextCompat.getColor(holder.itemView.context, data.timeColor))

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
        holder.time.typeface = fontCount
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
        return item.size
    }
}