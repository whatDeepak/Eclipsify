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
import com.vyarth.ellipsify.activities.article.ArticlesActivity
import com.vyarth.ellipsify.activities.explore.AffirmationActivity
import com.vyarth.ellipsify.activities.explore.BreathingActivity
import com.vyarth.ellipsify.activities.explore.MeditationActivity
import com.vyarth.ellipsify.activities.explore.MusicTherapyActivity
import com.vyarth.ellipsify.activities.journals.DailyJournalActivity
import com.vyarth.ellipsify.activities.journals.MoodJournalActivity
import com.vyarth.ellipsify.adapters.ExploreAdapter
import com.vyarth.ellipsify.databinding.FragmentExploreBinding
import com.vyarth.ellipsify.model.Explore
import com.vyarth.ellipsify.model.MusicTherapy


class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding: FragmentExploreBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

        val tvTitle: TextView = binding.tvExploreTitle
        val customTypeface = Typeface.createFromAsset(requireContext().assets, "epilogue_semibold.ttf")
        tvTitle.typeface = customTypeface


        val explores= listOf(
            Explore("Meditation", "Let’s embark on a journey of tranquility and self-discovery.", R.drawable.bg_meditate, R.drawable.explore_meditate,"Inner Calm",R.color.xplrMeditate,R.drawable.explore_arrow),

            Explore("Breathing", "Find exercises to bring peace in your life.", R.drawable.bg_breathe, R.drawable.explore_breathe, "Let’s Start",R.color.xplrBreathe,R.drawable.explore_arrow),

            Explore("Daily Affirmations", "Empower yourself with positive affirmations. ", R.drawable.bg_affirm, R.drawable.explore_affirm,"Destroy Negativity",R.color.xplrAffirm,R.drawable.explore_arrow),

            Explore("Music Therapy", "Let Music heal your mind and soul.", R.drawable.bg_music, R.drawable.explore_music, "Play Music",R.color.xplrMusic,R.drawable.explore_arrow),

            Explore("Articles", "Expand your awareness and share Wisdom.", R.drawable.bg_article, R.drawable.explore_article, "Enhance Wisdom",R.color.xplrArticle,R.drawable.explore_arrow)

        )

        val activityClasses = listOf(
            MeditationActivity::class.java,
            BreathingActivity::class.java,
            AffirmationActivity::class.java,
            MusicTherapyActivity::class.java,
            ArticlesActivity::class.java
            // Add more activity classes as needed
        )

        // Get reference to the RecyclerView
        val recyclerView: RecyclerView = binding.exploreRecyclerView

        // Set layout manager and adapter
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = ExploreAdapter(explores, activityClasses)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}