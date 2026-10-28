package io.github.silascui.ktIztro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * 现代 Android Material 3 色彩方案 (Mystic Celestial Chart Palette)
 * 完全与 DESIGN.md 对齐的高阶暗金/青玉/赤砂设计系统
 */
val MysticDarkColorScheme = darkColorScheme(
    primary = M3Primary,
    onPrimary = M3OnPrimary,
    primaryContainer = M3PrimaryContainer,
    onPrimaryContainer = M3OnPrimaryContainer,
    inversePrimary = M3InversePrimary,
    secondary = M3Secondary,
    onSecondary = M3OnSecondary,
    secondaryContainer = M3SecondaryContainer,
    onSecondaryContainer = M3OnSecondaryContainer,
    tertiary = M3Tertiary,
    onTertiary = M3OnTertiary,
    tertiaryContainer = M3TertiaryContainer,
    onTertiaryContainer = M3OnTertiaryContainer,
    background = M3Background,
    onBackground = M3OnBackground,
    surface = M3Surface,
    onSurface = M3OnSurface,
    surfaceVariant = M3SurfaceVariant,
    onSurfaceVariant = M3OnSurfaceVariant,
    surfaceTint = M3SurfaceTint,
    inverseSurface = M3InverseSurface,
    inverseOnSurface = M3InverseOnSurface,
    error = M3Error,
    onError = M3OnError,
    errorContainer = M3ErrorContainer,
    onErrorContainer = M3OnErrorContainer,
    outline = M3Outline,
    outlineVariant = M3OutlineVariant,
    surfaceDim = M3SurfaceDim,
    surfaceBright = M3SurfaceBright,
    surfaceContainerLowest = M3SurfaceContainerLowest,
    surfaceContainerLow = M3SurfaceContainerLow,
    surfaceContainer = M3SurfaceContainer,
    surfaceContainerHigh = M3SurfaceContainerHigh,
    surfaceContainerHighest = M3SurfaceContainerHighest
)

/**
 * 星盘特有语义扩展设计属性
 */
@Immutable
data class CelestialColors(
    val starMajor: Color = StarMajorColor,
    val starAuspicious: Color = StarSoftColor,
    val starMalefic: Color = StarToughColor,
    val starFlower: Color = StarFlowerColor,
    val starAdjective: Color = StarAdjectiveColor,
    val starFlow: Color = StarFlowColor,
    val primaryFixed: Color = M3PrimaryFixed,
    val goldFixed: Color = M3PrimaryFixed,
    val tertiaryFixed: Color = M3TertiaryFixed,
    val secondaryFixed: Color = M3SecondaryFixed,
    val cinnabarRed: Color = M3Secondary,
    val borderSubtle: Color = CelestialBorderSubtle,
    val borderFocus: Color = CelestialBorderFocus,
    val activeGlow: Color = CelestialActiveGlow,
    val lineTrine: Color = CelestialLineTrine,
    val lineOpposite: Color = CelestialLineOpposite,
    val trineFill: Color = CelestialTrineFill
)

val LocalCelestialColors = staticCompositionLocalOf { CelestialColors() }

/**
 * 现代 Jetpack Compose Material 3 统一主题入口
 */
@Composable
fun ZwdsTheme(
    darkTheme: Boolean = true, // 按照 DESIGN.md 规范，默认采用豪华暗夜曜金质感
    content: @Composable () -> Unit
) {
    val celestialColors = CelestialColors()

    CompositionLocalProvider(
        LocalCelestialColors provides celestialColors
    ) {
        MaterialTheme(
            colorScheme = MysticDarkColorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
