package io.github.silascui.ktIztro.engine

import io.github.silascui.ktIztro.calendar.LunarCalendarHelper
import io.github.silascui.ktIztro.model.Constants
import io.github.silascui.ktIztro.model.FiveElementsClass
import io.github.silascui.ktIztro.model.Gender
import io.github.silascui.ktIztro.model.HoroscopeScope
import io.github.silascui.ktIztro.model.Star
import io.github.silascui.ktIztro.model.StarType

object StarLocation {

    /**
     * 计算紫微星和天府星所在宫位索引（0为寅宫）
     */
    fun getZiweiAndTianfuIndex(
        solarDateStr: String,
        timeIndex: Int,
        fiveElementsClass: FiveElementsClass
    ): Pair<Int, Int> {
        val lunarInfo = LunarCalendarHelper.solarToLunar(solarDateStr)
        val maxDays = LunarCalendarHelper.getTotalDaysOfLunarMonth(solarDateStr)

        var day = if (timeIndex == 12) lunarInfo.lunarDay + 1 else lunarInfo.lunarDay
        if (day > maxDays) {
            day -= maxDays
        }

        val fiveElementsVal = fiveElementsClass.number
        var offset = -1
        var quotient = 0
        var remainder = -1

        do {
            offset++
            val divisor = day + offset
            quotient = divisor / fiveElementsVal
            remainder = divisor % fiveElementsVal
        } while (remainder != 0)

        quotient %= 12
        var ziweiIndex = quotient - 1

        if (offset % 2 == 0) {
            ziweiIndex += offset
        } else {
            ziweiIndex -= offset
        }

        ziweiIndex = Constants.fixIndex(ziweiIndex)
        val tianfuIndex = Constants.fixIndex(12 - ziweiIndex)

        return Pair(ziweiIndex, tianfuIndex)
    }

    /**
     * 安禄存、擎羊、陀罗、天马
     */
    data class LuYangTuoMa(val luIndex: Int, val yangIndex: Int, val tuoIndex: Int, val maIndex: Int)

    fun getLuYangTuoMaIndex(heavenlyStem: String, earthlyBranch: String): LuYangTuoMa {
        val maIndex = when (earthlyBranch) {
            "寅", "午", "戌" -> Constants.earthlyBranchToPalaceIndex("申")
            "申", "子", "辰" -> Constants.earthlyBranchToPalaceIndex("寅")
            "巳", "酉", "丑" -> Constants.earthlyBranchToPalaceIndex("亥")
            "亥", "卯", "未" -> Constants.earthlyBranchToPalaceIndex("巳")
            else -> 0
        }

        val luIndex = when (heavenlyStem) {
            "甲" -> Constants.earthlyBranchToPalaceIndex("寅")
            "乙" -> Constants.earthlyBranchToPalaceIndex("卯")
            "丙", "戊" -> Constants.earthlyBranchToPalaceIndex("巳")
            "丁", "己" -> Constants.earthlyBranchToPalaceIndex("午")
            "庚" -> Constants.earthlyBranchToPalaceIndex("申")
            "辛" -> Constants.earthlyBranchToPalaceIndex("酉")
            "壬" -> Constants.earthlyBranchToPalaceIndex("亥")
            "癸" -> Constants.earthlyBranchToPalaceIndex("子")
            else -> 0
        }

        val yangIndex = Constants.fixIndex(luIndex + 1)
        val tuoIndex = Constants.fixIndex(luIndex - 1)

        return LuYangTuoMa(luIndex, yangIndex, tuoIndex, maIndex)
    }

    /**
     * 安天魁天钺（按年干）
     */
    data class KuiYue(val kuiIndex: Int, val yueIndex: Int)

    fun getKuiYueIndex(heavenlyStem: String): KuiYue {
        val (kuiBranch, yueBranch) = when (heavenlyStem) {
            "甲", "戊", "庚" -> Pair("丑", "未")
            "乙", "己" -> Pair("子", "申")
            "辛" -> Pair("午", "寅")
            "丙", "丁" -> Pair("亥", "酉")
            "壬", "癸" -> Pair("卯", "巳")
            else -> Pair("丑", "未")
        }
        return KuiYue(
            Constants.earthlyBranchToPalaceIndex(kuiBranch),
            Constants.earthlyBranchToPalaceIndex(yueBranch)
        )
    }

    /**
     * 安左辅右弼（按生月）
     */
    data class ZuoYou(val zuoIndex: Int, val youIndex: Int)

    fun getZuoYouIndex(lunarMonth: Int): ZuoYou {
        val zuoIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("辰") + (lunarMonth - 1))
        val youIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("戌") - (lunarMonth - 1))
        return ZuoYou(zuoIndex, youIndex)
    }

    /**
     * 安文昌文曲（按时支）
     */
    data class ChangQu(val changIndex: Int, val quIndex: Int)

    fun getChangQuIndex(timeIndex: Int): ChangQu {
        val changIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("戌") - Constants.fixIndex(timeIndex))
        val quIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("辰") + Constants.fixIndex(timeIndex))
        return ChangQu(changIndex, quIndex)
    }

    /**
     * 日系星：三台、八座、恩光、天贵
     */
    data class DailyStars(val santaiIndex: Int, val bazuoIndex: Int, val enguangIndex: Int, val tianguiIndex: Int)

    fun getDailyStarIndex(solarDateStr: String, timeIndex: Int, fixLeap: Boolean = true): DailyStars {
        val lunar = LunarCalendarHelper.solarToLunar(solarDateStr)
        val monthIndex = LunarCalendarHelper.fixLunarMonthIndex(solarDateStr, timeIndex, fixLeap)
        val (zuoIndex, youIndex) = getZuoYouIndex(monthIndex + 1)
        val (changIndex, quIndex) = getChangQuIndex(timeIndex)
        val dayIndex = LunarCalendarHelper.fixLunarDayIndex(lunar.lunarDay, timeIndex)

        val santaiIndex = Constants.fixIndex((zuoIndex + dayIndex) % 12)
        val bazuoIndex = Constants.fixIndex((youIndex - dayIndex) % 12)
        val enguangIndex = Constants.fixIndex(((changIndex + dayIndex) % 12) - 1)
        val tianguiIndex = Constants.fixIndex(((quIndex + dayIndex) % 12) - 1)

        return DailyStars(santaiIndex, bazuoIndex, enguangIndex, tianguiIndex)
    }

    /**
     * 时系星：台辅、封诰、地空、地劫
     */
    data class TimelyStars(val taifuIndex: Int, val fenggaoIndex: Int, val kongIndex: Int, val jieIndex: Int)

    fun getTimelyStarIndex(timeIndex: Int): TimelyStars {
        val fixedT = Constants.fixIndex(timeIndex)
        val taifuIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("午") + fixedT)
        val fenggaoIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("寅") + fixedT)
        val haiIndex = Constants.earthlyBranchToPalaceIndex("亥")
        val kongIndex = Constants.fixIndex(haiIndex - fixedT)
        val jieIndex = Constants.fixIndex(haiIndex + fixedT)
        return TimelyStars(taifuIndex, fenggaoIndex, kongIndex, jieIndex)
    }

    /**
     * 火星铃星（按年支及生时）
     */
    data class HuoLing(val huoIndex: Int, val lingIndex: Int)

    fun getHuoLingIndex(yearZhi: String, timeIndex: Int): HuoLing {
        val fixedT = Constants.fixIndex(timeIndex)
        val (huoBase, lingBase) = when (yearZhi) {
            "寅", "午", "戌" -> Pair("丑", "卯")
            "申", "子", "辰" -> Pair("寅", "戌")
            "巳", "酉", "丑" -> Pair("卯", "戌")
            "亥", "卯", "未" -> Pair("酉", "戌")
            else -> Pair("丑", "卯")
        }
        val huoIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex(huoBase) + fixedT)
        val lingIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex(lingBase) + fixedT)
        return HuoLing(huoIndex, lingIndex)
    }

    /**
     * 红鸾天喜（按年支）
     */
    data class LuanXi(val hongluanIndex: Int, val tianxiIndex: Int)

    fun getLuanXiIndex(yearZhi: String): LuanXi {
        val branchIdx = Constants.EARTHLY_BRANCHES.indexOf(yearZhi)
        val hongluanIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("卯") - branchIdx)
        val tianxiIndex = Constants.fixIndex(hongluanIndex + 6)
        return LuanXi(hongluanIndex, tianxiIndex)
    }

    /**
     * 月系星：天姚、天刑、解神、阴煞、天月、天巫
     */
    data class MonthlyStars(
        val jieshenIndex: Int,
        val tianyaoIndex: Int,
        val tianxingIndex: Int,
        val yinshaIndex: Int,
        val tianyueIndex: Int,
        val tianwuIndex: Int
    )

    fun getMonthlyStarIndex(solarDateStr: String, timeIndex: Int, fixLeap: Boolean = true): MonthlyStars {
        val monthIndex = LunarCalendarHelper.fixLunarMonthIndex(solarDateStr, timeIndex, fixLeap)

        val jieshenBranches = listOf("申", "戌", "子", "寅", "辰", "午")
        val jieshenIndex = Constants.earthlyBranchToPalaceIndex(jieshenBranches[monthIndex / 2])
        val tianyaoIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("丑") + monthIndex)
        val tianxingIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("酉") + monthIndex)

        val yinshaBranches = listOf("寅", "子", "戌", "申", "午", "辰")
        val yinshaIndex = Constants.earthlyBranchToPalaceIndex(yinshaBranches[monthIndex % 6])

        val tianyueBranches = listOf("戌", "巳", "辰", "寅", "未", "卯", "亥", "未", "寅", "午", "戌", "寅")
        val tianyueIndex = Constants.earthlyBranchToPalaceIndex(tianyueBranches[monthIndex])

        val tianwuBranches = listOf("巳", "申", "寅", "亥")
        val tianwuIndex = Constants.earthlyBranchToPalaceIndex(tianwuBranches[monthIndex % 4])

        return MonthlyStars(jieshenIndex, tianyaoIndex, tianxingIndex, yinshaIndex, tianyueIndex, tianwuIndex)
    }

    /**
     * 年系杂曜索引集合
     */
    data class YearlyStars(
        val huagaiIndex: Int,
        val xianchiIndex: Int,
        val guchenIndex: Int,
        val guasuIndex: Int,
        val tiancaiIndex: Int,
        val tianshouIndex: Int,
        val tianchuIndex: Int,
        val posuiIndex: Int,
        val feilianIndex: Int,
        val longchiIndex: Int,
        val fenggeIndex: Int,
        val tiankuIndex: Int,
        val tianxuIndex: Int,
        val tianguanIndex: Int,
        val tianfuIndex: Int,
        val tiandeIndex: Int,
        val yuedeIndex: Int,
        val tiankongIndex: Int,
        val jieluIndex: Int,
        val kongwangIndex: Int,
        val xunkongIndex: Int,
        val tianshangIndex: Int,
        val tianshiIndex: Int
    )

    fun getYearlyStarIndex(
        yearGan: String,
        yearZhi: String,
        soulIndex: Int,
        bodyIndex: Int
    ): YearlyStars {
        val branchIdx = Constants.EARTHLY_BRANCHES.indexOf(yearZhi)
        val stemIdx = Constants.HEAVENLY_STEMS.indexOf(yearGan)

        val (hgBranch, xcBranch) = when (yearZhi) {
            "寅", "午", "戌" -> Pair("戌", "卯")
            "申", "子", "辰" -> Pair("辰", "酉")
            "巳", "酉", "丑" -> Pair("丑", "午")
            "亥", "卯", "未" -> Pair("未", "子")
            else -> Pair("戌", "卯")
        }
        val huagaiIndex = Constants.earthlyBranchToPalaceIndex(hgBranch)
        val xianchiIndex = Constants.earthlyBranchToPalaceIndex(xcBranch)

        val (guBranch, guaBranch) = when (yearZhi) {
            "寅", "卯", "辰" -> Pair("巳", "丑")
            "巳", "午", "未" -> Pair("申", "辰")
            "申", "酉", "戌" -> Pair("亥", "未")
            "亥", "子", "丑" -> Pair("寅", "戌")
            else -> Pair("巳", "丑")
        }
        val guchenIndex = Constants.earthlyBranchToPalaceIndex(guBranch)
        val guasuIndex = Constants.earthlyBranchToPalaceIndex(guaBranch)

        val tiancaiIndex = Constants.fixIndex(soulIndex + branchIdx)
        val tianshouIndex = Constants.fixIndex(bodyIndex + branchIdx)

        val tianchuBranches = listOf("巳", "午", "子", "巳", "午", "申", "寅", "午", "酉", "亥")
        val tianchuIndex = Constants.earthlyBranchToPalaceIndex(tianchuBranches[stemIdx])

        val posuiBranches = listOf("巳", "丑", "酉")
        val posuiIndex = Constants.earthlyBranchToPalaceIndex(posuiBranches[branchIdx % 3])

        val feilianBranches = listOf("申", "酉", "戌", "巳", "午", "未", "寅", "卯", "辰", "亥", "子", "丑")
        val feilianIndex = Constants.earthlyBranchToPalaceIndex(feilianBranches[branchIdx])

        val longchiIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("辰") + branchIdx)
        val fenggeIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("戌") - branchIdx)
        val tiankuIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("午") - branchIdx)
        val tianxuIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("午") + branchIdx)

        val tianguanBranches = listOf("未", "辰", "巳", "寅", "卯", "酉", "亥", "酉", "戌", "午")
        val tianguanIndex = Constants.earthlyBranchToPalaceIndex(tianguanBranches[stemIdx])

        val tianfuBranches = listOf("酉", "申", "子", "亥", "卯", "寅", "午", "巳", "午", "巳")
        val tianfuIndex = Constants.earthlyBranchToPalaceIndex(tianfuBranches[stemIdx])

        val tiandeIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("酉") + branchIdx)
        val yuedeIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex("巳") + branchIdx)
        val tiankongIndex = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex(yearZhi) + 1)

        val jieluBranches = listOf("申", "午", "辰", "寅", "子")
        val jieluIndex = Constants.earthlyBranchToPalaceIndex(jieluBranches[stemIdx % 5])

        val kongwangBranches = listOf("酉", "未", "巳", "卯", "丑")
        val kongwangIndex = Constants.earthlyBranchToPalaceIndex(kongwangBranches[stemIdx % 5])

        var xkIdx = Constants.fixIndex(Constants.earthlyBranchToPalaceIndex(yearZhi) + 9 - stemIdx + 1)
        val yinyang = branchIdx % 2
        if (yinyang != xkIdx % 2) {
            xkIdx = Constants.fixIndex(xkIdx + 1)
        }

        // 天使居疾厄，天伤居仆役
        val friendsPalaceOffset = Constants.PALACE_NAMES.indexOf("仆役")
        val healthPalaceOffset = Constants.PALACE_NAMES.indexOf("疾厄")
        val tianshangIndex = Constants.fixIndex(friendsPalaceOffset + soulIndex)
        val tianshiIndex = Constants.fixIndex(healthPalaceOffset + soulIndex)

        return YearlyStars(
            huagaiIndex = huagaiIndex,
            xianchiIndex = xianchiIndex,
            guchenIndex = guchenIndex,
            guasuIndex = guasuIndex,
            tiancaiIndex = tiancaiIndex,
            tianshouIndex = tianshouIndex,
            tianchuIndex = tianchuIndex,
            posuiIndex = posuiIndex,
            feilianIndex = feilianIndex,
            longchiIndex = longchiIndex,
            fenggeIndex = fenggeIndex,
            tiankuIndex = tiankuIndex,
            tianxuIndex = tianxuIndex,
            tianguanIndex = tianguanIndex,
            tianfuIndex = tianfuIndex,
            tiandeIndex = tiandeIndex,
            yuedeIndex = yuedeIndex,
            tiankongIndex = tiankongIndex,
            jieluIndex = jieluIndex,
            kongwangIndex = kongwangIndex,
            xunkongIndex = xkIdx,
            tianshangIndex = tianshangIndex,
            tianshiIndex = tianshiIndex
        )
    }

    /**
     * 长生十二神
     * 阳男阴女顺行，阴男阳女逆行
     */
    fun getChangsheng12(
        fiveElementsClass: FiveElementsClass,
        gender: Gender,
        yearZhi: String
    ): List<String> {
        val startIdx = when (fiveElementsClass) {
            FiveElementsClass.WATER_2ND, FiveElementsClass.EARTH_5TH -> Constants.earthlyBranchToPalaceIndex("申")
            FiveElementsClass.WOOD_3RD -> Constants.earthlyBranchToPalaceIndex("亥")
            FiveElementsClass.METAL_4TH -> Constants.earthlyBranchToPalaceIndex("巳")
            FiveElementsClass.FIRE_6TH -> Constants.earthlyBranchToPalaceIndex("寅")
        }

        val branchYinYang = if (Constants.EARTHLY_BRANCHES.indexOf(yearZhi) % 2 == 0) "阳" else "阴"
        val isForward = (gender.yinYang == branchYinYang)

        val stars = listOf("长生", "沐浴", "冠带", "临官", "帝旺", "衰", "病", "死", "墓", "绝", "胎", "养")
        val result = MutableList(12) { "" }

        for (i in stars.indices) {
            val idx = if (isForward) Constants.fixIndex(startIdx + i) else Constants.fixIndex(startIdx - i)
            result[idx] = stars[i]
        }
        return result
    }

    /**
     * 博士十二神
     * 从禄存起，阳男阴女顺行，阴男阳女逆行
     */
    fun getBoshi12(yearGan: String, yearZhi: String, gender: Gender): List<String> {
        val (luIndex) = getLuYangTuoMaIndex(yearGan, yearZhi)
        val branchYinYang = if (Constants.EARTHLY_BRANCHES.indexOf(yearZhi) % 2 == 0) "阳" else "阴"
        val isForward = (gender.yinYang == branchYinYang)

        val stars = listOf("博士", "力士", "青龙", "小耗", "将军", "奏书", "飞廉", "喜神", "病符", "大耗", "伏兵", "官府")
        val result = MutableList(12) { "" }

        for (i in stars.indices) {
            val idx = if (isForward) Constants.fixIndex(luIndex + i) else Constants.fixIndex(luIndex - i)
            result[idx] = stars[i]
        }
        return result
    }

    /**
     * 岁前十二神与将前十二神
     */
    fun getYearly12(yearZhi: String): Pair<List<String>, List<String>> {
        val suiqianStars = listOf("岁建", "晦气", "丧门", "贯索", "官符", "小耗", "大耗", "龙德", "白虎", "天德", "吊客", "病符")
        val suiqianResult = MutableList(12) { "" }
        val branchPalaceIdx = Constants.earthlyBranchToPalaceIndex(yearZhi)

        for (i in suiqianStars.indices) {
            val idx = Constants.fixIndex(branchPalaceIdx + i)
            suiqianResult[idx] = suiqianStars[i]
        }

        val jqStartBranch = when (yearZhi) {
            "寅", "午", "戌" -> "午"
            "申", "子", "辰" -> "子"
            "巳", "酉", "丑" -> "酉"
            "亥", "卯", "未" -> "卯"
            else -> "午"
        }
        val jqStartIdx = Constants.earthlyBranchToPalaceIndex(jqStartBranch)
        val jiangqianStars = listOf("将星", "攀鞍", "岁驿", "息神", "华盖", "劫煞", "灾煞", "天煞", "指背", "咸池", "月煞", "亡神")
        val jiangqianResult = MutableList(12) { "" }

        for (i in jiangqianStars.indices) {
            val idx = Constants.fixIndex(jqStartIdx + i)
            jiangqianResult[idx] = jiangqianStars[i]
        }

        return Pair(suiqianResult, jiangqianResult)
    }

    /**
     * 流昌流曲（按天干）
     */
    data class FlowChangQu(val changIndex: Int, val quIndex: Int)

    fun getFlowChangQuIndex(heavenlyStem: String): FlowChangQu {
        val (cBranch, qBranch) = when (heavenlyStem) {
            "甲" -> Pair("巳", "酉")
            "乙" -> Pair("午", "申")
            "丙", "戊" -> Pair("申", "午")
            "丁", "己" -> Pair("酉", "巳")
            "庚" -> Pair("亥", "卯")
            "辛" -> Pair("子", "寅")
            "壬" -> Pair("寅", "子")
            "癸" -> Pair("卯", "亥")
            else -> Pair("巳", "酉")
        }
        return FlowChangQu(
            Constants.earthlyBranchToPalaceIndex(cBranch),
            Constants.earthlyBranchToPalaceIndex(qBranch)
        )
    }

    /**
     * 获取运限流耀 (魁钺昌曲禄羊陀马鸾喜)
     */
    fun getHoroscopeStars(
        heavenlyStem: String,
        earthlyBranch: String,
        scope: HoroscopeScope
    ): List<List<Star>> {
        val prefix = when (scope) {
            HoroscopeScope.DECADAL -> "运"
            HoroscopeScope.YEARLY -> "流"
            HoroscopeScope.MONTHLY -> "月"
            HoroscopeScope.DAILY -> "日"
            HoroscopeScope.HOURLY -> "时"
            else -> ""
        }

        val result = List(12) { mutableListOf<Star>() }

        val (kuiIdx, yueIdx) = getKuiYueIndex(heavenlyStem)
        val (changIdx, quIdx) = getFlowChangQuIndex(heavenlyStem)
        val (luIdx, yangIdx, tuoIdx, maIdx) = getLuYangTuoMaIndex(heavenlyStem, earthlyBranch)
        val (luanIdx, xiIdx) = getLuanXiIndex(earthlyBranch)

        result[kuiIdx].add(Star("${prefix}魁", StarType.HOROSCOPE, scope))
        result[yueIdx].add(Star("${prefix}钺", StarType.HOROSCOPE, scope))
        result[changIdx].add(Star("${prefix}昌", StarType.HOROSCOPE, scope))
        result[quIdx].add(Star("${prefix}曲", StarType.HOROSCOPE, scope))
        result[luIdx].add(Star("${prefix}禄", StarType.HOROSCOPE, scope))
        result[yangIdx].add(Star("${prefix}羊", StarType.HOROSCOPE, scope))
        result[tuoIdx].add(Star("${prefix}陀", StarType.HOROSCOPE, scope))
        result[maIdx].add(Star("${prefix}马", StarType.HOROSCOPE, scope))
        result[luanIdx].add(Star("${prefix}鸾", StarType.HOROSCOPE, scope))
        result[xiIdx].add(Star("${prefix}喜", StarType.HOROSCOPE, scope))

        return result
    }
}
