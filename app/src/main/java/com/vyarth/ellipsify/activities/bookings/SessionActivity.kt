package com.vyarth.ellipsify.activities.bookings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity
import com.vyarth.ellipsify.adapters.SessionAdapter
import java.net.URLEncoder

class SessionActivity : BaseActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_session)

        val sessionRV: RecyclerView = findViewById(R.id.sessionRV)
        sessionRV.layoutManager = LinearLayoutManager(this)
        sessionRV.adapter = SessionAdapter()

        val cardTitle: TextView = findViewById(R.id.cardTitle)
        val titleCustomTypeface = Typeface.createFromAsset(assets, "epilogue_bold.ttf")
        cardTitle.typeface = titleCustomTypeface

        val cardDesc: TextView = findViewById(R.id.cardDesc)
        val descCustomTypeface = Typeface.createFromAsset(assets, "poppins_regular.ttf")
        cardDesc.typeface = descCustomTypeface

        val cardCount: TextView = findViewById(R.id.cardTime)
        val timeCustomTypeface = Typeface.createFromAsset(assets, "poppins_regular.ttf")
        cardDesc.typeface = timeCustomTypeface

        setupActionBar()


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Check if location permission is granted, if not, request it
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
        } else {
            // Permission already granted, get the user's location
            getUserLocation()
        }

        // Locate the CardView that corresponds to the card you want to click
        val cardView: CardView = findViewById(R.id.cardView)

        // Set click listener to the CardView
        cardView.setOnClickListener {
//            // Check if location permission is granted
//            if (isLocationPermissionGranted()) {
//                // Location permission granted, initiate nearby search
//                initiateNearbySearch()
//            } else {
//                // Location permission not granted, request it
//                requestLocationPermission()
//            }
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun getUserLocation() {
        // Check if location permission is granted
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Location permission not granted, request it
            requestLocationPermission()
        } else {
            // Location permission granted, check if location services are enabled
            if (!isLocationEnabled()) {
                // Location services are not enabled, prompt the user to enable them
                Toast.makeText(
                    this,
                    "Please enable location services",
                    Toast.LENGTH_SHORT
                ).show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            } else {
                // Location services are enabled, get the last known location
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        location?.let {
                            // User's location retrieved successfully, you can use it here
                            val latitude = location.latitude
                            val longitude = location.longitude
                            // Now you can proceed to open the map and mark nearby therapists/consultants
                        }
                    }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationMode: Int = try {
            Settings.Secure.getInt(
                applicationContext.contentResolver,
                Settings.Secure.LOCATION_MODE
            )
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
            return false
        }
        return locationMode != Settings.Secure.LOCATION_MODE_OFF
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted, get the user's location
                getUserLocation()
            } else {
                // Location permission denied, show a message or handle it accordingly
                Toast.makeText(
                    this,
                    "Location permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initiateNearbySearch() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    // User's location retrieved successfully, initiate nearby search
                    val latitude = location.latitude
                    val longitude = location.longitude
                    val query = "therapist"
                    val encodedQuery = URLEncoder.encode(query, "utf-8")
                    val uri = Uri.parse("https://maps.google.com/maps?q=$encodedQuery&near=$latitude,$longitude")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
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
}