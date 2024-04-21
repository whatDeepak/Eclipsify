package com.vyarth.ellipsify.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.model.Post
import com.vyarth.ellipsify.firebase.FirestoreClass
import de.hdodenhof.circleimageview.CircleImageView

class PostsAdapter(
    private var posts: List<Post>
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewAuthor: TextView = itemView.findViewById(R.id.textViewAuthor)
        val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
        val buttonLike: ImageView = itemView.findViewById(R.id.buttonLike)
        val buttonComment: ImageView = itemView.findViewById(R.id.buttonComment)
        val userAvatar: CircleImageView = itemView.findViewById(R.id.user_avatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        val displayName = if (post.pseudoName != null && post.pseudoName.isNotEmpty()) post.pseudoName else post.author
        holder.textViewAuthor.text = displayName
        holder.textViewContent.text = post.content
        holder.textViewTime.text = getTimeAgo(post.timestamp)

        if (post.pseudoName.isNullOrEmpty()) {
            FirestoreClass().getUserData(
                onSuccess = { user ->
                    Glide.with(holder.itemView)
                        .load(user.image) // Use the user's image URL
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(holder.userAvatar)
                },
                onFailure = {
                    // Handle failure to retrieve user data
                })
        } else {
            holder.userAvatar.setImageResource(R.drawable.ic_user_place_holder)
        }

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

    private fun getTimeAgo(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - timestamp
        return when {
            timeDifference < 60000 -> "${timeDifference / 1000} seconds ago"
            timeDifference < 3600000 -> "${timeDifference / 60000} minutes ago"
            timeDifference < 86400000 -> "${timeDifference / 3600000} hours ago"
            timeDifference < 604800000 -> "${timeDifference / 86400000} days ago"
            timeDifference < 2592000000 -> "${timeDifference / 604800000} weeks ago"
            timeDifference < 31536000000 -> "${timeDifference / 2592000000} months ago"
            else -> "${timeDifference / 31536000000} years ago"
        }
    }
}
