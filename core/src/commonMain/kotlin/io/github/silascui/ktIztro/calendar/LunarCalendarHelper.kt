package io.github.silascui.ktIztro.calendar

import com.tyme.lunar.LunarDay
import com.tyme.lunar.LunarMonth
import com.tyme.lunar.LunarYear
import com.tyme.solar.SolarDay
import com.tyme.solar.SolarTime
import io.github.silascui.ktIztro.model.Constants
import io.github.silascui.ktIztro.model.FourPillars

/**
 * 农历与公历转换辅助类 (基于纯 Kotlin Multiplatform 的 tyme4kt 引擎)
 */
data class LunarDateInfo(
    val lunarYear: Int,
    val lunarMonth: Int,
    val lunarDay: Int,
    val isLeap: Boolean,
    val chineseString: String
)

object LunarCalendarHelper {

    private val DIGITS = listOf("〇", "一", "二", "三", "四", "五", "六", "七", "八", "九")

    /**
     * 将年份数字转换为中文形式 (如 2000 -> 二〇〇〇年)
     */
    fun toChineseYear(year: Int): String {
        return year.toString().map { DIGITS[it - '0'] }.joinToString("") + "年"
    }

    /**
     * 解析 YYYY-MM-DD 或 YYYY-M-D 字符串为年月日
     */
    fun parseDate(dateStr: String): Triple<Int, Int, Int> {
        val clean = dateStr.trim().replace('/', '-').replace('.', '-')
        val parts = clean.split('-')
        val y = parts[0].toInt()
        val m = parts[1].toInt()
        val d = parts[2].split(" ")[0].toInt()
        return Triple(y, m, d)
    }

    /**
     * 公历转农历
     */
    fun solarToLunar(solarDateStr: String): LunarDateInfo {
        val (year, month, day) = parseDate(solarDateStr)
        val solar = SolarDay.fromYmd(year, month, day)
        val lunar = solar.getLunarDay()
        val lMonthObj = lunar.getLunarMonth()
        val isLeap = lMonthObj.isLeap()
        val lMonth = kotlin.math.abs(lMonthObj.getMonthWithLeap())
        val lDay = lunar.day
        val lYear = lunar.year

        val yearStr = toChineseYear(lYear)
        val monthStr = (if (isLeap) "闰" else "") + lMonthObj.getName()
        val dayStr = lunar.getName()
        val chineseString = "$yearStr$monthStr$dayStr"

        return LunarDateInfo(
            lunarYear = lYear,
            lunarMonth = lMonth,
            lunarDay = lDay,
            isLeap = isLeap,
            chineseString = chineseString
        )
    }

    /**
     * 获取农历年份的闰月月份 (0表示无闰月，1..12表示闰几月)
     */
    fun getLeapMonthOfYear(lunarYear: Int): Int {
        return try {
            val ly = LunarYear.fromYear(lunarYear)
            ly.getLeapMonth()
        } catch (e: Exception) {
            0
        }
    }

    /**
     * 农历转公历 YYYY-MM-DD
     */
    fun lunarToSolar(lunarYear: Int, lunarMonth: Int, lunarDay: Int, isLeapMonth: Boolean = false): String {
        return try {
            val ly = LunarYear.fromYear(lunarYear)
            val leapMonth = ly.getLeapMonth()
            val monthToUse = if (isLeapMonth && leapMonth == lunarMonth) -lunarMonth else lunarMonth
            val lunar = LunarDay.fromYmd(lunarYear, monthToUse, lunarDay)
            val solar = lunar.getSolarDay()
            "${solar.year}-${solar.month}-${solar.day}"
        } catch (e: Exception) {
            "$lunarYear-$lunarMonth-$lunarDay"
        }
    }

    /**
     * 获取指定公历日期对应的农历月份天数 (29 或 30)
     */
    fun getTotalDaysOfLunarMonth(solarDateStr: String): Int {
        val (year, month, day) = parseDate(solarDateStr)
        val solar = SolarDay.fromYmd(year, month, day)
        val monthObj = solar.getLunarDay().getLunarMonth()
        return monthObj.getDayCount()
    }

    /**
     * 获取公历日期的生辰干支八字
     *
     * @param solarDateStr 公历日期
     * @param timeIndex 时辰索引 0..12
     * @param yearDivide "normal" (正月初一换年) 或 "exact" (立春换年)
     * @param monthDivide "normal" (初一换月) 或 "exact" (节气换月)
     */
    fun getFourPillarsBySolar(
        solarDateStr: String,
        timeIndex: Int,
        yearDivide: String = "normal",
        monthDivide: String = "exact"
    ): FourPillars {
        val (year, month, day) = parseDate(solarDateStr)
        val hour = if (timeIndex == 0) 0 else if (timeIndex == 12) 23 else (timeIndex * 2 - 1).coerceAtLeast(0)
        val solarTime = SolarTime.fromYmdHms(year, month, day, hour, 30, 0)
        val lunarHour = solarTime.getLunarHour()
        val eightChar = lunarHour.getEightChar()

        val yearSixty = eightChar.getYear()
        val monthSixty = eightChar.getMonth()
        val daySixty = eightChar.getDay()
        val timeSixty = eightChar.getHour()

        val yearGan: String
        val yearZhi: String
        if (yearDivide == "normal") {
            // 正月初一换年：取农历年的干支
            val ly = lunarHour.getLunarDay().getLunarMonth().getLunarYear()
            val sixty = ly.getSixtyCycle()
            yearGan = sixty.getHeavenStem().getName()
            yearZhi = sixty.getEarthBranch().getName()
        } else {
            // 立春换年（八字标准）
            yearGan = yearSixty.getHeavenStem().getName()
            yearZhi = yearSixty.getEarthBranch().getName()
        }

        val monthGan: String
        val monthZhi: String
        if (monthDivide == "exact") {
            monthGan = monthSixty.getHeavenStem().getName()
            monthZhi = monthSixty.getEarthBranch().getName()
        } else {
            // 初一换月，以年干五虎遁起月
            val lDay = lunarHour.getLunarDay()
            val lMonthObj = lDay.getLunarMonth()
            val isLeap = lMonthObj.isLeap()
            val fixLeap = if (isLeap && lDay.day > 15) 1 else 0
            val absMonth = kotlin.math.abs(lMonthObj.getMonthWithLeap())
            val tigerHead = Constants.TIGER_RULE[yearGan] ?: "丙"
            val tigerIdx = Constants.HEAVENLY_STEMS.indexOf(tigerHead)
            val mGanIdx = Constants.fixIndex(tigerIdx + absMonth - 1 + fixLeap, 10)
            monthGan = Constants.HEAVENLY_STEMS[mGanIdx]
            val monthlyBranches = listOf("寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "子", "丑")
            val mZhiIdx = Constants.fixIndex(absMonth - 1 + fixLeap, 12)
            monthZhi = monthlyBranches[mZhiIdx]
        }

        val dayGan = daySixty.getHeavenStem().getName()
        val dayZhi = daySixty.getEarthBranch().getName()
        val timeGan = timeSixty.getHeavenStem().getName()
        val timeZhi = timeSixty.getEarthBranch().getName()

        return FourPillars(
            yearGan = yearGan,
            yearZhi = yearZhi,
            monthGan = monthGan,
            monthZhi = monthZhi,
            dayGan = dayGan,
            dayZhi = dayZhi,
            timeGan = timeGan,
            timeZhi = timeZhi
        )
    }

    /**
     * 获取公历日期的星座 (如 "狮子座")
     */
    fun getSign(solarDateStr: String): String {
        val (year, month, day) = parseDate(solarDateStr)
        val solar = SolarDay.fromYmd(year, month, day)
        val name = solar.getConstellation().getName()
        return if (name.endsWith("座")) name else "${name}座"
    }

    /**
     * 获取生肖 (按年支)
     */
    fun getZodiac(yearZhi: String): String {
        val idx = Constants.EARTHLY_BRANCHES.indexOf(yearZhi)
        val animals = listOf("鼠", "牛", "虎", "兔", "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪")
        return if (idx >= 0) animals[idx] else ""
    }

    /**
     * 调整农历月份的索引
     *
     * 正月建寅，fixLeap为是否调整闰月情况：
     * 若调整闰月，则闰月前15天按上月算，后面天数按下月算
     */
    fun fixLunarMonthIndex(solarDateStr: String, timeIndex: Int, fixLeap: Boolean = true): Int {
        val (lYear, lMonth, lDay, isLeap) = solarToLunar(solarDateStr)
        val firstIndex = Constants.EARTHLY_BRANCHES.indexOf("寅") // 2
        val needToAdd = isLeap && fixLeap && lDay > 15 && timeIndex != 12
        return Constants.fixIndex(lMonth + 1 - firstIndex + (if (needToAdd) 1 else 0))
    }

    /**
     * 获取农历日期【天】的索引，晚子时将加一天
     */
    fun fixLunarDayIndex(lunarDay: Int, timeIndex: Int): Int {
        return if (timeIndex >= 12) lunarDay else lunarDay - 1
    }
}
