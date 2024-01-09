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
import com.vyarth.ellipsify.adapters.JournalAdapter
import com.vyarth.ellipsify.adapters.YouAdapter
import com.vyarth.ellipsify.databinding.FragmentJournalBinding
import com.vyarth.ellipsify.databinding.FragmentYouBinding
import com.vyarth.ellipsify.model.Journal
import com.vyarth.ellipsify.model.Profile

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

        val profiles = listOf(
            Profile("Streak", "day\n" + "current streak", "1", R.drawable.bg_streak,"Best streak X days"),

            Profile("Weekly status", "day\n" + "this week.", "1", R.drawable.bg_weekstatus, "M T W T F S S")
        )

        // Get reference to the RecyclerView
        val recyclerView: RecyclerView = binding.youRecyclerView

        // Set layout manager and adapter
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = YouAdapter(profiles)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle: TextView = binding.tvYouTitle
        val customTypeface = Typeface.createFromAsset(requireContext().assets, "epilogue_semibold.ttf")
        tvTitle.typeface = customTypeface
    }
}