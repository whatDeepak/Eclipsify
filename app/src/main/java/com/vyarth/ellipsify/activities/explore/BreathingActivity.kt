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
import com.vyarth.ellipsify.activities.music.ExerciseActivity
import com.vyarth.ellipsify.activities.music.MusicActivity
import com.vyarth.ellipsify.adapters.explore.BreathingAdapter
import com.vyarth.ellipsify.adapters.explore.MeditationAdapter
import com.vyarth.ellipsify.model.Breathing
import com.vyarth.ellipsify.model.Meditation

class BreathingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_breathing)

        setupActionBar()

        val itemList = listOf(
            Breathing("Zen Meditation", R.drawable.bg_med1, "4:50", R.color.med1, "ZenMeditation",R.drawable.med1),
            Breathing("Reduce Stress", R.drawable.bg_med2, "4:50", R.color.med2, "ReduceStress",R.drawable.med2),
            Breathing("Relaxation", R.drawable.bg_med3, "4:50", R.color.med3, "Relaxation",R.drawable.med3),
            Breathing("Increase Happiness", R.drawable.bg_med4, "4:50", R.color.med4, "IncreaseHappiness",R.drawable.med4),
            Breathing("Reduce Anxiety",R.drawable.bg_med5, "4:50", R.color.med5, "ReduceAnxiety",R.drawable.med5),
            Breathing("Personal Growth",R.drawable.bg_med6, "4:50", R.color.med6, "PersonalGrowth",R.drawable.med6)
            // Add more items as needed
        )

        val breathingActivityClasses = listOf(
            ExerciseActivity::class.java,
            ExerciseActivity::class.java,
            ExerciseActivity::class.java,
            ExerciseActivity::class.java,
            ExerciseActivity::class.java,
            ExerciseActivity::class.java
            // Add more activity classes as needed
        )

        val breathingRecyclerView: RecyclerView = findViewById(R.id.breathingRV)
        // Set layout manager and adapter
        breathingRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        breathingRecyclerView.adapter = BreathingAdapter(itemList, breathingActivityClasses)
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