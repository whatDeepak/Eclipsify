package com.vyarth.ellipsify.activities.community

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.adapters.PostsAdapter
import com.vyarth.ellipsify.model.Post

class CommunityActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postsAdapter: PostsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        recyclerView = findViewById(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Initialize the adapter with an empty list
        postsAdapter = PostsAdapter(ArrayList())
        recyclerView.adapter = postsAdapter

        // Load posts from Firestore
        loadPosts()

        val fabCreate: FloatingActionButton = findViewById(R.id.fab_create)
        fabCreate.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ENTRY_ACTIVITY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_ENTRY_ACTIVITY && resultCode == Activity.RESULT_OK) {
            // Check if data has changed
            val dataChanged = data?.getBooleanExtra("dataChanged", false) ?: false
            if (dataChanged) {
                loadPosts()
            }
        }
    }

    private fun loadPosts() {
        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
            .get()
            .addOnSuccessListener { result ->
                val postsList = ArrayList<Post>()
                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    postsList.add(post)
                }
                // Update the adapter with the loaded posts
                postsAdapter.updatePosts(postsList)
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    companion object {
        private const val REQUEST_CODE_ENTRY_ACTIVITY = 1001 // You can use any unique value
    }
}
