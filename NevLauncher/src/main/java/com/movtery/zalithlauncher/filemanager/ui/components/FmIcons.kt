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

package com.movtery.zalithlauncher.filemanager.ui.components

import com.movtery.zalithlauncher.ui.theme.nevShape
import com.movtery.zalithlauncher.ui.theme.LocalNevStyle
import androidx.compose.foundation.border
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import com.movtery.zalithlauncher.ui.theme.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.filemanager.logic.entry.ArchiveType

/**
 * Jenis ikon entri; warnanya diambil dari tema launcher yang aktif (bukan warna tetap),
 * sehingga folder/berkas ikut berubah bersama tema warna.
 */
private enum class FmFileIcon(
    @DrawableRes val iconRes: Int,
    val accent: FmAccent
) {
    DIRECTORY(R.drawable.ic_folder_filled, FmAccent.PRIMARY),
    ARCHIVE(R.drawable.ic_folder_zip_filled, FmAccent.TERTIARY),
    JAR(R.drawable.ic_coffee_filled, FmAccent.SECONDARY),
    APK(R.drawable.ic_android_filled, FmAccent.PRIMARY),
    CODE(R.drawable.ic_code, FmAccent.TERTIARY),
    TEXT(R.drawable.ic_text_snippet_filled, FmAccent.SECONDARY),
    IMAGE(R.drawable.ic_image_filled, FmAccent.PRIMARY),
    AUDIO(R.drawable.ic_audio_file_filled, FmAccent.TERTIARY),
    VIDEO(R.drawable.ic_movie_filled, FmAccent.SECONDARY),
    UNKNOWN(R.drawable.ic_draft_filled, FmAccent.MUTED);

    companion object {
        fun of(name: String): FmFileIcon {
            if (ArchiveType.of(name) != null) return ARCHIVE

            return when (name.substringAfterLast('.', "").lowercase()) {
                "jar" -> JAR
                "apk" -> APK
                "java", "kt", "kts", "c", "cpp", "h", "js", "ts", "py", "sh", "xml", "html", "css",
                "json", "yaml", "yml", "toml", "ini", "cfg" -> CODE
                "txt", "log", "md" -> TEXT
                "png", "jpg", "jpeg", "gif", "bmp", "webp", "svg" -> IMAGE
                "mp3", "wav", "ogg", "flac", "m4a", "aac" -> AUDIO
                "mp4", "mkv", "avi", "mov", "webm" -> VIDEO
                else -> UNKNOWN
            }
        }
    }
}

private enum class FmAccent { PRIMARY, SECONDARY, TERTIARY, MUTED }

object FmIcons {
    /** 根据条目类型与扩展名显示对应图标 */
    @Composable
    fun IconFor(
        name: String,
        isDirectory: Boolean,
        modifier: Modifier = Modifier,
    ) {
        val style = if (isDirectory) FmFileIcon.DIRECTORY else FmFileIcon.of(name)
        val scheme = MaterialTheme.colorScheme
        val accent = when (style.accent) {
            FmAccent.PRIMARY -> scheme.primary
            FmAccent.SECONDARY -> scheme.secondary
            FmAccent.TERTIARY -> scheme.tertiary
            FmAccent.MUTED -> scheme.onSurfaceVariant
        }
        //gaya potong-sudut: ubin bersudut potong; gaya klasik: lingkaran
        val shape = if (LocalNevStyle.current.cutCorners) nevShape(12.dp) else CircleShape
        Box(
            modifier = modifier
                .size(40.dp)
                .clip(shape)
                .background(accent.copy(alpha = 0.18f))
                .border(1.dp, accent.copy(alpha = 0.55f), shape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(style.iconRes),
                contentDescription = null,
                tint = accent
            )
        }
    }
}
