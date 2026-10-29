package io.github.silascui.ktIztro.model

/**
 * 运限流耀与四化单项
 */
data class ScopeHoroscope(
    val index: Int,
    val name: String,
    val heavenlyStem: String,
    val earthlyBranch: String,
    val palaceNames: List<String>,
    val mutagen: List<String>,              // 禄权科忌对应的四颗星名
    val stars: List<List<Star>> = List(12) { emptyList() }, // 12宫流耀
    val yearlyDecStar: Pair<List<String>, List<String>>? = null // suiqian12, jiangqian12
)

/**
 * 小限信息
 */
data class AgeHoroscope(
    val index: Int,
    val nominalAge: Int,
    val name: String,
    val heavenlyStem: String,
    val earthlyBranch: String,
    val palaceNames: List<String>,
    val mutagen: List<String>
)

/**
 * 完整运限信息
 */
data class Horoscope(
    val solarDate: String,
    val lunarDate: String,
    val decadal: ScopeHoroscope,
    val age: AgeHoroscope,
    val yearly: ScopeHoroscope,
    val monthly: ScopeHoroscope,
    val daily: ScopeHoroscope,
    val hourly: ScopeHoroscope
)

/**
 * 三方四正宫位
 */
data class SanFangSiZheng(
    val self: Palace,
    val opposite: Palace,
    val wealth: Palace,
    val career: Palace
)
