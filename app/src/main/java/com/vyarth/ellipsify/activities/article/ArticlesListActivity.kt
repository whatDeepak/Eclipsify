package com.vyarth.ellipsify.activities.article

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import org.w3c.dom.Text

class ArticlesListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articles_list)

        setupActionBar()

        val title = intent.getStringExtra("article_title") ?: ""
        val desc = intent.getStringExtra("article_desc") ?: ""
        val image = intent.getIntExtra("article_image",0)
        val bgColor = intent.getIntExtra("article_bg",0)

        val titleEditText = findViewById<TextView>(R.id.article_title)
        val textEditText = findViewById<TextView>(R.id.article_desc)
        val cardView= findViewById<CardView>(R.id.articleCV)
        val imageView= findViewById<ImageView>(R.id.article_image)

        titleEditText.text=title
        val titleCustomTypeface = Typeface.createFromAsset(assets, "epilogue_bold.ttf")
        titleEditText.typeface = titleCustomTypeface
        textEditText.text = desc
        val descCustomTypeface = Typeface.createFromAsset(assets, "poppins_regular.ttf")
        titleEditText.typeface = descCustomTypeface


        cardView.setBackgroundResource(bgColor)
        imageView.setImageResource(image)
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