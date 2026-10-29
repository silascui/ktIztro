package io.github.silascui.ktIztro.ai.service

import platform.Foundation.NSUserDefaults

actual object PlatformSettings {
    actual fun getString(key: String, defaultValue: String): String {
        return NSUserDefaults.standardUserDefaults.stringForKey(key) ?: defaultValue
    }

    actual fun putString(key: String, value: String) {
        NSUserDefaults.standardUserDefaults.setObject(value, forKey = key)
    }
}
