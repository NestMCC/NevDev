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

package com.movtery.zalithlauncher.ui.theme

import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp

val AppTypography = Typography()

/** Tipografi Cyber: seluruh teks memakai font monospace ala terminal. */
val CyberTypography: Typography = AppTypography.let { t ->
    val m = FontFamily.Monospace
    t.copy(
        displayLarge = t.displayLarge.copy(fontFamily = m),
        displayMedium = t.displayMedium.copy(fontFamily = m),
        displaySmall = t.displaySmall.copy(fontFamily = m),
        headlineLarge = t.headlineLarge.copy(fontFamily = m),
        headlineMedium = t.headlineMedium.copy(fontFamily = m),
        headlineSmall = t.headlineSmall.copy(fontFamily = m),
        titleLarge = t.titleLarge.copy(fontFamily = m),
        titleMedium = t.titleMedium.copy(fontFamily = m),
        titleSmall = t.titleSmall.copy(fontFamily = m),
        bodyLarge = t.bodyLarge.copy(fontFamily = m),
        bodyMedium = t.bodyMedium.copy(fontFamily = m),
        bodySmall = t.bodySmall.copy(fontFamily = m),
        labelLarge = t.labelLarge.copy(fontFamily = m),
        labelMedium = t.labelMedium.copy(fontFamily = m),
        labelSmall = t.labelSmall.copy(fontFamily = m)
    )
}

/** Bentuk Material Cyber: tombol, kolom teks, chip, dan dialog bersudut potong diagonal. */
val CyberShapes = Shapes(
    extraSmall = CutCornerShape(topStart = 0.dp, topEnd = 4.dp, bottomEnd = 0.dp, bottomStart = 4.dp),
    small = CutCornerShape(topStart = 0.dp, topEnd = 6.dp, bottomEnd = 0.dp, bottomStart = 6.dp),
    medium = CutCornerShape(topStart = 0.dp, topEnd = 10.dp, bottomEnd = 0.dp, bottomStart = 10.dp),
    large = CutCornerShape(topStart = 0.dp, topEnd = 14.dp, bottomEnd = 0.dp, bottomStart = 14.dp),
    extraLarge = CutCornerShape(topStart = 0.dp, topEnd = 20.dp, bottomEnd = 0.dp, bottomStart = 20.dp)
)
