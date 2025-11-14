package com.example.aquallera

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.rememberTopAppBarState
import androidx.core.content.ContextCompat

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        setupClickListeners()
        setupBottomNavigation()
    }

    private fun setupClickListeners() {
        val tvLocateStations = findViewById<LinearLayout>(R.id.LocateStations)

        tvLocateStations.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navMap = findViewById<LinearLayout>(R.id.navMap)
        val navOrders = findViewById<LinearLayout>(R.id.navOrder)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        //primary active navigation
        setActiveTab(navHome)

        navHome.setOnClickListener {
            setActiveTab(navHome)
        }

        navMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        navOrders.setOnClickListener {
            val intent = Intent(this, OrdersActivity::class.java)
            startActivity(intent)
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
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
                R.id.navHome ->findViewById<TextView>(R.id.tvHome)
                R.id.navMap -> findViewById<TextView>(R.id.tvMap)
                R.id.navOrder -> findViewById<TextView>(R.id.tvOrder)
                R.id.navProfile -> findViewById<TextView>(R.id.tvProfile)
                else -> null
            }
            if(tab == activeTab){
                // Active tab - interactive border and color
                tab.background = ContextCompat.getDrawable(this, R.drawable.tab_active_border)
            } else {
                // Inactive tab - no border
                tab.background = ContextCompat.getDrawable(this, R.drawable.tab_border_highlight)
            }
        }
    }
}