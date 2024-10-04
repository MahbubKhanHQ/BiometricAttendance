package com.example.biometricattendance

import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {
    private lateinit var dbHelper: AttendanceDatabaseHelper
    private val userId = 1 // You should fetch the actual logged-in user ID
    private val checkInLocation = Location("").apply {
        latitude = 37.4219999 // Example latitude (should be actual location for your check-in point)
        longitude = -122.0840575 // Example longitude
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize database helper
        dbHelper = AttendanceDatabaseHelper(this)

        // Buttons
        val btnCheckIn = findViewById<Button>(R.id.btnCheckIn)
        val btnCheckOut = findViewById<Button>(R.id.btnCheckOut)
        val btnViewAttendance = findViewById<Button>(R.id.btnViewAttendance)

        // Handle check-in button click
        btnCheckIn.setOnClickListener {
            handleCheckIn()
        }

        // Handle check-out button click
        btnCheckOut.setOnClickListener {
            handleCheckOut()
        }

        // Handle view attendance button click
        btnViewAttendance.setOnClickListener {
            val attendanceHistory = dbHelper.getAttendanceHistory(userId)
            // For simplicity, show attendance history in a Toast message (in production, you'd show this in a ListView or RecyclerView)
            Toast.makeText(this, attendanceHistory.joinToString("\n"), Toast.LENGTH_LONG).show()
        }
    }

    // Handle Check-In
    private fun handleCheckIn() {
        if (!dbHelper.isCheckOutAllowed(userId)) {
            Toast.makeText(this, "You have already checked in today.", Toast.LENGTH_LONG).show()
            return
        }

        // Step 1: Perform biometric authentication
        if (BiometricUtils.isBiometricAvailable(this)) {
            BiometricUtils.authenticate(this) { isAuthenticated ->
                if (isAuthenticated) {
                    // Step 2: Get the current GPS location
                    LocationUtils.getCurrentLocation(this) { currentLocation ->
                        if (currentLocation != null) {
                            // Step 3: Check if the user is within the allowed check-in radius
                            if (LocationUtils.isWithinCheckInRadius(currentLocation, checkInLocation)) {
                                // Allow check-in
                                dbHelper.recordAttendance(userId, "Check-In")
                                Toast.makeText(this, "Check-In successful.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this, "You are not within the check-in location radius.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(this, "Could not retrieve location.", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Biometric authentication failed.", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "Biometric authentication is not available.", Toast.LENGTH_LONG).show()
        }
    }

    // Handle Check-Out
    private fun handleCheckOut() {
        if (!dbHelper.isCheckOutAllowed(userId)) {
            BiometricUtils.authenticate(this) { isAuthenticated ->
                if (isAuthenticated) {
                    LocationUtils.getCurrentLocation(this) { currentLocation ->
                        if (currentLocation != null && LocationUtils.isWithinCheckInRadius(currentLocation, checkInLocation)) {
                            dbHelper.recordAttendance(userId, "Check-Out")
                            Toast.makeText(this, "Check-Out successful.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "You are not within the check-out location radius.", Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Biometric authentication failed.", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, "You have already checked out today.", Toast.LENGTH_LONG).show()
        }
    }
}
