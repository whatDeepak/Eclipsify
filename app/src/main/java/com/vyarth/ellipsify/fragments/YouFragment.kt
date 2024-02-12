package com.vyarth.ellipsify.fragments

import android.graphics.Color
import android.graphics.Paint
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
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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
                val calendar = Calendar.getInstance()
                val weekLogin = LinkedHashMap<String, Boolean>()
                var totalLogins = 0

                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

                for (i in Calendar.SUNDAY..Calendar.SATURDAY) {
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                    val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)

                    // Check if the timestamps map contains the current date
                    val isLogin = timestamps.containsKey(date) && timestamps[date] == true
                    weekLogin[dayOfWeek] = isLogin

                    // Increment the total login count if there is a login for the current day
                    totalLogins += if (isLogin) 1 else 0

                    // Move to the next day
                    calendar.add(Calendar.DAY_OF_WEEK, 1)
                }
                Log.e("taggggggggg",weekLogin.toString())
                // Update the UI with the new data
                val profiles = listOf(
                    Profile("Streak", "day\n" + "current streak", currentStreak, R.drawable.bg_streak, "Best streak $bestStreak days", emptyMap()),
                    Profile("Weekly status", "day\n" + "this week.", totalLogins.toString(), R.drawable.bg_weekstatus, "S   M   T   W   T   F   S", weekLogin)
                )

                // Get reference to the RecyclerView
                val recyclerView: RecyclerView = binding.youRecyclerView

                // Set layout manager and adapter
                recyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                recyclerView.adapter = YouAdapter(profiles)
            }
        }

        // Inside onCreateView method of YouFragment
        val currentWeekDates = getCurrentWeekDates()

        val moodEntries = mutableListOf<BarEntry>()

// Fetch mood data for each day of the current week
        for ((index, date) in currentWeekDates.withIndex()) {
            FirestoreClass().fetchSelectedEmotion(userId, date,
                onSuccess = { mood ->
                    // Map mood to numerical value
                    val moodValue = when (mood) {
                        "Sad" -> 1f
                        "Angry" -> 2f
                        "Manic" -> 3f
                        "Calm" -> 4f
                        "Happy" -> 5f
                        else -> 0f // Default value
                    }

                    // Add mood entry to the list
                    moodEntries.add(BarEntry(index.toFloat(), moodValue))

                    // If mood entries for all days are fetched, create chart
                    if (moodEntries.size == currentWeekDates.size) {
                        createBarChart(moodEntries)
                    }
                },
                onFailure = { exception ->
                    // Handle failure here
                    Log.e("Fetch Mood Error", "Error fetching mood for date $date: $exception")
                }
            )
        }



        val cardView = binding.moodGraph
        cardView.setBackgroundResource(R.drawable.bg_moodgraph)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun getCurrentWeekDates(): List<String> {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMM, yyyy", Locale.getDefault())
        val currentWeekDates = mutableListOf<String>()

        // Iterate over each day of the current week
        for (i in Calendar.SUNDAY..Calendar.SATURDAY) {
            calendar.set(Calendar.DAY_OF_WEEK, i)
            val date = dateFormat.format(calendar.time)
            currentWeekDates.add(date)
        }
        return currentWeekDates
    }

    private fun createBarChart(entries: List<BarEntry>) {
        val barDataSet = BarDataSet(entries, "Mood Data")
        val data = BarData(barDataSet)
        barDataSet.valueTextSize = 0.0f // Disable displaying values on top of bars
        barDataSet.color = resources.getColor(R.color.bar_color)

        // Customize chart appearance and behavior
        val barChart = binding.barChart
        barChart.data = data
        // Customize description (title)
        barChart.description.text = "MOOD GRAPH"
        barChart.description.textColor = Color.BLACK
        barChart.description.textSize = 14f // in sp
        barChart.description.textAlign = Paint.Align.CENTER // or any other alignment you prefer

        barChart.invalidate()

        // Customize X axis
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textSize = 10f
        xAxis.textColor = Color.BLACK
        xAxis.valueFormatter = IndexAxisValueFormatter(arrayOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"))

        // Customize Y axis
        val yAxisLeft = barChart.axisLeft
        yAxisLeft.setDrawGridLines(false)
        yAxisLeft.textSize = 13f
        yAxisLeft.textColor = Color.BLACK
        yAxisLeft.valueFormatter = IndexAxisValueFormatter(arrayOf("", "Sad", "Angry", "Manic", "Calm", "Happy"))

        // Disable right Y axis
        val yAxisRight = barChart.axisRight
        yAxisRight.isEnabled = false

        // Disable chart interactions
        barChart.setPinchZoom(false)
        barChart.isScaleXEnabled = false
        barChart.isScaleYEnabled = false
        barChart.legend.isEnabled = false
        barChart.description.isEnabled = false
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