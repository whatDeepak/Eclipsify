package com.vyarth.ellipsify.fragments

import android.content.Intent
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
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.journals.DailyJournalActivity
import com.vyarth.ellipsify.activities.journals.MoodJournalActivity
import com.vyarth.ellipsify.adapters.EmotionsAdapter
import com.vyarth.ellipsify.adapters.JournalAdapter
import com.vyarth.ellipsify.databinding.FragmentJournalBinding
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.Journal


class JournalFragment : Fragment() {

    private var _binding: FragmentJournalBinding? = null
    private val binding: FragmentJournalBinding get() = _binding!!

    private var dailyJournalCount = ""
    private var moodJournalCount = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)

        val tvTitle: TextView = binding.tvJournalTitle
        val customTypeface = Typeface.createFromAsset(requireContext().assets, "epilogue_semibold.ttf")
        tvTitle.typeface = customTypeface

        // Fetch the total number of journals from Firestore
        FirestoreClass().getTotalJournalsCount(
            onSuccess = { totalJournals ->
                dailyJournalCount = "$totalJournals Journal"
                Log.e("Firestore", dailyJournalCount)
                updateUIWithJournals()
                // Update UI with the correct data after fetching the count

            },
            onFailure = { e ->
                // Handle failure
                Log.e("Firestore", "Error getting total journals count", e)
            }
        )
        // Fetch the total number of journals from Firestore
        FirestoreClass().getMoodTotalJournalsCount(
            onSuccess = { totalJournals ->
                moodJournalCount = "$totalJournals Journal"
                Log.e("Firestore", moodJournalCount)
                updateUIWithJournals()
                // Update UI with the correct data after fetching the count

            },
            onFailure = { e ->
                // Handle failure
                Log.e("Firestore", "Error getting total journals count", e)
            }
        )

        return binding.root
    }

    private fun updateUIWithJournals() {
        if (!isAdded) {
            return
        }
        val journals = listOf(
            Journal("Daily Journal", "Express your daily thoughts , feelings and experiences.", R.drawable.bg_journal, R.drawable.journal_daily, dailyJournalCount, R.color.jrnlDaily),
            Journal("Mood Journal", "Write your emotions and keep track of mood patterns.", R.drawable.bg_moodjournal, R.drawable.journal_mood, moodJournalCount, R.color.jrnlMood)
        )

        val activityClasses = listOf(
            DailyJournalActivity::class.java,
            MoodJournalActivity::class.java
            // Add more activity classes as needed
        )

        // Get reference to the RecyclerView
        val recyclerView: RecyclerView = binding.journalRecyclerView

        // Set layout manager and adapter
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = JournalAdapter(journals, activityClasses, dailyJournalCount, moodJournalCount)
    }

    private fun onJournalItemClicked(journal: Journal) {
        // Handle item click, for example, start a new activity
        val intent = Intent(requireContext(), DailyJournalActivity::class.java)
        intent.putExtra("journal_title", journal.title)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}