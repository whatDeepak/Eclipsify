package com.vyarth.ellipsify.activities.journals

import android.content.Intent
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.activities.SignInActivity
import com.vyarth.ellipsify.adapters.DatePickerAdapter
import com.vyarth.ellipsify.adapters.EmotionsAdapter
import com.vyarth.ellipsify.model.DatePicker
import com.vyarth.ellipsify.model.Emotion
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DailyJournalActivity : BaseActivity(), DatePickerAdapter.DatePickerClickListener {

    private lateinit var datePickerAdapter: DatePickerAdapter
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
        datePickerAdapter = DatePickerAdapter(daysOfWeek, defaultSelectedPosition, this)
        dPRecyclerView.adapter = datePickerAdapter

        val dpDiv: LinearLayout=findViewById(R.id.dp_div)
        dpDiv.setOnClickListener{openDatePicker()}

        val curDate = Calendar.getInstance().time
        // Define the desired date format
        val dateFormat = SimpleDateFormat("EEEE, dd MMM, yyyy", Locale.getDefault())
        // Format the current date
        val formattedDate = dateFormat.format(curDate)
        // Find your TextView in the layout
        val textViewDate: TextView = findViewById(R.id.journal_date)
        // Set the formatted date to the TextView
        textViewDate.text = formattedDate

        //FAB CREATE
        val fabCreate:FloatingActionButton=findViewById(R.id.fab_create)
        fabCreate.setOnClickListener{
            startActivity(Intent(this, DailyJournalEntryActivity::class.java))
        }
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

            updateDataForSelectedWeek(it)

            // Update the TextView with the selected date
            updateSelectedDateText(it)
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

    private fun updateDataForSelectedWeek(selectedDate: Long) {
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.timeInMillis = selectedDate

        val daysOfWeek = mutableListOf<DatePicker>()

        // Set the selected date as the first day of the week
        selectedCalendar.firstDayOfWeek = Calendar.SUNDAY
        selectedCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        for (i in Calendar.SUNDAY..Calendar.SATURDAY) {
            val dayOfMonth = selectedCalendar.get(Calendar.DAY_OF_MONTH)
            daysOfWeek.add(DatePicker(dayOfMonth, 0))
            selectedCalendar.add(Calendar.DAY_OF_WEEK, 1)
        }

        // Update the adapter with the new data
        datePickerAdapter.updateData(daysOfWeek)


        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

        // Find the index of the selected date in the new data
        val selectedDateIndex = daysOfWeek.indexOfFirst { it.date == dayOfMonth }

        // Update the selected position
        if (selectedDateIndex != -1) {
            datePickerAdapter.setSelectedPosition(selectedDateIndex)
        }

        updateSelectedDateText(selectedDate)
    }


    private fun updateSelectedDateText(selectedDate: Long) {
        val curDate = Date(selectedDate)
        val dateFormat = SimpleDateFormat("EEEE, dd MMM, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(curDate)

        // Find your TextView in the layout
        val textViewDate: TextView = findViewById(R.id.journal_date)
        textViewDate.text = formattedDate

        val monthYearText = SimpleDateFormat("MMMM, yyyy", Locale.getDefault()).format(curDate.time)
        findViewById<TextView>(R.id.date_picker_text).text = monthYearText
    }

    private fun updateJournalDateText(selectedDay: Int) {
        val currentCalendar = Calendar.getInstance()
        val currentDayOfMonth = currentCalendar.get(Calendar.DAY_OF_MONTH)

        // Clone the current calendar to avoid modifying it
        val tempCalendar = currentCalendar.clone() as Calendar

        // Adjust the temporary calendar to the selected day of the month
        tempCalendar.add(Calendar.DAY_OF_MONTH, selectedDay - currentDayOfMonth)

        // Check if the month has changed
        val isMonthChanged = (selectedDay < currentDayOfMonth) || (selectedDay == 1 && currentDayOfMonth == tempCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))

        // If the month has changed, move to the next month
        if (isMonthChanged) {
            tempCalendar.add(Calendar.MONTH, 1)
        }

        val dateFormat = SimpleDateFormat("EEEE, dd MMM, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(tempCalendar.time)

        // Find your TextView in the layout
        val textViewDate: TextView = findViewById(R.id.journal_date)
        textViewDate.text = formattedDate

        val monthYearText = SimpleDateFormat("MMMM, yyyy", Locale.getDefault()).format(tempCalendar.time)
        findViewById<TextView>(R.id.date_picker_text).text = monthYearText
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

    override fun onDatePickerItemClick(selectedDate: Int) {
        updateJournalDateText(selectedDate)
        Log.e("dateeee",selectedDate.toString())
    }
}