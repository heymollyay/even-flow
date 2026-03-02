package com.example.periodtracker.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

object UserData {
    private const val PREFS_FILE = "user_data"
    private const val KEY_USERNAME = "username"
    private const val KEY_HAS_LOGGED_IN = "has_logged_in"

    //cycle data collected in onboarding
    private const val KEY_LAST_PERIOD_START = "last_period_start"
    private const val KEY_PERIOD_DURATION = "period_duration"
    private const val KEY_CYCLE_LENGTH = "cycle_length"
    private const val KEY_LONG_TERM_CONTRACEPTIVES = "long_term_contraceptives"



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

    fun saveLastPeriodStart(context: Context, epochMs: Long) {
        getPreferences(context).edit().putLong(KEY_LAST_PERIOD_START, epochMs).apply() //convert date to ms
    }

    fun getLastPeriodStart(context: Context): Long {
        return getPreferences(context).getLong(KEY_LAST_PERIOD_START, -1L)
    }

    fun savePeriodDuration(context: Context, days: Int) {
        getPreferences(context).edit().putInt(KEY_PERIOD_DURATION, days).apply()
    }

    fun getPeriodDuration(context: Context): Int {
        return getPreferences(context).getInt(KEY_PERIOD_DURATION, 5) //avg period 5 days
    }

    fun saveCycleLength(context: Context, days: Int) {
        getPreferences(context).edit().putInt(KEY_CYCLE_LENGTH, days).apply()
    }

    fun getCycleLength(context:Context): Int {
        return getPreferences(context).getInt(KEY_CYCLE_LENGTH, 28) //avg cycle 28 days
    }

    fun saveLongTermContraceptives(context: Context, contraceptive: String) {
        getPreferences(context).edit().putString(KEY_LONG_TERM_CONTRACEPTIVES, contraceptive).apply()
    }

    fun getLongTermContraceptives(context: Context): String {
        return getPreferences(context).getString(KEY_LONG_TERM_CONTRACEPTIVES, "")?:""
    }



}