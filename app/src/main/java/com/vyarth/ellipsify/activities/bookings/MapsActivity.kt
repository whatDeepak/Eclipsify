package com.vyarth.ellipsify.activities.bookings

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.vyarth.ellipsify.R
import com.vyarth.ellipsify.activities.BaseActivity

class MapsActivity : BaseActivity() {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Initialize the Places API with your API key
        Places.initialize(applicationContext, com.vyarth.ellipsify.BuildConfig.placesApiKey)

        // Create a Places client
        placesClient = Places.createClient(this)

        // Initialize location client
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Setup MapView
        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map ->
            googleMap = map
            googleMap.uiSettings.isZoomControlsEnabled = true
            // Add other UI settings if necessary

            // Enable user's current location on the map if permission is granted
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
                getCurrentLocationAndSearchNearby()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }

    }

    private fun getCurrentLocationAndSearchNearby() {
        // Get last known location
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                location?.let {
                    // Center the map to the user's current location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))


                    // Perform nearby search for therapists
                    searchForNearbyTherapists(currentLatLng)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error retrieving location", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to search for nearby therapists and add markers on the map
    private fun searchForNearbyTherapists(currentLatLng: LatLng) {
        // Define the requested fields for places
        val placeFields = listOf(
            Place.Field.NAME,
            Place.Field.LAT_LNG,
            Place.Field.TYPES
        )

        // Create a request for the current place
        val request = FindCurrentPlaceRequest.newInstance(placeFields)

        // Perform the request for nearby places
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            placesClient.findCurrentPlace(request)
                .addOnSuccessListener { response ->
                    // Handle the nearby places (therapists or counselors)
                    response.placeLikelihoods.forEach { placeLikelihood ->
                        val place = placeLikelihood.place
                        val placeLatLng = place.latLng

                        // Check if the place is a therapist (you can adjust this based on your criteria)
                        if (place.types.contains(Place.Type.DOCTOR) || place.types.contains(Place.Type.HEALTH)) {
                            // Add a marker for each therapist or counselor on the map
                            googleMap.addMarker(
                                MarkerOptions()
                                    .position(placeLatLng)
                                    .title(place.name)
                            )
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Location permission granted
                getCurrentLocationAndSearchNearby()
            } else {
                // Location permission denied, handle it accordingly
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
