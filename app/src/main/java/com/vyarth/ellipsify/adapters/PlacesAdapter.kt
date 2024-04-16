package com.vyarth.ellipsify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.Place
import com.vyarth.ellipsify.R

class PlaceAdapter(private val placeList: List<Place>) : RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.items_places, parent, false)
        return PlaceViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val place = placeList[position]
        holder.bind(place)
    }

    override fun getItemCount(): Int {
        return placeList.size
    }

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        private val addressTextView: TextView = itemView.findViewById(R.id.addressTextView)

        fun bind(place: Place) {
            nameTextView.text = place.name
            addressTextView.text = place.address
        }
    }
}
