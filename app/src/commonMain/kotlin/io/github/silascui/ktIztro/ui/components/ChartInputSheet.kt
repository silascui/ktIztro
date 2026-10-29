package io.github.silascui.ktIztro.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.silascui.ktIztro.ui.theme.AmberGold
import io.github.silascui.ktIztro.ui.theme.SapphireBlue
import io.github.silascui.ktIztro.ui.theme.VermilionRed
import io.github.silascui.ktIztro.calendar.LunarCalendarHelper
import io.github.silascui.ktIztro.model.Constants
import io.github.silascui.ktIztro.model.Gender

data class PresetChart(
    val title: String,
    val subtitle: String,
    val solarDate: String,
    val lunarYear: Int,
    val lunarMonth: Int,
    val lunarDay: Int,
    val timeIndex: Int,
    val gender: Gender,
    val fixLeap: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChartInputSheet(
    initialSolarDate: String,
    initialTimeIndex: Int,
    initialGender: Gender,
    initialFixLeap: Boolean,
    initialIsLunar: Boolean = false,
    onConfirm: (
        solarDate: String,
        timeIndex: Int,
        gender: Gender,
        fixLeap: Boolean,
        isLunar: Boolean,
        lunarYear: Int,
        lunarMonth: Int,
        lunarDay: Int,
        isLeapMonth: Boolean
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 历法类型：false 为公历(阳历)，true 为农历(阴历)
    var isLunarMode by remember { mutableStateOf(initialIsLunar) }

    // 公历输入状态
    val dateParts = initialSolarDate.split("-")
    var solarYearText by remember { mutableStateOf(dateParts.getOrElse(0) { "2000" }) }
    var solarMonthText by remember { mutableStateOf(dateParts.getOrElse(1) { "8" }) }
    var solarDayText by remember { mutableStateOf(dateParts.getOrElse(2) { "16" }) }

    // 农历输入状态
    val initLunar = remember {
        LunarCalendarHelper.solarToLunar(initialSolarDate)
    }
    var lunarYearText by remember { mutableStateOf(initLunar.lunarYear.toString()) }
    var lunarMonthText by remember { mutableStateOf(initLunar.lunarMonth.toString()) }
    var lunarDayText by remember { mutableStateOf(initLunar.lunarDay.toString()) }
    var isLeapMonth by remember { mutableStateOf(initLunar.isLeap) }

    var selectedTimeIndex by remember { mutableIntStateOf(initialTimeIndex) }
    var selectedGender by remember { mutableStateOf(initialGender) }
    var fixLeap by remember { mutableStateOf(initialFixLeap) }

    val presets = remember {
        listOf(
            PresetChart("庚辰女命", "2000-08-16 寅时 · 破军文昌木三局", "2000-8-16", 2000, 7, 17, 2, Gender.FEMALE, true),
            PresetChart("庚午男命", "1990-05-18 午时 · 阳男火六局", "1990-5-18", 1990, 4, 24, 6, Gender.MALE, true),
            PresetChart("甲子男命", "1984-11-20 辰时 · 紫府朝垣格局", "1984-11-20", 1984, 10, 28, 4, Gender.MALE, true),
            PresetChart("戊寅火贪", "1998-03-24 子时 · 贪狼爆发格局", "1998-3-24", 1998, 2, 26, 0, Gender.MALE, true)
        )
    }

    // 计算农历年份是否存在闰月
    val currentLunarYear = lunarYearText.trim().toIntOrNull() ?: 2000
    val leapMonthOfYear = remember(currentLunarYear) {
        LunarCalendarHelper.getLeapMonthOfYear(currentLunarYear)
    }

    // 实时双向计算预览信息
    val previewInfo = remember(isLunarMode, solarYearText, solarMonthText, solarDayText, lunarYearText, lunarMonthText, lunarDayText, isLeapMonth) {
        try {
            if (!isLunarMode) {
                // 公历模式：转农历预览
                val sy = solarYearText.trim().toIntOrNull() ?: 2000
                val sm = (solarMonthText.trim().toIntOrNull() ?: 1).coerceIn(1, 12)
                val sd = (solarDayText.trim().toIntOrNull() ?: 1).coerceIn(1, 31)
                val sStr = "$sy-$sm-$sd"
                val lInfo = LunarCalendarHelper.solarToLunar(sStr)
                val fp = LunarCalendarHelper.getFourPillarsBySolar(sStr, selectedTimeIndex)
                "对应农历: ${lInfo.chineseString} · ${fp.toChineseString()}"
            } else {
                // 农历模式：转公历预览
                val ly = lunarYearText.trim().toIntOrNull() ?: 2000
                val lm = (lunarMonthText.trim().toIntOrNull() ?: 1).coerceIn(1, 12)
                val ld = (lunarDayText.trim().toIntOrNull() ?: 1).coerceIn(1, 30)
                val sConverted = LunarCalendarHelper.lunarToSolar(ly, lm, ld, isLeapMonth)
                val fp = LunarCalendarHelper.getFourPillarsBySolar(sConverted, selectedTimeIndex)
                val leapText = if (isLeapMonth) " (闰月)" else ""
                "对应阳历: $sConverted · $ly 年$lm 月$ld 日$leapText"
            }
        } catch (e: Exception) {
            "日期输入中..."
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "紫微排盘设定",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = VermilionRed
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 1. 历法选择 Tab：公历(阳历) / 农历(阴历)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(3.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (!isLunarMode) VermilionRed else Color.Transparent)
                        .clickable {
                            if (isLunarMode) {
                                // 切换到公历模式：把当前农历反推为公历
                                val ly = lunarYearText.trim().toIntOrNull() ?: 2000
                                val lm = lunarMonthText.trim().toIntOrNull() ?: 8
                                val ld = lunarDayText.trim().toIntOrNull() ?: 16
                                val sol = LunarCalendarHelper.lunarToSolar(ly, lm, ld, isLeapMonth)
                                val p = sol.split("-")
                                if (p.size >= 3) {
                                    solarYearText = p[0]
                                    solarMonthText = p[1]
                                    solarDayText = p[2]
                                }
                                isLunarMode = false
                            }
                        }
                        .padding(vertical = 7.dp)
                ) {
                    Text(
                        text = "公历 (阳历)",
                        fontSize = 13.sp,
                        fontWeight = if (!isLunarMode) FontWeight.Bold else FontWeight.Medium,
                        color = if (!isLunarMode) Color.White else Color.DarkGray
                    )
                }

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isLunarMode) VermilionRed else Color.Transparent)
                        .clickable {
                            if (!isLunarMode) {
                                // 切换到农历模式：把当前公历转换为农历
                                val sy = solarYearText.trim().toIntOrNull() ?: 2000
                                val sm = solarMonthText.trim().toIntOrNull() ?: 8
                                val sd = solarDayText.trim().toIntOrNull() ?: 16
                                val lInfo = LunarCalendarHelper.solarToLunar("$sy-$sm-$sd")
                                lunarYearText = lInfo.lunarYear.toString()
                                lunarMonthText = lInfo.lunarMonth.toString()
                                lunarDayText = lInfo.lunarDay.toString()
                                isLeapMonth = lInfo.isLeap
                                isLunarMode = true
                            }
                        }
                        .padding(vertical = 7.dp)
                ) {
                    Text(
                        text = "农历 (阴历)",
                        fontSize = 13.sp,
                        fontWeight = if (isLunarMode) FontWeight.Bold else FontWeight.Medium,
                        color = if (isLunarMode) Color.White else Color.DarkGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 经典示例命盘快速载入
            Text(
                text = "经典命盘 (点击快速载入)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(5.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                itemsIndexed(presets) { _, preset ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(0.8.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                            .clickable {
                                val p = preset.solarDate.split("-")
                                solarYearText = p[0]
                                solarMonthText = p[1]
                                solarDayText = p[2]
                                lunarYearText = preset.lunarYear.toString()
                                lunarMonthText = preset.lunarMonth.toString()
                                lunarDayText = preset.lunarDay.toString()
                                isLeapMonth = false
                                selectedTimeIndex = preset.timeIndex
                                selectedGender = preset.gender
                                fixLeap = preset.fixLeap
                            }
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Column {
                            Text(
                                text = preset.title,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = VermilionRed
                            )
                            Text(
                                text = preset.subtitle,
                                fontSize = 8.5.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. 日期输入区
            if (!isLunarMode) {
                // 公历模式输入
                Text(
                    text = "出生公历日期 (年 - 月 - 日)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = solarYearText,
                        onValueChange = { solarYearText = it },
                        label = { Text("年", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.3f)
                    )
                    OutlinedTextField(
                        value = solarMonthText,
                        onValueChange = { solarMonthText = it },
                        label = { Text("月", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = solarDayText,
                        onValueChange = { solarDayText = it },
                        label = { Text("日", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            } else {
                // 农历模式输入
                Text(
                    text = "出生农历日期 (阴历年 - 月 - 日)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = lunarYearText,
                        onValueChange = { lunarYearText = it },
                        label = { Text("农历年", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.3f)
                    )
                    OutlinedTextField(
                        value = lunarMonthText,
                        onValueChange = { lunarMonthText = it },
                        label = { Text("月(1-12)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = lunarDayText,
                        onValueChange = { lunarDayText = it },
                        label = { Text("日(1-30)", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 闰月选择逻辑
                val curM = lunarMonthText.trim().toIntOrNull() ?: 1
                val canBeLeap = (leapMonthOfYear > 0 && curM == leapMonthOfYear)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (canBeLeap) AmberGold.copy(alpha = 0.12f) else Color(0xFFF1F5F9))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "闰月设置",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (canBeLeap) AmberGold else Color.DarkGray
                            )
                            if (leapMonthOfYear > 0) {
                                Text(
                                    text = " (该年闰${leapMonthOfYear}月)",
                                    fontSize = 10.5.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            } else {
                                Text(
                                    text = " (该年无闰月)",
                                    fontSize = 10.5.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                        Text(
                            text = if (canBeLeap) "当前选择月份为闰月月份，可勾选设为闰月" else "非闰月月份（仅当月份为闰月时生效）",
                            fontSize = 9.5.sp,
                            color = Color.Gray
                        )
                    }

                    Switch(
                        checked = isLeapMonth && canBeLeap,
                        onCheckedChange = { isLeapMonth = it },
                        enabled = canBeLeap
                    )
                }
            }

            // 实时换算预览条
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFEFF6FF))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text(
                    text = previewInfo,
                    fontSize = 10.5.sp,
                    color = SapphireBlue,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 性别选择
            Text(
                text = "性别",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                listOf(Gender.MALE, Gender.FEMALE).forEach { g ->
                    val isSel = (selectedGender == g)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) VermilionRed else Color(0xFFF1F5F9))
                            .clickable { selectedGender = g }
                            .padding(horizontal = 22.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = if (g == Gender.MALE) "乾造 (男)" else "坤造 (女)",
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSel) Color.White else Color.DarkGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 时辰选择
            Text(
                text = "出生时辰 (十二时辰)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.height(5.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                itemsIndexed(Constants.CHINESE_TIME) { idx, timeName ->
                    val isSel = (selectedTimeIndex == idx)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) VermilionRed else Color(0xFFF1F5F9))
                            .clickable { selectedTimeIndex = idx }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = timeName,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else Color.DarkGray
                            )
                            Text(
                                text = Constants.TIME_RANGE[idx],
                                fontSize = 8.sp,
                                color = if (isSel) Color.White.copy(alpha = 0.85f) else Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 调整闰月开关 (按十五分前后月)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "按十五分前后月", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Text(text = "农历闰月十五前按本月计，十五后按下月计", fontSize = 9.5.sp, color = Color.Gray)
                }
                Switch(
                    checked = fixLeap,
                    onCheckedChange = { fixLeap = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val finalSolarDate: String
                    val finalLunarY: Int
                    val finalLunarM: Int
                    val finalLunarD: Int
                    val finalIsLeap: Boolean

                    if (!isLunarMode) {
                        val sy = solarYearText.trim().toIntOrNull() ?: 2000
                        val sm = (solarMonthText.trim().toIntOrNull() ?: 8).coerceIn(1, 12)
                        val sd = (solarDayText.trim().toIntOrNull() ?: 16).coerceIn(1, 31)
                        finalSolarDate = "$sy-$sm-$sd"
                        val lInfo = LunarCalendarHelper.solarToLunar(finalSolarDate)
                        finalLunarY = lInfo.lunarYear
                        finalLunarM = lInfo.lunarMonth
                        finalLunarD = lInfo.lunarDay
                        finalIsLeap = lInfo.isLeap
                    } else {
                        finalLunarY = lunarYearText.trim().toIntOrNull() ?: 2000
                        finalLunarM = (lunarMonthText.trim().toIntOrNull() ?: 7).coerceIn(1, 12)
                        finalLunarD = (lunarDayText.trim().toIntOrNull() ?: 17).coerceIn(1, 30)
                        finalIsLeap = isLeapMonth && (leapMonthOfYear == finalLunarM)
                        finalSolarDate = LunarCalendarHelper.lunarToSolar(finalLunarY, finalLunarM, finalLunarD, finalIsLeap)
                    }

                    onConfirm(
                        finalSolarDate,
                        selectedTimeIndex,
                        selectedGender,
                        fixLeap,
                        isLunarMode,
                        finalLunarY,
                        finalLunarM,
                        finalLunarD,
                        finalIsLeap
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = VermilionRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text("开始排盘", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}
