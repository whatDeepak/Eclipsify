package com.vyarth.ellipsify.fragments

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.adapters.EmotionsAdapter
import com.vyarth.ellipsify.adapters.JournalAdapter
import com.vyarth.ellipsify.databinding.FragmentJournalBinding
import com.vyarth.ellipsify.model.Journal


class JournalFragment : Fragment() {

    private var _binding: FragmentJournalBinding? = null
    private val binding: FragmentJournalBinding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)

        val tvTitle: TextView = binding.tvJournalTitle
        val customTypeface = Typeface.createFromAsset(requireContext().assets, "epilogue_semibold.ttf")
        tvTitle.typeface = customTypeface


        val journals = listOf(
            Journal("Daily Journal", "Express your daily thoughts , feelings and experiences.", R.drawable.bg_journal, R.drawable.journal_daily,"0 Journals",R.color.jrnlDaily),

            Journal("Mood Journal", "Write your emotions and keep track of mood patterns.", R.drawable.bg_moodjournal, R.drawable.journal_mood, "0 Journals",R.color.jrnlMood)
        )

        // Get reference to the RecyclerView
        val recyclerView: RecyclerView = binding.journalRecyclerView

        // Set layout manager and adapter
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = JournalAdapter(journals)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}