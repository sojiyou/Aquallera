package com.example.aquallera

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.maps.plugin.gestures.gestures

class MapActivity : AppCompatActivity() {

    private lateinit var waterStation: TextView
    private lateinit var viewDetails: Button
    private lateinit var mapView: MapView

    // Water station coordinates (Baguio, Benguet approximate coordinates)
    private val waterStationLocation = Point.fromLngLat(120.5931, 16.4023)
    private val waterStationName = "Living Water Refilling Station"

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        initializeViews()
        setupMap()
        setupClickListeners()
        setupBottomNavigation()
    }

    private fun initializeViews() {
        waterStation = findViewById(R.id.waterStationName)
        viewDetails = findViewById(R.id.viewDetailsBtn)
        mapView = findViewById(R.id.mapView)

        // Set initial water station name
        waterStation.text = "Tap the pin on map to select station"
    }

    private fun setupMap() {
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS) {
            // Move camera to Baguio area
            val cameraOptions = CameraOptions.Builder()
                .center(waterStationLocation)
                .zoom(14.0)
                .build()
            mapView.getMapboxMap().setCamera(cameraOptions)

            // Add marker for water station
            addWaterStationMarker()

            // Setup map click listener
            setupMapClickListener()

            // Request location permissions if not granted
            if (!hasLocationPermission()) {
                requestLocationPermission()
            } else {
                // Enable location tracking if permissions are granted
                enableLocationTracking()
            }
        }
    }

    private fun addWaterStationMarker() {
        val annotationApi = mapView.annotations
        val pointAnnotationManager = annotationApi.createPointAnnotationManager()

        val pointAnnotationOptions = PointAnnotationOptions()
            .withPoint(waterStationLocation)
            .withIconImage("marker-icon-15") // This uses Mapbox's built-in marker icon
            .withIconSize(1.5) // Make the icon slightly larger
            .withIconColor("#FF0000") // Red color for the pin

        pointAnnotationManager.create(pointAnnotationOptions)

        // Add click listener specifically for annotations
        pointAnnotationManager.addClickListener { annotation ->
            // When the pin is clicked, update the water station name
            waterStation.text = waterStationName

            // Show a small toast confirmation
            Toast.makeText(this@MapActivity, "Selected: $waterStationName", Toast.LENGTH_SHORT).show()
            true
        }
    }

    private fun setupMapClickListener() {
        mapView.gestures.addOnMapClickListener { point ->
            // Check if the click is near our water station (within a small radius)
            val distance = calculateDistance(point, waterStationLocation)

            // If click is within 0.01 degrees (approximately 1km) of the station
            if (distance < 0.01) {
                // Show water station name in the TextView
                waterStation.text = waterStationName

                // Show a small toast confirmation
                Toast.makeText(this@MapActivity, "Selected: $waterStationName", Toast.LENGTH_SHORT).show()
                true // Event handled
            } else {
                false // Event not handled
            }
        }
    }

    private fun calculateDistance(point1: Point, point2: Point): Double {
        val latDiff = point1.latitude() - point2.latitude()
        val lngDiff = point1.longitude() - point2.longitude()
        return Math.sqrt(latDiff * latDiff + lngDiff * lngDiff)
    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    private fun enableLocationTracking() {
        // You can implement location tracking here using Mapbox LocationComponent
        // This would show user's current location on the map
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocationTracking()
            }
        }
    }

    private fun setupClickListeners() {
        waterStation.setOnClickListener {
            // Center map on water station when name is clicked
            val cameraOptions = CameraOptions.Builder()
                .center(waterStationLocation)
                .zoom(16.0)
                .build()
            mapView.getMapboxMap().setCamera(cameraOptions)

            // Ensure the station name is displayed
            waterStation.text = waterStationName
        }

        viewDetails.setOnClickListener {
            // Show water station details
            showWaterStationDetails()
        }
    }

    private fun showWaterStationDetails() {
        val details = """
            $waterStationName
            
            Address: Generosa Mansion, 83 Manuel Roxas, Baguio, 2600 Benguet
            Phone: 09273239968
            Hours: 7AM - 7PM
            Services: Delivery and Pick up
        """.trimIndent()

        Toast.makeText(this, details, Toast.LENGTH_LONG).show()
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navMap = findViewById<LinearLayout>(R.id.navMap)
        val navOrders = findViewById<LinearLayout>(R.id.navOrder)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        // Set Map as active
        setActiveTab(navMap)

        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        navMap.setOnClickListener {
            // Already on map page, just refresh if needed
            setActiveTab(navMap)
        }

        navOrders.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
            finish()
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setActiveTab(activeTab: LinearLayout) {
        val allTabs = listOf(
            findViewById<LinearLayout>(R.id.navHome),
            findViewById<LinearLayout>(R.id.navMap),
            findViewById<LinearLayout>(R.id.navOrder),
            findViewById<LinearLayout>(R.id.navProfile)
        )

        allTabs.forEach { tab ->
            val textView = when (tab.id) {
                R.id.navHome -> findViewById<TextView>(R.id.tvHome)
                R.id.navMap -> findViewById<TextView>(R.id.tvMap)
                R.id.navOrder -> findViewById<TextView>(R.id.tvOrder)
                R.id.navProfile -> findViewById<TextView>(R.id.tvProfile)
                else -> null
            }

            if (tab == activeTab) {
                textView?.setTextColor(Color.parseColor("#2196F3"))
                tab.background = ContextCompat.getDrawable(this, R.drawable.tab_active_border)
            } else {
                textView?.setTextColor(Color.parseColor("#757575"))
                tab.background = ContextCompat.getDrawable(this, R.drawable.tab_border_highlight)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}