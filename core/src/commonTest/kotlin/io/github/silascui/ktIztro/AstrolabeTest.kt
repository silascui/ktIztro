package io.github.silascui.ktIztro

import io.github.silascui.ktIztro.engine.IztroEngine
import io.github.silascui.ktIztro.model.Gender
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AstrolabeTest {

    @Test
    fun testBySolar20000816() {
        val result = IztroEngine.bySolar(
            solarDateStr = "2000-8-16",
            timeIndex = 2,
            gender = Gender.FEMALE,
            fixLeap = true
        )

        assertEquals("2000-8-16", result.solarDate)
        assertEquals("二〇〇〇年七月十七", result.lunarDate)
        assertEquals("庚辰 甲申 丙午 庚寅", result.chineseDate)
        assertEquals("寅时", result.time)
        assertEquals("狮子座", result.sign)
        assertEquals("龙", result.zodiac)
        assertEquals("午", result.earthlyBranchOfSoulPalace)
        assertEquals("戌", result.earthlyBranchOfBodyPalace)
        assertEquals("破军", result.soul)
        assertEquals("文昌", result.body)
        assertEquals("木三局", result.fiveElementsClass)

        // 十二宫完整性
        assertEquals(12, result.palaces.size)
        val mingGong = result.palace("命宫")
        assertNotNull(mingGong)
        assertEquals("午", mingGong!!.earthlyBranch)
        assertEquals("壬", mingGong.heavenlyStem)

        // 父母宫
        val parentsPalace = result.palace("父母")
        assertNotNull(parentsPalace)
        assertEquals("未", parentsPalace!!.earthlyBranch)

        // 三方四正
        val sanFang = result.getSanFangSiZheng(mingGong.index)
        assertEquals("命宫", sanFang.self.name)
        assertEquals("迁移", sanFang.opposite.name)
        assertEquals("官禄", sanFang.career.name)
        assertEquals("财帛", sanFang.wealth.name)

        // 运限测试
        val horoscope = IztroEngine.horoscope(result, "2023-8-19", 2)
        assertEquals(2, horoscope.decadal.index)
        assertEquals("庚", horoscope.decadal.heavenlyStem)
        assertEquals("辰", horoscope.decadal.earthlyBranch)
        assertEquals(listOf("太阳", "武曲", "太阴", "天同"), horoscope.decadal.mutagen)

        assertEquals(9, horoscope.age.index)
        assertEquals(24, horoscope.age.nominalAge)

        assertEquals(1, horoscope.yearly.index)
        assertEquals("癸", horoscope.yearly.heavenlyStem)
        assertEquals("卯", horoscope.yearly.earthlyBranch)
    }

    @Test
    fun testPalacesOrder() {
        val result = IztroEngine.bySolar("1995-10-15", 4, Gender.MALE)
        assertEquals(12, result.palaces.size)
        assertEquals("寅", result.palaces[0].earthlyBranch)
        assertEquals("卯", result.palaces[1].earthlyBranch)
        assertEquals("辰", result.palaces[2].earthlyBranch)
        assertEquals("巳", result.palaces[3].earthlyBranch)
        assertEquals("午", result.palaces[4].earthlyBranch)
        assertEquals("未", result.palaces[5].earthlyBranch)
        assertEquals("申", result.palaces[6].earthlyBranch)
        assertEquals("酉", result.palaces[7].earthlyBranch)
        assertEquals("戌", result.palaces[8].earthlyBranch)
        assertEquals("亥", result.palaces[9].earthlyBranch)
        assertEquals("子", result.palaces[10].earthlyBranch)
        assertEquals("丑", result.palaces[11].earthlyBranch)
    }

    @Test
    fun testByLunar() {
        // 2000-8-16 对应 农历庚辰年七月十七
        val lunarChart = IztroEngine.byLunar(
            lunarYear = 2000,
            lunarMonth = 7,
            lunarDay = 17,
            timeIndex = 2,
            gender = Gender.FEMALE
        )
        assertEquals("2000-8-16", lunarChart.solarDate)
        assertEquals("庚辰 甲申 丙午 庚寅", lunarChart.chineseDate)
        assertEquals("午", lunarChart.earthlyBranchOfSoulPalace)

        // 闰月测试：2001年闰四月，2020年闰四月，2023年闰二月
        assertEquals(4, io.github.silascui.ktIztro.calendar.LunarCalendarHelper.getLeapMonthOfYear(2001))
        assertEquals(4, io.github.silascui.ktIztro.calendar.LunarCalendarHelper.getLeapMonthOfYear(2020))
        assertEquals(2, io.github.silascui.ktIztro.calendar.LunarCalendarHelper.getLeapMonthOfYear(2023))
        assertEquals(0, io.github.silascui.ktIztro.calendar.LunarCalendarHelper.getLeapMonthOfYear(2021))

        // 农历与公历双向转换测试
        val solarDate = io.github.silascui.ktIztro.calendar.LunarCalendarHelper.lunarToSolar(2000, 7, 17, false)
        assertEquals("2000-8-16", solarDate)

        val lunarInfo = io.github.silascui.ktIztro.calendar.LunarCalendarHelper.solarToLunar("2000-8-16")
        assertEquals(2000, lunarInfo.lunarYear)
        assertEquals(7, lunarInfo.lunarMonth)
        assertEquals(17, lunarInfo.lunarDay)
        assertEquals(false, lunarInfo.isLeap)
    }
}
