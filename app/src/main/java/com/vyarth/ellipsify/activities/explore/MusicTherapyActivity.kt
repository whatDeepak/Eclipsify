package com.vyarth.ellipsify.activities.explore

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.activities.music.MusicActivity
import com.vyarth.ellipsify.activities.music.Sounds2Activity
import com.vyarth.ellipsify.activities.music.SoundsActivity
import com.vyarth.ellipsify.adapters.BedtimeStoryAdapter
import com.vyarth.ellipsify.adapters.SleepMusicAdapter
import com.vyarth.ellipsify.adapters.explore.MusicTherapyAdapter
import com.vyarth.ellipsify.model.BedtimeStory
import com.vyarth.ellipsify.model.MusicTherapy
import com.vyarth.ellipsify.model.SleepMusic

class MusicTherapyActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_therapy)

        setupActionBar()

        val sounds= listOf(
            MusicTherapy("Crown Chakra", "CrownChakra",R.drawable.healing_sound1),
            MusicTherapy("40 Hz Binaural Beats", "40HzBinauralBeats" ,R.drawable.healing_sound2)
        )

        val soundsActivityClasses = listOf(
            SoundsActivity::class.java,
            SoundsActivity::class.java
            // Add more activity classes as needed
        )

        // Get reference to the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.soundsRV)

        // Set layout manager and adapter
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = MusicTherapyAdapter(sounds, soundsActivityClasses)


        val music = listOf(
            SleepMusic("Calm Music", "A calm and therapeutic song.", "01:40","CalmMusic",R.drawable.music1),
            SleepMusic("Birds Singing", "Sweet Music of Birds Singing.", "02:00","BirdsSinging",R.drawable.music2),
            SleepMusic("Ocean Meditation", "Relaxing feel of ocean.", "02:00","OceanMeditation",R.drawable.music4),
            SleepMusic("Waterfall Jungle Birds", "Soft ambient sound of Waterfall.", "02:09","WaterfallJungleBirds",R.drawable.music3)
        )

        val musicActivityClasses = listOf(
            Sounds2Activity::class.java,
            Sounds2Activity::class.java,
            Sounds2Activity::class.java,
            Sounds2Activity::class.java,
            Sounds2Activity::class.java
            // Add more activity classes as needed
        )

        // Get reference to the RecyclerView
        val musicRecyclerView: RecyclerView = findViewById(R.id.recommendationRV)

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