package com.vyarth.ellipsify.fragments

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.MainActivity
import com.vyarth.ellipsify.activities.ProfileActivity
import com.vyarth.ellipsify.adapters.EmotionsAdapter
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.Emotion
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.applyWindowFlags()

        val profileUser: CircleImageView = view.findViewById(R.id.user_avatar)
        profileUser.setOnClickListener {
            // Launch the sign in screen.
            startActivity(Intent(requireContext(), ProfileActivity::class.java))
        }

        val emotions = listOf(
            Emotion("Happy", R.color.happyBg, R.drawable.mood_happy),
            Emotion("Calm", R.color.calmBg, R.drawable.mood_calm),
            Emotion("Manic", R.color.manicBg, R.drawable.mood_manic),
            Emotion("Angry", R.color.angryBg, R.drawable.mood_angry),
            Emotion("Sad", R.color.sadBg, R.drawable.mood_sad)
        )

        // Get reference to the RecyclerView
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)

        // Set layout manager and adapter
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = EmotionsAdapter(emotions)

        val greetingTextView: TextView = view.findViewById(R.id.greeting_main)
        // Load custom typeface from the "assets" folder
        val customTypeface = Typeface.createFromAsset(requireContext().assets, "epilogue_medium.ttf")
        // Apply custom typeface to the button
        greetingTextView.typeface = customTypeface

        // Call the function to get user data
        FirestoreClass().getUserData(
            onSuccess = { user ->
                // User data retrieved successfully
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


                context?.let { nonNullContext ->

                    // Apply medium style and increased text size to everything before the comma
                    spannableString.setSpan(
                        StyleSpan(Typeface.NORMAL),
                        0,              // Start index of the string
                        commaIndex,     // End index before the comma
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    // Apply style and color to everything before the comma
                    spannableString.setSpan(
                        ForegroundColorSpan(ContextCompat.getColor(nonNullContext, R.color.mediumTextColor)),
                        0,              // Start index of the string
                        commaIndex,     // End index before the comma
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    // Apply bold style and increased text size to everything after the comma
                    spannableString.setSpan(
                        TextAppearanceSpan(nonNullContext, R.style.BoldTextAppearance),
                        commaIndex + 2,  // Start index after the comma and newline
                        greeting.length, // End index of the string
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                    spannableString.setSpan(
                        ForegroundColorSpan(resources.getColor(R.color.boldTextColor)),
                        commaIndex + 2,  // Start index after the comma and newline
                        greeting.length, // End index of the string
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )

                }

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
            in 4..11 -> "Good Morning,$userName!"
            in 12..15 -> "Good Afternoon,$userName!"
            in 16..20 -> "Good Evening,$userName!"
            else -> "Good Night,$userName!"
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = HomeFragment()
    }


}