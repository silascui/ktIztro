package io.github.silascui.ktIztro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.silascui.ktIztro.model.Star
import io.github.silascui.ktIztro.model.StarType
import io.github.silascui.ktIztro.ui.theme.FlowerStarColor
import io.github.silascui.ktIztro.ui.theme.LabelBadgeTextStyle
import io.github.silascui.ktIztro.ui.theme.StarMajorTextStyle
import io.github.silascui.ktIztro.ui.theme.StarMinorTextStyle

/**
 * Android Material 3 架构星曜视图 (遵循 DESIGN.md 规范)
 * 1. 甲级主星：Imperial Gold (MaterialTheme.colorScheme.primary)
 * 2. 六吉辅曜：Celadon Jade (MaterialTheme.colorScheme.tertiary)
 * 3. 六煞刑煞：Cinnabar Red (MaterialTheme.colorScheme.secondary)
 * 4. 杂曜神煞：Misty Slate (MaterialTheme.colorScheme.onSurfaceVariant)
 * 5. 四化印章：禄、权、科、忌 标准 M3 印章配色与 extraSmall (2dp) 微型圆角
 */
@Composable
fun IzstarView(
    star: Star,
    flyingMutagen: String? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val isMajor = star.type == StarType.MAJOR

    val starColor = when (star.type) {
        StarType.MAJOR -> colorScheme.primary
        StarType.SOFT, StarType.HELPER, StarType.LUCUN, StarType.TIANMA -> colorScheme.tertiary
        StarType.TOUGH -> colorScheme.secondary
        StarType.FLOWER -> FlowerStarColor
        StarType.HOROSCOPE -> colorScheme.tertiaryContainer
        StarType.ADJECTIVE -> colorScheme.onSurfaceVariant
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(vertical = 0.5.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    ) {
        // 1. 星曜名称
        Text(
            text = star.name,
            color = starColor,
            style = if (isMajor) StarMajorTextStyle else StarMinorTextStyle,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Clip
        )

        // 2. 庙旺平陷状态
        if (star.brightness.isNotEmpty()) {
            val brightnessColor = when (star.brightness) {
                "庙", "旺" -> colorScheme.primary
                "得", "利" -> colorScheme.tertiary
                "平" -> colorScheme.secondary
                "陷" -> colorScheme.secondary.copy(alpha = 0.85f)
                else -> colorScheme.onSurfaceVariant
            }
            Text(
                text = star.brightness,
                color = brightnessColor,
                fontSize = 7.5.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(start = 1.dp),
                lineHeight = 8.sp
            )
        }

        // 3. Material 3 四化微型印章
        val displayMutagen = flyingMutagen ?: star.mutagen
        if (displayMutagen.isNotEmpty()) {
            val (badgeBg, badgeTextColor) = when (displayMutagen) {
                "禄" -> Pair(colorScheme.primary, colorScheme.onPrimary)
                "权" -> Pair(colorScheme.tertiary, colorScheme.onTertiary)
                "科" -> Pair(colorScheme.tertiaryContainer, colorScheme.onTertiaryContainer)
                "忌" -> Pair(colorScheme.errorContainer, colorScheme.onErrorContainer)
                else -> Pair(colorScheme.primary, colorScheme.onPrimary)
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(start = 1.5.dp)
                    .size(11.5.dp)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(badgeBg)
                    .then(
                        if (flyingMutagen != null) {
                            Modifier.border(0.8.dp, colorScheme.primary, MaterialTheme.shapes.extraSmall)
                        } else Modifier
                    )
            ) {
                Text(
                    text = displayMutagen,
                    color = badgeTextColor,
                    style = LabelBadgeTextStyle,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}
