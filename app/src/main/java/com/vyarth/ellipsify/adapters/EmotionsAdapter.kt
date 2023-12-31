package com.vyarth.ellipsify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.model.Emotion

class EmotionsAdapter(private val emotions: List<Emotion>) :
    RecyclerView.Adapter<EmotionsAdapter.EmotionViewHolder>() {

    // ViewHolder class
    class EmotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emotionTextView: TextView = itemView.findViewById(R.id.emotionTextView)
        val cardView: CardView = itemView.findViewById(R.id.cardView) // Replace with the actual ID of your CardView
        val emotionImageView: ImageView = itemView.findViewById(R.id.emotionImageView) // Replace with the actual ID of your ImageView
    }

    // Create new ViewHolders (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmotionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.emotion_item, parent, false)
        return EmotionViewHolder(itemView)
    }


    // Replace the contents of a ViewHolder (invoked by the layout manager)
    override fun onBindViewHolder(holder: EmotionViewHolder, position: Int) {
        val emotion = emotions[position]


        holder.emotionTextView.text = emotion.mood

        // Set background color
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.context, emotion.backgroundColor))

        // Set image
        holder.emotionImageView.setImageResource(emotion.imageResId)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return emotions.size
    }
}
