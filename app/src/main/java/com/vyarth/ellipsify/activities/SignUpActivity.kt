package com.vyarth.ellipsify.activities

import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.auth.User
import com.vyarth.ellipsify.R

class SignUpActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Set the status bar text color to dark
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        setupActionBar()

        // Click event for sign-up button.
        val btnSignUp=findViewById<Button>(R.id.btn_register_intro)
        btnSignUp.setOnClickListener {
            registerUser()
        }
    }
    private fun setupActionBar() {
        val toolbarSignUpActivity = findViewById<Toolbar>(R.id.toolbar_sign_up_activity)
        val tvTitle: TextView = findViewById(R.id.tv_title)
        val customTypeface = Typeface.createFromAsset(assets, "poppins_medium.ttf")
        tvTitle.typeface = customTypeface

        setSupportActionBar(toolbarSignUpActivity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbarSignUpActivity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to register a user to our app using the Firebase.
     */
    private fun registerUser() {
        // Here we get the text from editText and trim the space
        val etName = findViewById<TextInputLayout>(R.id.et_layout_name)?.findViewById<AppCompatEditText>(R.id.et_name)
        val etEmail = findViewById<TextInputLayout>(R.id.et_layout_email)?.findViewById<AppCompatEditText>(R.id.et_email)
        val etPassword = findViewById<TextInputLayout>(R.id.et_layout_password)?.findViewById<AppCompatEditText>(R.id.et_password)

        val name: String = etName?.text.toString().trim { it <= ' ' }
        val email: String = etEmail?.text.toString().trim { it <= ' ' }
        val password: String = etPassword?.text.toString().trim { it <= ' ' }

        if (validateForm(name, email, password)) {
            // Show the progress dialog.
            showProgressDialog(resources.getString(R.string.please_wait))
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->

                        // If the registration is successfully done
                        if (task.isSuccessful) {

                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!
                            // Registered Email
                            val registeredEmail = firebaseUser.email!!

//                            val user = User(firebaseUser.uid, name, registeredEmail)
//
//                            // call the registerUser function of FirestoreClass to make an entry in the database.
//                            FirestoreClass().registerUser(this@SignUpActivity, user)
                            Toast.makeText(
                                this,
                                "$name you have"+" succesfully registered the email"+" address $registeredEmail",
                                Toast.LENGTH_LONG
                            ).show()
                            FirebaseAuth.getInstance().signOut()
                            finish()
                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                task.exception!!.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
        }
    }

    /**
     * A function to validate the entries of a new user.
     */
    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter name.")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter email.")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter password.")
                false
            }
            else -> {
                true
            }
        }
    }
}