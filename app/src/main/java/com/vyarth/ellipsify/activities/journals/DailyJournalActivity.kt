package com.vyarth.ellipsify.activities.journals

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.adapters.DatePickerAdapter
import com.vyarth.ellipsify.adapters.EmotionsAdapter
import com.vyarth.ellipsify.model.DatePicker
import com.vyarth.ellipsify.model.Emotion
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DailyJournalActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_journal)

        setupActionBar()

        val currentDate = Calendar.getInstance()
        val monthYearText = SimpleDateFormat("MMMM, yyyy", Locale.getDefault()).format(currentDate.time)
        findViewById<TextView>(R.id.date_picker_text).text = monthYearText

        val daysOfWeek = mutableListOf<DatePicker>()
        val currentCalendar = Calendar.getInstance()
        currentCalendar.firstDayOfWeek = Calendar.SUNDAY

        for (i in Calendar.SUNDAY..Calendar.SATURDAY) {
            currentCalendar.set(Calendar.DAY_OF_WEEK, i)
            val dayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH)
            val month = currentCalendar.get(Calendar.MONTH) + 1 // Months are 0-based
            daysOfWeek.add(DatePicker(dayOfMonth, 0))
        }

        // Find today's date in the list and set selectedPosition
        val today = currentDate.get(Calendar.DAY_OF_MONTH)
        val todayIndex = daysOfWeek.indexOfFirst { it.date == today }
        val defaultSelectedPosition = if (todayIndex != -1) todayIndex else -1  // If today is not found, default to the first item


        val dPRecyclerView: RecyclerView = findViewById(R.id.datePickerRV)

        // Set layout manager and adapter
        dPRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        dPRecyclerView.adapter = DatePickerAdapter(daysOfWeek,defaultSelectedPosition)

        val dpDiv: LinearLayout=findViewById(R.id.dp_div)
        dpDiv.setOnClickListener{openDatePicker()}
    }

    private fun openDatePicker() {
        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

        datePicker.show(supportFragmentManager, "DatePicker")

        // Setting up the event for when ok is clicked
        datePicker.addOnPositiveButtonClickListener {
            // formatting date in dd-mm-yyyy format.
            val dateFormatter = SimpleDateFormat("dd-MM-yyyy")
            val date = dateFormatter.format(Date(it))
            Toast.makeText(this, "$date is selected", Toast.LENGTH_LONG).show()
        }

        // Setting up the event for when cancelled is clicked
        datePicker.addOnNegativeButtonClickListener {
            Toast.makeText(this, "${datePicker.headerText} is cancelled", Toast.LENGTH_LONG).show()
        }

        // Setting up the event for when back button is pressed
        datePicker.addOnCancelListener {
            Toast.makeText(this, "Date Picker Cancelled", Toast.LENGTH_LONG).show()
        }
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