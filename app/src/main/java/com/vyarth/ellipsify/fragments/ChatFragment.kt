package com.vyarth.ellipsify.fragments

import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.vyarth.ellipsify.R

class ChatFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_chat, container, false)

        val tvTitle: TextView = view.findViewById(R.id.tv_chat_title)
        val customTypeface = Typeface.createFromAsset(requireContext().assets, "poppins_medium.ttf")
        tvTitle.typeface = customTypeface

        return view
    }

    companion object {

        fun newInstance() = ChatFragment()
    }
}