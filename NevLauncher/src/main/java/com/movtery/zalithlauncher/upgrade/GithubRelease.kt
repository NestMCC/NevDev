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

package com.movtery.zalithlauncher.upgrade

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Respons GitHub Releases API (`/repos/{owner}/{repo}/releases/latest`).
 * Hanya field yang dipakai; field lain diabaikan.
 */
@Serializable
data class GithubRelease(
    @SerialName("tag_name")
    val tagName: String,
    @SerialName("name")
    val name: String? = null,
    @SerialName("body")
    val body: String? = null,
    @SerialName("html_url")
    val htmlUrl: String = "",
    @SerialName("published_at")
    val publishedAt: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null,
    @SerialName("assets")
    val assets: List<Asset> = emptyList()
) {
    @Serializable
    data class Asset(
        @SerialName("name")
        val name: String,
        @SerialName("browser_download_url")
        val downloadUrl: String,
        @SerialName("size")
        val size: Long = 0L
    )
}

/** Mengurai "v2.0.1", "2.0.1-beta" dst. menjadi (mayor, minor, patch); null bila tidak valid. */
fun parseVersionTriple(version: String): Triple<Int, Int, Int>? {
    val m = Regex("""(\d+)(?:\.(\d+))?(?:\.(\d+))?""").find(version) ?: return null
    val (a, b, c) = m.destructured
    return Triple(a.toIntOrNull() ?: return null, b.toIntOrNull() ?: 0, c.toIntOrNull() ?: 0)
}

/** true bila [remote] lebih baru dari [local] (dibandingkan sebagai nomor versi). */
fun isNewerVersion(remote: String, local: String): Boolean {
    val r = parseVersionTriple(remote) ?: return false
    val l = parseVersionTriple(local) ?: return true
    return compareValuesBy(r, l, { it.first }, { it.second }, { it.third }) > 0
}

private fun guessArch(fileName: String): RemoteData.RemoteFile.Arch {
    val n = fileName.lowercase()
    return when {
        "arm64" in n -> RemoteData.RemoteFile.Arch.ARM64
        "armeabi" in n || Regex("""[-_.]arm[-_.]""").containsMatchIn(n) -> RemoteData.RemoteFile.Arch.ARM
        "x86_64" in n || "x86-64" in n -> RemoteData.RemoteFile.Arch.X86_64
        "x86" in n -> RemoteData.RemoteFile.Arch.X86
        else -> RemoteData.RemoteFile.Arch.ALL
    }
}

/** Mengubah rilis GitHub menjadi [RemoteData] yang dipahami dialog pembaruan. */
fun GithubRelease.toRemoteData(): RemoteData {
    val version = tagName.removePrefix("v").removePrefix("V")
    val triple = parseVersionTriple(version) ?: Triple(0, 0, 0)
    val files = assets
        .filter { it.name.endsWith(".apk", ignoreCase = true) }
        .map {
            RemoteData.RemoteFile(
                fileName = it.name,
                uri = it.downloadUrl,
                arch = guessArch(it.name),
                size = it.size
            )
        }
    val markdown = buildString {
        body?.takeIf { it.isNotBlank() }?.let { append(it) }
        if (htmlUrl.isNotBlank()) {
            if (isNotEmpty()) append("\n\n")
            append("[GitHub Release]($htmlUrl)")
        }
    }
    return RemoteData(
        code = triple.first * 10000 + triple.second * 100 + triple.third,
        version = version,
        createdAt = publishedAt ?: createdAt ?: "1970-01-01T00:00:00Z",
        files = files,
        defaultBody = RemoteData.RemoteBody(language = "", markdown = markdown),
        bodies = emptyList()
    )
}
