package io.github.silascui.ktIztro.ai.service

import io.github.silascui.ktIztro.ai.model.AiConfig
import io.github.silascui.ktIztro.ai.model.AiProvider

object AiConfigManager {
    private const val KEY_PROVIDER = "ai_provider"
    private const val KEY_BASE_URL = "ai_base_url"
    private const val KEY_API_KEY = "ai_api_key"
    private const val KEY_MODEL_NAME = "ai_model_name"

    fun loadConfig(): AiConfig {
        val providerStr = PlatformSettings.getString(KEY_PROVIDER, AiProvider.DEEPSEEK.name)
        val provider = try {
            AiProvider.valueOf(providerStr)
        } catch (e: Exception) {
            AiProvider.DEEPSEEK
        }

        val baseUrl = PlatformSettings.getString(KEY_BASE_URL, provider.defaultBaseUrl)
        val apiKey = PlatformSettings.getString(KEY_API_KEY, "")
        val modelName = PlatformSettings.getString(KEY_MODEL_NAME, provider.defaultModel)

        return AiConfig(
            provider = provider,
            baseUrl = baseUrl,
            apiKey = apiKey,
            modelName = modelName
        )
    }

    fun saveConfig(config: AiConfig) {
        PlatformSettings.putString(KEY_PROVIDER, config.provider.name)
        PlatformSettings.putString(KEY_BASE_URL, config.baseUrl)
        PlatformSettings.putString(KEY_API_KEY, config.apiKey)
        PlatformSettings.putString(KEY_MODEL_NAME, config.modelName)
    }
}
