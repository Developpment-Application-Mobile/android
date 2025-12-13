package com.example.edukid_android.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.edukid_android.models.Parent
import com.google.gson.Gson

object PreferencesManager {
    private const val PREFS_NAME = "EduKidPrefs"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_PARENT_DATA = "parent_data"
    private const val KEY_REMEMBER_ME = "remember_me"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Save access token to SharedPreferences
     */
    fun saveAccessToken(context: Context, token: String) {
        getSharedPreferences(context).edit()
            .putString(KEY_ACCESS_TOKEN, token)
            .apply()
    }

    /**
     * Get access token from SharedPreferences
     */
    fun getAccessToken(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_ACCESS_TOKEN, null)
    }

    /**
     * Save parent data to SharedPreferences as JSON
     */
    fun saveParentData(context: Context, parent: Parent) {
        val gson = Gson()
        val parentJson = gson.toJson(parent)
        getSharedPreferences(context).edit()
            .putString(KEY_PARENT_DATA, parentJson)
            .apply()
    }

    /**
     * Get parent data from SharedPreferences
     */
    fun getParentData(context: Context): Parent? {
        val parentJson = getSharedPreferences(context).getString(KEY_PARENT_DATA, null)
        return if (parentJson != null) {
            try {
                // Use Gson to deserialize since we used Gson to serialize
                val gson = Gson()
                gson.fromJson(parentJson, Parent::class.java)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    /**
     * Save remember me preference
     */
    fun setRememberMe(context: Context, remember: Boolean) {
        getSharedPreferences(context).edit()
            .putBoolean(KEY_REMEMBER_ME, remember)
            .apply()
    }

    /**
     * Get remember me preference
     */
    fun getRememberMe(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_REMEMBER_ME, false)
    }

    /**
     * Clear all stored data (used on logout)
     */
    fun clearAll(context: Context) {
        getSharedPreferences(context).edit()
            .clear()
            .apply()
    }

    /**
     * Check if user has stored credentials
     */
    fun hasStoredCredentials(context: Context): Boolean {
        val token = getAccessToken(context)
        val parent = getParentData(context)
        return !token.isNullOrBlank() && parent != null
    }
}

