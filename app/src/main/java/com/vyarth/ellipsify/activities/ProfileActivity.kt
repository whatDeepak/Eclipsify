package com.vyarth.ellipsify.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.User
import de.hdodenhof.circleimageview.CircleImageView

class ProfileActivity : BaseActivity() {

    // A global variable for User Name
    private lateinit var mUserName: String

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
}