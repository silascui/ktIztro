package io.github.silascui.ktIztro.ai.service

import java.util.prefs.Preferences

actual object PlatformSettings {
    private val prefs = Preferences.userRoot().node("io/github/silascui/ktIztro/ai")

    actual fun getString(key: String, defaultValue: String): String {
        return prefs.get(key, defaultValue)
    }

    actual fun putString(key: String, value: String) {
        prefs.put(key, value)
    }
}
