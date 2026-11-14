/*
 * Zalith Launcher 2
 * Copyright (C) 2025-2026 MovTery <movtery228@qq.com> and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/gpl-3.0.txt>.
 */

package com.movtery.zalithlauncher.game.launch

import com.movtery.zalithlauncher.path.PathManager
import com.movtery.zalithlauncher.utils.logging.Logger
import java.io.File
import java.security.MessageDigest
import java.util.Properties

/**
 * Cache hasil pemindaian folder mods saat peluncuran.
 *
 * Tanpa cache, setiap kali Main ditekan semua jar di folder mods dibuka dan dibaca lagi (lambat bila mod banyak).
 * Cache hanya dipakai bila daftar file mod (nama, ukuran, waktu ubah) sama persis dengan pemindaian sebelumnya;
 * bila ada mod ditambah/dihapus/diubah, pemindaian penuh dilakukan lagi.
 */
object ModScanCache {
    private const val TAG = "ModScanCache"

    /** Hasil pemindaian yang dipakai saat peluncuran */
    class Result(val fpeEmulatorModDetected: Boolean, val touchProxy: Boolean)

    private val lock = Any()

    private fun cacheFile() = File(PathManager.DIR_FILES_PRIVATE, "mod_scan.cache")

    /** Tanda tangan folder mods: berubah jika ada file mod yang ditambah, dihapus, atau diubah */
    fun signature(modsDir: File): String {
        val files = modsDir.listFiles()?.filter { !it.isDirectory }.orEmpty()
        val digest = MessageDigest.getInstance("SHA-1")
        files.sortedBy { it.name }.forEach { file ->
            digest.update("${file.name}|${file.length()}|${file.lastModified()}\n".toByteArray())
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    /** @return hasil pemindaian sebelumnya jika folder mods tidak berubah, selain itu null */
    fun read(modsDir: File, signature: String): Result? = synchronized(lock) {
        runCatching {
            val file = cacheFile().takeIf { it.isFile } ?: return null
            val props = Properties().apply { file.inputStream().use { load(it) } }
            val parts = props.getProperty(modsDir.absolutePath)?.split('|') ?: return null
            if (parts.size != 3 || parts[0] != signature) return null
            Result(
                fpeEmulatorModDetected = parts[1] == "1",
                touchProxy = parts[2] == "1"
            )
        }.onFailure {
            Logger.warning(TAG, "Failed to read the mod scan cache", it)
        }.getOrNull()
    }

    fun write(modsDir: File, signature: String, result: Result) = synchronized(lock) {
        runCatching {
            val file = cacheFile()
            val props = Properties()
            if (file.isFile) file.inputStream().use { props.load(it) }
            props.setProperty(
                modsDir.absolutePath,
                "$signature|${if (result.fpeEmulatorModDetected) 1 else 0}|${if (result.touchProxy) 1 else 0}"
            )
            file.outputStream().use { props.store(it, null) }
        }.onFailure {
            Logger.warning(TAG, "Failed to write the mod scan cache", it)
        }
    }
}
