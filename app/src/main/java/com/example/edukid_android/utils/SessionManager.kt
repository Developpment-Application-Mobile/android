package com.example.edukid_android.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.edukid_android.models.Child
import com.google.gson.Gson

class SessionManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    private val gson = Gson()
    
    companion object {
        private const val PREFS_NAME = "EduKidChildSession"
        private const val KEY_CHILD_DATA = "child_data"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    /**
     * Save child session to SharedPreferences
     */
    fun saveChildSession(child: Child) {
        val childJson = gson.toJson(child)
        sharedPreferences.edit().apply {
            putString(KEY_CHILD_DATA, childJson)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }
    
    /**
     * Retrieve saved child session
     */
    fun getChildSession(): Child? {
        return try {
            val childJson = sharedPreferences.getString(KEY_CHILD_DATA, null)
            if (childJson != null) {
                gson.fromJson(childJson, Child::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            android.util.Log.e("SessionManager", "Error retrieving child session: ${e.message}", e)
            null
        }
    }
    
    /**
     * Clear child session (logout)
     */
    fun clearChildSession() {
        sharedPreferences.edit().apply {
            remove(KEY_CHILD_DATA)
            putBoolean(KEY_IS_LOGGED_IN, false)
            apply()
        }
    }
    
    /**
     * Check if a child session exists
     */
    fun isChildLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) &&
                sharedPreferences.getString(KEY_CHILD_DATA, null) != null
    }
    
    /**
     * Update child session (useful for score updates, quiz removal, etc.)
     */
    fun updateChildSession(child: Child) {
        if (isChildLoggedIn()) {
            saveChildSession(child)
        }
    }
}
