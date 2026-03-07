package com.example.periodtracker.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

object UserData {
    private const val PREFS_FILE = "user_data"
    private const val KEY_USERNAME = "username"

    //cycle data collected in onboarding
    private const val KEY_LAST_PERIOD_START = "last_period_start"
    private const val KEY_CYCLE_LENGTH = "cycle_length"
    private const val KEY_LONG_TERM_CONTRACEPTIVES = "long_term_contraceptives"

    //cycle lengths
    private const val KEY_FOLLICULAR_LENGTH = "follicular_length"
    private const val KEY_MENSTRUAL_LENGTH = "menstrual_length"
    private const val KEY_LUTEAL_LENGTH = "luteal_length"
    private const val KEY_DAYS_UNTIL_MENSTRUATION = "days_until_menstruation"
    private const val KEY_CYCLE_PHASE = "cycle_phase"
    private const val KEY_CURRENT_DAY = "current_day"


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
        getPreferences(context).edit { putString(KEY_USERNAME, username) }
    }

    fun getUsername(context: Context): String {
        return getPreferences(context).getString(KEY_USERNAME, "")?:""
    }

    fun saveLastPeriodStart(context: Context, epochMs: Long?) {
        if (epochMs != null) {
            getPreferences(context).edit { putLong(KEY_LAST_PERIOD_START, epochMs) } //convert date to ms
        }

    }

    fun getLastPeriodStart(context: Context): Long? {
        val value = getPreferences(context).getLong(KEY_LAST_PERIOD_START, -1L)
        return if (value == -1L) null else value
    }


    fun saveCycleLength(context: Context, days: Int) {
        getPreferences(context).edit { putInt(KEY_CYCLE_LENGTH, days) }
    }

    fun getCycleLength(context:Context): Int {
        return getPreferences(context).getInt(KEY_CYCLE_LENGTH, 28)
    }

    fun saveLongTermContraceptives(context: Context, contraceptive: String) {
        getPreferences(context).edit { putString(KEY_LONG_TERM_CONTRACEPTIVES, contraceptive) }
    }

    fun getLongTermContraceptives(context: Context): String {
        return getPreferences(context).getString(KEY_LONG_TERM_CONTRACEPTIVES, "")?:""
    }
    fun saveLutealLength(context: Context, days: Int) {
        getPreferences(context).edit { putInt(KEY_LUTEAL_LENGTH, days) }
    }
    fun getLutealLength(context: Context): Int {
        return getPreferences(context).getInt(KEY_LUTEAL_LENGTH, 12)
    }

    fun saveMenstrualLength(context: Context, days: Int) {
        getPreferences(context).edit { putInt(KEY_MENSTRUAL_LENGTH, days) }
    }

    fun getMenstrualLength(context: Context): Int {
        return getPreferences(context).getInt(KEY_MENSTRUAL_LENGTH, 4) //4 is average across all cycle lengths
    }

    fun saveFollicularLength(context: Context, days: Int) {
        getPreferences(context).edit { putInt(KEY_FOLLICULAR_LENGTH, days) }
    }

    fun getFollicularLength(context: Context): Int {
        return getPreferences(context).getInt(KEY_FOLLICULAR_LENGTH, 10)
    }

    fun saveDaysUntilMenstruation(context: Context, days: Int) {
        getPreferences(context).edit { putInt(KEY_DAYS_UNTIL_MENSTRUATION, days) }
    }

    fun getDaysUntilMenstruation(context: Context): Int {
        return getPreferences(context).getInt(KEY_DAYS_UNTIL_MENSTRUATION, 0)
    }

    fun saveCyclePhase(context: Context, phase: String) {
        getPreferences(context).edit { putString(KEY_CYCLE_PHASE, phase) }
    }

    fun getCyclePhase(context: Context): String {
        return getPreferences(context).getString(KEY_CYCLE_PHASE, "follicular")?:""
    }

    fun saveCurrentDay(context: Context, phase: Int) {
        getPreferences(context).edit { putInt(KEY_CURRENT_DAY, phase) }
    }

    fun getCurrentDay(context: Context): Int {
        return getPreferences(context).getInt(KEY_CURRENT_DAY, 0)

    }
}