package com.vyarth.ellipsify.adapters


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
import com.google.android.material.imageview.ShapeableImageView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.BedtimeStory
import com.vyarth.ellipsify.model.Journal
import com.vyarth.ellipsify.model.SleepMusic

class SleepMusicAdapter(private val music: List<SleepMusic>,
                        private val activityClasses: List<Class<out AppCompatActivity>>):
    RecyclerView.Adapter<SleepMusicAdapter.SleepMusicHolder>(){

    // ViewHolder class
    class SleepMusicHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.musicTitle)
        val desc: TextView = itemView.findViewById(R.id.musicDesc)
        val time: TextView = itemView.findViewById(R.id.musicTime)
        val image: ShapeableImageView = itemView.findViewById(R.id.musicImage)
    }

    // Create new ViewHolders (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SleepMusicHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.sleep_music_item, parent, false)
        return SleepMusicHolder(itemView)
    }


    // Replace the contents of a ViewHolder (invoked by the layout manager)
    override fun onBindViewHolder(holder: SleepMusicHolder, position: Int) {
        val data = music[position]
        val activityClass = activityClasses[position]


        holder.title.text = data.title
        holder.desc.text=data.desc

        // Set background color
        holder.image.setBackgroundResource(data.image);

        holder.time.text = data.time

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
        holder.desc.typeface= fontDesc

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
        return music.size
    }
}