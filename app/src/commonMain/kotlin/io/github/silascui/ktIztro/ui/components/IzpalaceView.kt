package io.github.silascui.ktIztro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.silascui.ktIztro.model.Palace
import io.github.silascui.ktIztro.model.Star
import io.github.silascui.ktIztro.model.StarType
import io.github.silascui.ktIztro.ui.theme.CelestialBorderFocus
import io.github.silascui.ktIztro.ui.theme.CelestialBorderSubtle

/**
 * Android Material 3 架构十二宫位单元格视图 (遵循 DESIGN.md 规范)
 * 1. 容器：MaterialTheme.colorScheme.surfaceContainer (普通) / surfaceContainerHigh (激活)
 * 2. 边框：金辉极细外框 (CelestialBorderSubtle) 与高亮光晕 (CelestialBorderFocus)
 * 3. 字体：Noto Serif 宫名金印 + Noto Sans 辅助神煞与小限
 * 4. 底部：宫干飞星入口 + 身宫与真实大限区间 + 宫名
 */
@Composable
fun IzpalaceView(
    palace: Palace,
    isSelected: Boolean,
    isOpposite: Boolean,
    isTrine: Boolean,
    taiChiName: String? = null,
    transitPalaceName: String? = null,
    transitScopeName: String? = null,
    flowStars: List<Star> = emptyList(),
    flyingMutagens: Map<String, String>? = null,
    isFlyingStemActive: Boolean = false,
    onPalaceClick: () -> Unit,
    onGongGanClick: () -> Unit,
    onPalaceLongClick: () -> Unit,
    onStarClick: (Star) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes

    val borderColor = when {
        isSelected -> CelestialBorderFocus
        isOpposite -> colorScheme.tertiary
        isTrine -> colorScheme.primary.copy(alpha = 0.5f)
        isFlyingStemActive -> colorScheme.primary
        else -> CelestialBorderSubtle
    }

    val borderWidth = when {
        isSelected -> 1.5.dp
        isOpposite || isTrine || isFlyingStemActive -> 1.2.dp
        else -> 0.8.dp
    }

    val bgColor = when {
        isSelected -> colorScheme.surfaceContainerHigh
        isOpposite -> colorScheme.surfaceContainerHigh.copy(alpha = 0.7f)
        isTrine -> colorScheme.surfaceContainer.copy(alpha = 0.95f)
        else -> colorScheme.surfaceContainer
    }

    // 智能星曜分列（十四主星归左，煞曜桃花与杂曜对齐于右）
    val (leftStars, rightStars) = remember(palace) {
        val left = mutableListOf<Star>()
        val right = mutableListOf<Star>()

        // 1. 十四主星归左列
        left.addAll(palace.majorStars)

        // 2. 六吉星、禄马
        val softList = palace.minorStars.filter {
            it.type in listOf(StarType.SOFT, StarType.HELPER, StarType.LUCUN, StarType.TIANMA)
        }

        // 3. 煞曜
        val toughList = palace.minorStars.filter { it.type == StarType.TOUGH }

        // 4. 桃花与杂曜
        val flowerList = (palace.minorStars + palace.adjectiveStars).filter { it.type == StarType.FLOWER }
        val adjList = palace.adjectiveStars.filter { it.type == StarType.ADJECTIVE }

        if (left.isNotEmpty()) {
            left.addAll(softList.take(1))
            right.addAll(toughList)
            if (softList.size > 1) {
                right.addAll(softList.drop(1))
            }
            right.addAll(flowerList.take(1))
            val remaining = (4 - right.size).coerceAtLeast(1)
            right.addAll(adjList.take(remaining))
        } else {
            // 空宫借星排布
            if (softList.isNotEmpty()) {
                left.addAll(softList.take(2))
                right.addAll(toughList)
                if (softList.size > 2) right.addAll(softList.drop(2))
            } else if (toughList.isNotEmpty()) {
                left.addAll(toughList.take(2))
                right.addAll(toughList.drop(2))
            }
            right.addAll(flowerList.take(1))
            val remaining = (4 - right.size).coerceAtLeast(1)
            right.addAll(adjList.take(remaining))
        }

        Pair(left, right)
    }

    Box(
        modifier = modifier
            .clip(shapes.medium)
            .background(bgColor)
            .border(borderWidth, borderColor, shapes.medium)
            .clickable { onPalaceClick() }
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. 上部星曜排列区
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 左列：主星及重要吉星
                    Column(
                        modifier = Modifier.weight(1.05f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        if (leftStars.isEmpty()) {
                            Text(
                                text = "借对宫",
                                fontSize = 8.5.sp,
                                fontFamily = FontFamily.Serif,
                                color = colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        } else {
                            leftStars.forEach { star ->
                                val flyM = flyingMutagens?.get(star.name)
                                IzstarView(
                                    star = star,
                                    flyingMutagen = flyM,
                                    onClick = { onStarClick(star) }
                                )
                            }
                        }
                    }

                    // 右列：煞曜与神煞杂曜
                    Column(
                        modifier = Modifier.weight(0.95f),
                        horizontalAlignment = Alignment.End
                    ) {
                        rightStars.forEach { star ->
                            val flyM = flyingMutagens?.get(star.name)
                            IzstarView(
                                star = star,
                                flyingMutagen = flyM,
                                onClick = { onStarClick(star) }
                            )
                        }
                    }
                }

                // 运限流曜显示
                if (flowStars.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 0.5.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        flowStars.take(2).forEach { star ->
                            IzstarView(
                                star = star,
                                onClick = { onStarClick(star) }
                            )
                        }
                    }
                }
            }

            // 2. 中部：运限动态宫名或长生/小限标签
            Column(modifier = Modifier.fillMaxWidth()) {
                if (transitPalaceName != null && transitScopeName != null) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .clip(shapes.extraSmall)
                            .background(colorScheme.tertiary.copy(alpha = 0.18f))
                            .padding(horizontal = 3.dp, vertical = 0.5.dp)
                    ) {
                        Text(
                            text = "$transitScopeName·$transitPalaceName",
                            color = colorScheme.tertiary,
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 1.dp, vertical = 0.5.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${palace.changsheng12} · ${palace.suiqian12}",
                        fontSize = 7.sp,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        softWrap = false
                    )

                    val xiaoxianAge = palace.ages.firstOrNull() ?: 1
                    Text(
                        text = "小限${xiaoxianAge.toString().padStart(2, '0')}",
                        fontSize = 7.sp,
                        color = colorScheme.tertiary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // 3. 底部底栏：M3 规范底栏 (宫干地支 | 身宫与真实大限区间 | 宫位名称金印)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                    .background(
                        if (isSelected) colorScheme.primary.copy(alpha = 0.12f)
                        else Color.Transparent
                    )
                    .border(
                        width = 0.6.dp,
                        color = if (isSelected) colorScheme.primary.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.06f),
                        shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                    )
                    .padding(horizontal = 2.dp, vertical = 1.5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 宫干地支（可点选触发宫干飞星）
                    Box(
                        modifier = Modifier
                            .clip(shapes.extraSmall)
                            .background(
                                if (isFlyingStemActive) colorScheme.primary
                                else Color.Transparent
                            )
                            .clickable { onGongGanClick() }
                            .padding(horizontal = 1.5.dp, vertical = 0.5.dp)
                    ) {
                        Text(
                            text = "${palace.heavenlyStem}${palace.earthlyBranch}",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isFlyingStemActive) colorScheme.onPrimary else if (isSelected) colorScheme.primary else colorScheme.onSurfaceVariant
                        )
                    }

                    // 中间：身宫徽记 + 大限区间
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (palace.isBodyPalace) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 2.dp)
                                    .clip(shapes.extraSmall)
                                    .background(colorScheme.secondaryContainer)
                                    .padding(horizontal = 2.dp, vertical = 0.2.dp)
                            ) {
                                Text(
                                    text = "身",
                                    fontSize = 6.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSecondaryContainer
                                )
                            }
                        }

                        Text(
                            text = "${palace.decadal.range.first}-${palace.decadal.range.second}",
                            fontSize = 8.sp,
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // 宫位名称（长按立太极）
                    val displayName = taiChiName ?: palace.name
                    Text(
                        text = displayName,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) colorScheme.primary else colorScheme.primaryContainer,
                        modifier = Modifier.clickable { onPalaceLongClick() }
                    )
                }
            }
        }
    }
}
