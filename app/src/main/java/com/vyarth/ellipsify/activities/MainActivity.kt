package com.vyarth.ellipsify.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TextAppearanceSpan
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.adapters.EmotionsAdapter
import com.vyarth.ellipsify.databinding.ActivityMainBinding
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.fragments.ChatFragment
import com.vyarth.ellipsify.fragments.ExploreFragment
import com.vyarth.ellipsify.fragments.HomeFragment

import com.vyarth.ellipsify.model.Emotion
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyWindowFlags()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavBar.setupWithNavController(navController)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)



    }

    fun applyWindowFlags() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = resources.getColor(R.color.status_bar_color)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Set the status bar text color to dark for Android M and above
            val decor: View = window.decorView
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}