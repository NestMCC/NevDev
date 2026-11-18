package com.movtery.zalithlauncher.game.crash

import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.setting.enums.AiProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Klien AI opsional untuk analisis crash. Memakai API key milik pengguna sendiri;
 * log (yang sudah disamarkan) hanya dikirim saat pengguna menekan tombol analisis.
 */
object AiCrashClient {
    private val client by lazy {
        OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .build()
    }

    private val SYSTEM_PROMPT = """
        Kamu adalah ahli diagnosa crash Minecraft Java Edition yang berjalan di Android lewat peluncur (launcher) Nev Launcher berbasis PojavLauncher.
        Pengguna mengirim potongan log atau laporan crash. Jawab dalam bahasa Indonesia yang ringkas dan mudah dipahami pemula.
        Format jawaban:
        1. "Penyebab kemungkinan": 1-3 butir, urutkan dari yang paling mungkin, sebut nama mod/komponen bila terlihat di log.
        2. "Cara memperbaiki": langkah konkret berurutan (mis. ganti versi Java, tambah RAM, hapus/perbarui mod tertentu, ganti renderer).
        3. "Tingkat keyakinan": rendah/sedang/tinggi, dan sebutkan bila log kurang lengkap.
        Jangan mengarang. Jika tidak yakin, katakan apa yang perlu dicek berikutnya. Jangan minta data pribadi.
    """.trimIndent()

    fun isConfigured(): Boolean = AllSettings.aiCrashApiKey.getValue().isNotBlank()

    /** Mengembalikan teks analisis, atau melempar exception berisi pesan kesalahan. */
    suspend fun analyze(logExcerpt: String, context: String): String = withContext(Dispatchers.IO) {
        val key = AllSettings.aiCrashApiKey.getValue().trim()
        require(key.isNotEmpty()) { "API key belum diisi" }
        val provider = AllSettings.aiCrashProvider.getValue()
        val base = AllSettings.aiCrashBaseUrl.getValue().trim().ifEmpty { provider.defaultBaseUrl }.trimEnd('/')
        val model = AllSettings.aiCrashModel.getValue().trim().ifEmpty { provider.defaultModel }
        val userText = "Konteks: $context\n\nLog:\n```\n$logExcerpt\n```"

        val request = when (provider) {
            AiProvider.ANTHROPIC -> {
                val body = JSONObject()
                    .put("model", model)
                    .put("max_tokens", 1200)
                    .put("system", SYSTEM_PROMPT)
                    .put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", userText)))
                Request.Builder()
                    .url("$base/v1/messages")
                    .header("x-api-key", key)
                    .header("anthropic-version", "2023-06-01")
                    .post(body.toString().toRequestBody("application/json".toMediaType()))
                    .build()
            }
            AiProvider.OPENAI_COMPATIBLE -> {
                val body = JSONObject()
                    .put("model", model)
                    .put("max_tokens", 1200)
                    .put("messages", JSONArray()
                        .put(JSONObject().put("role", "system").put("content", SYSTEM_PROMPT))
                        .put(JSONObject().put("role", "user").put("content", userText)))
                Request.Builder()
                    .url("$base/v1/chat/completions")
                    .header("Authorization", "Bearer $key")
                    .post(body.toString().toRequestBody("application/json".toMediaType()))
                    .build()
            }
        }

        client.newCall(request).execute().use { resp ->
            val raw = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) {
                val msg = runCatching { JSONObject(raw).optJSONObject("error")?.optString("message") }.getOrNull()
                throw RuntimeException("HTTP ${resp.code}: ${msg?.takeIf { it.isNotBlank() } ?: raw.take(200)}")
            }
            val json = JSONObject(raw)
            when (provider) {
                AiProvider.ANTHROPIC -> {
                    val arr = json.getJSONArray("content")
                    buildString {
                        for (i in 0 until arr.length()) {
                            val o = arr.getJSONObject(i)
                            if (o.optString("type") == "text") append(o.optString("text"))
                        }
                    }
                }
                AiProvider.OPENAI_COMPATIBLE ->
                    json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
            }.trim().ifEmpty { throw RuntimeException("Jawaban AI kosong") }
        }
    }
}
