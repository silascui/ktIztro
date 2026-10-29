package io.github.silascui.ktIztro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.silascui.ktIztro.model.Astrolabe
import io.github.silascui.ktIztro.model.Constants
import io.github.silascui.ktIztro.model.Horoscope
import io.github.silascui.ktIztro.model.HoroscopeScope
import io.github.silascui.ktIztro.model.Star

/**
 * Android Material 3 架构 4x4 十二宫格核心天盘矩阵视图 (遵循 DESIGN.md 规范)
 */
@Composable
fun IztrolabeView(
    astrolabe: Astrolabe,
    horoscope: Horoscope?,
    selectedScope: HoroscopeScope,
    onScopeSelected: (HoroscopeScope) -> Unit,
    selectedPalaceIndex: Int,
    onPalaceSelected: (Int) -> Unit,
    activeGongGanStem: String?,
    onGongGanToggled: (String) -> Unit,
    taiChiPointIndex: Int,
    onTaiChiToggled: (Int) -> Unit,
    onStarClick: (Star) -> Unit,
    onYearChange: (Int) -> Unit,
    onResetToday: () -> Unit,
    showAspectLines: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes

    // 宫位索引在 4x4 矩阵中的行列坐标 (row 0..3, col 0..3)
    // 巳(3) 午(4) 未(5) 申(6)
    // 辰(2) [中宫 2x2]  酉(7)
    // 卯(1) [中宫 2x2]  戌(8)
    // 寅(0) 丑(11) 子(10) 亥(9)
    val palaceCoords = remember {
        mapOf(
            3 to Pair(0, 0),
            4 to Pair(0, 1),
            5 to Pair(0, 2),
            6 to Pair(0, 3),
            2 to Pair(1, 0),
            7 to Pair(1, 3),
            1 to Pair(2, 0),
            8 to Pair(2, 3),
            0 to Pair(3, 0),
            11 to Pair(3, 1),
            10 to Pair(3, 2),
            9 to Pair(3, 3)
        )
    }

    // 三方四正宫位计算
    val sanFang = remember(selectedPalaceIndex) {
        astrolabe.getSanFangSiZheng(selectedPalaceIndex)
    }
    val oppositeIndex = sanFang.opposite.index
    val careerIndex = sanFang.career.index
    val wealthIndex = sanFang.wealth.index

    // 飞星四化映射 (由点击某个宫位的宫干触发)
    val flyingMutagensMap = remember(activeGongGanStem) {
        if (activeGongGanStem != null) {
            val list = Constants.HEAVENLY_STEM_MUTAGENS[activeGongGanStem]
            if (list != null && list.size >= 4) {
                mapOf(
                    list[0] to "禄",
                    list[1] to "权",
                    list[2] to "科",
                    list[3] to "忌"
                )
            } else null
        } else null
    }

    // 立太极宫名映射
    val taiChiNames = remember(taiChiPointIndex) {
        if (taiChiPointIndex >= 0) {
            (0 until 12).map { i ->
                val idx = Constants.fixIndex(i - taiChiPointIndex)
                Constants.PALACE_NAMES[idx]
            }
        } else null
    }

    // 当前选中的运限动态宫名与流曜
    val transitPalaceNames = remember(horoscope, selectedScope) {
        when (selectedScope) {
            HoroscopeScope.DECADAL -> horoscope?.decadal?.palaceNames
            HoroscopeScope.YEARLY -> horoscope?.yearly?.palaceNames
            HoroscopeScope.MONTHLY -> horoscope?.monthly?.palaceNames
            HoroscopeScope.DAILY -> horoscope?.daily?.palaceNames
            HoroscopeScope.HOURLY -> horoscope?.hourly?.palaceNames
            else -> null
        }
    }

    val transitStars = remember(horoscope, selectedScope) {
        when (selectedScope) {
            HoroscopeScope.DECADAL -> horoscope?.decadal?.stars
            HoroscopeScope.YEARLY -> horoscope?.yearly?.stars
            HoroscopeScope.MONTHLY -> horoscope?.monthly?.stars
            HoroscopeScope.DAILY -> horoscope?.daily?.stars
            HoroscopeScope.HOURLY -> horoscope?.hourly?.stars
            else -> null
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.76f)
            .clip(shapes.extraLarge)
            .background(colorScheme.surfaceContainerLowest)
            .border(1.dp, Color.White.copy(alpha = 0.10f), shapes.extraLarge)
            .padding(1.5.dp)
    ) {
        val cellWidth = maxWidth / 4
        val cellHeight = maxHeight / 4

        // 1. 放置外围十二宫
        astrolabe.palaces.forEach { palace ->
            val coords = palaceCoords[palace.index] ?: return@forEach
            val (row, col) = coords

            val isSelected = (palace.index == selectedPalaceIndex)
            val isOpposite = (palace.index == oppositeIndex)
            val isTrine = (palace.index == careerIndex || palace.index == wealthIndex)
            val isStemActive = (palace.heavenlyStem == activeGongGanStem)

            val taiChiName = taiChiNames?.get(palace.index)
            val transitName = transitPalaceNames?.get(palace.index)
            val flowStars = transitStars?.get(palace.index) ?: emptyList()

            IzpalaceView(
                palace = palace,
                isSelected = isSelected,
                isOpposite = isOpposite,
                isTrine = isTrine,
                taiChiName = taiChiName,
                transitPalaceName = transitName,
                transitScopeName = if (selectedScope != HoroscopeScope.ORIGIN) selectedScope.displayName else null,
                flowStars = flowStars,
                flyingMutagens = flyingMutagensMap,
                isFlyingStemActive = isStemActive,
                onPalaceClick = { onPalaceSelected(palace.index) },
                onGongGanClick = { onGongGanToggled(palace.heavenlyStem) },
                onPalaceLongClick = { onTaiChiToggled(palace.index) },
                onStarClick = onStarClick,
                modifier = Modifier
                    .size(cellWidth, cellHeight)
                    .offset(x = cellWidth * col, y = cellHeight * row)
                    .padding(0.8.dp)
            )
        }

        // 2. 放置中心 2x2 中宫
        IzpalaceCenterView(
            astrolabe = astrolabe,
            horoscope = horoscope,
            selectedScope = selectedScope,
            selectedPalaceIndex = selectedPalaceIndex,
            activeFlyingStem = activeGongGanStem,
            showAspectLines = showAspectLines,
            onScopeSelected = onScopeSelected,
            onYearChange = onYearChange,
            onResetToday = onResetToday,
            activeTaiChiPalaceName = if (taiChiPointIndex >= 0) astrolabe.palaces[taiChiPointIndex].name else null,
            onResetTaiChi = { onTaiChiToggled(-1) },
            modifier = Modifier
                .size(cellWidth * 2, cellHeight * 2)
                .offset(x = cellWidth, y = cellHeight)
                .padding(0.8.dp)
        )
    }
}
