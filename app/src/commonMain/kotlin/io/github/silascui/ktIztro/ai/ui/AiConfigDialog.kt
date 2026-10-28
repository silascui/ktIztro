package io.github.silascui.ktIztro.ai.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.silascui.ktIztro.ai.model.AiConfig
import io.github.silascui.ktIztro.ai.model.AiProvider
import io.github.silascui.ktIztro.ui.theme.VermilionRed

@Composable
fun AiConfigDialog(
    initialConfig: AiConfig,
    onSave: (AiConfig) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedProvider by remember { mutableStateOf(initialConfig.provider) }
    var baseUrl by remember { mutableStateOf(initialConfig.baseUrl) }
    var apiKey by remember { mutableStateOf(initialConfig.apiKey) }
    var modelName by remember { mutableStateOf(initialConfig.modelName) }
    var showApiKey by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "AI 解盘大模型配置",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = VermilionRed
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "选择大模型提供商预设:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Provider 选择标签
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    AiProvider.values().forEach { p ->
                        val isSel = (selectedProvider == p)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isSel) VermilionRed else Color(0xFFF1F5F9))
                                .border(0.5.dp, if (isSel) VermilionRed else Color(0xFFE2E8F0), RoundedCornerShape(4.dp))
                                .clickable {
                                    selectedProvider = p
                                    baseUrl = p.defaultBaseUrl
                                    modelName = p.defaultModel
                                }
                                .padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = when (p) {
                                    AiProvider.DEEPSEEK -> "DeepSeek"
                                    AiProvider.QWEN -> "通义千问"
                                    AiProvider.OLLAMA -> "Ollama本地"
                                    AiProvider.CUSTOM -> "自定义"
                                },
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) Color.White else Color.DarkGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Base URL
                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it },
                    label = { Text("API Base URL", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Model Name
                OutlinedTextField(
                    value = modelName,
                    onValueChange = { modelName = it },
                    label = { Text("Model 模型名称", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // API Key (Ollama 不需要)
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key", fontSize = 11.sp) },
                    placeholder = { Text(if (selectedProvider == AiProvider.OLLAMA) "本地部署无需填 Key" else "sk-...", fontSize = 11.sp) },
                    singleLine = true,
                    visualTransformation = if (showApiKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        Text(
                            text = if (showApiKey) "隐藏" else "显示",
                            fontSize = 11.sp,
                            color = VermilionRed,
                            modifier = Modifier
                                .clickable { showApiKey = !showApiKey }
                                .padding(end = 8.dp)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "提示: 支持所有兼容 OpenAI 格式的大模型 (如 DeepSeek, 通义千问, Kimi, Ollama, SiliconFlow 等)。API Key 仅保存在本机私有存储，绝不上报。",
                    fontSize = 9.sp,
                    color = Color.Gray,
                    lineHeight = 12.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        AiConfig(
                            provider = selectedProvider,
                            baseUrl = baseUrl.trim(),
                            apiKey = apiKey.trim(),
                            modelName = modelName.trim()
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = VermilionRed)
            ) {
                Text("保存设置", fontSize = 13.sp)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("取消", fontSize = 13.sp)
            }
        }
    )
}
