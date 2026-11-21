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

package com.movtery.zalithlauncher.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Tema desain "Futuristik" (nama kode Cryogenic): emas hangat di atas navy pekat,
 * dengan permukaan kaca gelap dan bernuansa futuristik.
 * Palet ini dibuat sendiri mengikuti arah visual yang dijelaskan di
 * https://github.com/RussoViller/Cryogenic-Launcher (repo itu tidak berisi kode, hanya deskripsi).
 */
internal val cryogenicDark: ColorScheme = darkColorScheme(
    primary = Color(0xFFF5B52E),
    onPrimary = Color(0xFF3A2600),
    primaryContainer = Color(0xFF5C4208),
    onPrimaryContainer = Color(0xFFFFE3A3),
    secondary = Color(0xFFE9C46A),
    onSecondary = Color(0xFF3A2C00),
    secondaryContainer = Color(0xFF4A3A10),
    onSecondaryContainer = Color(0xFFFFEDBF),
    tertiary = Color(0xFFFFDB7A),
    onTertiary = Color(0xFF3A2C00),
    tertiaryContainer = Color(0xFF574500),
    onTertiaryContainer = Color(0xFFFFEFC2),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF050B1E),
    onBackground = Color(0xFFF2F0EA),
    surface = Color(0xFF0A1128),
    onSurface = Color(0xFFF2F0EA),
    surfaceVariant = Color(0xFF1B2440),
    onSurfaceVariant = Color(0xFFC9C5B8),
    outline = Color(0xFF8C7A47),
    outlineVariant = Color(0xFF5E4E1E),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFF2F0EA),
    inverseOnSurface = Color(0xFF141A30),
    inversePrimary = Color(0xFF8A5A00),
    surfaceDim = Color(0xFF050B1E),
    surfaceBright = Color(0xFF2A3354),
    surfaceContainerLowest = Color(0xFF030716),
    surfaceContainerLow = Color(0xFF0B1330),
    surfaceContainer = Color(0xFF111A3A),
    surfaceContainerHigh = Color(0xFF182247),
    surfaceContainerHighest = Color(0xFF212C57),
)

internal val cryogenicLight: ColorScheme = lightColorScheme(
    primary = Color(0xFF8A5A00),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDEA0),
    onPrimaryContainer = Color(0xFF2B1A00),
    secondary = Color(0xFF6E5A1C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF9E2A0),
    onSecondaryContainer = Color(0xFF241A00),
    tertiary = Color(0xFF7A6000),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFE9A6),
    onTertiaryContainer = Color(0xFF241A00),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFF9EA),
    onBackground = Color(0xFF1B160A),
    surface = Color(0xFFFFF9EA),
    onSurface = Color(0xFF1B160A),
    surfaceVariant = Color(0xFFEFE3C4),
    onSurfaceVariant = Color(0xFF50462C),
    outline = Color(0xFF857550),
    outlineVariant = Color(0xFFD7C9A0),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF2E2A1C),
    inverseOnSurface = Color(0xFFF8F0DA),
    inversePrimary = Color(0xFFF5B52E),
    surfaceDim = Color(0xFFE5D9B8),
    surfaceBright = Color(0xFFFFF9EA),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFBF1D8),
    surfaceContainer = Color(0xFFF6EACB),
    surfaceContainerHigh = Color(0xFFF0E3BF),
    surfaceContainerHighest = Color(0xFFE9DCB4),
)
