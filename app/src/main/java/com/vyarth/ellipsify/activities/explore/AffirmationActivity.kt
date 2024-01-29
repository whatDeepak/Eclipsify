package com.vyarth.ellipsify.activities.explore

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.activities.music.ExerciseActivity
import com.vyarth.ellipsify.adapters.explore.AffirmationAdapter
import com.vyarth.ellipsify.adapters.explore.BreathingAdapter
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.Affirmations
import com.vyarth.ellipsify.model.Breathing
import www.sanju.zoomrecyclerlayout.ZoomRecyclerLayout

class AffirmationActivity : BaseActivity() {

    private val itemList = mutableListOf<Affirmations>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_affirmation)

        setupActionBar()

        val backgroundResourceIds = arrayOf(R.drawable.bg_med1, R.drawable.bg_med2, R.drawable.bg_med3, R.drawable.bg_med4, R.drawable.bg_med5)

        val currentDate = getCurrentDate()
        FirestoreClass().getAffirmationsForCurrentDate(currentDate) { affirmations ->
            // Update itemList with fetched affirmations
            affirmations.forEachIndexed { index, affirmation ->
                val backgroundResourceId = backgroundResourceIds[index % backgroundResourceIds.size]
                if (index < itemList.size) {
                    itemList[index] = Affirmations(affirmation, backgroundResourceId)
                } else {
                    itemList.add(Affirmations(affirmation, backgroundResourceId))
                }
            }
            // Notify the adapter of the data change if you are using a RecyclerView

            // After updating the itemList, log the first item's title
            Log.e("mnjbj", itemList.getOrNull(0)?.title.toString())

            // Set up RecyclerView after fetching data
            val affirmationRecyclerView: RecyclerView = findViewById(R.id.affirmationRV)
            val linearLayoutManager = ZoomRecyclerLayout(this)
            linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
            linearLayoutManager.reverseLayout = true
            linearLayoutManager.stackFromEnd = true
            affirmationRecyclerView.layoutManager = linearLayoutManager
            // Set layout manager and adapter
            affirmationRecyclerView.adapter = AffirmationAdapter(itemList)

            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(affirmationRecyclerView)
            affirmationRecyclerView.isNestedScrollingEnabled = false
        }

    }

    private fun getCurrentDate(): String {
        // Implement your logic to get the current date, for example:
        // val currentDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        // return currentDate

        // Replace the above logic with your actual implementation
        return "1" // For testing, assuming the current date is 1
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