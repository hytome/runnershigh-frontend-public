package com.example.runnershigh.data.health

import android.content.Context

object HealthConnectConsentStore {
    private const val PREFS_NAME = "health_connect_prefs"
    private const val KEY_CONSENTED = "health_connect_consented"

    fun hasConsented(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_CONSENTED, false)
    }

    fun setConsented(context: Context, consented: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_CONSENTED, consented).apply()
    }
}
