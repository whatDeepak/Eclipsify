package com.vyarth.ellipsify.activities.journals

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.firebase.FirestoreClass

class MoodJournalEntryActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_journal_entry)

        setupActionBar()

//        val currentDate = Calendar.getInstance()
//        val monthYearText = SimpleDateFormat("EEEE, dd MMM, yyyy", Locale.getDefault()).format(currentDate.time)
//        findViewById<TextView>(R.id.create_date).text = monthYearText

        val fabCreate: FloatingActionButton =findViewById(R.id.fab_create)
        fabCreate.setOnClickListener{
            saveToFirebaseMood()
        }

        // Inside the onCreate method of DailyJournalEntryActivity
        val selectedDate = intent.getStringExtra("selectedDate") ?: ""
        val title = intent.getStringExtra("title") ?: ""
        val text = intent.getStringExtra("text") ?: ""

        // Use selectedDate, title, and text as needed

//        // Format the selected date
//        val formattedDate = SimpleDateFormat("EEEE, dd MMM, yyyy", Locale.getDefault()).format(selectedDate)

        // Set the formatted date to the TextView
        findViewById<TextView>(R.id.create_date).text = selectedDate

        // Set the title and text to the EditText fields
        val titleEditText = findViewById<AppCompatEditText>(R.id.create_title)
        val textEditText = findViewById<AppCompatEditText>(R.id.create_text)

        titleEditText.setText(title)
        textEditText.setText(text)

        titleEditText.setOnClickListener { showMoodMenu(it) }

    }

    private fun showMoodMenu(anchorView: View) {
        val popupMenu = PopupMenu(this, anchorView)
        val inflater: MenuInflater = popupMenu.menuInflater
        inflater.inflate(R.menu.mood_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            // Handle menu item selection
            when (menuItem.itemId) {
                R.id.mood_happy -> setMood("Happy")
                R.id.mood_calm -> setMood("Calm")
                R.id.mood_manic -> setMood("Manic")
                R.id.mood_angry -> setMood("Angry")
                R.id.mood_sad -> setMood("Sad")
            }
            true
        }

        try {
            val fieldMPopup = popupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception){
            Log.e("Main", "Error showing menu icons.", e)
        } finally {
            popupMenu.show()
        }

    }


    private fun setMood(mood: String) {
        // Set the selected mood as the title in the EditText
        val titleEditText = findViewById<AppCompatEditText>(R.id.create_title)
        titleEditText.setText(mood)
    }

    private fun saveToFirebaseMood() {
        // Get the title and text from your EditText fields
        val title = findViewById<AppCompatEditText>(R.id.create_title).text.toString().trim()
        val text = findViewById<AppCompatEditText>(R.id.create_text).text.toString().trim()

        // Check if title and text are not empty
        if (title.isNotEmpty() && text.isNotEmpty()) {
            FirestoreClass().saveMoodJournalEntry(title, text,
                onSuccess = {
                    // Entry saved successfully
                    // Add any additional logic or UI updates here
                    Toast.makeText(this, "Journal Saved", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("dataChanged", true)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
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