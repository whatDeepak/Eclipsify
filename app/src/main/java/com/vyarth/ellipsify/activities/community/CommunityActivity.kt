package com.vyarth.ellipsify.activities.community

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.adapters.CategoriesAdapter
import com.vyarth.ellipsify.adapters.PostsAdapter
import com.vyarth.ellipsify.model.Post

class CommunityActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postsAdapter: PostsAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter
    private var allPosts: List<Post> = ArrayList()

    private var selectedCategory: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)

        setupActionBar()

        recyclerView = findViewById(R.id.recyclerViewPosts)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Initialize the adapter with an empty list
        postsAdapter = PostsAdapter(this,ArrayList())
        recyclerView.adapter = postsAdapter

        // Load posts from Firestore
        loadPosts()

        val fabCreate: FloatingActionButton = findViewById(R.id.fab_create)
        fabCreate.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ENTRY_ACTIVITY)
        }

        // Initialize the horizontal RecyclerView for categories
        val recyclerViewCategories: RecyclerView = findViewById(R.id.recyclerViewCategories)
        recyclerViewCategories.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val categories = getCategories()
        categoriesAdapter = CategoriesAdapter(this, categories)
        recyclerViewCategories.adapter = categoriesAdapter

        // Handle default selection
        val defaultCategory = "All"
        selectedCategory = defaultCategory
        categoriesAdapter.setOnItemClickListener { category ->
            selectedCategory = category
            filterPostsByCategory(selectedCategory)
        }
        categoriesAdapter.notifyDataSetChanged()
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

    private fun getCategories(): List<String> {
        // Load categories from Firestore and return
        return listOf("All", "Love", "Heartbreak", "Study", "Relationship",
            "Career", "Friends", "Family", "Mental Health", "Self Development") // Sample categories
    }

    private fun loadPosts() {
        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val postsList = ArrayList<Post>()
                for (document in result) {
                    val post = document.toObject(Post::class.java)
                    postsList.add(post)
                }
                allPosts = postsList
                // Update the adapter with the loaded posts
                postsAdapter.updatePosts(postsList)
            }
            .addOnFailureListener { exception ->
                // Handle the error
            }
    }

    private fun filterPostsByCategory(category: String?) {
        if (category.isNullOrEmpty() || category == "All") {
            // Show all posts if no category is selected or "All" is selected
            postsAdapter.updatePosts(allPosts)
        } else {
            // Filter posts by the selected category
            val filteredPosts = allPosts.filter { it.categories?.contains(category) ?: false }
            postsAdapter.updatePosts(filteredPosts)
        }
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

    companion object {
        private const val REQUEST_CODE_ENTRY_ACTIVITY = 1001 // You can use any unique value
    }

    override fun onResume() {
        super.onResume()
        loadPosts()
    }
}
