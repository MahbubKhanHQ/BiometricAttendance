package com.example.biometricattendance

import android.content.Intent // For navigating between activities
import android.os.Bundle // For the Bundle class in onCreate
import android.widget.Button // For accessing Button views
import android.widget.EditText // For accessing EditText views
import android.widget.Toast // For showing toast messages
import androidx.appcompat.app.AppCompatActivity // For using AppCompatActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var databaseHelper: AttendanceDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) { // Fixed the override issue
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        // Initialize database helper
        databaseHelper = AttendanceDatabaseHelper(this)

        // Find views and set up the sign-in button's click listener
        findViewById<Button>(R.id.btnSignIn).setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim() // Trim whitespace
            val password = findViewById<EditText>(R.id.etPassword).text.toString()

            // Check if user exists in the database
            val user = databaseHelper.getUser(email)
            if (user != null && user.password == password) {
                // Login successful, navigate to HomeActivity
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish() // Close SignInActivity to prevent going back to it
            } else {
                // Login failed, show error message
                Toast.makeText(this, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

