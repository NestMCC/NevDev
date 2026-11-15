/*
 * Nev Launcher
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

package com.movtery.zalithlauncher.setting.enums

import androidx.compose.ui.layout.ContentScale
import androidx.media3.ui.AspectRatioFrameLayout
import com.movtery.zalithlauncher.R

/**
 * Cara latar belakang (gambar/video) mengisi layar.
 */
enum class BackgroundScale(
    val textRes: Int,
    val contentScale: ContentScale,
    val resizeMode: Int
) {
    /** Seluruh gambar terlihat dan memenuhi layar (bisa sedikit melar) */
    Stretch(R.string.settings_launcher_background_scale_stretch, ContentScale.FillBounds, AspectRatioFrameLayout.RESIZE_MODE_FILL),

    /** Seluruh gambar terlihat dengan rasio asli (bisa ada bilah kosong di tepi) */
    Fit(R.string.settings_launcher_background_scale_fit, ContentScale.Fit, AspectRatioFrameLayout.RESIZE_MODE_FIT),

    /** Memenuhi layar dengan rasio asli, tepi gambar terpotong */
    Crop(R.string.settings_launcher_background_scale_crop, ContentScale.Crop, AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
}
