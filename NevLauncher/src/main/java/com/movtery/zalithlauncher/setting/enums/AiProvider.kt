package com.movtery.zalithlauncher.setting.enums

/** Jenis API AI yang dipakai untuk analisis crash (dengan API key milik pengguna). */
enum class AiProvider(val label: String, val defaultBaseUrl: String, val defaultModel: String) {
    ANTHROPIC("Claude (Anthropic)", "https://api.anthropic.com", "claude-sonnet-5-5"),
    OPENAI_COMPATIBLE("OpenAI / kompatibel", "https://api.openai.com", "gpt-4o-mini")
}
