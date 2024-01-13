package com.vyarth.ellipsify.activities

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.User
import de.hdodenhof.circleimageview.CircleImageView
import com.vyarth.ellipsify.firebase.FirestoreFeedback
import com.vyarth.ellipsify.model.Feedback

class ProfileActivity : BaseActivity() {

    // A global variable for User Name
    private lateinit var mUserName: String
    private lateinit var feedbackDialog: Dialog

    private lateinit var feedbackCommentEditText: EditText


    private var selectedRating: Int = 0


    companion object{
        const val MY_PROFILE_REQUEST_CODE: Int = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        FirestoreClass().loadUserData(this)

        setupActionBar()


        val btnAccount=findViewById<ConstraintLayout>(R.id.account_div)
        btnAccount.setOnClickListener {
            startActivityForResult(
                Intent(this, AccountActivity::class.java),
                MY_PROFILE_REQUEST_CODE
            )
        }

        feedbackDialog = Dialog(this)
        feedbackDialog.setContentView(R.layout.dialog_feedback)



        // Configure and show the feedback dialog on button click
        val feedbackButton: ConstraintLayout = findViewById(R.id.feedback)
        feedbackButton.setOnClickListener {
            showFeedbackDialog()
        }

        val contactUsButton: ConstraintLayout = findViewById(R.id.contact)
        contactUsButton.setOnClickListener {
            openEmailClient()
        }

        // Inside your HomeFragment or any other relevant class
        val logoutButton: Button = findViewById<Button>(R.id.btn_logout)
        logoutButton.setOnClickListener {
            // Call the logout function
            FirestoreClass().logoutUser(this)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK
            && requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            // Get the user updated details.
            FirestoreClass().loadUserData(this)
        }else {
            Log.e("Cancelled", "Cancelled")
        }
        setResult(Activity.RESULT_OK)
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

    /**
     * A function to get the current user details from firebase.
     */
    fun updateUserDetails(user: User) {
        mUserName = user.name

        // The instance of the user image of the navigation view.
        val navUserImage = findViewById<CircleImageView>(R.id.user_avatar)

        Log.d("UserData", "User Image URL: ${user.image}")


            // Use requireContext() safely here
            Glide
                .with(this)
                .load(user.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(navUserImage)

        // The instance of the user name TextView of the navigation view.
        val username = findViewById<TextView>(R.id.user_name)
        // Set the user name
        username.text = user.name
    }

    private fun showFeedbackDialog() {
        // Set up references to views in the dialog
        val titleTextView: TextView = feedbackDialog.findViewById(R.id.feedback_title)
        val submitBtn: Button = feedbackDialog.findViewById(R.id.feedback_submit)

        // Customize the title if needed
        // titleTextView.text = "Your Custom Title"

        // Show the dialog
        feedbackDialog.show()

        // Set click listeners for submit button
        submitBtn.setOnClickListener {
            onSubmitClick()
            feedbackDialog.dismiss()
        }
    }


    fun onStarClick(view: View) {
        // Identify which emoji was clicked
        val clickedEmoji = view as ImageView

        // If the clicked emoji is already selected, clear the selection
        if (clickedEmoji.tag.toString().toInt() == selectedRating) {
            selectedRating = 0
            clickedEmoji.background=null
        } else {
            // Reset all stars to unselected state
            resetStars()

            // Update the selected rating
            selectedRating = clickedEmoji.getTag().toString().toInt()

            // Add a border or stroke to the selected emoji
            addBorderToEmoji(clickedEmoji)
        }
    }

    // Add a border or stroke to the selected emoji
    private fun addBorderToEmoji(selectedEmoji: ImageView) {
        val borderWidth = resources.getDimensionPixelSize(R.dimen.selected_emoji_border_width)
        val borderColor = ContextCompat.getColor(this, R.color.selected_emoji_border_color)

        val borderDrawable = GradientDrawable()
        borderDrawable.shape = GradientDrawable.OVAL
        borderDrawable.setStroke(borderWidth, borderColor)

        selectedEmoji.background = borderDrawable
    }

    // Reset all stars to unselected state
    // Reset all stars to unselected state
    private fun resetStars() {
        val emoji1 = findViewById<ImageView>(R.id.rating_1)
        val emoji2 = findViewById<ImageView>(R.id.rating_2)
        val emoji3 = findViewById<ImageView>(R.id.rating_3)
        val emoji4 = findViewById<ImageView>(R.id.rating_4)
        val emoji5 = findViewById<ImageView>(R.id.rating_5)

        // Set the background to a transparent drawable for all emojis
        emoji1?.setBackgroundResource(android.R.color.transparent)
        emoji2?.setBackgroundResource(android.R.color.transparent)
        emoji3?.setBackgroundResource(android.R.color.transparent)
        emoji4?.setBackgroundResource(android.R.color.transparent)
        emoji5?.setBackgroundResource(android.R.color.transparent)
    }




    // Handle submit button click
    fun onSubmitClick() {
        // Retrieve the dialog view

        // Retrieve values from the dialog (comments)
        val feedbackCommentEditText: EditText = feedbackDialog.findViewById(R.id.feedback_comment)
        val feedbackComment: String = feedbackCommentEditText.text.toString()

        if (selectedRating == 0) {
            // Handle the case where no rating is selected
            Toast.makeText(this, "Please select a valid rating", Toast.LENGTH_SHORT).show()
            return
        }

        // Ensure that the EditText is not null before accessing its text
        if (feedbackComment.isEmpty()) {
            Toast.makeText(this, "Please enter feedback comments", Toast.LENGTH_SHORT).show()
            return
        }

        // Retrieve user information
        val userId = FirestoreClass().getCurrentUserID()
        val userName = mUserName

        // Create a Feedback object
        val feedback = Feedback(selectedRating, feedbackComment, userId, userName)

        // Store the feedback in Firestore
        FirestoreFeedback.storeFeedback(feedback)

        // Clear the feedback comment EditText
        feedbackCommentEditText.text.clear()

        // Reset the emoji rating
        resetStars()

        // Dismiss the dialog
        feedbackDialog.dismiss()

        Toast.makeText(this, "Thank You for your valuable Feedback <3", Toast.LENGTH_SHORT).show()
    }

    private fun openEmailClient() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto: help.eclipsify@gmail.com")
        }
        startActivity(intent)
    }

}