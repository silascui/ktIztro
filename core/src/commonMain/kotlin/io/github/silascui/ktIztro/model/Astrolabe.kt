package io.github.silascui.ktIztro.model

/**
 * 完整紫微斗数命盘（星盘）
 */
data class Astrolabe(
    val gender: Gender,
    val solarDate: String,                      // 公历日期，如 "2000-8-16"
    val lunarDate: String,                      // 农历日期，如 "二〇〇〇年七月十七"
    val chineseDate: String,                    // 干支八字，如 "庚辰 甲申 丙午 庚寅"
    val fourPillars: FourPillars,               // 年月日时四柱
    val time: String,                           // 时辰，如 "寅时"
    val timeRange: String,                      // 时间段，如 "03:00~05:00"
    val sign: String,                           // 西洋星座，如 "狮子座"
    val zodiac: String,                         // 生肖，如 "龙"
    val earthlyBranchOfSoulPalace: String,      // 命宫地支，如 "午"
    val earthlyBranchOfBodyPalace: String,      // 身宫地支，如 "戌"
    val soul: String,                           // 命主，如 "破军"
    val body: String,                           // 身主，如 "文昌"
    val fiveElementsClass: String,              // 五行局，如 "木三局"
    val palaces: List<Palace>                   // 十二宫位（以寅宫为 0，顺时针排列）
) {
    /**
     * 根据宫名获取宫位
     */
    fun palace(name: String): Palace? {
        return palaces.firstOrNull { it.name == name }
    }

    /**
     * 根据地支获取宫位
     */
    fun palaceByBranch(earthlyBranch: String): Palace? {
        return palaces.firstOrNull { it.earthlyBranch == earthlyBranch }
    }

    /**
     * 获取指定宫位的三方四正
     * 本宫、对宫 (+6)、财帛三合位 (+4)、官禄三合位 (+8)
     */
    fun getSanFangSiZheng(palaceIndex: Int): SanFangSiZheng {
        val self = palaces[Constants.fixIndex(palaceIndex)]
        val opposite = palaces[Constants.fixIndex(palaceIndex + 6)]
        val career = palaces[Constants.fixIndex(palaceIndex + 4)]
        val wealth = palaces[Constants.fixIndex(palaceIndex + 8)]
        return SanFangSiZheng(self, opposite, wealth, career)
    }

    /**
     * 查找某颗星曜所在的宫位
     */
    fun findPalaceWithStar(starName: String): Palace? {
        return palaces.firstOrNull { it.hasStar(starName) }
    }
}

/**
 * 四柱干支
 */
data class FourPillars(
    val yearGan: String,
    val yearZhi: String,
    val monthGan: String,
    val monthZhi: String,
    val dayGan: String,
    val dayZhi: String,
    val timeGan: String,
    val timeZhi: String
) {
    fun toChineseString(): String {
        return "$yearGan$yearZhi $monthGan$monthZhi $dayGan$dayZhi $timeGan$timeZhi"
    }
}
