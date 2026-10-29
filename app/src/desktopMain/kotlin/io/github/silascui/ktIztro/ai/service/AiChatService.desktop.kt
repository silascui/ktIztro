package io.github.silascui.ktIztro.ai.service

import io.github.silascui.ktIztro.ai.model.AiChatMessage
import io.github.silascui.ktIztro.ai.model.AiConfig
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

actual object AiChatService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    actual fun streamChatCompletion(
        config: AiConfig,
        messages: List<AiChatMessage>,
        onDelta: (contentDelta: String, reasoningDelta: String?) -> Unit,
        onComplete: (fullContent: String, fullReasoning: String?) -> Unit,
        onError: (Throwable) -> Unit
    ): AiCancellable {
        val baseUrl = config.baseUrl.trim().removeSuffix("/")
        val endpoint = if (baseUrl.endsWith("/chat/completions")) baseUrl else "$baseUrl/chat/completions"

        val jsonBody = JSONObject().apply {
            put("model", config.modelName.trim())
            put("stream", true)
            put("temperature", 0.7)

            val msgsArray = JSONArray()
            messages.forEach { m ->
                val mObj = JSONObject().apply {
                    put("role", m.role)
                    put("content", m.content)
                }
                msgsArray.put(mObj)
            }
            put("messages", msgsArray)
        }

        val requestBuilder = Request.Builder()
            .url(endpoint)
            .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .header("Content-Type", "application/json")

        if (config.apiKey.isNotBlank()) {
            requestBuilder.header("Authorization", "Bearer ${config.apiKey.trim()}")
        }

        val call = client.newCall(requestBuilder.build())

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (!call.isCanceled()) {
                    onError(e)
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string() ?: ""
                    val errorMsg = try {
                        val obj = JSONObject(errorBody)
                        obj.optJSONObject("error")?.optString("message")
                            ?: obj.optString("message", "HTTP ${response.code}: $errorBody")
                    } catch (e: Exception) {
                        "HTTP ${response.code}: $errorBody"
                    }
                    onError(Exception(errorMsg))
                    return
                }

                val body = response.body
                if (body == null) {
                    onError(Exception("Empty response body"))
                    return
                }

                val fullContent = StringBuilder()
                val fullReasoning = StringBuilder()

                try {
                    val reader = BufferedReader(InputStreamReader(body.byteStream()))
                    var line: String?

                    while (reader.readLine().also { line = it } != null) {
                        if (call.isCanceled()) break

                        val trimmed = line?.trim() ?: continue
                        if (trimmed.isEmpty() || trimmed.startsWith(":") || trimmed == "data:") continue

                        if (trimmed == "data: [DONE]") {
                            break
                        }

                        if (trimmed.startsWith("data: ")) {
                            val dataJson = trimmed.substring(6)
                            try {
                                val root = JSONObject(dataJson)
                                val choices = root.optJSONArray("choices") ?: continue
                                if (choices.length() > 0) {
                                    val choice = choices.getJSONObject(0)
                                    val delta = choice.optJSONObject("delta")
                                    val contentDelta = delta?.optString("content", "") ?: ""
                                    val reasoningDelta = if (delta != null && delta.has("reasoning_content")) {
                                        delta.optString("reasoning_content", "").ifEmpty { null }
                                    } else null

                                    if (contentDelta.isNotEmpty() || (reasoningDelta != null && reasoningDelta.isNotEmpty())) {
                                        if (contentDelta.isNotEmpty()) fullContent.append(contentDelta)
                                        if (reasoningDelta != null && reasoningDelta.isNotEmpty()) fullReasoning.append(reasoningDelta)
                                        onDelta(contentDelta, reasoningDelta)
                                    }
                                }
                            } catch (e: Exception) {
                                // 忽略单行解析错误
                            }
                        }
                    }

                    onComplete(fullContent.toString(), if (fullReasoning.isEmpty()) null else fullReasoning.toString())
                } catch (e: Exception) {
                    if (!call.isCanceled()) {
                        onError(e)
                    }
                } finally {
                    body.close()
                }
            }
        })

        return AiCancellable { call.cancel() }
    }
}
