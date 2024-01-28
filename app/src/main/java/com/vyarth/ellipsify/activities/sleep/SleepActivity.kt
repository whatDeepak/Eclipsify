package com.vyarth.ellipsify.activities.sleep

import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.activities.music.MusicActivity
import com.vyarth.ellipsify.adapters.BedtimeStoryAdapter
import com.vyarth.ellipsify.adapters.SleepMusicAdapter
import com.vyarth.ellipsify.model.BedtimeStory
import com.vyarth.ellipsify.model.SleepMusic

class SleepActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep)

        setupActionBar()


        val story = listOf(
            BedtimeStory("The Dream Weaver's Lullaby", "Enchanting journey with the Dream Weaver in a magical dreamscape.", R.drawable.bg_affirm, "04:32", R.color.xplrAffirm,"TheDreamWeaver'sLullaby",R.drawable.bedtime1),
            BedtimeStory("The Starlight Symphony", " Luna dances through galaxies in a celestial orchestra of stardust.", R.drawable.bg_sleep, "02:53", R.color.homeSleep,"TheStarlightSymphony",R.drawable.bedtime2)
        )

        val storyActivityClasses = listOf(
            MusicActivity::class.java,
            MusicActivity::class.java
            // Add more activity classes as needed
        )

        // Get reference to the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.bedtimeRV)

        // Set layout manager and adapter
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = BedtimeStoryAdapter(story, storyActivityClasses)

        val music = listOf(
            SleepMusic("In The Light", "A calm and therapeutic song.", "04:36","InTheLight",R.drawable.music1),
            SleepMusic("Quiet Time", "Soothing relaxation music.", "02:25","QuietTime",R.drawable.music2),
            SleepMusic("Relaxing Green Nature", "Relaxing feel of green nature.", "03:43","RelaxingGreenNature",R.drawable.music3),
            SleepMusic("Serenity", "Soft ambient song for deep relaxation.", "03:06","Serenity",R.drawable.music4),
            SleepMusic("Our Hopes And Dreams", "Emotional piano solo, with quiet, ambient strings.", "03:55","OurHopesAndDreams",R.drawable.music5),
        )

        val musicActivityClasses = listOf(
            MusicActivity::class.java,
            MusicActivity::class.java,
            MusicActivity::class.java,
            MusicActivity::class.java,
            MusicActivity::class.java
            // Add more activity classes as needed
        )

        // Get reference to the RecyclerView
        val musicRecyclerView: RecyclerView = findViewById(R.id.sleepmusicRV)

        // Set layout manager and adapter
        musicRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        musicRecyclerView.adapter = SleepMusicAdapter(music, musicActivityClasses)
    }


    private fun setupActionBar() {

        val toolbarSignInActivity=findViewById<Toolbar>(R.id.toolbar_profile)
        val tvTitle: TextView = findViewById(R.id.profile_title)
        val customTypeface = Typeface.createFromAsset(assets, "poppins_medium.ttf")
        tvTitle.typeface = customTypeface
        setSupportActionBar(toolbarSignInActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbarSignInActivity.setNavigationOnClickListener { onBackPressed() }
    }

}