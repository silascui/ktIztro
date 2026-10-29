package io.github.silascui.ktIztro.ai.service

import android.content.Context
import android.content.SharedPreferences

actual object PlatformSettings {
    var appContext: Context? = null
    private const val PREFS_NAME = "zwds_ai_config"

    actual fun getString(key: String, defaultValue: String): String {
        val sp: SharedPreferences? = appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sp?.getString(key, defaultValue) ?: defaultValue
    }

    actual fun putString(key: String, value: String) {
        val sp: SharedPreferences? = appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sp?.edit()?.putString(key, value)?.apply()
    }
}
