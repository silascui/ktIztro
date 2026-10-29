package io.github.silascui.ktIztro.model

/**
 * 星曜对象
 */
data class Star(
    val name: String,
    val type: StarType,
    val scope: HoroscopeScope = HoroscopeScope.ORIGIN,
    val brightness: String = "",
    val mutagen: String = ""
) {
    val isMajor: Boolean get() = type == StarType.MAJOR
    val isSoft: Boolean get() = type == StarType.SOFT
    val isTough: Boolean get() = type == StarType.TOUGH
    val isHoroscope: Boolean get() = type == StarType.HOROSCOPE
}
