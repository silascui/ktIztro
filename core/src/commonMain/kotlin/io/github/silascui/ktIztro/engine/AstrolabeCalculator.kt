package io.github.silascui.ktIztro.engine

import io.github.silascui.ktIztro.calendar.LunarCalendarHelper
import io.github.silascui.ktIztro.model.Astrolabe
import io.github.silascui.ktIztro.model.Constants
import io.github.silascui.ktIztro.model.EarthlyBranch
import io.github.silascui.ktIztro.model.Gender
import io.github.silascui.ktIztro.model.HoroscopeScope
import io.github.silascui.ktIztro.model.Palace
import io.github.silascui.ktIztro.model.Star
import io.github.silascui.ktIztro.model.StarType

object AstrolabeCalculator {

    fun calculate(
        solarDateStr: String,
        timeIndex: Int,
        gender: Gender,
        fixLeap: Boolean = true,
        yearDivide: String = "normal"
    ): Astrolabe {
        val fourPillars = LunarCalendarHelper.getFourPillarsBySolar(solarDateStr, timeIndex, yearDivide)
        val lunarInfo = LunarCalendarHelper.solarToLunar(solarDateStr)
        val sign = LunarCalendarHelper.getSign(solarDateStr)
        val zodiac = LunarCalendarHelper.getZodiac(fourPillars.yearZhi)

        val soulAndBody = PalaceCalculator.getSoulAndBody(solarDateStr, timeIndex, fixLeap, yearDivide)
        val soulIndex = soulAndBody.soulIndex
        val bodyIndex = soulAndBody.bodyIndex
        val heavenlyStemOfSoul = soulAndBody.heavenlyStemOfSoul
        val earthlyBranchOfSoul = soulAndBody.earthlyBranchOfSoul

        val fiveElementsClass = PalaceCalculator.getFiveElementsClass(heavenlyStemOfSoul, earthlyBranchOfSoul)
        val palaceNames = PalaceCalculator.getPalaceNames(soulIndex)

        val (decadals, ages) = PalaceCalculator.getDecadalsAndAges(
            solarDateStr = solarDateStr,
            timeIndex = timeIndex,
            gender = gender,
            soulIndex = soulIndex,
            fiveElementsClass = fiveElementsClass,
            yearZhi = fourPillars.yearZhi,
            yearGan = fourPillars.yearGan
        )

        // 1. 安十四主星
        val (ziweiIndex, tianfuIndex) = StarLocation.getZiweiAndTianfuIndex(solarDateStr, timeIndex, fiveElementsClass)
        val yearMutagens = Constants.HEAVENLY_STEM_MUTAGENS[fourPillars.yearGan] ?: emptyList()

        fun getMutagen(starName: String): String {
            val idx = yearMutagens.indexOf(starName)
            return when (idx) {
                0 -> "禄"
                1 -> "权"
                2 -> "科"
                3 -> "忌"
                else -> ""
            }
        }

        val majorStarsList = List(12) { mutableListOf<Star>() }
        val ziweiGroup = listOf("紫微", "天机", "", "太阳", "武曲", "天同", "", "", "廉贞")
        val tianfuGroup = listOf("天府", "太阴", "贪狼", "巨门", "天相", "天梁", "七杀", "", "", "", "破军")

        ziweiGroup.forEachIndexed { i, name ->
            if (name.isNotEmpty()) {
                val pIdx = Constants.fixIndex(ziweiIndex - i)
                majorStarsList[pIdx].add(
                    Star(
                        name = name,
                        type = StarType.MAJOR,
                        scope = HoroscopeScope.ORIGIN,
                        brightness = StarBrightness.getBrightness(name, pIdx),
                        mutagen = getMutagen(name)
                    )
                )
            }
        }

        tianfuGroup.forEachIndexed { i, name ->
            if (name.isNotEmpty()) {
                val pIdx = Constants.fixIndex(tianfuIndex + i)
                majorStarsList[pIdx].add(
                    Star(
                        name = name,
                        type = StarType.MAJOR,
                        scope = HoroscopeScope.ORIGIN,
                        brightness = StarBrightness.getBrightness(name, pIdx),
                        mutagen = getMutagen(name)
                    )
                )
            }
        }

        // 2. 安十四辅星
        val minorStarsList = List(12) { mutableListOf<Star>() }
        val monthIndex = LunarCalendarHelper.fixLunarMonthIndex(solarDateStr, timeIndex, fixLeap)
        val (zuoIdx, youIdx) = StarLocation.getZuoYouIndex(monthIndex + 1)
        val (changIdx, quIdx) = StarLocation.getChangQuIndex(timeIndex)
        val (kuiIdx, yueIdx) = StarLocation.getKuiYueIndex(fourPillars.yearGan)
        val (huoIdx, lingIdx) = StarLocation.getHuoLingIndex(fourPillars.yearZhi, timeIndex)
        val timely = StarLocation.getTimelyStarIndex(timeIndex)
        val luYangTuoMa = StarLocation.getLuYangTuoMaIndex(fourPillars.yearGan, fourPillars.yearZhi)

        minorStarsList[zuoIdx].add(Star("左辅", StarType.SOFT, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("左辅", zuoIdx), getMutagen("左辅")))
        minorStarsList[youIdx].add(Star("右弼", StarType.SOFT, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("右弼", youIdx), getMutagen("右弼")))
        minorStarsList[changIdx].add(Star("文昌", StarType.SOFT, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("文昌", changIdx), getMutagen("文昌")))
        minorStarsList[quIdx].add(Star("文曲", StarType.SOFT, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("文曲", quIdx), getMutagen("文曲")))
        minorStarsList[kuiIdx].add(Star("天魁", StarType.SOFT, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("天魁", kuiIdx)))
        minorStarsList[yueIdx].add(Star("天钺", StarType.SOFT, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("天钺", yueIdx)))
        minorStarsList[luYangTuoMa.luIndex].add(Star("禄存", StarType.LUCUN, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("禄存", luYangTuoMa.luIndex)))
        minorStarsList[luYangTuoMa.maIndex].add(Star("天马", StarType.TIANMA, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("天马", luYangTuoMa.maIndex)))
        minorStarsList[timely.kongIndex].add(Star("地空", StarType.TOUGH, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("地空", timely.kongIndex)))
        minorStarsList[timely.jieIndex].add(Star("地劫", StarType.TOUGH, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("地劫", timely.jieIndex)))
        minorStarsList[huoIdx].add(Star("火星", StarType.TOUGH, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("火星", huoIdx)))
        minorStarsList[lingIdx].add(Star("铃星", StarType.TOUGH, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("铃星", lingIdx)))
        minorStarsList[luYangTuoMa.yangIndex].add(Star("擎羊", StarType.TOUGH, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("擎羊", luYangTuoMa.yangIndex)))
        minorStarsList[luYangTuoMa.tuoIndex].add(Star("陀罗", StarType.TOUGH, HoroscopeScope.ORIGIN, StarBrightness.getBrightness("陀罗", luYangTuoMa.tuoIndex)))

        // 3. 安杂曜
        val adjStarsList = List(12) { mutableListOf<Star>() }
        val daily = StarLocation.getDailyStarIndex(solarDateStr, timeIndex, fixLeap)
        val luanXi = StarLocation.getLuanXiIndex(fourPillars.yearZhi)
        val monthly = StarLocation.getMonthlyStarIndex(solarDateStr, timeIndex, fixLeap)
        val yearly = StarLocation.getYearlyStarIndex(fourPillars.yearGan, fourPillars.yearZhi, soulIndex, bodyIndex)

        adjStarsList[luanXi.hongluanIndex].add(Star("红鸾", StarType.FLOWER))
        adjStarsList[luanXi.tianxiIndex].add(Star("天喜", StarType.FLOWER))
        adjStarsList[monthly.tianyaoIndex].add(Star("天姚", StarType.FLOWER))
        adjStarsList[yearly.xianchiIndex].add(Star("咸池", StarType.FLOWER))
        adjStarsList[monthly.jieshenIndex].add(Star("解神", StarType.HELPER))

        adjStarsList[daily.santaiIndex].add(Star("三台", StarType.ADJECTIVE))
        adjStarsList[daily.bazuoIndex].add(Star("八座", StarType.ADJECTIVE))
        adjStarsList[daily.enguangIndex].add(Star("恩光", StarType.ADJECTIVE))
        adjStarsList[daily.tianguiIndex].add(Star("天贵", StarType.ADJECTIVE))

        adjStarsList[yearly.longchiIndex].add(Star("龙池", StarType.ADJECTIVE))
        adjStarsList[yearly.fenggeIndex].add(Star("凤阁", StarType.ADJECTIVE))
        adjStarsList[yearly.tiancaiIndex].add(Star("天才", StarType.ADJECTIVE))
        adjStarsList[yearly.tianshouIndex].add(Star("天寿", StarType.ADJECTIVE))
        adjStarsList[timely.taifuIndex].add(Star("台辅", StarType.ADJECTIVE))
        adjStarsList[timely.fenggaoIndex].add(Star("封诰", StarType.ADJECTIVE))
        adjStarsList[monthly.tianwuIndex].add(Star("天巫", StarType.ADJECTIVE))
        adjStarsList[yearly.huagaiIndex].add(Star("华盖", StarType.ADJECTIVE))
        adjStarsList[yearly.tianguanIndex].add(Star("天官", StarType.ADJECTIVE))
        adjStarsList[yearly.tianfuIndex].add(Star("天福", StarType.ADJECTIVE))
        adjStarsList[yearly.tianchuIndex].add(Star("天厨", StarType.ADJECTIVE))
        adjStarsList[monthly.tianyueIndex].add(Star("天月", StarType.ADJECTIVE))
        adjStarsList[yearly.tiandeIndex].add(Star("天德", StarType.ADJECTIVE))
        adjStarsList[yearly.yuedeIndex].add(Star("月德", StarType.ADJECTIVE))
        adjStarsList[yearly.tiankongIndex].add(Star("天空", StarType.ADJECTIVE))
        adjStarsList[yearly.xunkongIndex].add(Star("旬空", StarType.ADJECTIVE))
        adjStarsList[yearly.jieluIndex].add(Star("截路", StarType.ADJECTIVE))
        adjStarsList[yearly.kongwangIndex].add(Star("空亡", StarType.ADJECTIVE))
        adjStarsList[yearly.guchenIndex].add(Star("孤辰", StarType.ADJECTIVE))
        adjStarsList[yearly.guasuIndex].add(Star("寡宿", StarType.ADJECTIVE))
        adjStarsList[yearly.feilianIndex].add(Star("蜚廉", StarType.ADJECTIVE))
        adjStarsList[yearly.posuiIndex].add(Star("破碎", StarType.ADJECTIVE))
        adjStarsList[monthly.tianxingIndex].add(Star("天刑", StarType.ADJECTIVE))
        adjStarsList[monthly.yinshaIndex].add(Star("阴煞", StarType.ADJECTIVE))
        adjStarsList[yearly.tiankuIndex].add(Star("天哭", StarType.ADJECTIVE))
        adjStarsList[yearly.tianxuIndex].add(Star("天虚", StarType.ADJECTIVE))
        adjStarsList[yearly.tianshiIndex].add(Star("天使", StarType.ADJECTIVE))
        adjStarsList[yearly.tianshangIndex].add(Star("天伤", StarType.ADJECTIVE))

        // 4. 杂曜与神煞序列
        val changsheng12 = StarLocation.getChangsheng12(fiveElementsClass, gender, fourPillars.yearZhi)
        val boshi12 = StarLocation.getBoshi12(fourPillars.yearGan, fourPillars.yearZhi, gender)
        val (suiqian12, jiangqian12) = StarLocation.getYearly12(fourPillars.yearZhi)

        // 5. 组合12宫
        val startTigerStem = Constants.TIGER_RULE[fourPillars.yearGan] ?: "丙"
        val tigerIdx = Constants.HEAVENLY_STEMS.indexOf(startTigerStem)

        val palaces = (0 until 12).map { i ->
            val palaceStem = Constants.HEAVENLY_STEMS[Constants.fixIndex(tigerIdx + i, 10)]
            val palaceBranch = Constants.palaceIndexToEarthlyBranch(i)
            val isOriginal = palaceBranch != "子" && palaceBranch != "丑" && palaceStem == fourPillars.yearGan

            Palace(
                index = i,
                name = palaceNames[i],
                isBodyPalace = (i == bodyIndex),
                isOriginalPalace = isOriginal,
                heavenlyStem = palaceStem,
                earthlyBranch = palaceBranch,
                majorStars = majorStarsList[i],
                minorStars = minorStarsList[i],
                adjectiveStars = adjStarsList[i],
                changsheng12 = changsheng12[i],
                boshi12 = boshi12[i],
                jiangqian12 = jiangqian12[i],
                suiqian12 = suiqian12[i],
                decadal = decadals[i],
                ages = ages[i]
            )
        }

        // 命主以命宫地支定，身主以年支定
        val soulBranch = EarthlyBranch.fromChinese(earthlyBranchOfSoul)
        val yearBranch = EarthlyBranch.fromChinese(fourPillars.yearZhi)
        val soul = soulBranch.soul
        val body = yearBranch.body

        val earthlyBranchOfBodyPalace = Constants.palaceIndexToEarthlyBranch(bodyIndex)

        return Astrolabe(
            gender = gender,
            solarDate = solarDateStr,
            lunarDate = lunarInfo.chineseString,
            chineseDate = fourPillars.toChineseString(),
            fourPillars = fourPillars,
            time = Constants.CHINESE_TIME[timeIndex],
            timeRange = Constants.TIME_RANGE[timeIndex],
            sign = sign,
            zodiac = zodiac,
            earthlyBranchOfSoulPalace = earthlyBranchOfSoul,
            earthlyBranchOfBodyPalace = earthlyBranchOfBodyPalace,
            soul = soul,
            body = body,
            fiveElementsClass = fiveElementsClass.chinese,
            palaces = palaces
        )
    }
}
