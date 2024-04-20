package com.vyarth.ellipsify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.model.Post

class PostsAdapter(
    private var posts: List<Post>
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewAuthor: TextView = itemView.findViewById(R.id.textViewAuthor)
        val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
        val buttonLike: Button = itemView.findViewById(R.id.buttonLike)
        val buttonComment: Button = itemView.findViewById(R.id.buttonComment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.textViewAuthor.text = post.author
        holder.textViewContent.text = post.content
        holder.buttonLike.text = "Like (${post.likes})"
        holder.buttonComment.text = "Comments (${post.comments.size})"

        // Handle like button click
        holder.buttonLike.setOnClickListener {
            // Increase the like count and update Firestore
            val db = FirebaseFirestore.getInstance()
            db.collection("posts").document(post.id)
                .update("likes", post.likes + 1)
        }

        // Handle comment button click
        holder.buttonComment.setOnClickListener {
            // Handle the comment action
            // You can open a new activity to handle comments
        }
    }

    override fun getItemCount() = posts.size

    fun updatePosts(newPosts: List<Post>) {
        posts = newPosts
        notifyDataSetChanged()
    }
}
