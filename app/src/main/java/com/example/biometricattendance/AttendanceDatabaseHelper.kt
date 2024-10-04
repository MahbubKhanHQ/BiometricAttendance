package com.example.biometricattendance

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Helper class to manage attendance database
class AttendanceDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "attendance.db"
        private const val DATABASE_VERSION = 1

        // Table Names and Columns
        private const val TABLE_USER = "user"
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_NAME = "name" // Added name column
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"

        private const val TABLE_ATTENDANCE = "attendance"
        private const val COLUMN_ATTENDANCE_ID = "id"
        private const val COLUMN_USER_ID_FK = "userId"
        private const val COLUMN_TYPE = "type"
        private const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create user table
        val createUserTable = ("CREATE TABLE $TABLE_USER (" +
                "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAME TEXT, " + // Added name to the table schema
                "$COLUMN_EMAIL TEXT UNIQUE, " +
                "$COLUMN_PASSWORD TEXT)")
        db.execSQL(createUserTable)

        // Create attendance table
        val createAttendanceTable = ("CREATE TABLE $TABLE_ATTENDANCE (" +
                "$COLUMN_ATTENDANCE_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_USER_ID_FK INTEGER, " +
                "$COLUMN_TYPE TEXT, " +
                "$COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY($COLUMN_USER_ID_FK) REFERENCES $TABLE_USER($COLUMN_USER_ID))")
        db.execSQL(createAttendanceTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ATTENDANCE")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        onCreate(db)
    }

    // Function to record attendance
    fun recordAttendance(userId: Int, type: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER_ID_FK, userId)
            put(COLUMN_TYPE, type)
        }
        db.insert(TABLE_ATTENDANCE, null, values)
    }

    // Function to check if Check-Out is allowed (no multiple check-outs in one day)
    fun isCheckOutAllowed(userId: Int): Boolean {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_ATTENDANCE WHERE $COLUMN_USER_ID_FK = ? AND $COLUMN_TYPE = 'Check-Out' AND $COLUMN_TIMESTAMP >= date('now', 'start of day')"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        val checkOutAllowed = !cursor.moveToFirst()
        cursor.close()
        return checkOutAllowed
    }

    // Function to get attendance history
    fun getAttendanceHistory(userId: Int): List<String> {
        val db = readableDatabase
        val query = "SELECT $COLUMN_TYPE, $COLUMN_TIMESTAMP FROM $TABLE_ATTENDANCE WHERE $COLUMN_USER_ID_FK = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        val attendanceList = mutableListOf<String>()

        while (cursor.moveToNext()) {
            val type = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE))
            val timestamp = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
            attendanceList.add("$type at $timestamp")
        }
        cursor.close()
        return attendanceList
    }

    // Function to get a user by email
    fun getUser(email: String): User? {
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_USER WHERE $COLUMN_EMAIL = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(email))

        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID))
            val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)) // Fetch name
            val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            User(name, id, email, password) // Create and return a User object
        } else {
            null // User not found
        }.also {
            cursor.close() // Close the cursor to avoid memory leaks
        }
    }

    // Function to get the total number of users
    fun getUserCount(): Int {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_USER"
        val cursor: Cursor = db.rawQuery(query, null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0) // Get the count
        }
        cursor.close()
        return count
    }

    // Function to add a new user
    fun addUser(user: User) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, user.name) // Insert name
            put(COLUMN_EMAIL, user.email)
            put(COLUMN_PASSWORD, user.password)
        }
        db.insert(TABLE_USER, null, values)
    }
}
