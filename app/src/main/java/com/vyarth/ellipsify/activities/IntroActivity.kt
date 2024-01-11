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
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.User
import com.vyarth.ellipsify.utils.Constants

class IntroActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth

    private val mFireStore = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        setContentView(R.layout.activity_intro)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Set the status bar text color to dark
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        // Assume you have a button with the id "btn_login_intro"
        val btnLoginIntro: Button = findViewById(R.id.btn_login_intro)
        val btnGoogleIntro: Button = findViewById(R.id.btn_google_intro)
        // Load custom typeface from the "assets" folder
        val customTypeface = Typeface.createFromAsset(assets, "poppins_medium.ttf")
        // Apply custom typeface to the button
        btnLoginIntro.typeface = customTypeface
        btnGoogleIntro.typeface = customTypeface


//        btnGoogleIntro.setOnClickListener {
//            // Launch the sign up screen.
//            startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
//        }
        btnGoogleIntro.setOnClickListener {
            signInWithGoogle()
        }

        btnLoginIntro.setOnClickListener {
            // Launch the sign in screen.
            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }
    }
    private fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    // Get user details from Google sign-in
                    val name = user?.displayName
                    val email = user?.email

                    // Set the username based on the email (you can customize this logic)
                    val username = email?.substringBefore("@") ?: ""

                    // Check if the user already exists in the database
                    checkIfUserExistsInDatabase(email, name, username)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun checkIfUserExistsInDatabase(email: String?, name: String?, username: String?) {
        // Check if the user already exists based on email
        mFireStore.collection(Constants.USERS)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // User doesn't exist, proceed with registration
                    val userInfo = User(getCurrentUserID(), name ?: "", email ?: "", username?:"")
                    registerUserInFirestore(this@IntroActivity, userInfo)
                } else {
                    // User with the same email already exists, start MainActivity
                    startActivity(Intent(this@IntroActivity, MainActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener { e ->
                // Handle the failure to check user existence
                Log.e(TAG, "Error checking user existence", e)
            }
    }



    private fun registerUserInFirestore(activity: Activity, userInfo: User) {
        // Use the FirestoreClass to register the user
        FirestoreClass().registerUser(activity, userInfo)
    }

    fun userRegisteredSuccessIntro() {

        Toast.makeText(
            this@IntroActivity,
            "You have successfully registered.",
            Toast.LENGTH_SHORT
        ).show()

        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */

        startActivity(Intent(this@IntroActivity, MainActivity::class.java))
        finish()
    }


    companion object {
        private const val RC_SIGN_IN = 9001
        private const val TAG = "GoogleSignIn"
    }
}