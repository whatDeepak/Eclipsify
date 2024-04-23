package com.vyarth.ellipsify.activities.community

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.adapters.CommentsAdapter
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.Comment
import com.vyarth.ellipsify.model.Post
import java.util.Date
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class PostDetailsActivity : BaseActivity() {

    private lateinit var post: Post
    private lateinit var commentsAdapter: CommentsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        setupActionBar()

        // Retrieve the post and its comments from intent
        post = intent.getParcelableExtra<Post>("post")!!

        // Display post details
        findViewById<TextView>(R.id.textViewAuthor).text = post.author
        findViewById<TextView>(R.id.textViewContent).text = post.content
        findViewById<TextView>(R.id.textViewTime).text = getTimeAgo(post.timestamp)


        // Set up RecyclerView for comments
        commentsAdapter = CommentsAdapter(post.comments)
        val recyclerViewComments: RecyclerView = findViewById(R.id.recyclerViewComments)
        recyclerViewComments.layoutManager = LinearLayoutManager(this)
        recyclerViewComments.adapter = commentsAdapter

        loadComments()

        // Handle posting a new comment
        val buttonPostComment: ImageButton = findViewById(R.id.buttonPostComment)
        val editTextComment: EditText = findViewById(R.id.text_input)
        // Inside buttonPostComment click listener
        buttonPostComment.setOnClickListener {
            val postId = post.id
            saveToFirebase(postId)
        }

        // Like functionality
        val buttonLike: ImageView = findViewById(R.id.buttonLike)
        val currentUserID = FirestoreClass().getCurrentUserID()
        if (post.likedBy.contains(currentUserID)) {
            buttonLike.setImageResource(R.drawable.likedbutton) // Change to liked icon
        } else {
            buttonLike.setImageResource(R.drawable.likebutton) // Change to default like icon
        }
        buttonLike.setOnClickListener {
            toggleLike(post, buttonLike)
        }

        val deleteButton: ImageView = findViewById(R.id.deleteButton)
        deleteButton.visibility = if (currentUserID == post.authorId) View.VISIBLE else View.GONE
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog(post)
        }

        val userAvatarImageView: CircleImageView = findViewById(R.id.user_avatar)
        if (post.pseudoName.isNullOrEmpty()) {
            FirestoreClass().getUserData(
                onSuccess = { user ->
                    Glide.with(this)
                        .load(user.image) // Use the user's image URL
                        .centerCrop()
                        .placeholder(R.drawable.ic_user_place_holder)
                        .into(userAvatarImageView)
                },
                onFailure = {
                    // Handle failure to retrieve user data
                })
        } else {
            userAvatarImageView.setImageResource(R.drawable.ic_user_place_holder)
        }

    }

    private fun toggleLike(post: Post, buttonLike: ImageView) {
        val currentUserID = FirestoreClass().getCurrentUserID()
        if (post.likedBy.contains(currentUserID)) {
            // User already liked the post, so remove the like
            post.likedBy = post.likedBy.filter { it != currentUserID }
            buttonLike.setImageResource(R.drawable.likebutton) // Change to default like icon
        } else {
            // User hasn't liked the post, so add the like
            post.likedBy = post.likedBy + currentUserID
            buttonLike.setImageResource(R.drawable.likedbutton) // Change to liked icon
        }

        // Update the like count in Firestore
        val db = FirebaseFirestore.getInstance()
        db.collection("posts").document(post.id)
            .update("likedBy", post.likedBy)
    }


    private fun showDeleteConfirmationDialog(post: Post) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Delete Post")
        alertDialogBuilder.setMessage("Are you sure you want to delete this post?")
        alertDialogBuilder.setCancelable(true)

        alertDialogBuilder.setPositiveButton("Yes") { dialog, _ ->
            // Delete the post from the database
            FirestoreClass().deletePost(post.id,
                onSuccess = {
                    // Post deleted successfully
                    Toast.makeText(this, "Post deleted", Toast.LENGTH_SHORT).show()
                    finish() // Close the activity after deletion
                },
                onFailure = { e ->
                    // Handle the error
                    Toast.makeText(this, "Error deleting post: ${e.message}", Toast.LENGTH_SHORT).show()
                })
            dialog.dismiss()
        }

        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun saveToFirebase(postId: String) {
        val commentContent = findViewById<AppCompatEditText>(R.id.text_input).text.toString().trim()

        // Check if comment content is not empty
        if (commentContent.isNotEmpty()) {
            FirestoreClass().getUserData(
                onSuccess = { user ->
                    val userId = user.id
                    val comment = Comment(
                        id = "", // This will be autogenerated by Firestore
                        postId = postId,
                        author = user.name,
                        content = commentContent,
                        timestamp = Date().time
                    )

                    // Save the comment
                    FirestoreClass().postComment(comment,
                        onSuccess = {
                            loadComments()
                            findViewById<AppCompatEditText>(R.id.text_input).text?.clear()
                            // Comment saved successfully
                            // Add any additional logic or UI updates here
                            Toast.makeText(this, "Comment Saved", Toast.LENGTH_SHORT).show()
                        },
                        onFailure = { e ->
                            // Handle failure
                            Log.e("Firestore", "Error saving Comment", e)
                        },
                        onCommentsUpdated = { comments ->
                            // Update UI with the latest comments
                            commentsAdapter.updateComments(comments)
                        }
                    )
                },
                onFailure = {
                    // Handle failure to retrieve user data
                }
            )
        } else {
            // Show an error message if comment content is empty
            Toast.makeText(this, "Please enter some text for the comment", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadComments() {
        val postId = post.id
        val db = FirebaseFirestore.getInstance()

        // Get a reference to the "posts" collection
        val postsCollection = db.collection("posts")

        // Get a reference to the specific post document
        val postDocument = postsCollection.document(postId)

        // Fetch comments for the post
        postDocument.get().addOnSuccessListener { documentSnapshot ->
            val post = documentSnapshot.toObject(Post::class.java)
            if (post != null) {
                // Get the comments associated with the post
                val comments = post.comments.sortedByDescending { it.timestamp }
                // Update the UI with the comments
                commentsAdapter.updateComments(comments)
            } else {
                // Handle case where post is not found
                showToast("Post not found")
            }
        }.addOnFailureListener { e ->
            // Handle failure to fetch comments
            showToast("Error fetching comments: ${e.message}")
        }
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupActionBar() {

        val toolbarSignInActivity=findViewById<Toolbar>(R.id.toolbar_profile)
        val tvTitle: TextView = findViewById(R.id.profile_title)
        val customTypeface = Typeface.createFromAsset(assets, "poppins_medium.ttf")
        tvTitle.typeface = customTypeface
        setSupportActionBar(toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbarSignInActivity.setNavigationOnClickListener { onBackPressed() }
    }
}