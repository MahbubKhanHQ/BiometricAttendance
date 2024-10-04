package com.example.biometricattendance

data class Attendance(
    val id: Int,
    val userId: Int,
    val type: String,
    val timestamp: String
)
