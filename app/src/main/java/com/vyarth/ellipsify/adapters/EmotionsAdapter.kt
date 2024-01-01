package com.vyarth.ellipsify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R

class EmotionsAdapter(private val emotions: List<String>) :
    RecyclerView.Adapter<EmotionsAdapter.EmotionViewHolder>() {

    // ViewHolder class
    class EmotionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emotionTextView: TextView = itemView.findViewById(R.id.emotionTextView)
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
        holder.emotionTextView.text = emotion
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return emotions.size
    }
}
