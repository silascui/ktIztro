package io.github.silascui.ktIztro.ai.service

expect object PlatformSettings {
    fun getString(key: String, defaultValue: String): String
    fun putString(key: String, value: String)
}
