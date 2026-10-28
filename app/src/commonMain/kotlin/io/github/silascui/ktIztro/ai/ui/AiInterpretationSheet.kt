package io.github.silascui.ktIztro.ai.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.silascui.ktIztro.ai.model.AiChatMessage
import io.github.silascui.ktIztro.ai.model.AiConfig
import io.github.silascui.ktIztro.ai.prompt.AiPromptBuilder
import io.github.silascui.ktIztro.ai.service.AiCancellable
import io.github.silascui.ktIztro.ai.service.AiChatService
import io.github.silascui.ktIztro.ai.service.AiConfigManager
import io.github.silascui.ktIztro.ui.theme.AmberGold
import io.github.silascui.ktIztro.ui.theme.SapphireBlue
import io.github.silascui.ktIztro.ui.theme.VermilionRed
import io.github.silascui.ktIztro.model.Astrolabe
import io.github.silascui.ktIztro.model.Horoscope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiInterpretationSheet(
    astrolabe: Astrolabe,
    horoscope: Horoscope?,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var config by remember { mutableStateOf(AiConfigManager.loadConfig()) }
    var showConfigDialog by remember { mutableStateOf(false) }

    val messages = remember { mutableStateListOf<AiChatMessage>() }
    var isGenerating by remember { mutableStateOf(false) }
    var currentCall by remember { mutableStateOf<AiCancellable?>(null) }
    var inputQuery by remember { mutableStateOf("") }

    val listState = rememberLazyListState()

    val quickTopics = remember {
        listOf(
            "🌟 全盘总论" to "全盘总论",
            "💼 事业官禄" to "事业官禄",
            "💰 财运财富" to "财运财富",
            "❤️ 婚恋感情" to "婚恋感情",
            "📅 流年运势" to "流年运势",
            "🩺 健康疾厄" to "健康疾厄"
        )
    }

    // 发送消息核心逻辑
    fun sendMessage(userText: String, topicKey: String? = null) {
        if (isGenerating) return

        val chartContext = AiPromptBuilder.buildChartContext(astrolabe, horoscope)
        val questionPrompt = if (topicKey != null) {
            AiPromptBuilder.buildQuestionPrompt(topicKey)
        } else {
            userText
        }

        // 构建请求列表：包含系统人设与命盘事实
        val apiMessages = mutableListOf<AiChatMessage>()
        apiMessages.add(
            AiChatMessage(
                role = "system",
                content = "${AiPromptBuilder.buildSystemPrompt()}\n\n以下为当前用户的客观紫微斗数排盘数据事实：\n$chartContext"
            )
        )

        // 添加历史对话（除去上一轮的 system）
        messages.forEach { m ->
            apiMessages.add(m)
        }

        // 添加当前用户输入
        val displayUserMsg = AiChatMessage(
            role = "user",
            content = userText
        )
        messages.add(displayUserMsg)

        val apiUserMsg = if (topicKey != null) {
            AiChatMessage(role = "user", content = questionPrompt)
        } else {
            displayUserMsg
        }
        apiMessages.add(apiUserMsg)

        // 占位 Assistant 消息
        val assistantMsgId = io.github.silascui.ktIztro.ai.model.randomUuid()
        val assistantMsg = AiChatMessage(
            id = assistantMsgId,
            role = "assistant",
            content = "",
            isStreaming = true
        )
        messages.add(assistantMsg)
        isGenerating = true

        coroutineScope.launch {
            listState.animateScrollToItem(messages.size - 1)
        }

        val call = AiChatService.streamChatCompletion(
            config = config,
            messages = apiMessages,
            onDelta = { delta, reasoningDelta ->
                val idx = messages.indexOfFirst { it.id == assistantMsgId }
                if (idx != -1) {
                    val current = messages[idx]
                    val updatedReasoning = if (reasoningDelta != null) {
                        (current.reasoningContent ?: "") + reasoningDelta
                    } else {
                        current.reasoningContent
                    }
                    val updated = current.copy(
                        content = current.content + delta,
                        reasoningContent = updatedReasoning
                    )
                    messages[idx] = updated
                }
            },
            onComplete = { _, _ ->
                val idx = messages.indexOfFirst { it.id == assistantMsgId }
                if (idx != -1) {
                    messages[idx] = messages[idx].copy(isStreaming = false)
                }
                isGenerating = false
                currentCall = null
            },
            onError = { err ->
                val idx = messages.indexOfFirst { it.id == assistantMsgId }
                if (idx != -1) {
                    val current = messages[idx]
                    val errText = if (current.content.isEmpty()) {
                        "⚠️ 解盘请求异常: ${err.message}\n请点击右上角⚙️检查 API Key 或 Base URL 配置。"
                    } else {
                        current.content + "\n\n⚠️ [生成中断: ${err.message}]"
                    }
                    messages[idx] = current.copy(content = errText, isStreaming = false)
                }
                isGenerating = false
                currentCall = null
            }
        )

        currentCall = call
    }

    ModalBottomSheet(
        onDismissRequest = {
            currentCall?.cancel()
            onDismiss()
        },
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            // 1. 顶栏：标题、模型状态标识、配置按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "🔮 AI 命理大师解盘",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = VermilionRed
                        )
                        Box(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(SapphireBlue.copy(alpha = 0.1f))
                                .clickable { showConfigDialog = true }
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = config.modelName,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SapphireBlue
                            )
                        }
                    }
                    Text(
                        text = "基于本地 `:core` 精准排盘事实 · 大模型智能推理",
                        fontSize = 9.5.sp,
                        color = Color.Gray
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (messages.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                currentCall?.cancel()
                                messages.clear()
                                isGenerating = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "清空会话",
                                tint = Color.Gray
                            )
                        }
                    }

                    IconButton(onClick = { showConfigDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "模型设置",
                            tint = VermilionRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // 2. 快捷解盘提示词胶囊导航
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(quickTopics) { (label, key) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .border(0.6.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .clickable(enabled = !isGenerating) {
                                sendMessage(label, key)
                            }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isGenerating) Color.Gray else Color(0xFF1E293B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. 对话气泡展示列表
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty()) {
                    // 空白引导页
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "点击上方快捷胶囊，开启专业解盘",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = VermilionRed
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "AI 将直接读取当前命盘（${astrolabe.chineseDate} · ${astrolabe.fiveElementsClass}）的所有星曜庙旺、四化与三方四正事实，不靠盲猜，杜绝幻觉。",
                            fontSize = 11.sp,
                            color = Color.Gray,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        if (config.apiKey.isBlank() && config.provider != io.github.silascui.ktIztro.ai.model.AiProvider.OLLAMA) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AmberGold.copy(alpha = 0.12f))
                                    .clickable { showConfigDialog = true }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "💡 您尚未配置 API Key，点此配置 DeepSeek / Qwen 密钥",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = AmberGold
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(messages) { msg ->
                            ChatMessageBubble(msg)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 4. 底部自由提问输入栏
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = { Text("针对此盘追问任何问题...", fontSize = 12.sp) },
                    singleLine = true,
                    enabled = !isGenerating,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                if (isGenerating) {
                    IconButton(
                        onClick = {
                            currentCall?.cancel()
                            isGenerating = false
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(VermilionRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "停止生成",
                            tint = Color.White
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            val text = inputQuery.trim()
                            if (text.isNotEmpty()) {
                                inputQuery = ""
                                sendMessage(text)
                            }
                        },
                        enabled = inputQuery.isNotBlank(),
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(if (inputQuery.isNotBlank()) VermilionRed else Color(0xFFE2E8F0))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "发送",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }

    if (showConfigDialog) {
        AiConfigDialog(
            initialConfig = config,
            onSave = { newConfig ->
                config = newConfig
                AiConfigManager.saveConfig(newConfig)
                showConfigDialog = false
            },
            onDismiss = { showConfigDialog = false }
        )
    }
}

@Composable
private fun ChatMessageBubble(message: AiChatMessage) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(VermilionRed)
                    .padding(2.dp)
            ) {
                Text(text = "师", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Column(
            modifier = Modifier.weight(1f, fill = false),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // 若有思考过程 (DeepSeek-R1 / Qwen Reasoning)
            var showReasoning by remember { mutableStateOf(false) }
            if (!message.reasoningContent.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFFF1F5F9))
                        .clickable { showReasoning = !showReasoning }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (showReasoning) "▼ 收起思考过程" else "▶ 展开思维链推理 (${message.reasoningContent.length}字)",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                }

                AnimatedVisibility(visible = showReasoning) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(0.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        SelectionContainer {
                            Text(
                                text = message.reasoningContent,
                                fontSize = 10.5.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isUser) 12.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 12.dp
                        )
                    )
                    .background(if (isUser) VermilionRed else Color(0xFFF8FAFC))
                    .border(
                        width = if (isUser) 0.dp else 0.8.dp,
                        color = if (isUser) Color.Transparent else Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            topEnd = 12.dp,
                            bottomStart = if (isUser) 12.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 12.dp
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                SelectionContainer {
                    val displayText = if (message.isStreaming && message.content.isEmpty()) {
                        "命理大师正在研判星盘中..."
                    } else if (message.isStreaming) {
                        message.content + " ▌"
                    } else {
                        message.content
                    }

                    Text(
                        text = displayText,
                        color = if (isUser) Color.White else Color(0xFF1E293B),
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFCBD5E1))
            ) {
                Text(text = "我", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
