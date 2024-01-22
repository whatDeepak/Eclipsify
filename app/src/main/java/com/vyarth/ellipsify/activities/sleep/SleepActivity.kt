package com.vyarth.ellipsify.activities.sleep

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.activities.journals.DailyJournalActivity
import com.vyarth.ellipsify.activities.journals.MoodJournalActivity
import com.vyarth.ellipsify.adapters.BedtimeStoryAdapter
import com.vyarth.ellipsify.adapters.JournalAdapter
import com.vyarth.ellipsify.model.BedtimeStory
import com.vyarth.ellipsify.model.Journal

class SleepActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sleep)

        setupActionBar()


        val story = listOf(
            BedtimeStory("The Dream Weaver's Lullaby", "Enchanting journey with the Dream Weaver in a magical dreamscape.", R.drawable.bg_affirm, "00:00", R.color.xplrAffirm),
            BedtimeStory("The Starlight Symphony", " Luna dances through galaxies in a celestial orchestra of stardust.", R.drawable.bg_sleep, "00.00", R.color.homeSleep)
        )

        val activityClasses = listOf(
            DailyJournalActivity::class.java,
            MoodJournalActivity::class.java
            // Add more activity classes as needed
        )

        // Get reference to the RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.bedtimeRV)

        // Set layout manager and adapter
        recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = BedtimeStoryAdapter(story, activityClasses)
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