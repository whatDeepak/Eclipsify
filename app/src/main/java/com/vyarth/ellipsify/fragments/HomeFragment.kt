package com.vyarth.ellipsify.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.play.core.integrity.e
import com.google.firebase.firestore.FirebaseFirestore
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.MainActivity
import com.vyarth.ellipsify.activities.ProfileActivity
import com.vyarth.ellipsify.activities.article.ArticlesActivity
import com.vyarth.ellipsify.activities.journals.DailyJournalActivity
import com.vyarth.ellipsify.activities.journals.DailyJournalEntryActivity
import com.vyarth.ellipsify.activities.sleep.SleepActivity
import com.vyarth.ellipsify.adapters.EmotionsAdapter
import com.vyarth.ellipsify.adapters.HomeAdapter
import com.vyarth.ellipsify.databinding.FragmentHomeBinding
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.Emotion
import com.vyarth.ellipsify.model.Home
import com.vyarth.ellipsify.model.User
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    // A global variable for User Name
    private lateinit var mUserName: String

    private val firestoreClass = FirestoreClass()
    private var selectedEmotion: Emotion? = null
//    private var progressDialog: Dialog? = null
//    private var isFragmentAttached: Boolean = false

    companion object{
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }


    private var _binding : FragmentHomeBinding? = null
    private val binding : FragmentHomeBinding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

//        showSplashScreen()
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            if (isFragmentAttached) {
//                progressDialog?.dismiss()
//            }
//        }, 1000)

        firestoreClass.loadUserData(requireContext())

        // Inflate the layout for this fragment
        (activity as? MainActivity)?.applyWindowFlags()

        val profileUser: CircleImageView = binding.userAvatar
        profileUser.setOnClickListener {
            // Launch the sign in screen.
            startActivityForResult((Intent(requireContext(), ProfileActivity::class.java)),
                MY_PROFILE_REQUEST_CODE)
        }

        checkDailyMoodEntry()
        setEmotionClickListener()

        val btnJournal: AppCompatButton = binding.btnJournal
        btnJournal.setOnClickListener {
            val intent = Intent(requireContext(), DailyJournalEntryActivity::class.java)
            startActivity(intent)
        }

        val btnArticles: AppCompatButton = binding.btnArticles
        btnArticles.setOnClickListener {
            val intent = Intent(requireContext(), ArticlesActivity::class.java)
            startActivity(intent)
        }

        val list = listOf(
            Home("1 on 1 Sessions", "Let’s open up to the things that matter the most ", R.drawable.bg_sessions, R.drawable.main_sessions,"Book Now",R.color.homeSessions,R.drawable.book_now),

            Home("Sleep Serenity", "Say goodbye to restless nights and awaken refreshed.", R.drawable.bg_sleep, R.drawable.home_sleep, "Relax Now",R.color.homeSleep,R.drawable.btn_sleep)
        )

        val activityClasses = listOf(
            DailyJournalActivity::class.java,
            SleepActivity::class.java
            // Add more activity classes as needed
        )

        // Get reference to the RecyclerView
        val homeRecyclerView: RecyclerView = binding.homeRecyclerView

        // Set layout manager and adapter
        homeRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        homeRecyclerView.adapter = HomeAdapter(list,activityClasses)

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

        greetingMessage()
        getDailyQuote()


        // Assuming you have a method to get the current user ID
        val userId = FirestoreClass().getCurrentUserID()

// Get a reference to the user's document in the login_history collection
        val userDocumentRef = FirebaseFirestore.getInstance()
            .collection("login_history")
            .document(userId)

// Get the current timestamps array (if it exists) or create a new one
        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayDate = dateFormat.format(Date())

            val timestampsMap = if (documentSnapshot.exists()) {
                documentSnapshot.get("timestamps") as? Map<String, Boolean> ?: mapOf()
            } else {
                mapOf()
            }.toMutableMap()

            // Check if there's already a timestamp for today
            if (!timestampsMap.containsKey(todayDate)) {
                // Add the timestamp for today
                timestampsMap[todayDate] = true

                // Update the timestamps map in the document
                userDocumentRef.set(mapOf("timestamps" to timestampsMap))
                    .addOnSuccessListener {
                        // Successfully created the document with timestamps
                        Log.e("Firestore", "Document created")
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        Log.e("Firestore", "Error creating document", e)
                    }
            } else {
                // Timestamp for today already exists
                Log.e("Firestore", "Timestamp for today already exists")
            }
        }


        return binding.root;

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    private fun greetingMessage(){
        val greetingTextView: TextView = binding.greetingMain
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

    /**
     * A function to get the current user details from firebase.
     */
    fun updateUserDetails(user: User) {
        mUserName = user.name

        // The instance of the user image of the navigation view.
        val navUserImage = binding.userAvatar

        Log.e("UserData", "User Image URL: ${user.image}")

            Glide
                .with(requireContext())
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(navUserImage)

    }

    private fun getDailyQuote() {
        firestoreClass.getDailyQuote(
            onSuccess = { quote ->
                binding.btnQuotes.text = quote

            },
            onFailure = { e ->
                Log.e("HomeFragment", "Error getting daily quote", e)
            }
        )
    }

    private fun checkDailyMoodEntry() {
        val userId = firestoreClass.getCurrentUserID()
        val currentDate = firestoreClass.getCurrentFormattedDate()

        firestoreClass.checkDailyMoodEntry(userId, currentDate,
            onSuccess = { moodEntryExists ->
                if (moodEntryExists) {
                    // Hide the daily_checkin LinearLayout
                    //binding.dailyCheckin.visibility = View.GONE
                }
            },
            onFailure = { e ->
                Log.e("HomeFragment", "Error checking daily mood entry", e)
            }
        )
    }

    private fun storeDailyMood(emotion: String) {
        val userId = firestoreClass.getCurrentUserID()
        val currentDate = firestoreClass.getCurrentFormattedDate()

        firestoreClass.storeDailyMood(userId, currentDate, emotion,
            onSuccess = {
                // Hide the daily_checkin LinearLayout
                //binding.dailyCheckin.visibility = View.GONE
            },
            onFailure = { e ->
                Log.e("HomeFragment", "Error storing daily mood", e)
            }
        )
    }

    private fun updateSelectedMoodInDatabase(emotion: Emotion) {
        selectedEmotion = emotion
        if (selectedEmotion != null) {
            // If a mood was previously selected, update the database
            storeDailyMood(emotion.mood)
        } else {
            // If no mood was selected, store the new mood in the database
            storeDailyMood(emotion.mood)
        }
    }

    private fun setEmotionClickListener() {
        val emotions = listOf(
            Emotion("Happy", R.color.happyBg, R.drawable.mood_happy, R.color.selected_happyBg,false),
            Emotion("Calm", R.color.calmBg, R.drawable.mood_calm, R.color.selected_calmBg,false),
            Emotion("Manic", R.color.manicBg, R.drawable.mood_manic, R.color.selected_manicBg,false),
            Emotion("Angry", R.color.angryBg, R.drawable.mood_angry, R.color.selected_angryBg,false),
            Emotion("Sad", R.color.sadBg, R.drawable.mood_sad, R.color.selected_sadBg,false)
        )

        // Get reference to the RecyclerView
        val emotionRecyclerView: RecyclerView = binding.recyclerView

        // Set layout manager and adapter
        emotionRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val emotionsAdapter = EmotionsAdapter(emotions)
        emotionRecyclerView.adapter = emotionsAdapter

        // Fetch selected emotion from database
        val userId = FirestoreClass().getCurrentUserID()
        val currentDate = FirestoreClass().getCurrentFormattedDate()
        FirestoreClass().fetchSelectedEmotion(userId, currentDate,
            onSuccess = { selectedEmotion ->
                // Update the UI based on the fetched selected emotion
                emotions.forEach { emotion ->
                    emotion.isSelected = emotion.mood == selectedEmotion
                }
                emotionsAdapter.notifyDataSetChanged()
            },
            onFailure = { e ->
                Log.e("HomeFragment", "Error fetching selected emotion", e)
            }
        )

        // Set item click listener
        emotionsAdapter.setOnItemClickListener { emotion ->
            // Update the selected emotion
            emotions.forEach { it.isSelected = it == emotion }
            // Update the UI
            emotionsAdapter.notifyDataSetChanged()
            // Update the selected mood in the database
            updateSelectedMoodInDatabase(emotion)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == ProfileActivity.MY_PROFILE_REQUEST_CODE
        ) {
            // Get the user updated details.
            greetingMessage()
            FirestoreClass().loadUserData(requireContext())
        }else {
            Log.e("Cancelled", "Cancelled")
        }
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        isFragmentAttached = true
//    }
//
//    override fun onDetach() {
//        super.onDetach()
//        isFragmentAttached = false
//    }
//    private fun showSplashScreen() {
//        if (isFragmentAttached) {
//            val splashView = layoutInflater.inflate(R.layout.dialog_progress,null)
//            progressDialog = Dialog(requireContext())
//            progressDialog?.setContentView(splashView)
//            progressDialog?.setCancelable(false)
//            progressDialog?.show()
//
//        }
//    }

}