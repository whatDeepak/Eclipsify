package com.vyarth.ellipsify.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.community.PostDetailsActivity
import com.vyarth.ellipsify.model.Post
import com.vyarth.ellipsify.firebase.FirestoreClass
import de.hdodenhof.circleimageview.CircleImageView

class PostsAdapter(
    private val context: Context,
    private var posts: List<Post>
) : RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewAuthor: TextView = itemView.findViewById(R.id.textViewAuthor)
        val textViewContent: TextView = itemView.findViewById(R.id.textViewContent)
        val textViewTime: TextView = itemView.findViewById(R.id.textViewTime)
        val buttonLike: ImageView = itemView.findViewById(R.id.buttonLike)
        val buttonComment: ImageView = itemView.findViewById(R.id.buttonComment)
        val userAvatar: CircleImageView = itemView.findViewById(R.id.user_avatar)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
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

        val currentUserID = FirestoreClass().getCurrentUserID()
        if (post.likedBy.contains(currentUserID)) {
            holder.buttonLike.setImageResource(R.drawable.likedbutton) // Change to liked icon
        } else {
            holder.buttonLike.setImageResource(R.drawable.likebutton) // Change to default like icon
        }

        holder.deleteButton.visibility = if (currentUserID == post.authorId) View.VISIBLE else View.GONE

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
            // Toggle like status
            if (post.likedBy.contains(currentUserID)) {
                // User already liked the post, so remove the like
                post.likedBy = post.likedBy.filter { it != currentUserID }
                holder.buttonLike.setImageResource(R.drawable.likebutton) // Change to default like icon
            } else {
                // User hasn't liked the post, so add the like
                post.likedBy = post.likedBy + currentUserID
                holder.buttonLike.setImageResource(R.drawable.likedbutton) // Change to liked icon
            }

            // Update the like count in Firestore
            val db = FirebaseFirestore.getInstance()
            db.collection("posts").document(post.id)
                .update("likedBy", post.likedBy)
        }

        // Handle comment button click
        holder.buttonComment.setOnClickListener {
            // Handle the comment action
            // You can open a new activity to handle comments
            val post = posts[position]
            val intent = Intent(context, PostDetailsActivity::class.java)
            intent.putExtra("post", post)
            context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            if (currentUserID == post.authorId) {
                showDeleteConfirmationDialog(post)
            }
        }
    }

    private fun showDeleteConfirmationDialog(post: Post) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Delete Post")
        alertDialogBuilder.setMessage("Are you sure you want to delete this post?")
        alertDialogBuilder.setCancelable(true)

        alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
            // Delete the post from the database
            FirestoreClass().deletePost(post.id,
                onSuccess = {
                    // Post deleted successfully
                    Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                    updatePosts(posts.filterNot { it.id == post.id })
                },
                onFailure = { e ->
                    // Handle the error
                    Toast.makeText(context, "Error deleting post: ${e.message}", Toast.LENGTH_SHORT).show()
                })
            dialog.dismiss()
        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
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
