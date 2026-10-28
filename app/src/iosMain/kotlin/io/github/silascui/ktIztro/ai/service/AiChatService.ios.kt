package io.github.silascui.ktIztro.ai.service

import io.github.silascui.ktIztro.ai.model.AiChatMessage
import io.github.silascui.ktIztro.ai.model.AiConfig

actual object AiChatService {
    actual fun streamChatCompletion(
        config: AiConfig,
        messages: List<AiChatMessage>,
        onDelta: (contentDelta: String, reasoningDelta: String?) -> Unit,
        onComplete: (fullContent: String, fullReasoning: String?) -> Unit,
        onError: (Throwable) -> Unit
    ): AiCancellable {
        onError(UnsupportedOperationException("AI streaming on iOS will be enabled with native NSURLSession"))
        return AiCancellable { }
    }
}
