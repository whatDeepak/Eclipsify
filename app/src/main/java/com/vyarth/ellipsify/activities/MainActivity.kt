package com.vyarth.ellipsify.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.firebase.FirestoreClass
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var greetingTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Set the status bar text color to dark
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        val profileUser: CircleImageView = findViewById(R.id.user_avatar)
        profileUser.setOnClickListener {
            // Launch the sign in screen.
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
        }

        // Call the function to get user data
        FirestoreClass().getUserData(
            onSuccess = { user ->
                // User data retrieved successfully
                val greetingTextView: TextView = findViewById(R.id.greeting_main)
                val greeting = getGreetingMessage(user.name)
                greetingTextView.text = greeting

                // Find the index of the comma
                val commaIndex = greeting.indexOf(",")

                // Create a StringBuilder to build the modified string
                val modifiedString = StringBuilder(greeting)

                // Insert a newline character after the comma
                modifiedString.insert(commaIndex + 1, "\n")

                // Create a SpannableString
                val spannableString = SpannableString(modifiedString.toString())

                // Apply medium style and increased text size to everything before the comma
                spannableString.setSpan(
                    StyleSpan(Typeface.NORMAL),
                    0,              // Start index of the string
                    commaIndex,     // End index before the comma
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // Apply bold style and increased text size to everything after the comma
                spannableString.setSpan(
                    TextAppearanceSpan(this, R.style.BoldTextAppearance),
                    commaIndex + 2,  // Start index after the comma and newline
                    greeting.length, // End index of the string
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                // Set the final SpannableString to the TextView and increase text size
                greetingTextView.text = spannableString

            },
            onFailure = {
                // Handle failure to retrieve user data
            }
        )
    }
    private fun getGreetingMessage(userName: String): String {
        val cal = Calendar.getInstance()
        val timeOfDay = cal[Calendar.HOUR_OF_DAY]

        return when (timeOfDay) {
            in 0..11 -> "Good Morning,$userName!"
            in 12..15 -> "Good Afternoon,$userName!"
            in 16..20 -> "Good Evening,$userName!"
            else -> "Good Night,$userName!"
        }
    }
}