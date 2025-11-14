package com.example.aquallera

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
class OrdersActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_orders)

        setupSampleOrders()
        setupBottomNavigation()
    }

    private fun setupSampleOrders() {
        val ordersContainer = findViewById<LinearLayout>(R.id.ordersContainer)

        //Clear the container
        ordersContainer.removeAllViews()

        // Add multiple sample orders to test scrolling
        addOrderTicket(ordersContainer, "Living Water Refilling Station", "November 6, 2025", "₱20.00", "#01")
        addOrderTicket(ordersContainer, "Pure Water Station", "November 5, 2025", "₱25.00", "#02")
        addOrderTicket(ordersContainer, "Mountain Spring Water", "November 4, 2025", "₱18.00", "#03")
    }

    private fun addOrderTicket(container: LinearLayout, stationName: String, date: String, price: String, ticketNumber: String) {
        val ticketView = LayoutInflater.from(this).inflate(R.layout.order_ticket_item, container, false)
        val tvStationName = ticketView.findViewById<TextView>(R.id.tvStationName)
        val tvOrderDate = ticketView.findViewById<TextView>(R.id.tvOrderDate)
        val tvTicketNumber = ticketView.findViewById<TextView>(R.id.tvTicketNumber)
        val tvOrderPrice = ticketView.findViewById<TextView>(R.id.tvOrderPrice)

        tvStationName.text = stationName
        tvOrderDate.text = "Date $date"
        tvTicketNumber.text = "Ticket #$ticketNumber"
        tvOrderPrice.text = price

        ticketView.setOnClickListener {
            // Temporary: Show a toast when clicked
            Toast.makeText(this, "Opening receipt for $ticketNumber", Toast.LENGTH_SHORT).show()

            /* when receipt / confirmation page is ready
            val intent = Intent(this, ReceiptActivity::class.java)
            intent.putExtra("TICKET_NUMBER", ticketNumber)
            intent.putExtra("STATION_NAME", stationName)
            intent.putExtra("ORDER_DATE", date)
            intent.putExtra("ORDER_PRICE", price)
            startActivity(intent)
            */
        }

        container.addView(ticketView)
    }

    private fun setupBottomNavigation() {
        val navHome = findViewById<LinearLayout>(R.id.navHome)
        val navMap = findViewById<LinearLayout>(R.id.navMap)
        val navOrders = findViewById<LinearLayout>(R.id.navOrder)
        val navProfile = findViewById<LinearLayout>(R.id.navProfile)

        // Set Orders as active
        setActiveTab(navOrders)

        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }

        navMap.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }

        navOrders.setOnClickListener {
            // reload orders page
            setActiveTab(navOrders)
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
}