package io.github.silascui.ktIztro

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Polyline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.silascui.ktIztro.ai.ui.AiInterpretationSheet
import io.github.silascui.ktIztro.engine.IztroEngine
import io.github.silascui.ktIztro.model.Constants
import io.github.silascui.ktIztro.model.Gender
import io.github.silascui.ktIztro.model.HoroscopeScope
import io.github.silascui.ktIztro.model.Star
import io.github.silascui.ktIztro.ui.components.ChartInputSheet
import io.github.silascui.ktIztro.ui.components.IztrolabeView
import io.github.silascui.ktIztro.ui.components.PatternDetector
import io.github.silascui.ktIztro.ui.components.StarDetailSheet
import io.github.silascui.ktIztro.ui.theme.LocalCelestialColors
import io.github.silascui.ktIztro.ui.theme.ZwdsTheme
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun App() {
    ZwdsTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surfaceDim
        ) {
            ZwdsApp()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZwdsApp() {
    val colors = MaterialTheme.colorScheme
    val celestialColors = LocalCelestialColors.current
    val shapes = MaterialTheme.shapes

    // 命盘输入状态 (默认甲子年格局，与 Stitch 设计样例完全对齐)
    var solarDate by remember { mutableStateOf("1984-04-08") }
    var timeIndex by remember { mutableIntStateOf(0) } // 早子时 / 夜子时
    var gender by remember { mutableStateOf(Gender.MALE) }
    var fixLeap by remember { mutableStateOf(true) }
    var isLunarInput by remember { mutableStateOf(false) }
    var inputLunarYear by remember { mutableIntStateOf(1984) }
    var inputLunarMonth by remember { mutableIntStateOf(3) }
    var inputLunarDay by remember { mutableIntStateOf(8) }
    var inputIsLeapMonth by remember { mutableStateOf(false) }

    // 排盘对象
    val astrolabe = remember(solarDate, timeIndex, gender, fixLeap) {
        IztroEngine.bySolar(
            solarDateStr = solarDate,
            timeIndex = timeIndex,
            gender = gender,
            fixLeap = fixLeap
        )
    }

    // 交互状态 (选中的宫位)
    var selectedPalaceIndex by remember(astrolabe) {
        mutableIntStateOf(astrolabe.palace("命宫")?.index ?: 0)
    }

    // 运限状态
    val currentYear = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year
    }
    var targetDateYear by remember { mutableIntStateOf(currentYear) }
    val targetDateStr = remember(targetDateYear) { "$targetDateYear-10-27" }
    var selectedScope by remember { mutableStateOf(HoroscopeScope.ORIGIN) }

    val horoscope = remember(astrolabe, targetDateStr) {
        IztroEngine.horoscope(astrolabe, targetDateStr, timeIndex)
    }

    // 飞星宫干激活状态
    var activeGongGanStem by remember { mutableStateOf<String?>(null) }

    // 立太极宫位索引 (-1表示还原)
    var taiChiPointIndex by remember { mutableIntStateOf(-1) }

    // 控制开关：三方四正连线显隐
    var showAspectLines by remember { mutableStateOf(true) }

    // 控制开关：图例弹窗
    var showLegendDialog by remember { mutableStateOf(false) }

    // 模态弹窗状态
    var selectedStar by remember { mutableStateOf<Star?>(null) }
    var showInputSheet by remember { mutableStateOf(false) }
    var showAiSheet by remember { mutableStateOf(false) }

    // 底部 Tab 选单状态 (0: 主盘, 1: 格局, 2: 大限流年, 3: 档案库/AI)
    var currentTab by remember { mutableIntStateOf(0) }

    // 格局检测
    val patterns = remember(astrolabe) {
        PatternDetector.detect(astrolabe)
    }

    Scaffold(
        containerColor = colors.surfaceDim,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 罗盘校准图标金框
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(shapes.large)
                                .background(colors.surfaceContainerHigh)
                                .border(1.dp, colors.primary.copy(alpha = 0.35f), shapes.large),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Explore,
                                contentDescription = "罗盘",
                                tint = colors.primary,
                                modifier = Modifier.size(19.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "紫微天盘",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = colors.primary,
                                letterSpacing = 0.5.sp,
                                lineHeight = 16.sp
                            )
                            val scopeZhi = if (selectedScope != HoroscopeScope.ORIGIN) " · ${selectedScope.displayName}" else ""
                            Text(
                                text = "${astrolabe.fourPillars.yearGan}${astrolabe.fourPillars.yearZhi}太岁 · ${astrolabe.fiveElementsClass}$scopeZhi",
                                fontSize = 10.sp,
                                color = colors.onSurfaceVariant,
                                lineHeight = 11.sp
                            )
                        }
                    }
                },
                actions = {
                    // 流年切换速选按钮
                    Box(
                        modifier = Modifier
                            .clip(shapes.medium)
                            .background(colors.surfaceContainerHigh)
                            .border(1.dp, colors.primary.copy(alpha = 0.25f), shapes.medium)
                            .clickable {
                                selectedScope = if (selectedScope == HoroscopeScope.YEARLY) HoroscopeScope.ORIGIN else HoroscopeScope.YEARLY
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = "流年",
                                tint = colors.primary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = if (selectedScope == HoroscopeScope.YEARLY) " 本命" else " 流年",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.primary
                            )
                        }
                    }

                    // AI 解盘入口
                    IconButton(onClick = { showAiSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI解盘",
                            tint = colors.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 排盘设置
                    IconButton(onClick = { showInputSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "排盘设置",
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 复位到命宫
                    IconButton(onClick = {
                        selectedPalaceIndex = astrolabe.palace("命宫")?.index ?: 0
                        activeGongGanStem = null
                        taiChiPointIndex = -1
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "复位到命宫",
                            tint = colors.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surfaceDim
                )
            )
        },
        bottomBar = {
            // Stitch 规范底部固定 Tab 导航条
            NavigationBar(
                containerColor = colors.surfaceDim.copy(alpha = 0.95f),
                modifier = Modifier
                    .border(width = 0.5.dp, color = Color.White.copy(alpha = 0.06f))
                    .height(56.dp)
            ) {
                NavigationBarItem(
                    selected = (currentTab == 0),
                    onClick = { currentTab = 0 },
                    icon = { Icon(Icons.Default.GridView, contentDescription = "主盘", modifier = Modifier.size(20.dp)) },
                    label = { Text("主盘", fontSize = 10.sp, fontFamily = FontFamily.Serif) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.primary,
                        selectedTextColor = colors.primary,
                        indicatorColor = colors.surfaceContainerHigh,
                        unselectedIconColor = colors.onSurfaceVariant,
                        unselectedTextColor = colors.onSurfaceVariant
                    )
                )

                NavigationBarItem(
                    selected = (currentTab == 1),
                    onClick = { currentTab = 1 },
                    icon = { Icon(Icons.Default.Hub, contentDescription = "格局", modifier = Modifier.size(20.dp)) },
                    label = { Text("格局", fontSize = 10.sp, fontFamily = FontFamily.Serif) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.primary,
                        selectedTextColor = colors.primary,
                        indicatorColor = colors.surfaceContainerHigh,
                        unselectedIconColor = colors.onSurfaceVariant,
                        unselectedTextColor = colors.onSurfaceVariant
                    )
                )

                NavigationBarItem(
                    selected = (currentTab == 2),
                    onClick = {
                        currentTab = 2
                        selectedScope = HoroscopeScope.DECADAL
                    },
                    icon = { Icon(Icons.Default.Timeline, contentDescription = "大限流年", modifier = Modifier.size(20.dp)) },
                    label = { Text("大限流年", fontSize = 10.sp, fontFamily = FontFamily.Serif) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.primary,
                        selectedTextColor = colors.primary,
                        indicatorColor = colors.surfaceContainerHigh,
                        unselectedIconColor = colors.onSurfaceVariant,
                        unselectedTextColor = colors.onSurfaceVariant
                    )
                )

                NavigationBarItem(
                    selected = (currentTab == 3),
                    onClick = {
                        currentTab = 3
                        showAiSheet = true
                    },
                    icon = { Icon(Icons.Default.FolderShared, contentDescription = "档案库", modifier = Modifier.size(20.dp)) },
                    label = { Text("档案库", fontSize = 10.sp, fontFamily = FontFamily.Serif) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = colors.primary,
                        selectedTextColor = colors.primary,
                        indicatorColor = colors.surfaceContainerHigh,
                        unselectedIconColor = colors.onSurfaceVariant,
                        unselectedTextColor = colors.onSurfaceVariant
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                // 1. 4x4 十二宫格核心天盘矩阵
                IztrolabeView(
                    astrolabe = astrolabe,
                    horoscope = horoscope,
                    selectedScope = selectedScope,
                    onScopeSelected = { selectedScope = it },
                    selectedPalaceIndex = selectedPalaceIndex,
                    onPalaceSelected = { selectedPalaceIndex = it },
                    activeGongGanStem = activeGongGanStem,
                    onGongGanToggled = { stem ->
                        activeGongGanStem = if (activeGongGanStem == stem) null else stem
                    },
                    taiChiPointIndex = taiChiPointIndex,
                    onTaiChiToggled = { idx ->
                        taiChiPointIndex = if (taiChiPointIndex == idx) -1 else idx
                    },
                    onStarClick = { star -> selectedStar = star },
                    onYearChange = { delta -> targetDateYear += delta },
                    onResetToday = { targetDateYear = currentYear },
                    showAspectLines = showAspectLines
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Stitch 规范：底部命盘分析概览抽屉面板
                val currentPalace = astrolabe.palaces[selectedPalaceIndex]
                val sanFang = astrolabe.getSanFangSiZheng(selectedPalaceIndex)

                // 推演与合相控制操作条
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(12.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(colors.primary)
                        )
                        Text(
                            text = "推演与合相控制",
                            fontFamily = FontFamily.Serif,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.onSurface,
                            letterSpacing = 0.5.sp
                        )
                    }

                    // 右侧控制按钮组
                    Row(
                        modifier = Modifier
                            .clip(shapes.large)
                            .background(colors.surfaceContainerLowest.copy(alpha = 0.8f))
                            .border(0.5.dp, Color.White.copy(alpha = 0.06f), shapes.large)
                            .padding(2.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        // 三方四正开关
                        Box(
                            modifier = Modifier
                                .clip(shapes.medium)
                                .background(if (showAspectLines) colors.primary else colors.surfaceContainerHigh)
                                .clickable { showAspectLines = !showAspectLines }
                                .padding(horizontal = 7.dp, vertical = 3.5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Polyline,
                                    contentDescription = "三方四正",
                                    tint = if (showAspectLines) colors.onPrimary else colors.onSurface,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = " 三方四正",
                                    fontSize = 10.5.sp,
                                    fontWeight = if (showAspectLines) FontWeight.Bold else FontWeight.Medium,
                                    color = if (showAspectLines) colors.onPrimary else colors.onSurface
                                )
                            }
                        }

                        // 四化飞星触发
                        Box(
                            modifier = Modifier
                                .clip(shapes.medium)
                                .background(
                                    if (activeGongGanStem != null) colors.tertiary else colors.surfaceContainerHigh
                                )
                                .clickable {
                                    activeGongGanStem = if (activeGongGanStem == currentPalace.heavenlyStem) null else currentPalace.heavenlyStem
                                }
                                .padding(horizontal = 7.dp, vertical = 3.5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SyncAlt,
                                    contentDescription = "四化飞星",
                                    tint = if (activeGongGanStem != null) colors.onTertiary else colors.onSurface,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = " 四化飞星",
                                    fontSize = 10.5.sp,
                                    fontWeight = if (activeGongGanStem != null) FontWeight.Bold else FontWeight.Medium,
                                    color = if (activeGongGanStem != null) colors.onTertiary else colors.onSurface
                                )
                            }
                        }

                        // 庙陷图例弹窗
                        Box(
                            modifier = Modifier
                                .clip(shapes.medium)
                                .background(if (showLegendDialog) colors.primary else colors.surfaceContainerHigh)
                                .clickable { showLegendDialog = !showLegendDialog }
                                .padding(horizontal = 7.dp, vertical = 3.5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.BrightnessHigh,
                                    contentDescription = "庙陷图例",
                                    tint = if (showLegendDialog) colors.onPrimary else colors.onSurface,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = " 庙陷图例",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (showLegendDialog) colors.onPrimary else colors.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Stitch 焦点宫位卡片 (`palace-detail-card`)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shapes.extraLarge)
                        .background(colors.surfaceContainerLow)
                        .border(1.dp, colors.primary.copy(alpha = 0.22f), shapes.extraLarge)
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        // 选中宫位头部
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(colors.primary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "${currentPalace.name} · ${currentPalace.heavenlyStem}${currentPalace.earthlyBranch} (${currentPalace.earthlyBranch})",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp,
                                    color = colors.primary
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(shapes.extraSmall)
                                        .background(colors.primary.copy(alpha = 0.15f))
                                        .border(0.6.dp, colors.primary.copy(alpha = 0.35f), shapes.extraSmall)
                                        .padding(horizontal = 5.dp, vertical = 1.5.dp)
                                ) {
                                    Text(
                                        text = "大限 ${currentPalace.decadal.range.first}-${currentPalace.decadal.range.second}岁",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colors.primary
                                    )
                                }

                                if (currentPalace.isOriginalPalace) {
                                    Box(
                                        modifier = Modifier
                                            .clip(shapes.extraSmall)
                                            .background(colors.primary.copy(alpha = 0.15f))
                                            .border(0.6.dp, colors.primary.copy(alpha = 0.35f), shapes.extraSmall)
                                            .padding(horizontal = 5.dp, vertical = 1.5.dp)
                                    ) {
                                        Text(
                                            text = "来因宫",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.primary
                                        )
                                    }
                                }

                                if (currentPalace.isBodyPalace) {
                                    Box(
                                        modifier = Modifier
                                            .clip(shapes.extraSmall)
                                            .background(colors.secondary.copy(alpha = 0.15f))
                                            .border(0.6.dp, colors.secondary.copy(alpha = 0.35f), shapes.extraSmall)
                                            .padding(horizontal = 5.dp, vertical = 1.5.dp)
                                    ) {
                                        Text(
                                            text = "身宫",
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.secondary
                                        )
                                    }
                                }
                            }
                        }

                        // 三方四正构成指示器 (4列对称指示)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            StitchAspectCell(
                                role = "本宫 (主干)",
                                name = "${currentPalace.name} (${currentPalace.earthlyBranch})",
                                borderColor = colors.primary.copy(alpha = 0.4f),
                                roleColor = colors.primary,
                                modifier = Modifier.weight(1f)
                            )
                            StitchAspectCell(
                                role = "对宫 (冲照)",
                                name = "${sanFang.opposite.name} (${sanFang.opposite.earthlyBranch})",
                                borderColor = colors.tertiary.copy(alpha = 0.4f),
                                roleColor = colors.tertiary,
                                modifier = Modifier.weight(1f)
                            )
                            StitchAspectCell(
                                role = "三合 (财帛位)",
                                name = "${sanFang.wealth.name} (${sanFang.wealth.earthlyBranch})",
                                borderColor = Color.White.copy(alpha = 0.12f),
                                roleColor = celestialColors.goldFixed,
                                modifier = Modifier.weight(1f)
                            )
                            StitchAspectCell(
                                role = "三合 (官禄位)",
                                name = "${sanFang.career.name} (${sanFang.career.earthlyBranch})",
                                borderColor = Color.White.copy(alpha = 0.12f),
                                roleColor = celestialColors.goldFixed,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // 格局定性与飞星化象速览
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(shapes.large)
                                .background(colors.surfaceContainerLowest.copy(alpha = 0.85f))
                                .border(0.6.dp, Color.White.copy(alpha = 0.06f), shapes.large)
                                .padding(8.dp)
                        ) {
                            val isSoulPalace = selectedPalaceIndex == (astrolabe.palace("命宫")?.index ?: 0)
                            val matchedPattern = if (isSoulPalace) patterns.firstOrNull() else null
                            val headerLabel = if (matchedPattern != null) "星曜格局：" else "主曜配置："
                            val headerTitle = matchedPattern?.name ?: if (currentPalace.majorStars.isNotEmpty()) {
                                currentPalace.majorStars.joinToString(" ") { "${it.name}${if (it.brightness.isNotEmpty()) "(${it.brightness})" else ""}" }
                            } else "无主星 (借对宫)"

                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = headerLabel,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = colors.primary
                                        )
                                        Text(
                                            text = headerTitle,
                                            fontFamily = FontFamily.Serif,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = celestialColors.tertiaryFixed
                                        )
                                    }

                                    val stemM = Constants.HEAVENLY_STEM_MUTAGENS[currentPalace.heavenlyStem]
                                    val stemMDesc = if (stemM != null && stemM.size >= 4) {
                                        "${currentPalace.heavenlyStem}${stemM[0].take(1)}${stemM[1].take(1)}${stemM[2].take(1)}${stemM[3].take(1)}"
                                    } else ""

                                    Box(
                                        modifier = Modifier
                                            .clip(shapes.extraSmall)
                                            .background(colors.surfaceContainerHigh)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "飞星: $stemMDesc",
                                            fontSize = 9.sp,
                                            color = colors.onSurfaceVariant
                                        )
                                    }
                                }

                                val descText = matchedPattern?.description
                                if (descText != null) {
                                    Text(
                                        text = descText,
                                        fontSize = 11.sp,
                                        color = colors.onSurfaceVariant,
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // 3. Stitch 规范：星曜亮度与四化印章图例弹窗
            AnimatedVisibility(
                visible = showLegendDialog,
                enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
            ) {
                StitchLegendSheet(onClose = { showLegendDialog = false })
            }
        }
    }

    // 星曜释义底栏
    StarDetailSheet(
        star = selectedStar,
        onDismiss = { selectedStar = null }
    )

    // 排盘参数编辑底栏
    if (showInputSheet) {
        ChartInputSheet(
            initialSolarDate = solarDate,
            initialTimeIndex = timeIndex,
            initialGender = gender,
            initialFixLeap = fixLeap,
            initialIsLunar = isLunarInput,
            onConfirm = { newSolarDate, newTimeIndex, newGender, newFixLeap, isLunar, lY, lM, lD, isLeap ->
                solarDate = newSolarDate
                timeIndex = newTimeIndex
                gender = newGender
                fixLeap = newFixLeap
                isLunarInput = isLunar
                inputLunarYear = lY
                inputLunarMonth = lM
                inputLunarDay = lD
                inputIsLeapMonth = isLeap
                showInputSheet = false
            },
            onDismiss = { showInputSheet = false }
        )
    }

    // AI 命理大师解盘底栏
    if (showAiSheet) {
        AiInterpretationSheet(
            astrolabe = astrolabe,
            horoscope = horoscope,
            onDismiss = { showAiSheet = false }
        )
    }
}

@Composable
private fun StitchAspectCell(
    role: String,
    name: String,
    borderColor: Color,
    roleColor: Color,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(shapes.medium)
            .background(colors.surfaceContainer)
            .border(0.8.dp, borderColor, shapes.medium)
            .padding(horizontal = 2.dp, vertical = 4.dp)
    ) {
        Text(
            text = role,
            fontFamily = FontFamily.Serif,
            fontSize = 8.5.sp,
            color = roleColor,
            maxLines = 1,
            softWrap = false
        )
        Text(
            text = name,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onSurface,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 1.dp)
        )
    }
}

/**
 * Stitch 规范：庙陷与四化图例弹窗
 */
@Composable
private fun StitchLegendSheet(onClose: () -> Unit) {
    val colors = MaterialTheme.colorScheme
    val celestialColors = LocalCelestialColors.current
    val shapes = MaterialTheme.shapes

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shapes.extraLarge)
            .background(colors.surfaceContainerHigh)
            .border(1.dp, colors.primary.copy(alpha = 0.35f), shapes.extraLarge)
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BrightnessHigh,
                        contentDescription = null,
                        tint = colors.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "星曜亮度与四化印章图例",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = colors.primary
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "关闭",
                        tint = colors.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 左栏：星曜亮度五阶
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shapes.medium)
                        .background(colors.surfaceContainer)
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(text = "星曜亮度五阶：", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                    LegendRow("庙", "光辉极盛 (吉星最吉)", colors.primary)
                    LegendRow("旺", "光芒次盛 (得地发力)", celestialColors.goldFixed)
                    LegendRow("得/利", "平和显曜 (平常发挥)", colors.tertiary)
                    LegendRow("平/陷", "暗淡力衰 (煞星更凶)", colors.secondary)
                }

                // 右栏：生年四化印记
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(shapes.medium)
                        .background(colors.surfaceContainer)
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(text = "生年四化印记：", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = colors.primary)
                    MutagenLegendRow("禄", "缘起财禄，吉庆增益", colors.primary, colors.onPrimary)
                    MutagenLegendRow("权", "威严掌控，变动拓展", colors.tertiary, colors.onTertiary)
                    MutagenLegendRow("科", "声名考第，贵人解危", colors.tertiaryContainer, celestialColors.tertiaryFixed)
                    MutagenLegendRow("忌", "执念阻滞，波折反省", colors.errorContainer, celestialColors.cinnabarRed)
                }
            }
        }
    }
}

@Composable
private fun LegendRow(title: String, desc: String, color: Color) {
    val colors = MaterialTheme.colorScheme
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = " · $desc", fontSize = 8.sp, color = colors.onSurfaceVariant, maxLines = 1)
    }
}

@Composable
private fun MutagenLegendRow(label: String, desc: String, bg: Color, fg: Color) {
    val colors = MaterialTheme.colorScheme
    val shapes = MaterialTheme.shapes
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .clip(shapes.extraSmall)
                .background(bg)
                .padding(horizontal = 2.dp, vertical = 0.2.dp)
        ) {
            Text(text = label, fontSize = 7.sp, fontWeight = FontWeight.Black, color = fg)
        }
        Text(text = " · $desc", fontSize = 8.sp, color = colors.onSurfaceVariant, maxLines = 1)
    }
}
