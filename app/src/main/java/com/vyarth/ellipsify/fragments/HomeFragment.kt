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
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.MainActivity
import com.vyarth.ellipsify.activities.ProfileActivity
import com.vyarth.ellipsify.adapters.EmotionsAdapter
import com.vyarth.ellipsify.adapters.HomeAdapter
import com.vyarth.ellipsify.adapters.JournalAdapter
import com.vyarth.ellipsify.databinding.FragmentHomeBinding
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.Emotion
import com.vyarth.ellipsify.model.Home
import com.vyarth.ellipsify.model.Journal
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding? = null
    private val binding : FragmentHomeBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        (activity as? MainActivity)?.applyWindowFlags()

        val profileUser: CircleImageView = binding.userAvatar
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
        val emotionRecyclerView: RecyclerView = binding.recyclerView

        // Set layout manager and adapter
        emotionRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        emotionRecyclerView.adapter = EmotionsAdapter(emotions)


        val list = listOf(
            Home("1 on 1 Sessions", "Let’s open up to the things that matter the most ", R.drawable.bg_sessions, R.drawable.main_sessions,"Book Now",R.color.homeSessions,R.drawable.book_now),

            Home("Sleep Serenity", "Say goodbye to restless nights and awaken refreshed.", R.drawable.bg_sleep, R.drawable.home_sleep, "Relax Now",R.color.homeSleep,R.drawable.btn_sleep)
        )

        // Get reference to the RecyclerView
        val homeRecyclerView: RecyclerView = binding.homeRecyclerView

        // Set layout manager and adapter
        homeRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        homeRecyclerView.adapter = HomeAdapter(list)

        val greetingTextView: TextView = binding.greetingMain
        // Load custom typeface from the "assets" folder
        val customTypeface = Typeface.createFromAsset(requireContext().assets, "epilogue_medium.ttf")
        // Apply custom typeface to the button
        greetingTextView.typeface = customTypeface


        val quotesTextView: AppCompatButton= binding.btnQuotes
        // Load custom typeface from the "assets" folder
        val quotesTypeface = Typeface.createFromAsset(requireContext().assets, "epilogue_regular.ttf")
        // Apply custom typeface to the button
        quotesTextView.typeface = quotesTypeface


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
        return binding.root;

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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


}