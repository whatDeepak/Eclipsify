package com.vyarth.ellipsify.activities.article

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.activities.music.MusicActivity
import com.vyarth.ellipsify.adapters.explore.ArticlesAdapter
import com.vyarth.ellipsify.adapters.explore.MeditationAdapter
import com.vyarth.ellipsify.firebase.FirestoreClass
import com.vyarth.ellipsify.model.Articles
import com.vyarth.ellipsify.model.Meditation

class ArticlesActivity : BaseActivity() {

    // Declare articlesRecyclerView at the class level
    private lateinit var articlesRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articles)

        setupActionBar()

        val titles = arrayOf("Nurturing Mental Wellness")

        val titleColors = mapOf(
            "Nurturing Mental Wellness" to R.color.art1,
            // Add more titles and colors as needed
        )

        val titleDrawables = mapOf(
            "Nurturing Mental Wellness" to R.drawable.articles1,
            // Add more titles and drawables as needed
        )

        // Initialize itemList
        val itemList = mutableListOf<Articles>()

        // Initialize articlesRecyclerView
        articlesRecyclerView = findViewById(R.id.articlesRV)

        // Fetch article descriptions from Firestore and update the itemList
        for (title in titles) {
            FirestoreClass().getArticleDescByTitle(title) { desc ->
                val formattedDesc = desc?.replace("/n", "\n") ?: ""
                val color = titleColors[title]
                val drawable = titleDrawables[title]

                val article = color?.let {
                    if (drawable != null) {
                        Articles(title, formattedDesc ?: "", it, drawable)
                    } else {
                        null
                    }
                }

                // Add the article to itemList if it's not null
                article?.let { updateRecyclerView(itemList, article) }
            }
        }

        val articlesActivityClasses = listOf(
            ArticlesListActivity::class.java,
            // Add more activity classes as needed
        )

        // Set layout manager and adapter
        articlesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        articlesRecyclerView.adapter = ArticlesAdapter(itemList, articlesActivityClasses)
    }

    private fun updateRecyclerView(itemList: MutableList<Articles>, article: Articles) {
        itemList += article

        // Notify the adapter of the data change if you are using a RecyclerView
        (articlesRecyclerView.adapter as ArticlesAdapter).notifyDataSetChanged()
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