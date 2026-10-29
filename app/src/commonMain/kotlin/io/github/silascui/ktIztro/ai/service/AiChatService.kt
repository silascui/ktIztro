package io.github.silascui.ktIztro.ai.service

import io.github.silascui.ktIztro.ai.model.AiChatMessage
import io.github.silascui.ktIztro.ai.model.AiConfig

fun interface AiCancellable {
    fun cancel()
}

expect object AiChatService {
    fun streamChatCompletion(
        config: AiConfig,
        messages: List<AiChatMessage>,
        onDelta: (contentDelta: String, reasoningDelta: String?) -> Unit,
        onComplete: (fullContent: String, fullReasoning: String?) -> Unit,
        onError: (Throwable) -> Unit
    ): AiCancellable
}
