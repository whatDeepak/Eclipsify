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
    // Function to search for nearby therapists based on a query and add markers on the map
    private fun searchForNearbyTherapists(currentLatLng: LatLng) {
        // Define the search radius in meters (e.g., 10 km)
        val searchRadius = 10000 // 10,000 meters (10 km)

        // Define the requested fields for places
        val placeFields = listOf(
            Place.Field.NAME,
            Place.Field.LAT_LNG
        )

        // Create a FindCurrentPlaceRequest using the specified fields
        val request = FindCurrentPlaceRequest.newInstance(placeFields)

        // Define the list of keywords to search for
        val searchKeywords = listOf(
            "therapist",
            "therapy",
            "counsellor",
            "counselling",
            "counsel",
            "psychologist",
            "mental wellness"
        )

        // Perform the nearby search using the Places API
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            placesClient.findCurrentPlace(request)
                .addOnSuccessListener { response ->
                    // Iterate through the place likelihoods
                    response.placeLikelihoods.forEach { placeLikelihood ->
                        val place = placeLikelihood.place
                        val placeLatLng = place.latLng

                        // Check if the place's name contains any of the search keywords
                        val name = place.name.toLowerCase()
                        val isMatch = searchKeywords.any { keyword ->
                            name.contains(keyword.toLowerCase())
                        }

                        // If the place matches any of the keywords and the location is not null
                        if (isMatch && placeLatLng != null) {
                            // Calculate the distance from the current location to the place
                            val distance = FloatArray(1)
                            android.location.Location.distanceBetween(
                                currentLatLng.latitude,
                                currentLatLng.longitude,
                                placeLatLng.latitude,
                                placeLatLng.longitude,
                                distance
                            )

                            // If the place is within the search radius, add a marker
                            if (distance[0] <= searchRadius) {
                                googleMap.addMarker(
                                    MarkerOptions()
                                        .position(placeLatLng)
                                        .title(place.name)
                                )
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    // Handle any errors
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Request permissions if they are not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
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
