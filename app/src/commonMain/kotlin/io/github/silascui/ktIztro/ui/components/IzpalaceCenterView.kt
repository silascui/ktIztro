package io.github.silascui.ktIztro.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.silascui.ktIztro.model.Astrolabe
import io.github.silascui.ktIztro.model.Constants
import io.github.silascui.ktIztro.model.Gender
import io.github.silascui.ktIztro.model.Horoscope
import io.github.silascui.ktIztro.model.HoroscopeScope
import io.github.silascui.ktIztro.ui.theme.CelestialLineOpposite
import io.github.silascui.ktIztro.ui.theme.CelestialLineTrine
import io.github.silascui.ktIztro.ui.theme.CelestialTrineFill

/**
 * 60花甲子纳音速查
 */
fun getNaYin(ganZhi: String): String {
    return when (ganZhi) {
        "甲子", "乙丑" -> "海中金"
        "丙寅", "丁卯" -> "炉中火"
        "戊辰", "己巳" -> "大林木"
        "庚午", "辛未" -> "路旁土"
        "壬申", "癸酉" -> "剑锋金"
        "甲戌", "乙亥" -> "山头火"
        "丙子", "丁丑" -> "涧下水"
        "戊寅", "己卯" -> "城头土"
        "庚辰", "辛巳" -> "白蜡金"
        "壬午", "癸未" -> "杨柳木"
        "甲申", "乙酉" -> "泉中水"
        "丙戌", "丁亥" -> "屋上土"
        "戊子", "己丑" -> "霹雳火"
        "庚寅", "辛卯" -> "松柏木"
        "壬辰", "癸巳" -> "长流水"
        "甲午", "乙未" -> "沙中金"
        "丙申", "丁酉" -> "山下火"
        "戊戌", "己亥" -> "平地木"
        "庚子", "辛丑" -> "壁上土"
        "壬寅", "癸卯" -> "金箔金"
        "甲辰", "乙巳" -> "覆灯火"
        "丙午", "丁未" -> "天河水"
        "戊申", "己酉" -> "大驿土"
        "庚戌", "辛亥" -> "钗钏金"
        "壬子", "癸丑" -> "桑柘木"
        "甲寅", "乙卯" -> "大溪水"
        "丙辰", "丁巳" -> "沙中土"
        "戊午", "己未" -> "天上火"
        "庚申", "辛酉" -> "石榴木"
        "壬戌", "癸亥" -> "大海水"
        else -> ""
    }
}

/**
 * Android Material 3 架构中宫神龛枢纽视图 (占中心 2x2 格，遵循 DESIGN.md 规范)
 * 1. 底层：三合金色微芒半透明填充
 * 2. 中层：中宫八字神龛（乾/坤造印标、五行局、四柱纳音、核心命身主、生年四化）
 * 3. 顶层：三方四正连线（金色虚线三合 + 翠青对冲线）
 */
@Composable
fun IzpalaceCenterView(
    astrolabe: Astrolabe,
    horoscope: Horoscope?,
    selectedScope: HoroscopeScope,
    selectedPalaceIndex: Int = 0,
    activeFlyingStem: String? = null,
    showAspectLines: Boolean = true,
    onScopeSelected: (HoroscopeScope) -> Unit,
    onYearChange: (Int) -> Unit,
    onResetToday: () -> Unit,
    activeTaiChiPalaceName: String? = null,
    onResetTaiChi: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes

    val centerGradient = Brush.verticalGradient(
        listOf(colorScheme.surface, colorScheme.surfaceContainerLowest)
    )

    Box(
        modifier = modifier
            .clip(shapes.large)
            .background(centerGradient)
            .border(1.dp, colorScheme.primary.copy(alpha = 0.25f), shapes.large)
            .padding(1.5.dp)
    ) {
        // 1. 底层 Canvas：三合金色微芒填充
        if (showAspectLines) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width / 2f
                val h = size.height / 2f

                val points = listOf(
                    Offset(0f, h * 2f),         // 0: 寅
                    Offset(0f, h * 1.5f),       // 1: 卯
                    Offset(0f, h * 0.5f),       // 2: 辰
                    Offset(0f, 0f),             // 3: 巳
                    Offset(w * 0.5f, 0f),       // 4: 午
                    Offset(w * 1.5f, 0f),       // 5: 未
                    Offset(w * 2f, 0f),         // 6: 申
                    Offset(w * 2f, h * 0.5f),   // 7: 酉
                    Offset(w * 2f, h * 1.5f),   // 8: 戌
                    Offset(w * 2f, h * 2f),     // 9: 亥
                    Offset(w * 1.5f, h * 2f),   // 10: 子
                    Offset(w * 0.5f, h * 2f)    // 11: 丑
                )

                val idx = selectedPalaceIndex.coerceIn(0, 11)
                val q4Idx = Constants.fixIndex(idx + 4)
                val h4Idx = Constants.fixIndex(idx - 4)

                val pSelf = points[idx]
                val pCareer = points[q4Idx]
                val pWealth = points[h4Idx]

                val trianglePath = Path().apply {
                    moveTo(pSelf.x, pSelf.y)
                    lineTo(pCareer.x, pCareer.y)
                    lineTo(pWealth.x, pWealth.y)
                    close()
                }
                drawPath(path = trianglePath, color = CelestialTrineFill)
            }
        }

        // 2. 中层：中宫八字与生辰核心信息板
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 2.5.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // 头部主标题与格局局数
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val zaotag = if (astrolabe.gender == Gender.MALE) "乾造" else "坤造"
                    val yinYangTag = "${astrolabe.gender.yinYang}${astrolabe.gender.chinese}"

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(shapes.extraSmall)
                                .background(colorScheme.primaryContainer)
                                .padding(horizontal = 3.dp, vertical = 0.5.dp)
                        ) {
                            Text(
                                text = zaotag,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Serif,
                                color = colorScheme.onPrimaryContainer
                            )
                        }

                        Text(
                            text = " 紫微斗数命盘 ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Serif,
                            color = colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )

                        Box(
                            modifier = Modifier
                                .clip(shapes.extraSmall)
                                .background(colorScheme.tertiaryContainer.copy(alpha = 0.35f))
                                .border(0.6.dp, colorScheme.tertiary.copy(alpha = 0.5f), shapes.extraSmall)
                                .padding(horizontal = 3.dp, vertical = 0.5.dp)
                        ) {
                            Text(
                                text = astrolabe.fiveElementsClass,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.tertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(1.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "$yinYangTag · 公历 ${astrolabe.solarDate} · 农历 ${astrolabe.lunarDate}",
                            fontSize = 6.8.sp,
                            letterSpacing = (-0.2).sp,
                            color = colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // 四柱八字排盘与六十甲子纳音 (4列对称排布)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shapes.extraSmall)
                        .background(colorScheme.surfaceContainerLow.copy(alpha = 0.85f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.06f), shapes.extraSmall)
                        .padding(vertical = 1.5.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val yearPillar = "${astrolabe.fourPillars.yearGan}${astrolabe.fourPillars.yearZhi}"
                    val monthPillar = "${astrolabe.fourPillars.monthGan}${astrolabe.fourPillars.monthZhi}"
                    val dayPillar = "${astrolabe.fourPillars.dayGan}${astrolabe.fourPillars.dayZhi}"
                    val timePillar = "${astrolabe.fourPillars.timeGan}${astrolabe.fourPillars.timeZhi}"

                    CelestialPillarColumn("年柱", yearPillar, getNaYin(yearPillar), isPrimary = true)
                    CelestialPillarColumn("月柱", monthPillar, getNaYin(monthPillar), isPrimary = false)
                    CelestialPillarColumn("日柱", dayPillar, getNaYin(dayPillar), isPrimary = false)
                    CelestialPillarColumn("时柱", timePillar, getNaYin(timePillar), isPrimary = true)
                }

                // 核心参数条 (命主 / 身主 / 身宫 / 生肖 4列)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shapes.extraSmall)
                        .background(colorScheme.surfaceContainerLowest.copy(alpha = 0.65f))
                        .border(0.5.dp, Color.White.copy(alpha = 0.05f), shapes.extraSmall)
                        .padding(horizontal = 2.dp, vertical = 1.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CelestialParamCell("命主", astrolabe.soul, colorScheme.primary)
                    CelestialParamCell("身主", astrolabe.body, colorScheme.secondary)
                    CelestialParamCell("身宫", "${astrolabe.earthlyBranchOfBodyPalace}宫", colorScheme.primaryContainer)
                    CelestialParamCell("生肖", astrolabe.zodiac, colorScheme.onSurface)
                }

                // 生年四化汇总图谱 (禄/权/科/忌)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 1.dp, vertical = 0.5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${astrolabe.fourPillars.yearGan}年生年四化",
                            fontSize = 7.5.sp,
                            fontFamily = FontFamily.Serif,
                            color = colorScheme.primary.copy(alpha = 0.9f)
                        )
                        Text(
                            text = "星座: ${astrolabe.sign}",
                            fontSize = 7.sp,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    val yearStem = astrolabe.fourPillars.yearGan
                    val yearMutagens = Constants.HEAVENLY_STEM_MUTAGENS[yearStem] ?: listOf("廉贞", "破军", "武曲", "太阳")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        CelestialMutagenBadge(
                            label = "禄",
                            starName = yearMutagens.getOrElse(0) { "" },
                            badgeColor = colorScheme.primary,
                            textColor = colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                        CelestialMutagenBadge(
                            label = "权",
                            starName = yearMutagens.getOrElse(1) { "" },
                            badgeColor = colorScheme.tertiary,
                            textColor = colorScheme.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                        CelestialMutagenBadge(
                            label = "科",
                            starName = yearMutagens.getOrElse(2) { "" },
                            badgeColor = colorScheme.tertiaryContainer,
                            textColor = colorScheme.tertiary,
                            modifier = Modifier.weight(1f)
                        )
                        CelestialMutagenBadge(
                            label = "忌",
                            starName = yearMutagens.getOrElse(3) { "" },
                            badgeColor = colorScheme.errorContainer,
                            textColor = colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // 宫干飞星与太极状态提示
                if (activeFlyingStem != null) {
                    val fMutagens = Constants.HEAVENLY_STEM_MUTAGENS[activeFlyingStem]
                    if (fMutagens != null && fMutagens.size >= 4) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shapes.extraSmall)
                                .background(colorScheme.primary.copy(alpha = 0.15f))
                                .padding(horizontal = 2.dp, vertical = 0.5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "飞星: ${activeFlyingStem}干",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.primary
                            )
                            Text(
                                text = "禄·${fMutagens[0]} 权·${fMutagens[1]} 科·${fMutagens[2]} 忌·${fMutagens[3]}",
                                fontSize = 7.sp,
                                color = colorScheme.onSurface
                            )
                        }
                    }
                }

                if (activeTaiChiPalaceName != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shapes.extraSmall)
                            .background(colorScheme.tertiary.copy(alpha = 0.15f))
                            .clickable { onResetTaiChi() }
                            .padding(horizontal = 2.dp, vertical = 0.5.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "太极: 【$activeTaiChiPalaceName】为新命宫",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.tertiary
                        )
                        Text(
                            text = "[点击还原]",
                            fontSize = 7.sp,
                            color = colorScheme.secondary
                        )
                    }
                }
            }
        }

        // 3. 顶层 Canvas：三方四正线条与端点锚点 (金虚线三合 + 翡翠青冲照线)
        if (showAspectLines) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width / 2f
                val h = size.height / 2f

                val points = listOf(
                    Offset(0f, h * 2f),         // 0: 寅
                    Offset(0f, h * 1.5f),       // 1: 卯
                    Offset(0f, h * 0.5f),       // 2: 辰
                    Offset(0f, 0f),             // 3: 巳
                    Offset(w * 0.5f, 0f),       // 4: 午
                    Offset(w * 1.5f, 0f),       // 5: 未
                    Offset(w * 2f, 0f),         // 6: 申
                    Offset(w * 2f, h * 0.5f),   // 7: 酉
                    Offset(w * 2f, h * 1.5f),   // 8: 戌
                    Offset(w * 2f, h * 2f),     // 9: 亥
                    Offset(w * 1.5f, h * 2f),   // 10: 子
                    Offset(w * 0.5f, h * 2f)    // 11: 丑
                )

                val idx = selectedPalaceIndex.coerceIn(0, 11)
                val dgIdx = Constants.fixIndex(idx + 6)
                val q4Idx = Constants.fixIndex(idx + 4)
                val h4Idx = Constants.fixIndex(idx - 4)

                val pSelf = points[idx]
                val pOpp = points[dgIdx]
                val pCareer = points[q4Idx]
                val pWealth = points[h4Idx]

                // 1. 三合三角形金色虚线轮廓
                val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
                val trianglePath = Path().apply {
                    moveTo(pSelf.x, pSelf.y)
                    lineTo(pCareer.x, pCareer.y)
                    lineTo(pWealth.x, pWealth.y)
                    close()
                }
                drawPath(
                    path = trianglePath,
                    color = CelestialLineTrine,
                    style = Stroke(width = 1.8.dp.toPx(), pathEffect = dashEffect)
                )

                // 2. 本对冲翡翠青实线
                drawLine(
                    color = CelestialLineOpposite,
                    start = pSelf,
                    end = pOpp,
                    strokeWidth = 2.0.dp.toPx()
                )

                // 3. 焦点与端点圆环 (白色实心基座 + 语义彩色锚点)
                // 对宫锚点
                drawCircle(color = Color.White, radius = 4.dp.toPx(), center = pOpp)
                drawCircle(color = CelestialLineOpposite, radius = 3.dp.toPx(), center = pOpp)

                // 官禄/财帛三合锚点
                drawCircle(color = Color.White, radius = 4.dp.toPx(), center = pCareer)
                drawCircle(color = CelestialLineTrine, radius = 3.dp.toPx(), center = pCareer)

                drawCircle(color = Color.White, radius = 4.dp.toPx(), center = pWealth)
                drawCircle(color = CelestialLineTrine, radius = 3.dp.toPx(), center = pWealth)

                // 本宫焦点圆点（暖金色双层光晕）
                drawCircle(color = colorScheme.primary.copy(alpha = 0.35f), radius = 6.5.dp.toPx(), center = pSelf)
                drawCircle(color = Color.White, radius = 4.5.dp.toPx(), center = pSelf)
                drawCircle(color = colorScheme.primary, radius = 3.2.dp.toPx(), center = pSelf)
            }
        }
    }
}

@Composable
private fun CelestialPillarColumn(
    label: String,
    ganZhi: String,
    naYin: String,
    isPrimary: Boolean
) {
    val colorScheme = MaterialTheme.colorScheme
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 7.sp,
            color = colorScheme.onSurfaceVariant,
            lineHeight = 7.5.sp
        )
        Text(
            text = ganZhi,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = if (isPrimary) colorScheme.primary else colorScheme.onSurface,
            lineHeight = 11.5.sp
        )
        Text(
            text = naYin,
            fontSize = 6.5.sp,
            color = colorScheme.onSurfaceVariant,
            lineHeight = 7.sp
        )
    }
}

@Composable
private fun CelestialParamCell(label: String, value: String, color: Color) {
    val colorScheme = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            fontSize = 7.sp,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 1.5.dp)
        )
        Text(
            text = value,
            fontSize = 7.5.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun CelestialMutagenBadge(
    label: String,
    starName: String,
    badgeColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(shapes.extraSmall)
            .background(badgeColor.copy(alpha = 0.15f))
            .border(0.5.dp, badgeColor.copy(alpha = 0.4f), shapes.extraSmall)
            .padding(vertical = 1.dp, horizontal = 1.dp)
    ) {
        Text(
            text = label,
            fontSize = 7.sp,
            fontWeight = FontWeight.Black,
            color = textColor
        )
        Text(
            text = starName,
            fontSize = 7.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Medium,
            color = colorScheme.onSurface,
            modifier = Modifier.padding(start = 1.dp),
            maxLines = 1
        )
    }
}
