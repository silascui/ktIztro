package io.github.silascui.ktIztro.ui.theme

import androidx.compose.ui.graphics.Color

// ===================================================================
// Mystic Celestial Chart - Android Material 3 Official Design Tokens
// 严格遵循 DESIGN.md 规范与 Android M3 最新设计体系
// ===================================================================

// --- Material 3 Core Palette Tokens ---
val M3Surface = Color(0xFF10141A)
val M3SurfaceDim = Color(0xFF10141A)
val M3SurfaceBright = Color(0xFF353940)
val M3SurfaceContainerLowest = Color(0xFF0A0E14)
val M3SurfaceContainerLow = Color(0xFF181C22)
val M3SurfaceContainer = Color(0xFF1C2026)
val M3SurfaceContainerHigh = Color(0xFF262A31)
val M3SurfaceContainerHighest = Color(0xFF31353C)
val M3SurfaceVariant = Color(0xFF31353C)
val M3SurfaceTint = Color(0xFFE9C349)

val M3OnSurface = Color(0xFFDFE2EB)
val M3OnSurfaceVariant = Color(0xFFD0C5AF)
val M3InverseSurface = Color(0xFFDFE2EB)
val M3InverseOnSurface = Color(0xFF2D3137)

val M3Outline = Color(0xFF99907C)
val M3OutlineVariant = Color(0xFF4D4635)

val M3Primary = Color(0xFFF2CA50)                    // Imperial Gold (甲级主星、核心高亮)
val M3OnPrimary = Color(0xFF3C2F00)
val M3PrimaryContainer = Color(0xFFD4AF37)           // 经典重金
val M3OnPrimaryContainer = Color(0xFF554300)
val M3InversePrimary = Color(0xFF735C00)

val M3Secondary = Color(0xFFFFB4A4)                  // Cinnabar Red (煞曜、警示、冲突)
val M3OnSecondary = Color(0xFF640D00)
val M3SecondaryContainer = Color(0xFF891E07)
val M3OnSecondaryContainer = Color(0xFFFF9A84)

val M3Tertiary = Color(0xFF7FDFCE)                   // Celadon Jade (吉星、化科、四化吉曜)
val M3OnTertiary = Color(0xFF003730)
val M3TertiaryContainer = Color(0xFF62C3B3)
val M3OnTertiaryContainer = Color(0xFF004E45)

val M3Error = Color(0xFFFFB4AB)                      // 化忌、警报
val M3OnError = Color(0xFF690005)
val M3ErrorContainer = Color(0xFF93000A)
val M3OnErrorContainer = Color(0xFFFFDAD6)

val M3PrimaryFixed = Color(0xFFFFE088)
val M3PrimaryFixedDim = Color(0xFFE9C349)
val M3OnPrimaryFixed = Color(0xFF241A00)
val M3OnPrimaryFixedVariant = Color(0xFF574500)

val M3SecondaryFixed = Color(0xFFFFDAD3)
val M3SecondaryFixedDim = Color(0xFFFFB4A4)
val M3OnSecondaryFixed = Color(0xFF3E0500)
val M3OnSecondaryFixedVariant = Color(0xFF891E07)

val M3TertiaryFixed = Color(0xFF93F4E2)
val M3TertiaryFixedDim = Color(0xFF77D7C6)
val M3OnTertiaryFixed = Color(0xFF00201C)
val M3OnTertiaryFixedVariant = Color(0xFF005047)

val M3Background = Color(0xFF10141A)
val M3OnBackground = Color(0xFFDFE2EB)

// --- Domain-Specific Celestial Semantics (DESIGN.md) ---
val CelestialBorderSubtle = Color(0xFFD4AF37).copy(alpha = 0.16f)
val CelestialBorderFocus = Color(0xFFD4AF37).copy(alpha = 0.55f)
val CelestialActiveGlow = Color(0xFFD4AF37).copy(alpha = 0.20f)
val CelestialSurfaceOverlay = Color(0xFFD4AF37).copy(alpha = 0.06f)

// 星曜专属层级色彩 (遵循 DESIGN.md: 甲级主星用 Imperial Gold，吉星用 Celadon Jade，煞星用 Cinnabar Red)
val StarMajorColor = M3Primary                       // 甲级十四主星：帝国金 (#F2CA50)
val StarSoftColor = M3Tertiary                       // 六吉辅曜：青翠碧玉 (#7FDFCE)
val StarToughColor = M3Secondary                     // 六煞刑煞：朱砂赤红 (#FFB4A4)
val StarFlowerColor = Color(0xFFF472B6)              // 桃花星：娇柔粉红
val StarAdjectiveColor = M3OnSurfaceVariant          // 杂曜神煞：黛青烟灰 (#D0C5AF)
val StarFlowColor = M3TertiaryFixedDim               // 运限流曜：天青蓝 (#77D7C6)

// 四化印章语义标色
val MutagenLuBadge = M3Primary                       // 禄：金黄色徽章
val MutagenQuanBadge = M3Tertiary                    // 权：翡翠青
val MutagenKeBadge = M3TertiaryFixed                 // 科：霁玉青
val MutagenJiBadge = M3ErrorContainer                // 忌：深绛红印

// 三方四正牵引线色彩
val CelestialLineTrine = M3Primary                   // 三合三方：金色虚线
val CelestialLineOpposite = M3Tertiary               // 对宫冲照：青翠实线
val CelestialTrineFill = M3Primary.copy(alpha = 0.06f)

// --- 别名兼容层 (保持与各业务 UI 无缝平滑对接) ---
val StitchGoldPrimary = M3Primary
val StitchGoldContainer = M3PrimaryContainer
val StitchGoldFixed = M3PrimaryFixed
val StitchOnPrimary = M3OnPrimary

val StitchSecondary = M3Secondary
val StitchSecondaryContainer = M3SecondaryContainer
val StitchOnSecondary = M3OnSecondary

val StitchTertiary = M3Tertiary
val StitchTertiaryContainer = M3TertiaryContainer
val StitchTertiaryFixed = M3TertiaryFixed
val StitchOnTertiary = M3OnTertiary

val StitchError = M3Error
val StitchErrorContainer = M3ErrorContainer

val StitchBgLowest = M3SurfaceContainerLowest
val StitchSurfaceDim = M3SurfaceDim
val StitchSurface = M3Surface
val StitchSurfaceLow = M3SurfaceContainerLow
val StitchSurfaceContainer = M3SurfaceContainer
val StitchSurfaceHigh = M3SurfaceContainerHigh
val StitchSurfaceHighest = M3SurfaceContainerHighest
val StitchSurfaceBright = M3SurfaceBright

val StitchSurfaceContainerLowest = M3SurfaceContainerLowest
val StitchSurfaceContainerLow = M3SurfaceContainerLow
val StitchSurfaceContainerHigh = M3SurfaceContainerHigh

val StitchOnSurface = M3OnSurface
val StitchOnSurfaceVariant = M3OnSurfaceVariant
val StitchOutline = M3Outline
val StitchOutlineVariant = M3OutlineVariant

val StitchChineseBorder = CelestialBorderSubtle
val StitchChineseBorderActive = CelestialBorderFocus
val StitchActiveGlow = CelestialActiveGlow

val StitchLineTrine = CelestialLineTrine
val StitchLineOpposite = CelestialLineOpposite
val StitchTrineFill = CelestialTrineFill

val MutagenLuColor = MutagenLuBadge
val MutagenQuanColor = MutagenQuanBadge
val MutagenKeColor = MutagenKeBadge
val MutagenJiColor = MutagenJiBadge

val MajorStarColor = StarMajorColor
val SoftStarColor = StarSoftColor
val ToughStarColor = StarToughColor
val FlowerStarColor = StarFlowerColor
val AdjectiveStarColor = StarAdjectiveColor
val FlowStarColor = StarFlowColor

val PaperBgWarm = M3SurfaceDim
val CardBgWarm = M3SurfaceContainer
val CellBgWarm = M3SurfaceContainer
val CellBgSelected = M3SurfaceContainerHigh
val CellBgOpposite = M3SurfaceContainer.copy(alpha = 0.95f)
val CellBgTrine = M3SurfaceContainer.copy(alpha = 0.9f)

val BorderWarm = CelestialBorderSubtle
val BorderSubtle = Color(0xFFFFFFFF).copy(alpha = 0.06f)
val BorderFocus = CelestialBorderFocus
val BorderAmber = M3PrimaryContainer
val BorderBlue = M3Tertiary

val InkBlack = M3OnSurface
val InkDark = M3OnSurface
val InkMedium = M3OnSurfaceVariant
val InkLight = M3OnSurfaceVariant.copy(alpha = 0.7f)

val VermilionRed = M3Secondary
val AmberGold = M3Primary
val JadeGreen = M3Tertiary
val SapphireBlue = M3Tertiary
val AmethystPurple = Color(0xFFA855F7)
val PeachPink = StarFlowerColor
val PaperBgLight = PaperBgWarm
val CardBgLight = CardBgWarm
val BorderLight = BorderWarm
val DarkBg = M3SurfaceDim
val DarkCard = M3SurfaceContainer
val DarkCell = M3SurfaceContainer
val DarkBorder = M3OutlineVariant
val DarkTextPrimary = M3OnSurface
val DarkTextSecondary = M3OnSurfaceVariant
