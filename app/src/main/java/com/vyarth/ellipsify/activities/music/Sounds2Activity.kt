package com.vyarth.ellipsify.activities.music

import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.Toolbar
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.firebase.FirestoreClass
import java.io.IOException
import java.util.concurrent.TimeUnit

class Sounds2Activity : BaseActivity() {

    private lateinit var exitDialog: Dialog
    private var mProgressDialog: Dialog? = null
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var seekBar: SeekBar
    private lateinit var playPauseButton: ImageView
    private lateinit var currentTimeTextView: TextView
    private lateinit var totalTimeTextView: TextView
    private lateinit var audioUri: String  // Store the audio URI obtained from FirestoreClass
    private lateinit var title: String
    private lateinit var refer: String
    private var imagebg: Int = 0

    private val handler = Handler()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sounds2)

        setupActionBar()
        setupExitDialog()

        this.onBackPressedDispatcher
            .addCallback(this, object: OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    exitDialog.show()
                }
            })


        title = intent.getStringExtra("story_title") ?: ""
        refer = intent.getStringExtra("story_refer") ?: ""
        imagebg = intent.getIntExtra("story_bg",0)


        updateUIelements()

        // Show ProgressDialog with text
        showProgressDialog("Preparing...")

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
        val image:ImageView=findViewById(R.id.music_card)

        titleTV.text=title
        image.setBackgroundResource(imagebg)
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
            playPauseButton.setImageResource(R.drawable.pause_sound2)
        } else {
            playPauseButton.setImageResource(R.drawable.play_sound2)
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

        toolbarSignInActivity.setNavigationOnClickListener {
            // Show the custom exit dialog when the back button is clicked
            exitDialog.show()
        }
    }

    private fun setupExitDialog() {
        exitDialog = Dialog(this)
        exitDialog.setContentView(R.layout.dialog_exit)

        // Set dialog background to transparent
        exitDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Get references to dialog components
        val exitText = exitDialog.findViewById<TextView>(R.id.exit_text)
        val yesButton = exitDialog.findViewById<AppCompatButton>(R.id.btn_yes)
        val cancelButton = exitDialog.findViewById<AppCompatButton>(R.id.btn_cancel)

        // Set the text for the exit message

        // Set up the "Yes" button click listener
        yesButton.setOnClickListener {
            // Handle "Yes" button click
            // For now, simply finish the activity
            finish()
        }

        // Set up the "Cancel" button click listener
        cancelButton.setOnClickListener {
            // Dismiss the dialog when "Cancel" is clicked
            exitDialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler.removeCallbacks(updateTimeTask)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Show the custom exit dialog when the back button is pressed
        exitDialog.show()
    }

}