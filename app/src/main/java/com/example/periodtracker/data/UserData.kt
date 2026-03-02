package com.example.periodtracker.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object UserData {
    private const val PREFS_FILE = "user_data"
    private const val KEY_USERNAME = "username"
    private const val KEY_HAS_LOGGED_IN = "has_logged_in"

    private fun getPreferences(context: Context) = EncryptedSharedPreferences.create(
        context,
        PREFS_FILE,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    // username encrypted on the device
    fun saveUsername(context: Context, username: String) {
        getPreferences(context).edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(context: Context): String {
        return getPreferences(context).getString(KEY_USERNAME, "")?:""
    }

    //tracks whether login screen can be skipped
    fun setHasLoggedIn(context:Context) {
        getPreferences(context).edit().putBoolean(KEY_HAS_LOGGED_IN, true).apply()
    }

    fun hasLoggedIn(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_HAS_LOGGED_IN, false)
    }
}