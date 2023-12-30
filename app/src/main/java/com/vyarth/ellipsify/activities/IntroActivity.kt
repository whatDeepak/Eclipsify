package com.vyarth.ellipsify.activities

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import com.vyarth.ellipsify.R

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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


        val btnSignUpIntro=findViewById<Button>(R.id.btn_google_intro)
        btnSignUpIntro.setOnClickListener {
            // Launch the sign up screen.
            startActivity(Intent(this@IntroActivity, SignUpActivity::class.java))
        }

        val btnSignInIntro=findViewById<Button>(R.id.btn_login_intro)
        btnSignInIntro.setOnClickListener {
            // Launch the sign in screen.
            startActivity(Intent(this@IntroActivity, SignInActivity::class.java))
        }
    }
}