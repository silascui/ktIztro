package io.github.silascui.ktIztro.engine

import io.github.silascui.ktIztro.calendar.LunarCalendarHelper
import io.github.silascui.ktIztro.model.Astrolabe
import io.github.silascui.ktIztro.model.Gender
import io.github.silascui.ktIztro.model.Horoscope

object IztroEngine {

    /**
     * 通过阳历公历排盘
     *
     * @param solarDateStr 公历日期，格式 "YYYY-MM-DD" 或 "YYYY-M-D"
     * @param timeIndex 出生时辰序号 (0..12，0为早子时，1为丑时，...，12为晚子时)
     * @param gender 性别 (Gender.MALE 或 Gender.FEMALE)
     * @param fixLeap 是否调整农历闰月（默认 true）
     * @param yearDivide 年分割规则，默认 "normal" (正月初一换年)
     */
    fun bySolar(
        solarDateStr: String,
        timeIndex: Int,
        gender: Gender,
        fixLeap: Boolean = true,
        yearDivide: String = "normal"
    ): Astrolabe {
        return AstrolabeCalculator.calculate(
            solarDateStr = solarDateStr,
            timeIndex = timeIndex,
            gender = gender,
            fixLeap = fixLeap,
            yearDivide = yearDivide
        )
    }

    /**
     * 通过农历排盘
     *
     * @param lunarYear 农历年
     * @param lunarMonth 农历月 (1..12)
     * @param lunarDay 农历日 (1..30)
     * @param timeIndex 出生时辰序号 (0..12)
     * @param gender 性别
     * @param isLeapMonth 是否为闰月
     * @param fixLeap 是否调整农历闰月
     */
    fun byLunar(
        lunarYear: Int,
        lunarMonth: Int,
        lunarDay: Int,
        timeIndex: Int,
        gender: Gender,
        isLeapMonth: Boolean = false,
        fixLeap: Boolean = true
    ): Astrolabe {
        val solarDateStr = LunarCalendarHelper.lunarToSolar(lunarYear, lunarMonth, lunarDay, isLeapMonth)
        return bySolar(
            solarDateStr = solarDateStr,
            timeIndex = timeIndex,
            gender = gender,
            fixLeap = fixLeap
        )
    }

    /**
     * 获取指定日期的运限（大限、流年、流月、流日、流时、小限）
     */
    fun horoscope(
        astrolabe: Astrolabe,
        targetDateStr: String,
        targetTimeIndex: Int? = null
    ): Horoscope {
        return HoroscopeCalculator.calculate(astrolabe, targetDateStr, targetTimeIndex)
    }
}
