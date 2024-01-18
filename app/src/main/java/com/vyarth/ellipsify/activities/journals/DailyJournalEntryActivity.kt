package com.vyarth.ellipsify.activities.journals

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DailyJournalEntryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_journal_entry)

        setupActionBar()

        val currentDate = Calendar.getInstance()
        val monthYearText = SimpleDateFormat("EEEE, dd MMM, yyyy", Locale.getDefault()).format(currentDate.time)
        findViewById<TextView>(R.id.create_date).text = monthYearText

        val fabCreate: FloatingActionButton =findViewById(R.id.fab_create)
        fabCreate.setOnClickListener{
            saveToFirebase()
        }
    }

    private fun saveToFirebase() {
        // Get the title and text from your EditText fields
        val title = findViewById<AppCompatEditText>(R.id.create_title).text.toString().trim()
        val text = findViewById<AppCompatEditText>(R.id.create_text).text.toString().trim()

        // Check if title and text are not empty
        if (title.isNotEmpty() && text.isNotEmpty()) {
            FirestoreClass().saveJournalEntry(title, text,
                onSuccess = {
                    // Entry saved successfully
                    // Add any additional logic or UI updates here
                    Toast.makeText(this, "Journal Saved", Toast.LENGTH_SHORT).show()        
                },
                onFailure = { e ->
                    // Handle failure
                    Log.e("Firestore", "Error saving journal entry", e)
                }
            )
        } else {
            // Show an error message if title or text is empty
            Toast.makeText(this, "Please enter a title and text for your journal entry", Toast.LENGTH_SHORT).show()
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