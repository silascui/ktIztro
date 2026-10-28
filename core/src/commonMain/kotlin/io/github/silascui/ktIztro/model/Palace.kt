package io.github.silascui.ktIztro.model

/**
 * 大限信息
 */
data class Decadal(
    val range: Pair<Int, Int>,
    val heavenlyStem: String,
    val earthlyBranch: String
)

/**
 * 宫位对象（对应紫微斗数十二宫之一）
 */
data class Palace(
    val index: Int,                         // 0..11, 0为寅宫
    val name: String,                       // 宫名，如 "命宫"、"财帛"
    val isBodyPalace: Boolean = false,      // 是否为身宫
    val isOriginalPalace: Boolean = false,  // 是否与生年干相同
    val heavenlyStem: String,               // 宫干，如 "丙"
    val earthlyBranch: String,              // 宫支，如 "寅"
    val majorStars: List<Star> = emptyList(),
    val minorStars: List<Star> = emptyList(),
    val adjectiveStars: List<Star> = emptyList(),
    val changsheng12: String = "",          // 长生十二神，如 "长生"
    val boshi12: String = "",               // 博士十二神，如 "博士"
    val jiangqian12: String = "",           // 将前十二神，如 "将星"
    val suiqian12: String = "",             // 岁前十二神，如 "岁建"
    val decadal: Decadal,                   // 大限范围与干支
    val ages: List<Int> = emptyList()       // 小限年龄
) {
    /**
     * 判断宫位是否无主星
     */
    fun isEmptyMajor(): Boolean = majorStars.isEmpty()

    /**
     * 判断是否包含某颗星曜
     */
    fun hasStar(starName: String): Boolean {
        return majorStars.any { it.name == starName } ||
                minorStars.any { it.name == starName } ||
                adjectiveStars.any { it.name == starName }
    }
}
