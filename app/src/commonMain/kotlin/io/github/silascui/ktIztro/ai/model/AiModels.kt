package io.github.silascui.ktIztro.ai.model

import kotlinx.datetime.Clock
import kotlin.random.Random

fun randomUuid(): String {
    val hex = "0123456789abcdef"
    val sb = StringBuilder(36)
    for (i in 0 until 36) {
        when (i) {
            8, 13, 18, 23 -> sb.append('-')
            14 -> sb.append('4')
            19 -> sb.append(hex[Random.nextInt(8, 12)])
            else -> sb.append(hex[Random.nextInt(16)])
        }
    }
    return sb.toString()
}

enum class AiProvider(val displayName: String, val defaultBaseUrl: String, val defaultModel: String) {
    DEEPSEEK(
        displayName = "DeepSeek (深度求索)",
        defaultBaseUrl = "https://api.deepseek.com/v1",
        defaultModel = "deepseek-chat"
    ),
    QWEN(
        displayName = "Qwen (阿里通义千问)",
        defaultBaseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1",
        defaultModel = "qwen-plus"
    ),
    OLLAMA(
        displayName = "Ollama (本地私有大模型)",
        defaultBaseUrl = "http://10.0.2.2:11434/v1", // 模拟器或局域网 IP
        defaultModel = "qwen2.5:14b"
    ),
    CUSTOM(
        displayName = "自定义 (OpenAI 兼容接口)",
        defaultBaseUrl = "https://api.openai.com/v1",
        defaultModel = "gpt-4o-mini"
    )
}

data class AiConfig(
    val provider: AiProvider = AiProvider.DEEPSEEK,
    val baseUrl: String = AiProvider.DEEPSEEK.defaultBaseUrl,
    val apiKey: String = "",
    val modelName: String = AiProvider.DEEPSEEK.defaultModel
)

data class AiChatMessage(
    val id: String = randomUuid(),
    val role: String, // "user", "assistant", "system"
    val content: String,
    val reasoningContent: String? = null, // 思考链内容 (如 DeepSeek-R1)
    val isStreaming: Boolean = false,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)
