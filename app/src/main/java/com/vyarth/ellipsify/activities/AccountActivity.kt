package com.vyarth.ellipsify.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.User
import com.vyarth.ellipsify.utils.Constants
import de.hdodenhof.circleimageview.CircleImageView
import java.io.IOException

class AccountActivity : BaseActivity() {

    // Add a global variable for URI of a selected image from phone storage.
    private var mSelectedImageFileUri: Uri? = null
    // A global variable for a user profile image URL
    private var mProfileImageURL: String = ""
    // A global variable for user details.
    private lateinit var mUserDetails: User

    companion object{
        private const val READ_STORAGE_PERMISSION_CODE=1
        private const val PICK_IMAGE_REQUEST_CODE=2

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        FirestoreClass().loadUserData(this)

        setupActionBar()

        val editProfile=findViewById<ImageView>(R.id.edit_profile_icon)

        editProfile.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED
            ) {
                Constants.showImageChooser(this)
            } else {
                /*Requests permissions to be granted to this application. These permissions
                 must be requested in your manifest, they should not be granted to your app,
                 and they should have protection level*/
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    Constants.READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        val btnUpdate=findViewById<Button>(R.id.btn_update)
        btnUpdate.setOnClickListener {
            if(mSelectedImageFileUri !=null){
                uploadUserImage()
            }else{
                showProgressDialog("Please Wait!")
                updateUserProfileData()
            }
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

    /**
     * A function to set the existing details in UI.
     */
    fun setUserDataInUI(user: User) {

        mUserDetails=user

        // The instance of the user image of the navigation view.
        val navUserImage = findViewById<CircleImageView>(R.id.account_user_image)

        // Use requireContext() safely here
        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        val etName=findViewById<AppCompatEditText>(R.id.account_name)
        val etUserName=findViewById<AppCompatEditText>(R.id.account_username)
        val etEmail=findViewById<AppCompatEditText>(R.id.account_email)
        val etMobile=findViewById<AppCompatEditText>(R.id.account_mobile)

        etName.setText(user.name)
        etUserName.setText(user.username)
        etEmail.setText(user.email)
        if (user.mobile != 0L) {
            etMobile.setText(user.mobile.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    "OOPS, you just denied the permission for storage. You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK
            && requestCode == Constants.PICK_IMAGE_REQUEST_CODE
            && data!!.data != null
        ) {
            // The uri of selection image from phone storage.
            mSelectedImageFileUri = data.data!!
            val ivProfileUserImage=findViewById<CircleImageView>(R.id.account_user_image)
            try {
                // Load the user image in the ImageView.
                Glide
                    .with(this)
                    .load(Uri.parse(mSelectedImageFileUri.toString())) // URI of the image
                    .centerCrop() // Scale type of the image.
                    .placeholder(R.drawable.ic_user_place_holder) // A default place holder
                    .into(ivProfileUserImage ) // the view in which the image will be loaded.
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * A function to upload the selected user image to firebase cloud storage.
     */
    private fun uploadUserImage() {

        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {

            //getting the storage reference
            val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
                "USER_IMAGE" + System.currentTimeMillis() + "."
                        + Constants.getFileExtension(this, mSelectedImageFileUri)
            )

            //adding the file to reference
            sRef.putFile(mSelectedImageFileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    // The image upload is success
                    Log.e(
                        "Firebase Image URL",
                        taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                    )

                    // Get the downloadable url from the task snapshot
                    taskSnapshot.metadata!!.reference!!.downloadUrl
                        .addOnSuccessListener { uri ->
                            Log.e("Downloadable Image URL", uri.toString())

                            // assign the image url to the variable.
                            mProfileImageURL = uri.toString()

                            // Call a function to update user details in the database.
                            updateUserProfileData()
                        }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this,
                        exception.message,
                        Toast.LENGTH_LONG
                    ).show()
                    hideProgressDialog()
                }
        }
    }

    /**
     * A function to update the user profile details into the database.
     */
    private fun updateUserProfileData() {

        val etName=findViewById<AppCompatEditText>(R.id.account_name)
        val etEmail=findViewById<AppCompatEditText>(R.id.account_email)
        val etUserName=findViewById<AppCompatEditText>(R.id.account_username)
        val etMobile=findViewById<AppCompatEditText>(R.id.account_mobile)

        val userHashMap = HashMap<String, Any>()


        if (mProfileImageURL.isNotEmpty() && mProfileImageURL != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageURL
        }

        if (etName.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = etName.text.toString()
        }

        if (etUserName.text.toString() != mUserDetails.username) {
            userHashMap[Constants.USERNAME] = etUserName.text.toString()
        }

        val mobileText = etMobile.text.toString()

        if (mobileText.isNotEmpty()) {
            try {
                val mobileNumber = mobileText.toLong()
                if (mobileNumber != mUserDetails.mobile) {
                    userHashMap[Constants.MOBILE] = mobileNumber
                }
            } catch (e: NumberFormatException) {
                // Handle the case where the text is not a valid Long
                // You may want to show an error message to the user
            }
        } else {
            // Handle the case where the text is empty
            // You may want to show an error message to the user
        }


        // Update the data in the database.
        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    fun profileUpdateSuccess() {

        hideProgressDialog()

        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show()

        setResult(Activity.RESULT_OK)
        finish()
    }

}