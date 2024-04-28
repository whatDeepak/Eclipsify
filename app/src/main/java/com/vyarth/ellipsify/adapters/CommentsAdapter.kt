package com.vyarth.ellipsify.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.Comment
import com.vyarth.ellipsify.model.Post

class CommentsAdapter(private val context: Context, private var comments: List<Comment>) :
    RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userAvatarImageView: ImageView = itemView.findViewById(R.id.user_avatar)
        val textViewCommentAuthor: TextView = itemView.findViewById(R.id.textViewCommentAuthor)
        val textViewCommentTime: TextView = itemView.findViewById(R.id.textViewCommentTime)
        val textViewCommentContent: TextView = itemView.findViewById(R.id.textViewCommentContent)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.textViewCommentAuthor.text = comment.author
        holder.textViewCommentContent.text = comment.content
        holder.textViewCommentTime.text = getTimeAgo(comment.timestamp)

        FirestoreClass().getUserDataAvatar(
            userId = comment.authorId,
            onSuccess = { user ->
                Glide.with(holder.itemView)
                    .load(user.image) // Use the user's image URL
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(holder.userAvatarImageView)
                },
                onFailure = {
                    // Handle failure to retrieve user data
                }
        )

        val currentUserID = FirestoreClass().getCurrentUserID()
        holder.deleteButton.visibility = if (currentUserID == comment.authorId) View.VISIBLE else View.GONE

        holder.deleteButton.setOnClickListener {
            if (currentUserID == comment.authorId) {
                showDeleteConfirmationDialog(comment)
            }
        }
    }

    private fun showDeleteConfirmationDialog(comment: Comment) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle("Delete Post")
        alertDialogBuilder.setMessage("Are you sure you want to delete this post?")
        alertDialogBuilder.setCancelable(true)

        alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
            // Delete the post from the database
            FirestoreClass().deleteComment(comment.id,comment.postId,
                onSuccess = {
                    // Post deleted successfully
                    Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show()
                    updateComments(comments.filterNot { it.id == comment.id })
                },
                onFailure = { e ->
                    // Handle the error
                    Toast.makeText(context, "Error deleting Comment: ${e.message}", Toast.LENGTH_SHORT).show()
                })
            dialog.dismiss()
        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
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
    override fun getItemCount() = comments.size

    fun updateComments(newComments: List<Comment>) {
        comments = newComments
        notifyDataSetChanged()
    }
}