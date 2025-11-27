package com.example.aquallera

import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.*

class CreateOrderActivity : AppCompatActivity() {

    private lateinit var tvSelectedDate: TextView
    private lateinit var calendarView: CalendarView
    private lateinit var tvSelectedTime: TextView
    private lateinit var rbDelivery: RadioButton
    private lateinit var rbPickup: RadioButton
    private lateinit var rgOrderType: RadioGroup
    private lateinit var fullNameInput: EditText
    private lateinit var streetInput: EditText
    private lateinit var additionalDetailsInput: EditText
    private lateinit var proceedButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_order) // Make sure this matches your XML filename

        initializeViews()
        setupCalendar()
        setupOrderTypeListener()
        setupProceedButton()
        setupBottomNavigation()

        // Set water station name from intent or use default
        setWaterStationName()
    }

    private fun initializeViews() {
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        calendarView = findViewById(R.id.calendarView)
        tvSelectedTime = findViewById(R.id.tvSelectedTime)
        rbDelivery = findViewById(R.id.rbDelivery)
        rbPickup = findViewById(R.id.rbPickup)
        rgOrderType = findViewById(R.id.rgOrderType)
        fullNameInput = findViewById(R.id.FullNameInput)
        streetInput = findViewById(R.id.streetInput)
        additionalDetailsInput = findViewById(R.id.additionalDetailsInput)
        proceedButton = findViewById(R.id.proceedButton)
    }

    private fun setWaterStationName() {
        val tvWaterStationName = findViewById<TextView>(R.id.tvWaterStationName)
        // You can set this from intent extras or keep the default
        val stationName = intent.getStringExtra("STATION_NAME") ?: "Aqua Llera Water Station"
        tvWaterStationName.text = stationName
    }

    private fun setupCalendar() {
        // Set minimum date to today
        calendarView.minDate = System.currentTimeMillis() - 1000 // Allow today

        // Handle calendar date selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val selectedDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }.time
            setSelectedDate(selectedDate)
        }

        // Set initial date to today
        setSelectedDate(Date())
    }

    private fun setSelectedDate(date: Date) {
        val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        tvSelectedDate.text = formattedDate
    }

    fun showTimePicker(view: View) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                setSelectedTime(selectedHour, selectedMinute)
            },
            hour,
            minute,
            false // 24-hour format set to false for AM/PM
        )

        timePickerDialog.show()
    }

    private fun setSelectedTime(hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val formattedTime = timeFormat.format(calendar.time)
        tvSelectedTime.text = formattedTime
    }

    private fun setupOrderTypeListener() {
        rgOrderType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbDelivery -> {
                    // Show delivery-related fields if needed
                    streetInput.visibility = View.VISIBLE
                    additionalDetailsInput.visibility = View.VISIBLE
                    // You can adjust hints or labels for delivery
                }
                R.id.rbPickup -> {
                    // Hide or adjust delivery fields for pickup
                    streetInput.visibility = View.GONE
                    additionalDetailsInput.visibility = View.GONE
                    // You can adjust hints or labels for pickup
                }
            }
        }
    }

    private fun setupProceedButton() {
        proceedButton.setOnClickListener {
            if (validateInputs()) {
                proceedToConfirmation()
            }
        }
    }

    private fun validateInputs(): Boolean {
        // Validate water quantity
        val waterQuantity = fullNameInput.text.toString().trim()
        if (waterQuantity.isEmpty()) {
            fullNameInput.error = "Please enter water quantity"
            fullNameInput.requestFocus()
            return false
        }

        // Validate date
        if (tvSelectedDate.text == "No date selected") {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate time
        if (tvSelectedTime.text == "No time selected") {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validate location for delivery
        if (rbDelivery.isChecked) {
            val street = streetInput.text.toString().trim()
            if (street.isEmpty()) {
                streetInput.error = "Please enter street address"
                streetInput.requestFocus()
                return false
            }
        }

        return true
    }

    private fun proceedToConfirmation() {
        // Collect all order data
        val orderType = if (rbDelivery.isChecked) "Delivery" else "Pickup"
        val selectedDate = tvSelectedDate.text.toString()
        val selectedTime = tvSelectedTime.text.toString()
        val waterQuantity = fullNameInput.text.toString().trim()
        val street = if (rbDelivery.isChecked) streetInput.text.toString().trim() else ""
        val additionalDetails = if (rbDelivery.isChecked) additionalDetailsInput.text.toString().trim() else ""
        val stationName = findViewById<TextView>(R.id.tvWaterStationName).text.toString()

        // Create intent to pass data to confirmation activity
        //val intent = Intent(this, OrderConfirmationActivity::class.java).apply {
            //putExtra("STATION_NAME", stationName)
            //putExtra("ORDER_TYPE", orderType)
            //putExtra("SELECTED_DATE", selectedDate)
            //putExtra("SELECTED_TIME", selectedTime)
            //putExtra("WATER_QUANTITY", waterQuantity)
            //putExtra("STREET", street)
            //putExtra("ADDITIONAL_DETAILS", additionalDetails)
        //}

        //startActivity(intent)

        // Optional: finish this activity if you don't want users to come back
        // finish()
    }

    // Optional: Toggle calendar visibility
    fun toggleCalendar(view: View) {
        if (calendarView.visibility == View.VISIBLE) {
            calendarView.visibility = View.GONE
        } else {
            calendarView.visibility = View.VISIBLE
        }
    }

    // Helper method to get the selected date as Date object
    private fun getSelectedDate(): Date? {
        return if (tvSelectedDate.text != "No date selected") {
            val dateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.getDefault())
            dateFormat.parse(tvSelectedDate.text.toString())
        } else {
            null
        }
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