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
import com.vyarth.ellipsify.model.Articles
import com.vyarth.ellipsify.model.Breathing

class ArticlesAdapter(private val item: List<Articles>,
                      private val activityClasses: List<Class<out AppCompatActivity>>,
):
    RecyclerView.Adapter<ArticlesAdapter.ArticlesHolder>(){

    // ViewHolder class
    class ArticlesHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.articlesTitle)
        val cardView: CardView = itemView.findViewById(R.id.articlesCardView) // Replace with the actual ID of your CardView
        val desc: TextView = itemView.findViewById(R.id.articlesDesc)
        val image: ImageView = itemView.findViewById(R.id.articlesImageView)
    }

    // Create new ViewHolders (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.articles_item, parent, false)
        return ArticlesHolder(itemView)
    }


    // Replace the contents of a ViewHolder (invoked by the layout manager)
    override fun onBindViewHolder(holder: ArticlesHolder, position: Int) {
        val data = item[position]
        val activityClass = activityClasses[position]

        holder.title.text = data.title

        // Set background color
        holder.cardView.setBackgroundResource(data.backgroundColor);
        // Set image
        holder.image.setImageResource(data.image)

        val maxCharsToShow = 50 // You can adjust this number based on your preference
        val truncatedText = if (data.desc?.length ?: 0 > maxCharsToShow) {
            "${data.desc?.substring(0, maxCharsToShow)}..."
        } else {
            data.desc
        }

        holder.desc.text = truncatedText


        val fontTitle = Typeface.createFromAsset(
            holder.itemView.context.assets,
            "epilogue_bold.ttf"
        )
        val fontDesc= Typeface.createFromAsset(
            holder.itemView.context.assets,
            "poppins_regular.ttf"
        )
        holder.desc.typeface = fontDesc
        holder.title.typeface= fontTitle

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, activityClass)
            intent.putExtra("article_title", data.title)
            intent.putExtra("article_desc", data.desc)
            intent.putExtra("article_image",data.image)
            intent.putExtra("article_bg",data.backgroundColor)
            holder.itemView.context.startActivity(intent)
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount(): Int {
        return item.size
    }
}