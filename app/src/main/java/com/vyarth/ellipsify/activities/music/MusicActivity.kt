package com.vyarth.ellipsify.activities.music

import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.media.AudioAttributes
import android.media.Image
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.widget.DialogTitle
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.firebase.FirestoreClass
import java.io.IOException
import java.util.concurrent.TimeUnit

class MusicActivity : BaseActivity() {

    private var mProgressDialog: Dialog? = null
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var playPauseButton: ImageView
    private lateinit var currentTimeTextView: TextView
    private lateinit var totalTimeTextView: TextView
    private lateinit var audioUri: String  // Store the audio URI obtained from FirestoreClass
    private lateinit var title: String
    private lateinit var refer: String
    private var bgcolor: Int = 0
    private var imagecolor: Int = 0

    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music)

        setupActionBar()

        title = intent.getStringExtra("story_title") ?: ""
        refer = intent.getStringExtra("story_refer") ?: ""
        bgcolor = intent.getIntExtra("story_bg",0)
        imagecolor = intent.getIntExtra("story_image",0)

        updateUIelements()

        // Show ProgressDialog with text
        showProgressDialog("Preparing Your Bedtime Story...")

        FirestoreClass().downloadAudio(refer, object : FirestoreClass.AudioDownloadListener {
            override fun onAudioDownloaded(uri: Uri?) {
                hideProgressDialog() // Hide ProgressDialog when download is complete

                if (uri != null) {
                    audioUri = uri.toString()

                    // Initialize UI components
                    initializeMediaPlayer()

                    // Set up seek bar and play/pause button listeners
                    setupPlaybackControls()
                } else {
                    // Handle the case where the download failed
                    // Show an error message or take appropriate action
                }
            }

        })
    }

    private fun updateUIelements() {
        val titleTV:TextView=findViewById(R.id.song_name)
        val card:CardView=findViewById(R.id.music_card)
        val image:ImageView=findViewById(R.id.card_image)

        titleTV.text=title
        card.setBackgroundResource(bgcolor)
        image.backgroundTintList = ColorStateList.valueOf(imagecolor)
    }

    private fun initializeMediaPlayer() {
        mediaPlayer = MediaPlayer()

        // Set audio attributes for media playback
        mediaPlayer.setAudioAttributes(
            AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )

        try {
            mediaPlayer.setDataSource(audioUri)
            mediaPlayer.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // Initialize UI components
        seekBar = findViewById(R.id.player_seekbar)
        playPauseButton = findViewById(R.id.player_center_icon)
        currentTimeTextView = findViewById(R.id.player_current_position)
        totalTimeTextView = findViewById(R.id.complete_position)

        // Set total time text
        val totalTime = mediaPlayer.duration
        totalTimeTextView.text = formatTime(totalTime.toLong())

        // Update seek bar progress
        updateSeekBar()

        // Set up play/pause button click listener
        playPauseButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            } else {
                mediaPlayer.start()
            }
            updatePlayPauseButton()
        }
    }

    private fun setupPlaybackControls() {
        // Set up seek bar change listener
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // Remove the update task to prevent conflicts while user is interacting with the seek bar
                handler.removeCallbacks(updateTimeTask)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // Resume the update task after user has finished interacting with the seek bar
                handler.postDelayed(updateTimeTask, 1000)
            }
        })

        // Update seek bar and timing text
        handler.postDelayed(updateTimeTask, 1000)
    }

    private val updateTimeTask: Runnable = object : Runnable {
        override fun run() {
            updateSeekBar()
            updatePlayPauseButton()
            handler.postDelayed(this, 1000)
        }
    }

    private fun updateSeekBar() {
        val currentPosition = mediaPlayer.currentPosition
        val totalDuration = mediaPlayer.duration

        // Update seek bar progress
        seekBar.max = totalDuration
        seekBar.progress = currentPosition

        // Update current time text
        currentTimeTextView.text = formatTime(currentPosition.toLong())
    }

    private fun updatePlayPauseButton() {
        if (mediaPlayer.isPlaying) {
            playPauseButton.setImageResource(R.drawable.pause)
        } else {
            playPauseButton.setImageResource(R.drawable.play)
        }
    }

    private fun formatTime(duration: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60
        return String.format("%02d:%02d", minutes, seconds)
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

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(updateTimeTask)
    }

}