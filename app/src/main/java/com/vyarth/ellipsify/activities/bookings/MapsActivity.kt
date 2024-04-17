package com.vyarth.ellipsify.activities.bookings

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.SearchByTextRequest
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
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                // Center the map on the user's current location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                // Perform search for nearby therapists and add markers on the map
                searchForNearbyTherapists(currentLatLng, "therapist", 3000.0)
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error retrieving location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchForNearbyTherapists(currentLatLng: LatLng, searchText: String, radius: Double) {
        // Define the fields of places to request
        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

        // Calculate the bounds around the current location using the specified radius
        val earthRadius = 6371000.0 // Earth's radius in meters
        val dLat = radius / earthRadius
        val dLng = radius / (earthRadius * Math.cos(Math.toRadians(currentLatLng.latitude)))

        val southWest = LatLng(
            currentLatLng.latitude - Math.toDegrees(dLat),
            currentLatLng.longitude - Math.toDegrees(dLng)
        )
        val northEast = LatLng(
            currentLatLng.latitude + Math.toDegrees(dLat),
            currentLatLng.longitude + Math.toDegrees(dLng)
        )

        // Define the request for search by text
        val searchByTextRequest = SearchByTextRequest.builder(searchText, placeFields)
            .setMaxResultCount(10) // You can adjust the number of results
            .setLocationRestriction(RectangularBounds.newInstance(southWest, northEast))
            .build()

        // Perform the search request
        placesClient.searchByText(searchByTextRequest)
            .addOnSuccessListener { response ->
                // Iterate through the places found in the response
                response.places.forEach { place ->
                    val placeLatLng = place.latLng
                    val placeName = place.name

                    Log.d("PlacesFound", "Place Name: ${placeName}, LatLng: $placeLatLng")

                    // Add a marker for each place on the map
                    if (placeLatLng != null && placeName != null) {
                        googleMap.addMarker(
                            MarkerOptions()
                                .position(placeLatLng)
                                .title(placeName)
                        )
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }


    // Function to calculate distance between two LatLng points in meters
    private fun calculateDistance(point1: LatLng, point2: LatLng): Double {
        val earthRadius = 6371000.0 // Radius of the earth in meters
        val dLat = Math.toRadians(point2.latitude - point1.latitude)
        val dLng = Math.toRadians(point2.longitude - point1.longitude)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(point1.latitude)) * Math.cos(Math.toRadians(point2.latitude)) *
                Math.sin(dLng / 2) * Math.sin(dLng / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
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
                // Location permission denied
                Toast.makeText(
                    this,
                    "Location permission denied",
                    Toast.LENGTH_SHORT
                ).show()
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
