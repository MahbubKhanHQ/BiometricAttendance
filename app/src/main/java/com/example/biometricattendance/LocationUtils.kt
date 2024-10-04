package com.example.biometricattendance

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

object LocationUtils {

    private const val CHECK_IN_RADIUS_METERS = 100  // Example check-in radius

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, callback: (Location?) -> Unit) {
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        val locationTask: Task<Location> = fusedLocationClient.lastLocation

        locationTask.addOnSuccessListener { location: Location? ->
            callback(location)
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to get location.", Toast.LENGTH_SHORT).show()
            callback(null)
        }
    }

    fun isWithinCheckInRadius(currentLocation: Location, checkInLocation: Location): Boolean {
        val distance = currentLocation.distanceTo(checkInLocation)
        return distance <= CHECK_IN_RADIUS_METERS
    }
}
