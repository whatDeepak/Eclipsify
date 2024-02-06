package com.vyarth.ellipsify.fragments

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.adapters.JournalAdapter
import com.vyarth.ellipsify.adapters.YouAdapter
import com.vyarth.ellipsify.databinding.FragmentJournalBinding
import com.vyarth.ellipsify.databinding.FragmentYouBinding
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.Journal
import com.vyarth.ellipsify.model.Profile
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class YouFragment : Fragment() {

    private var _binding: FragmentYouBinding? = null
    private val binding: FragmentYouBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYouBinding.inflate(inflater, container, false)

        val tvTitle: TextView = binding.tvYouTitle
        val customTypeface = Typeface.createFromAsset(requireContext().assets, "epilogue_semibold.ttf")
        tvTitle.typeface = customTypeface

        val userId = FirestoreClass().getCurrentUserID()

        // Get a reference to the user's document in the login_history collection
        val userDocumentRef = FirebaseFirestore.getInstance()
            .collection("login_history")
            .document(userId)

        // Get the current week's start and end dates
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val weekStartDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val weekEndDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)

        // Fetch the login timestamps
        userDocumentRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val timestamps = documentSnapshot.get("timestamps") as? Map<String, Boolean> ?: mapOf()

                // Calculate current streak
                val currentStreak = calculateCurrentStreak(timestamps).toString()

                // Calculate best streak ever
                val bestStreak = calculateBestStreak(timestamps)

                // Update UI or store the streak information as needed

                // Update the weekLogin HashMap
                // Update the weekLogin HashMap
                val calendar = Calendar.getInstance()
                val weekLogin = LinkedHashMap<String, Boolean>()
                var totalLogins = 0

                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                for (i in Calendar.SUNDAY..Calendar.SATURDAY) {
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                    val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
                    val isLogin = timestamps.containsKey(date) && timestamps[date] == true
                    weekLogin[dayOfWeek] = isLogin
                    totalLogins += if (isLogin) 1 else 0
                    calendar.add(Calendar.DAY_OF_WEEK, 1)   // Increment the day of the week
                }
                Log.e("taggggggggg",weekLogin.toString())
                // Update the UI with the new data
                val profiles = listOf(
                    Profile("Streak", "day\n" + "current streak", currentStreak, R.drawable.bg_streak, "Best streak $bestStreak days", emptyMap()),
                    Profile("Weekly status", "day\n" + "this week.", totalLogins.toString(), R.drawable.bg_weekstatus, "M   T   W   T   F   S   S", weekLogin)
                )

                // Get reference to the RecyclerView
                val recyclerView: RecyclerView = binding.youRecyclerView

                // Set layout manager and adapter
                recyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                recyclerView.adapter = YouAdapter(profiles)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun calculateCurrentStreak(timestamps: Map<String, Boolean>): Int {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayDate = dateFormat.format(Date())

        var currentStreak = 0
        var currentDate = todayDate

        // Check the consecutive logins from today backward
        while (timestamps.containsKey(currentDate) && timestamps[currentDate] == true) {
            currentStreak++
            currentDate = getPreviousDate(currentDate)
        }

        return currentStreak
    }

    private fun calculateBestStreak(timestamps: Map<String, Boolean>): Int {
        var bestStreak = 0
        var currentStreak = 0

        // Iterate over the timestamps to find the best streak
        for ((date, login) in timestamps) {
            if (login) {
                currentStreak++
                bestStreak = maxOf(bestStreak, currentStreak)
            } else {
                currentStreak = 0
            }
        }

        return bestStreak
    }

    private fun getPreviousDate(date: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.parse(date)
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        return dateFormat.format(calendar.time)
    }

}