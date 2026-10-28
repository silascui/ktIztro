package io.github.silascui.ktIztro.engine

import io.github.silascui.ktIztro.calendar.LunarCalendarHelper
import io.github.silascui.ktIztro.model.Constants
import io.github.silascui.ktIztro.model.Decadal
import io.github.silascui.ktIztro.model.EarthlyBranch
import io.github.silascui.ktIztro.model.FiveElementsClass
import io.github.silascui.ktIztro.model.Gender

data class SoulAndBodyInfo(
    val soulIndex: Int,
    val bodyIndex: Int,
    val heavenlyStemOfSoul: String,
    val earthlyBranchOfSoul: String
)

object PalaceCalculator {

    /**
     * 安命身宫
     * - 寅起正月，顺数至生月，逆数生时为命宫。
     * - 寅起正月，顺数至生月，顺数生时为身宫。
     */
    fun getSoulAndBody(
        solarDateStr: String,
        timeIndex: Int,
        fixLeap: Boolean = true,
        yearDivide: String = "normal"
    ): SoulAndBodyInfo {
        val fourPillars = LunarCalendarHelper.getFourPillarsBySolar(solarDateStr, timeIndex, yearDivide)
        val timeBranch = fourPillars.timeZhi
        val timeBranchIdx = Constants.EARTHLY_BRANCHES.indexOf(timeBranch)

        val monthIndex = LunarCalendarHelper.fixLunarMonthIndex(solarDateStr, timeIndex, fixLeap)

        val soulIndex = Constants.fixIndex(monthIndex - timeBranchIdx)
        val bodyIndex = Constants.fixIndex(monthIndex + timeBranchIdx)

        val startTigerStem = Constants.TIGER_RULE[fourPillars.yearGan] ?: "丙"
        val tigerIdx = Constants.HEAVENLY_STEMS.indexOf(startTigerStem)

        val soulStemIdx = Constants.fixIndex(tigerIdx + soulIndex, 10)
        val heavenlyStemOfSoul = Constants.HEAVENLY_STEMS[soulStemIdx]
        val earthlyBranchOfSoul = Constants.palaceIndexToEarthlyBranch(soulIndex)

        return SoulAndBodyInfo(
            soulIndex = soulIndex,
            bodyIndex = bodyIndex,
            heavenlyStemOfSoul = heavenlyStemOfSoul,
            earthlyBranchOfSoul = earthlyBranchOfSoul
        )
    }

    /**
     * 定五行局（以命宫天干地支纳音而定）
     * - 甲乙1 丙丁2 戊己3 庚辛4 壬癸5
     * - 子丑午未1 寅卯申酉2 辰戌巳亥3
     * - 干支相加多减五，木1 金2 水3 火4 土5
     */
    fun getFiveElementsClass(heavenlyStem: String, earthlyBranch: String): FiveElementsClass {
        val stemIdx = Constants.HEAVENLY_STEMS.indexOf(heavenlyStem)
        val stemNumber = stemIdx / 2 + 1

        val branchIdx = Constants.EARTHLY_BRANCHES.indexOf(earthlyBranch)
        val branchNumber = (Constants.fixIndex(branchIdx, 6) / 2) + 1

        var sum = stemNumber + branchNumber
        while (sum > 5) {
            sum -= 5
        }

        return when (sum) {
            1 -> FiveElementsClass.WOOD_3RD
            2 -> FiveElementsClass.METAL_4TH
            3 -> FiveElementsClass.WATER_2ND
            4 -> FiveElementsClass.FIRE_6TH
            5 -> FiveElementsClass.EARTH_5TH
            else -> FiveElementsClass.WATER_2ND
        }
    }

    /**
     * 获取从寅宫开始的十二宫名
     */
    fun getPalaceNames(fromSoulIndex: Int): List<String> {
        return List(12) { i ->
            val idx = Constants.fixIndex(i - fromSoulIndex)
            Constants.PALACE_NAMES[idx]
        }
    }

    /**
     * 起小限起始宫位索引
     * - 寅午戍人辰上起，申子辰人自戍宫，
     * - 巳酉丑人未宫始，亥卯未人起丑宫。
     */
    fun getAgeStartIndex(yearZhi: String): Int {
        return when (yearZhi) {
            "寅", "午", "戌" -> Constants.earthlyBranchToPalaceIndex("辰")
            "申", "子", "辰" -> Constants.earthlyBranchToPalaceIndex("戌")
            "巳", "酉", "丑" -> Constants.earthlyBranchToPalaceIndex("未")
            "亥", "卯", "未" -> Constants.earthlyBranchToPalaceIndex("丑")
            else -> 0
        }
    }

    /**
     * 起大限与小限
     */
    fun getDecadalsAndAges(
        solarDateStr: String,
        timeIndex: Int,
        gender: Gender,
        soulIndex: Int,
        fiveElementsClass: FiveElementsClass,
        yearZhi: String,
        yearGan: String
    ): Pair<List<Decadal>, List<List<Int>>> {
        val startTigerStem = Constants.TIGER_RULE[yearGan] ?: "丙"
        val tigerIdx = Constants.HEAVENLY_STEMS.indexOf(startTigerStem)

        val branchYinYang = EarthlyBranch.fromChinese(yearZhi).yinYang
        val isForward = (gender.yinYang == branchYinYang)

        val decadals = MutableList(12) { Decadal(Pair(0, 0), "", "") }

        for (i in 0 until 12) {
            val idx = if (isForward) Constants.fixIndex(soulIndex + i) else Constants.fixIndex(soulIndex - i)
            val start = fiveElementsClass.number + 10 * i
            val end = start + 9
            val stemIdx = Constants.fixIndex(tigerIdx + idx, 10)
            val stem = Constants.HEAVENLY_STEMS[stemIdx]
            val branch = Constants.palaceIndexToEarthlyBranch(idx)

            decadals[idx] = Decadal(Pair(start, end), stem, branch)
        }

        val ageStartIdx = getAgeStartIndex(yearZhi)
        val ages = MutableList(12) { mutableListOf<Int>() }

        for (i in 0 until 12) {
            val targetPalaceIdx = if (gender == Gender.MALE) {
                Constants.fixIndex(ageStartIdx + i)
            } else {
                Constants.fixIndex(ageStartIdx - i)
            }

            for (j in 0 until 10) {
                val age = 12 * j + i + 1
                ages[targetPalaceIdx].add(age)
            }
        }

        return Pair(decadals, ages)
    }
}
