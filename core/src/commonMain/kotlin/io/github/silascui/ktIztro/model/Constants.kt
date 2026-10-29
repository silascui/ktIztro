package io.github.silascui.ktIztro.model

object Constants {
    val HEAVENLY_STEMS = listOf("甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸")

    val EARTHLY_BRANCHES = listOf("子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥")

    // 紫微斗数十天干四化 【禄, 权, 科, 忌】
    val HEAVENLY_STEM_MUTAGENS: Map<String, List<String>> = mapOf(
        "甲" to listOf("廉贞", "破军", "武曲", "太阳"),
        "乙" to listOf("天机", "天梁", "紫微", "太阴"),
        "丙" to listOf("天同", "天机", "文昌", "廉贞"),
        "丁" to listOf("太阴", "天同", "天机", "巨门"),
        "戊" to listOf("贪狼", "太阴", "右弼", "天机"),
        "己" to listOf("武曲", "贪狼", "天梁", "文曲"),
        "庚" to listOf("太阳", "武曲", "太阴", "天同"),
        "辛" to listOf("巨门", "太阳", "文曲", "文昌"),
        "壬" to listOf("天梁", "紫微", "左辅", "武曲"),
        "癸" to listOf("破军", "巨门", "太阴", "贪狼")
    )

    // 五虎遁年起月诀 (正月建寅)
    // 甲己之年丙作首，乙庚之岁戊为头，丙辛必定寻庚起，丁壬壬位顺行流，若问戊癸何方发，甲寅之上好追求。
    val TIGER_RULE: Map<String, String> = mapOf(
        "甲" to "丙",
        "乙" to "戊",
        "丙" to "庚",
        "丁" to "壬",
        "戊" to "甲",
        "己" to "丙",
        "庚" to "戊",
        "辛" to "庚",
        "壬" to "壬",
        "癸" to "甲"
    )

    // 五鼠遁日起时诀
    // 甲己还加甲，乙庚丙作初，丙辛从戊起，丁壬庚子居，戊癸起壬子。
    val RAT_RULE: Map<String, String> = mapOf(
        "甲" to "甲",
        "乙" to "丙",
        "丙" to "戊",
        "丁" to "庚",
        "戊" to "壬",
        "己" to "甲",
        "庚" to "丙",
        "辛" to "戊",
        "壬" to "庚",
        "癸" to "壬"
    )

    // 十二宫名称（逆布十二宫：从命宫起）
    val PALACE_NAMES = listOf(
        "命宫", "父母", "福德", "田宅", "官禄", "仆役",
        "迁移", "疾厄", "财帛", "子女", "夫妻", "兄弟"
    )

    // 十三时辰
    val CHINESE_TIME = listOf(
        "早子时", "丑时", "寅时", "卯时", "辰时", "巳时",
        "午时", "未时", "申时", "酉时", "戌时", "亥时", "晚子时"
    )

    val TIME_RANGE = listOf(
        "00:00~01:00", "01:00~03:00", "03:00~05:00", "05:00~07:00",
        "07:00~09:00", "09:00~11:00", "11:00~13:00", "13:00~15:00",
        "15:00~17:00", "17:00~19:00", "19:00~21:00", "21:00~23:00", "23:00~00:00"
    )

    /**
     * 将索引限制在 0 until max 范围内
     */
    fun fixIndex(index: Int, max: Int = 12): Int {
        var res = index % max
        if (res < 0) {
            res += max
        }
        return res
    }

    /**
     * 将地支转为宫位索引（宫位以寅为 0）
     */
    fun earthlyBranchToPalaceIndex(earthlyBranch: String): Int {
        val branchIdx = EARTHLY_BRANCHES.indexOf(earthlyBranch)
        val yinIdx = EARTHLY_BRANCHES.indexOf("寅")
        return fixIndex(branchIdx - yinIdx)
    }

    /**
     * 将宫位索引转为地支（宫位以寅为 0）
     */
    fun palaceIndexToEarthlyBranch(palaceIndex: Int): String {
        val yinIdx = EARTHLY_BRANCHES.indexOf("寅")
        return EARTHLY_BRANCHES[fixIndex(palaceIndex + yinIdx)]
    }

    /**
     * 将时间小时数转换为时辰索引 (0..12)
     */
    fun hourToTimeIndex(hour: Int): Int {
        return when (hour) {
            0 -> 0
            23 -> 12
            else -> (hour + 1) / 2
        }
    }
}
