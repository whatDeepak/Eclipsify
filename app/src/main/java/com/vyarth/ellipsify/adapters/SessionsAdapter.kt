package com.vyarth.ellipsify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R

class SessionAdapter : RecyclerView.Adapter<SessionAdapter.SessionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.sessions_item, parent, false)
        return SessionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
        // No need to bind any data since the layout is the same for all items
    }

    override fun getItemCount(): Int {
        // Return the number of times you want the sessions_item layout to repeat
        return 3
    }

    inner class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define any views and initialize them here if needed
    }
}
