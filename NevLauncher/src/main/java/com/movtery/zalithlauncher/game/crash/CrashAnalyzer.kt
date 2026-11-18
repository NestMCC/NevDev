package com.movtery.zalithlauncher.game.crash

import com.movtery.zalithlauncher.R

/** Satu temuan dari analisis cepat: judul, penjelasan/saran, dan bukti dari log. */
data class CrashFinding(
    val titleRes: Int,
    val detailRes: Int,
    val evidence: String? = null,
    /** Nilai untuk disisipkan ke teks detail (mis. versi Java), boleh kosong */
    val arg: String? = null
)

/**
 * Analisis crash cepat berbasis aturan: berjalan di perangkat, tanpa internet.
 * Mendeteksi penyebab umum dari isi log atau laporan crash.
 */
object CrashAnalyzer {
    private val classVersionToJava = mapOf(
        52 to 8, 53 to 9, 54 to 10, 55 to 11, 56 to 12, 57 to 13, 58 to 14, 59 to 15, 60 to 16,
        61 to 17, 62 to 18, 63 to 19, 64 to 20, 65 to 21, 66 to 22, 67 to 23, 68 to 24, 69 to 25
    )

    fun analyze(log: String): List<CrashFinding> {
        val lines = log.lineSequence().toList()
        fun firstLine(vararg keys: String): String? =
            lines.firstOrNull { l -> keys.any { l.contains(it, ignoreCase = true) } }?.trim()?.take(220)

        val out = ArrayList<CrashFinding>()

        // Java terlalu lama untuk mod/game
        Regex("class file version (\\d+)").find(log)?.let { m ->
            val v = m.groupValues[1].toIntOrNull()
            val java = v?.let { classVersionToJava[it] }
            out += CrashFinding(
                R.string.crash_ai_java_title, R.string.crash_ai_java_detail,
                firstLine("class file version", "UnsupportedClassVersionError"),
                java?.toString() ?: "?"
            )
        } ?: run {
            if (log.contains("UnsupportedClassVersionError") || log.contains("has been compiled by a more recent version"))
                out += CrashFinding(R.string.crash_ai_java_title, R.string.crash_ai_java_detail,
                    firstLine("UnsupportedClassVersionError", "compiled by a more recent"), "?")
        }

        // RAM habis
        if (log.contains("OutOfMemoryError") || log.contains("Java heap space") ||
            log.contains("GC overhead limit") || log.contains("Out of memory")
        ) out += CrashFinding(R.string.crash_ai_oom_title, R.string.crash_ai_oom_detail,
            firstLine("OutOfMemoryError", "Java heap space", "GC overhead", "Out of memory"))

        // File jar/mod rusak
        if (log.contains("Invalid or corrupt jarfile") || log.contains("ZipException") ||
            log.contains("zip END header not found") || log.contains("zip file is empty")
        ) out += CrashFinding(R.string.crash_ai_corrupt_title, R.string.crash_ai_corrupt_detail,
            firstLine("Invalid or corrupt jarfile", "ZipException", "zip END header", "zip file is empty"))

        // Dependensi mod hilang / tidak cocok (Fabric/Quilt/Forge/NeoForge)
        if (log.contains("Incompatible mods found") || log.contains("Missing or unsupported mandatory dependencies") ||
            log.contains("Mod resolution failed") || log.contains("Unmet dependency") ||
            Regex("requires (any )?version", RegexOption.IGNORE_CASE).containsMatchIn(log) ||
            log.contains("depends on", ignoreCase = true) && log.contains("fabric", ignoreCase = true)
        ) out += CrashFinding(R.string.crash_ai_dependency_title, R.string.crash_ai_dependency_detail,
            firstLine("Incompatible mods found", "Missing or unsupported mandatory", "Unmet dependency", "requires version", "requires any version", "depends on"))

        // Mod ganda
        if (log.contains("Duplicate mods found") || log.contains("Found duplicate mods") || log.contains("duplicate mod", ignoreCase = true))
            out += CrashFinding(R.string.crash_ai_duplicate_title, R.string.crash_ai_duplicate_detail,
                firstLine("Duplicate mods", "duplicate mod"))

        // Mixin gagal: biasanya mod bentrok atau beda versi
        if (log.contains("Mixin apply failed") || log.contains("MixinApplyError") ||
            log.contains("InvalidInjectionException") || log.contains("MixinTransformerError") ||
            log.contains("Mixin transformation") && log.contains("failed")
        ) out += CrashFinding(R.string.crash_ai_mixin_title, R.string.crash_ai_mixin_detail,
            firstLine("Mixin apply failed", "MixinApplyError", "InvalidInjectionException", "MixinTransformerError"))

        // Kelas/metode hilang: versi mod tidak cocok dengan versi game/loader
        if (log.contains("NoSuchMethodError") || log.contains("NoSuchFieldError") ||
            log.contains("NoClassDefFoundError") || log.contains("ClassNotFoundException")
        ) out += CrashFinding(R.string.crash_ai_missing_class_title, R.string.crash_ai_missing_class_detail,
            firstLine("NoSuchMethodError", "NoSuchFieldError", "NoClassDefFoundError", "ClassNotFoundException"))

        // Masalah grafis / renderer
        if (log.contains("GLFW error") || log.contains("Failed to create context", ignoreCase = true) ||
            log.contains("EGL", ignoreCase = false) && log.contains("error", ignoreCase = true) ||
            log.contains("GL_INVALID") || log.contains("OpenGL", ignoreCase = true) && log.contains("fail", ignoreCase = true) ||
            log.contains("Pixel format not accelerated") || log.contains("shader", ignoreCase = true) && log.contains("compil", ignoreCase = true) && log.contains("error", ignoreCase = true)
        ) out += CrashFinding(R.string.crash_ai_gl_title, R.string.crash_ai_gl_detail,
            firstLine("GLFW error", "Failed to create context", "GL_INVALID", "Pixel format not accelerated", "EGL"))

        // Crash native (JVM/driver)
        if (log.contains("SIGSEGV") || log.contains("SIGABRT") || log.contains("SIGBUS") ||
            log.contains("hs_err_pid") || log.contains("A fatal error has been detected")
        ) out += CrashFinding(R.string.crash_ai_native_title, R.string.crash_ai_native_detail,
            firstLine("SIGSEGV", "SIGABRT", "SIGBUS", "fatal error has been detected"))

        // Mod yang disebut di jejak tumpukan
        val modJars = Regex("([A-Za-z0-9_.+\\-]+\\.jar)").findAll(log)
            .map { it.value }
            .filter { j -> !j.startsWith("client") && !j.startsWith("lwjgl") && !j.startsWith("authlib") && !j.startsWith("launchwrapper") && !j.contains("libraries") }
            .groupingBy { it }.eachCount().entries.sortedByDescending { it.value }.take(4).map { it.key }.toList()
        if (modJars.isNotEmpty() && out.isNotEmpty()) {
            out += CrashFinding(R.string.crash_ai_suspect_title, R.string.crash_ai_suspect_detail, modJars.joinToString("\n"))
        }

        // Tidak ada aturan yang cocok: tampilkan penyebab terakhir
        if (out.isEmpty()) {
            val cause = lines.lastOrNull { it.trimStart().startsWith("Caused by:") }
                ?: lines.lastOrNull { Regex("(Exception|Error)").containsMatchIn(it) && !it.trimStart().startsWith("at ") }
            out += CrashFinding(R.string.crash_ai_unknown_title, R.string.crash_ai_unknown_detail, cause?.trim()?.take(240))
        }
        return out
    }

    /** Ambil bagian log yang paling berguna untuk dikirim ke AI (maks [maxChars]). */
    fun excerptForAi(log: String, maxChars: Int = 20_000): String {
        val lines = log.lineSequence().toList()
        val keep = BooleanArray(lines.size)
        //baris penting + konteks 3 baris di sekitarnya
        lines.forEachIndexed { i, l ->
            if (l.contains("Exception") || l.contains("Error") || l.contains("Caused by") ||
                l.contains("FATAL", ignoreCase = true) || l.contains("crash", ignoreCase = true) ||
                l.contains("SIG") || l.contains("Mixin")
            ) for (j in (i - 3).coerceAtLeast(0)..(i + 3).coerceAtMost(lines.lastIndex)) keep[j] = true
        }
        //ekor log selalu ikut
        for (i in (lines.size - 120).coerceAtLeast(0) until lines.size) keep[i] = true
        val sb = StringBuilder()
        var prev = -2
        lines.forEachIndexed { i, l ->
            if (!keep[i]) return@forEachIndexed
            if (i != prev + 1) sb.append("...\n")
            sb.append(l).append('\n'); prev = i
        }
        val text = redact(sb.toString())
        return if (text.length <= maxChars) text else text.takeLast(maxChars)
    }

    /** Hapus data sensitif (token, jalur pengguna) sebelum log keluar dari perangkat. */
    fun redact(text: String): String = text
        .replace(Regex("(--accessToken|accessToken[=: ]+)\\s*\\S+", RegexOption.IGNORE_CASE), "$1 [disembunyikan]")
        .replace(Regex("(--uuid)\\s+\\S+"), "$1 [disembunyikan]")
        .replace(Regex("(--username)\\s+\\S+"), "$1 [disembunyikan]")
        .replace(Regex("(Bearer|token)[=: ]+[A-Za-z0-9._\\-]{20,}", RegexOption.IGNORE_CASE), "$1 [disembunyikan]")
        .replace(Regex("/storage/emulated/\\d+"), "/storage")
}
