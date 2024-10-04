package com.example.biometricattendance

import android.content.Intent // For navigating between activities
import android.os.Bundle // For the Bundle class in onCreate
import android.widget.Button // For accessing Button views
import android.widget.EditText // For accessing EditText views
import android.widget.Toast // For showing toast messages
import androidx.appcompat.app.AppCompatActivity // For using AppCompatActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var databaseHelper: AttendanceDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) { // Fixed the unresolved reference issue
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        databaseHelper = AttendanceDatabaseHelper(this)

        findViewById<Button>(R.id.btnSignUp).setOnClickListener {
            val name = findViewById<EditText>(R.id.etName).text.toString()
            val email = findViewById<EditText>(R.id.etEmail).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()

            if (!ValidationUtil.isValidEmail(email)) {
                Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Generate a new user ID (this could be done in different ways)
            val userId = databaseHelper.getUserCount() + 1 // Assuming you have a method to count users
            val user = User(id = userId, name = name, email = email, password = password) // Pass name to User constructor
            databaseHelper.addUser(user)

            Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignInActivity::class.java))
            finish() // Close SignUpActivity to prevent going back to it
        }
    }
}
