package com.example.ecojourney.data.local

import android.content.Context
import android.content.SharedPreferences

object SharedPrefsHelper {
    private const val PREFS_NAME = "CarbonPrefs"
    private const val KEY_PROGRESS = "carbonProgress"
    private const val KEY_DATE = "lastUpdatedDate"

    fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getProgress(context: Context): Float {
        return getSharedPreferences(context).getFloat(KEY_PROGRESS, 0f)
    }

    fun setProgress(context: Context, progress: Float) {
        with(getSharedPreferences(context).edit()) {
            putFloat(KEY_PROGRESS, progress)
            apply()
        }
    }

    fun getLastUpdatedDate(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_DATE, null)
    }

    fun setLastUpdatedDate(context: Context, date: String) {
        with(getSharedPreferences(context).edit()) {
            putString(KEY_DATE, date)
            apply()
        }
    }

}