package com.vyarth.ellipsify.activities.community

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.Post
import java.util.Date

class CreatePostActivity : BaseActivity() {
    private val selectedCategories = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        setupActionBar()


        val fabCreate: FloatingActionButton =findViewById(R.id.post_create)
        fabCreate.setOnClickListener{
            showCategoryDialog()
        }
    }

    private fun showCategoryDialog() {
        val categories = arrayOf(
            "Love", "Heartbreak", "Study", "Relationship",
            "Career", "Friends", "Family", "Mental Health", "Self Development"
        )

        val checkedItems = BooleanArray(categories.size) { false }

        AlertDialog.Builder(this)
            .setTitle("Select Categories")
            .setMultiChoiceItems(categories, checkedItems) { _, index, isChecked ->
                if (isChecked) {
                    selectedCategories.add(categories[index])
                } else {
                    selectedCategories.remove(categories[index])
                }
            }
            .setPositiveButton("OK") { dialog, _ ->
                saveToFirebase()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun saveToFirebase() {
        val content = findViewById<AppCompatEditText>(R.id.create_text).text.toString().trim()
        val pseudoName = findViewById<AppCompatEditText>(R.id.pseudo_name).text.toString().trim() // Get the pseudo name
        val timestamp = Date().time

        // Check if title and text are not empty
        if (content.isNotEmpty()) {
            FirestoreClass().getUserData(
                onSuccess = { user ->
                    val userId = user.id
                    val post = Post(
                        id = "",
                        authorId = userId,
                        author = user.name, // Use authorName here
                        pseudoName = if (pseudoName.isNotEmpty()) pseudoName else "", // Store the pseudo name
                        content = content,
                        timestamp = timestamp,
                        likes = 0,
                        likedBy = listOf(),
                        comments = listOf(),
                        categories = selectedCategories.toList()
                    )

                    // Save the post
                    FirestoreClass().savePost(post,
                        onSuccess = { updatedPost ->
                            // Entry saved successfully
                            // Add any additional logic or UI updates here
                            Toast.makeText(this, "Post Saved", Toast.LENGTH_SHORT).show()
                            val resultIntent = Intent()
                            resultIntent.putExtra("dataChanged", true)
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                    ) { e ->
                        // Handle failure
                        Log.e("Firestore", "Error saving Post", e)
                    }
                },
                onFailure = {
                    // Handle failure to retrieve user data
                }
            )
        } else {
            // Show an error message if title or text is empty
            Toast.makeText(this, "Please enter some text for the post", Toast.LENGTH_SHORT).show()
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
}