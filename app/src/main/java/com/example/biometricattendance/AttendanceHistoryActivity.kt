package com.example.biometricattendance

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class AttendanceHistoryActivity : AppCompatActivity() {
    private lateinit var dbHelper: AttendanceDatabaseHelper
    private val userId = 1 // This should be fetched dynamically

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_history)

        // Initialize database helper
        dbHelper = AttendanceDatabaseHelper(this)

        // Fetch attendance history
        val attendanceHistory = dbHelper.getAttendanceHistory(userId)

        // Find the ListView
        val listViewAttendance = findViewById<ListView>(R.id.listViewAttendance)

        // Create an ArrayAdapter to display the attendance history in the ListView
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, // Android's built-in layout for a simple list item
            attendanceHistory
        )

        // Set the adapter to the ListView
        listViewAttendance.adapter = adapter
    }
}
