package io.github.silascui.ktIztro.engine

import io.github.silascui.ktIztro.calendar.LunarCalendarHelper
import io.github.silascui.ktIztro.model.AgeHoroscope
import io.github.silascui.ktIztro.model.Astrolabe
import io.github.silascui.ktIztro.model.Constants
import io.github.silascui.ktIztro.model.Horoscope
import io.github.silascui.ktIztro.model.HoroscopeScope
import io.github.silascui.ktIztro.model.ScopeHoroscope

object HoroscopeCalculator {

    fun calculate(
        astrolabe: Astrolabe,
        targetDateStr: String,
        targetTimeIndex: Int? = null,
        horoscopeDivide: String = "normal"
    ): Horoscope {
        val tIndex = targetTimeIndex ?: 0
        val targetFourPillars = LunarCalendarHelper.getFourPillarsBySolar(
            targetDateStr,
            tIndex,
            yearDivide = horoscopeDivide,
            monthDivide = horoscopeDivide
        )

        val birthdayLunar = LunarCalendarHelper.solarToLunar(astrolabe.solarDate)
        val targetLunar = LunarCalendarHelper.solarToLunar(targetDateStr)

        val nominalAge = targetLunar.lunarYear - birthdayLunar.lunarYear + 1

        // 1. 大限
        var decadalIndex = -1
        var decadalStem = "甲"
        var decadalZhi = "寅"
        var isChildhood = false

        for (palace in astrolabe.palaces) {
            val (start, end) = palace.decadal.range
            if (nominalAge in start..end) {
                decadalIndex = palace.index
                decadalStem = palace.decadal.heavenlyStem
                decadalZhi = palace.decadal.earthlyBranch
                break
            }
        }

        if (decadalIndex < 0) {
            // 童限：一命二财三疾厄 四岁夫妻五福德 六岁事业为童限
            val childPalaceNames = listOf("命宫", "财帛", "疾厄", "夫妻", "福德", "官禄")
            val pName = childPalaceNames.getOrElse((nominalAge - 1).coerceAtLeast(0)) { "命宫" }
            val targetP = astrolabe.palace(pName) ?: astrolabe.palaces[0]
            isChildhood = true
            decadalIndex = targetP.index
            decadalStem = targetP.heavenlyStem
            decadalZhi = targetP.earthlyBranch
        }

        // 2. 小限
        var ageIndex = 0
        var ageStem = "甲"
        var ageZhi = "寅"
        for (palace in astrolabe.palaces) {
            if (palace.ages.contains(nominalAge)) {
                ageIndex = palace.index
                ageStem = palace.heavenlyStem
                ageZhi = palace.earthlyBranch
                break
            }
        }

        // 3. 流年
        val yearlyIndex = Constants.earthlyBranchToPalaceIndex(targetFourPillars.yearZhi)
        val yearlyStem = targetFourPillars.yearGan
        val yearlyZhi = targetFourPillars.yearZhi

        // 4. 流月
        val birthLeap = if (birthdayLunar.isLeap && birthdayLunar.lunarDay > 15) 1 else 0
        val targetLeap = if (targetLunar.isLeap && targetLunar.lunarDay > 15) 1 else 0
        val birthHourZhi = astrolabe.fourPillars.timeZhi
        val birthHourZhiIdx = Constants.EARTHLY_BRANCHES.indexOf(birthHourZhi)

        val monthlyIndex = Constants.fixIndex(
            yearlyIndex - (birthdayLunar.lunarMonth + birthLeap) + birthHourZhiIdx + (targetLunar.lunarMonth + targetLeap)
        )
        val monthlyStem = targetFourPillars.monthGan
        val monthlyZhi = targetFourPillars.monthZhi

        // 5. 流日
        val dailyIndex = Constants.fixIndex(monthlyIndex + targetLunar.lunarDay - 1)
        val dailyStem = targetFourPillars.dayGan
        val dailyZhi = targetFourPillars.dayZhi

        // 6. 流时
        val targetHourZhiIdx = Constants.EARTHLY_BRANCHES.indexOf(targetFourPillars.timeZhi)
        val hourlyIndex = Constants.fixIndex(dailyIndex + targetHourZhiIdx)
        val hourlyStem = targetFourPillars.timeGan
        val hourlyZhi = targetFourPillars.timeZhi

        // 运限四化与流耀
        val decadalMutagens = Constants.HEAVENLY_STEM_MUTAGENS[decadalStem] ?: emptyList()
        val decadalStars = StarLocation.getHoroscopeStars(decadalStem, decadalZhi, HoroscopeScope.DECADAL)

        val ageMutagens = Constants.HEAVENLY_STEM_MUTAGENS[ageStem] ?: emptyList()

        val yearlyMutagens = Constants.HEAVENLY_STEM_MUTAGENS[yearlyStem] ?: emptyList()
        val yearlyStars = StarLocation.getHoroscopeStars(yearlyStem, yearlyZhi, HoroscopeScope.YEARLY)
        val yearlyDecStar = StarLocation.getYearly12(yearlyZhi)

        val monthlyMutagens = Constants.HEAVENLY_STEM_MUTAGENS[monthlyStem] ?: emptyList()
        val monthlyStars = StarLocation.getHoroscopeStars(monthlyStem, monthlyZhi, HoroscopeScope.MONTHLY)

        val dailyMutagens = Constants.HEAVENLY_STEM_MUTAGENS[dailyStem] ?: emptyList()
        val dailyStars = StarLocation.getHoroscopeStars(dailyStem, dailyZhi, HoroscopeScope.DAILY)

        val hourlyMutagens = Constants.HEAVENLY_STEM_MUTAGENS[hourlyStem] ?: emptyList()
        val hourlyStars = StarLocation.getHoroscopeStars(hourlyStem, hourlyZhi, HoroscopeScope.HOURLY)

        return Horoscope(
            solarDate = targetDateStr,
            lunarDate = targetLunar.chineseString,
            decadal = ScopeHoroscope(
                index = decadalIndex,
                name = if (isChildhood) "童限" else "大限",
                heavenlyStem = decadalStem,
                earthlyBranch = decadalZhi,
                palaceNames = PalaceCalculator.getPalaceNames(decadalIndex),
                mutagen = decadalMutagens,
                stars = decadalStars
            ),
            age = AgeHoroscope(
                index = ageIndex,
                nominalAge = nominalAge,
                name = "小限",
                heavenlyStem = ageStem,
                earthlyBranch = ageZhi,
                palaceNames = PalaceCalculator.getPalaceNames(ageIndex),
                mutagen = ageMutagens
            ),
            yearly = ScopeHoroscope(
                index = yearlyIndex,
                name = "流年",
                heavenlyStem = yearlyStem,
                earthlyBranch = yearlyZhi,
                palaceNames = PalaceCalculator.getPalaceNames(yearlyIndex),
                mutagen = yearlyMutagens,
                stars = yearlyStars,
                yearlyDecStar = yearlyDecStar
            ),
            monthly = ScopeHoroscope(
                index = monthlyIndex,
                name = "流月",
                heavenlyStem = monthlyStem,
                earthlyBranch = monthlyZhi,
                palaceNames = PalaceCalculator.getPalaceNames(monthlyIndex),
                mutagen = monthlyMutagens,
                stars = monthlyStars
            ),
            daily = ScopeHoroscope(
                index = dailyIndex,
                name = "流日",
                heavenlyStem = dailyStem,
                earthlyBranch = dailyZhi,
                palaceNames = PalaceCalculator.getPalaceNames(dailyIndex),
                mutagen = dailyMutagens,
                stars = dailyStars
            ),
            hourly = ScopeHoroscope(
                index = hourlyIndex,
                name = "流时",
                heavenlyStem = hourlyStem,
                earthlyBranch = hourlyZhi,
                palaceNames = PalaceCalculator.getPalaceNames(hourlyIndex),
                mutagen = hourlyMutagens,
                stars = hourlyStars
            )
        )
    }
}
