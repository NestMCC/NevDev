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

import androidx.compose.animation.core.animateFloat
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Density
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamicColorScheme
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.setting.enums.isLauncherInDarkTheme
import com.movtery.zalithlauncher.ui.theme.components.activeMaskView
import com.movtery.zalithlauncher.utils.festival.Festival
import com.movtery.zalithlauncher.utils.festival.LocalFestivals
import com.movtery.zalithlauncher.viewmodel.BackgroundViewModel
import com.movtery.zalithlauncher.viewmodel.LocalBackgroundViewModel

private val embermireLight = lightColorScheme(
    primary = primaryLight.embermire,
    onPrimary = onPrimaryLight.embermire,
    primaryContainer = primaryContainerLight.embermire,
    onPrimaryContainer = onPrimaryContainerLight.embermire,
    secondary = secondaryLight.embermire,
    onSecondary = onSecondaryLight.embermire,
    secondaryContainer = secondaryContainerLight.embermire,
    onSecondaryContainer = onSecondaryContainerLight.embermire,
    tertiary = tertiaryLight.embermire,
    onTertiary = onTertiaryLight.embermire,
    tertiaryContainer = tertiaryContainerLight.embermire,
    onTertiaryContainer = onTertiaryContainerLight.embermire,
    error = errorLight.embermire,
    onError = onErrorLight.embermire,
    errorContainer = errorContainerLight.embermire,
    onErrorContainer = onErrorContainerLight.embermire,
    background = backgroundLight.embermire,
    onBackground = onBackgroundLight.embermire,
    surface = surfaceLight.embermire,
    onSurface = onSurfaceLight.embermire,
    surfaceVariant = surfaceVariantLight.embermire,
    onSurfaceVariant = onSurfaceVariantLight.embermire,
    outline = outlineLight.embermire,
    outlineVariant = outlineVariantLight.embermire,
    scrim = scrimLight.embermire,
    inverseSurface = inverseSurfaceLight.embermire,
    inverseOnSurface = inverseOnSurfaceLight.embermire,
    inversePrimary = inversePrimaryLight.embermire,
    surfaceDim = surfaceDimLight.embermire,
    surfaceBright = surfaceBrightLight.embermire,
    surfaceContainerLowest = surfaceContainerLowestLight.embermire,
    surfaceContainerLow = surfaceContainerLowLight.embermire,
    surfaceContainer = surfaceContainerLight.embermire,
    surfaceContainerHigh = surfaceContainerHighLight.embermire,
    surfaceContainerHighest = surfaceContainerHighestLight.embermire,
)

private val embermireDark = darkColorScheme(
    primary = primaryDark.embermire,
    onPrimary = onPrimaryDark.embermire,
    primaryContainer = primaryContainerDark.embermire,
    onPrimaryContainer = onPrimaryContainerDark.embermire,
    secondary = secondaryDark.embermire,
    onSecondary = onSecondaryDark.embermire,
    secondaryContainer = secondaryContainerDark.embermire,
    onSecondaryContainer = onSecondaryContainerDark.embermire,
    tertiary = tertiaryDark.embermire,
    onTertiary = onTertiaryDark.embermire,
    tertiaryContainer = tertiaryContainerDark.embermire,
    onTertiaryContainer = onTertiaryContainerDark.embermire,
    error = errorDark.embermire,
    onError = onErrorDark.embermire,
    errorContainer = errorContainerDark.embermire,
    onErrorContainer = onErrorContainerDark.embermire,
    background = backgroundDark.embermire,
    onBackground = onBackgroundDark.embermire,
    surface = surfaceDark.embermire,
    onSurface = onSurfaceDark.embermire,
    surfaceVariant = surfaceVariantDark.embermire,
    onSurfaceVariant = onSurfaceVariantDark.embermire,
    outline = outlineDark.embermire,
    outlineVariant = outlineVariantDark.embermire,
    scrim = scrimDark.embermire,
    inverseSurface = inverseSurfaceDark.embermire,
    inverseOnSurface = inverseOnSurfaceDark.embermire,
    inversePrimary = inversePrimaryDark.embermire,
    surfaceDim = surfaceDimDark.embermire,
    surfaceBright = surfaceBrightDark.embermire,
    surfaceContainerLowest = surfaceContainerLowestDark.embermire,
    surfaceContainerLow = surfaceContainerLowDark.embermire,
    surfaceContainer = surfaceContainerDark.embermire,
    surfaceContainerHigh = surfaceContainerHighDark.embermire,
    surfaceContainerHighest = surfaceContainerHighestDark.embermire,
)

private val velvetRoseLight = lightColorScheme(
    primary = primaryLight.velvetRose,
    onPrimary = onPrimaryLight.velvetRose,
    primaryContainer = primaryContainerLight.velvetRose,
    onPrimaryContainer = onPrimaryContainerLight.velvetRose,
    secondary = secondaryLight.velvetRose,
    onSecondary = onSecondaryLight.velvetRose,
    secondaryContainer = secondaryContainerLight.velvetRose,
    onSecondaryContainer = onSecondaryContainerLight.velvetRose,
    tertiary = tertiaryLight.velvetRose,
    onTertiary = onTertiaryLight.velvetRose,
    tertiaryContainer = tertiaryContainerLight.velvetRose,
    onTertiaryContainer = onTertiaryContainerLight.velvetRose,
    error = errorLight.velvetRose,
    onError = onErrorLight.velvetRose,
    errorContainer = errorContainerLight.velvetRose,
    onErrorContainer = onErrorContainerLight.velvetRose,
    background = backgroundLight.velvetRose,
    onBackground = onBackgroundLight.velvetRose,
    surface = surfaceLight.velvetRose,
    onSurface = onSurfaceLight.velvetRose,
    surfaceVariant = surfaceVariantLight.velvetRose,
    onSurfaceVariant = onSurfaceVariantLight.velvetRose,
    outline = outlineLight.velvetRose,
    outlineVariant = outlineVariantLight.velvetRose,
    scrim = scrimLight.velvetRose,
    inverseSurface = inverseSurfaceLight.velvetRose,
    inverseOnSurface = inverseOnSurfaceLight.velvetRose,
    inversePrimary = inversePrimaryLight.velvetRose,
    surfaceDim = surfaceDimLight.velvetRose,
    surfaceBright = surfaceBrightLight.velvetRose,
    surfaceContainerLowest = surfaceContainerLowestLight.velvetRose,
    surfaceContainerLow = surfaceContainerLowLight.velvetRose,
    surfaceContainer = surfaceContainerLight.velvetRose,
    surfaceContainerHigh = surfaceContainerHighLight.velvetRose,
    surfaceContainerHighest = surfaceContainerHighestLight.velvetRose,
)

private val velvetRoseDark = darkColorScheme(
    primary = primaryDark.velvetRose,
    onPrimary = onPrimaryDark.velvetRose,
    primaryContainer = primaryContainerDark.velvetRose,
    onPrimaryContainer = onPrimaryContainerDark.velvetRose,
    secondary = secondaryDark.velvetRose,
    onSecondary = onSecondaryDark.velvetRose,
    secondaryContainer = secondaryContainerDark.velvetRose,
    onSecondaryContainer = onSecondaryContainerDark.velvetRose,
    tertiary = tertiaryDark.velvetRose,
    onTertiary = onTertiaryDark.velvetRose,
    tertiaryContainer = tertiaryContainerDark.velvetRose,
    onTertiaryContainer = onTertiaryContainerDark.velvetRose,
    error = errorDark.velvetRose,
    onError = onErrorDark.velvetRose,
    errorContainer = errorContainerDark.velvetRose,
    onErrorContainer = onErrorContainerDark.velvetRose,
    background = backgroundDark.velvetRose,
    onBackground = onBackgroundDark.velvetRose,
    surface = surfaceDark.velvetRose,
    onSurface = onSurfaceDark.velvetRose,
    surfaceVariant = surfaceVariantDark.velvetRose,
    onSurfaceVariant = onSurfaceVariantDark.velvetRose,
    outline = outlineDark.velvetRose,
    outlineVariant = outlineVariantDark.velvetRose,
    scrim = scrimDark.velvetRose,
    inverseSurface = inverseSurfaceDark.velvetRose,
    inverseOnSurface = inverseOnSurfaceDark.velvetRose,
    inversePrimary = inversePrimaryDark.velvetRose,
    surfaceDim = surfaceDimDark.velvetRose,
    surfaceBright = surfaceBrightDark.velvetRose,
    surfaceContainerLowest = surfaceContainerLowestDark.velvetRose,
    surfaceContainerLow = surfaceContainerLowDark.velvetRose,
    surfaceContainer = surfaceContainerDark.velvetRose,
    surfaceContainerHigh = surfaceContainerHighDark.velvetRose,
    surfaceContainerHighest = surfaceContainerHighestDark.velvetRose,
)

private val mistwaveLight = lightColorScheme(
    primary = primaryLight.mistwave,
    onPrimary = onPrimaryLight.mistwave,
    primaryContainer = primaryContainerLight.mistwave,
    onPrimaryContainer = onPrimaryContainerLight.mistwave,
    secondary = secondaryLight.mistwave,
    onSecondary = onSecondaryLight.mistwave,
    secondaryContainer = secondaryContainerLight.mistwave,
    onSecondaryContainer = onSecondaryContainerLight.mistwave,
    tertiary = tertiaryLight.mistwave,
    onTertiary = onTertiaryLight.mistwave,
    tertiaryContainer = tertiaryContainerLight.mistwave,
    onTertiaryContainer = onTertiaryContainerLight.mistwave,
    error = errorLight.mistwave,
    onError = onErrorLight.mistwave,
    errorContainer = errorContainerLight.mistwave,
    onErrorContainer = onErrorContainerLight.mistwave,
    background = backgroundLight.mistwave,
    onBackground = onBackgroundLight.mistwave,
    surface = surfaceLight.mistwave,
    onSurface = onSurfaceLight.mistwave,
    surfaceVariant = surfaceVariantLight.mistwave,
    onSurfaceVariant = onSurfaceVariantLight.mistwave,
    outline = outlineLight.mistwave,
    outlineVariant = outlineVariantLight.mistwave,
    scrim = scrimLight.mistwave,
    inverseSurface = inverseSurfaceLight.mistwave,
    inverseOnSurface = inverseOnSurfaceLight.mistwave,
    inversePrimary = inversePrimaryLight.mistwave,
    surfaceDim = surfaceDimLight.mistwave,
    surfaceBright = surfaceBrightLight.mistwave,
    surfaceContainerLowest = surfaceContainerLowestLight.mistwave,
    surfaceContainerLow = surfaceContainerLowLight.mistwave,
    surfaceContainer = surfaceContainerLight.mistwave,
    surfaceContainerHigh = surfaceContainerHighLight.mistwave,
    surfaceContainerHighest = surfaceContainerHighestLight.mistwave,
)

private val mistwaveDark = darkColorScheme(
    primary = primaryDark.mistwave,
    onPrimary = onPrimaryDark.mistwave,
    primaryContainer = primaryContainerDark.mistwave,
    onPrimaryContainer = onPrimaryContainerDark.mistwave,
    secondary = secondaryDark.mistwave,
    onSecondary = onSecondaryDark.mistwave,
    secondaryContainer = secondaryContainerDark.mistwave,
    onSecondaryContainer = onSecondaryContainerDark.mistwave,
    tertiary = tertiaryDark.mistwave,
    onTertiary = onTertiaryDark.mistwave,
    tertiaryContainer = tertiaryContainerDark.mistwave,
    onTertiaryContainer = onTertiaryContainerDark.mistwave,
    error = errorDark.mistwave,
    onError = onErrorDark.mistwave,
    errorContainer = errorContainerDark.mistwave,
    onErrorContainer = onErrorContainerDark.mistwave,
    background = backgroundDark.mistwave,
    onBackground = onBackgroundDark.mistwave,
    surface = surfaceDark.mistwave,
    onSurface = onSurfaceDark.mistwave,
    surfaceVariant = surfaceVariantDark.mistwave,
    onSurfaceVariant = onSurfaceVariantDark.mistwave,
    outline = outlineDark.mistwave,
    outlineVariant = outlineVariantDark.mistwave,
    scrim = scrimDark.mistwave,
    inverseSurface = inverseSurfaceDark.mistwave,
    inverseOnSurface = inverseOnSurfaceDark.mistwave,
    inversePrimary = inversePrimaryDark.mistwave,
    surfaceDim = surfaceDimDark.mistwave,
    surfaceBright = surfaceBrightDark.mistwave,
    surfaceContainerLowest = surfaceContainerLowestDark.mistwave,
    surfaceContainerLow = surfaceContainerLowDark.mistwave,
    surfaceContainer = surfaceContainerDark.mistwave,
    surfaceContainerHigh = surfaceContainerHighDark.mistwave,
    surfaceContainerHighest = surfaceContainerHighestDark.mistwave,
)

private val glacierLight = lightColorScheme(
    primary = primaryLight.glacier,
    onPrimary = onPrimaryLight.glacier,
    primaryContainer = primaryContainerLight.glacier,
    onPrimaryContainer = onPrimaryContainerLight.glacier,
    secondary = secondaryLight.glacier,
    onSecondary = onSecondaryLight.glacier,
    secondaryContainer = secondaryContainerLight.glacier,
    onSecondaryContainer = onSecondaryContainerLight.glacier,
    tertiary = tertiaryLight.glacier,
    onTertiary = onTertiaryLight.glacier,
    tertiaryContainer = tertiaryContainerLight.glacier,
    onTertiaryContainer = onTertiaryContainerLight.glacier,
    error = errorLight.glacier,
    onError = onErrorLight.glacier,
    errorContainer = errorContainerLight.glacier,
    onErrorContainer = onErrorContainerLight.glacier,
    background = backgroundLight.glacier,
    onBackground = onBackgroundLight.glacier,
    surface = surfaceLight.glacier,
    onSurface = onSurfaceLight.glacier,
    surfaceVariant = surfaceVariantLight.glacier,
    onSurfaceVariant = onSurfaceVariantLight.glacier,
    outline = outlineLight.glacier,
    outlineVariant = outlineVariantLight.glacier,
    scrim = scrimLight.glacier,
    inverseSurface = inverseSurfaceLight.glacier,
    inverseOnSurface = inverseOnSurfaceLight.glacier,
    inversePrimary = inversePrimaryLight.glacier,
    surfaceDim = surfaceDimLight.glacier,
    surfaceBright = surfaceBrightLight.glacier,
    surfaceContainerLowest = surfaceContainerLowestLight.glacier,
    surfaceContainerLow = surfaceContainerLowLight.glacier,
    surfaceContainer = surfaceContainerLight.glacier,
    surfaceContainerHigh = surfaceContainerHighLight.glacier,
    surfaceContainerHighest = surfaceContainerHighestLight.glacier,
)

private val glacierDark = darkColorScheme(
    primary = primaryDark.glacier,
    onPrimary = onPrimaryDark.glacier,
    primaryContainer = primaryContainerDark.glacier,
    onPrimaryContainer = onPrimaryContainerDark.glacier,
    secondary = secondaryDark.glacier,
    onSecondary = onSecondaryDark.glacier,
    secondaryContainer = secondaryContainerDark.glacier,
    onSecondaryContainer = onSecondaryContainerDark.glacier,
    tertiary = tertiaryDark.glacier,
    onTertiary = onTertiaryDark.glacier,
    tertiaryContainer = tertiaryContainerDark.glacier,
    onTertiaryContainer = onTertiaryContainerDark.glacier,
    error = errorDark.glacier,
    onError = onErrorDark.glacier,
    errorContainer = errorContainerDark.glacier,
    onErrorContainer = onErrorContainerDark.glacier,
    background = backgroundDark.glacier,
    onBackground = onBackgroundDark.glacier,
    surface = surfaceDark.glacier,
    onSurface = onSurfaceDark.glacier,
    surfaceVariant = surfaceVariantDark.glacier,
    onSurfaceVariant = onSurfaceVariantDark.glacier,
    outline = outlineDark.glacier,
    outlineVariant = outlineVariantDark.glacier,
    scrim = scrimDark.glacier,
    inverseSurface = inverseSurfaceDark.glacier,
    inverseOnSurface = inverseOnSurfaceDark.glacier,
    inversePrimary = inversePrimaryDark.glacier,
    surfaceDim = surfaceDimDark.glacier,
    surfaceBright = surfaceBrightDark.glacier,
    surfaceContainerLowest = surfaceContainerLowestDark.glacier,
    surfaceContainerLow = surfaceContainerLowDark.glacier,
    surfaceContainer = surfaceContainerDark.glacier,
    surfaceContainerHigh = surfaceContainerHighDark.glacier,
    surfaceContainerHighest = surfaceContainerHighestDark.glacier,
)

private val verdantFieldLight = lightColorScheme(
    primary = primaryLight.verdantField,
    onPrimary = onPrimaryLight.verdantField,
    primaryContainer = primaryContainerLight.verdantField,
    onPrimaryContainer = onPrimaryContainerLight.verdantField,
    secondary = secondaryLight.verdantField,
    onSecondary = onSecondaryLight.verdantField,
    secondaryContainer = secondaryContainerLight.verdantField,
    onSecondaryContainer = onSecondaryContainerLight.verdantField,
    tertiary = tertiaryLight.verdantField,
    onTertiary = onTertiaryLight.verdantField,
    tertiaryContainer = tertiaryContainerLight.verdantField,
    onTertiaryContainer = onTertiaryContainerLight.verdantField,
    error = errorLight.verdantField,
    onError = onErrorLight.verdantField,
    errorContainer = errorContainerLight.verdantField,
    onErrorContainer = onErrorContainerLight.verdantField,
    background = backgroundLight.verdantField,
    onBackground = onBackgroundLight.verdantField,
    surface = surfaceLight.verdantField,
    onSurface = onSurfaceLight.verdantField,
    surfaceVariant = surfaceVariantLight.verdantField,
    onSurfaceVariant = onSurfaceVariantLight.verdantField,
    outline = outlineLight.verdantField,
    outlineVariant = outlineVariantLight.verdantField,
    scrim = scrimLight.verdantField,
    inverseSurface = inverseSurfaceLight.verdantField,
    inverseOnSurface = inverseOnSurfaceLight.verdantField,
    inversePrimary = inversePrimaryLight.verdantField,
    surfaceDim = surfaceDimLight.verdantField,
    surfaceBright = surfaceBrightLight.verdantField,
    surfaceContainerLowest = surfaceContainerLowestLight.verdantField,
    surfaceContainerLow = surfaceContainerLowLight.verdantField,
    surfaceContainer = surfaceContainerLight.verdantField,
    surfaceContainerHigh = surfaceContainerHighLight.verdantField,
    surfaceContainerHighest = surfaceContainerHighestLight.verdantField,
)

private val verdantFieldDark = darkColorScheme(
    primary = primaryDark.verdantField,
    onPrimary = onPrimaryDark.verdantField,
    primaryContainer = primaryContainerDark.verdantField,
    onPrimaryContainer = onPrimaryContainerDark.verdantField,
    secondary = secondaryDark.verdantField,
    onSecondary = onSecondaryDark.verdantField,
    secondaryContainer = secondaryContainerDark.verdantField,
    onSecondaryContainer = onSecondaryContainerDark.verdantField,
    tertiary = tertiaryDark.verdantField,
    onTertiary = onTertiaryDark.verdantField,
    tertiaryContainer = tertiaryContainerDark.verdantField,
    onTertiaryContainer = onTertiaryContainerDark.verdantField,
    error = errorDark.verdantField,
    onError = onErrorDark.verdantField,
    errorContainer = errorContainerDark.verdantField,
    onErrorContainer = onErrorContainerDark.verdantField,
    background = backgroundDark.verdantField,
    onBackground = onBackgroundDark.verdantField,
    surface = surfaceDark.verdantField,
    onSurface = onSurfaceDark.verdantField,
    surfaceVariant = surfaceVariantDark.verdantField,
    onSurfaceVariant = onSurfaceVariantDark.verdantField,
    outline = outlineDark.verdantField,
    outlineVariant = outlineVariantDark.verdantField,
    scrim = scrimDark.verdantField,
    inverseSurface = inverseSurfaceDark.verdantField,
    inverseOnSurface = inverseOnSurfaceDark.verdantField,
    inversePrimary = inversePrimaryDark.verdantField,
    surfaceDim = surfaceDimDark.verdantField,
    surfaceBright = surfaceBrightDark.verdantField,
    surfaceContainerLowest = surfaceContainerLowestDark.verdantField,
    surfaceContainerLow = surfaceContainerLowDark.verdantField,
    surfaceContainer = surfaceContainerDark.verdantField,
    surfaceContainerHigh = surfaceContainerHighDark.verdantField,
    surfaceContainerHighest = surfaceContainerHighestDark.verdantField,
)

private val urbanAshLight = lightColorScheme(
    primary = primaryLight.urbanAsh,
    onPrimary = onPrimaryLight.urbanAsh,
    primaryContainer = primaryContainerLight.urbanAsh,
    onPrimaryContainer = onPrimaryContainerLight.urbanAsh,
    secondary = secondaryLight.urbanAsh,
    onSecondary = onSecondaryLight.urbanAsh,
    secondaryContainer = secondaryContainerLight.urbanAsh,
    onSecondaryContainer = onSecondaryContainerLight.urbanAsh,
    tertiary = tertiaryLight.urbanAsh,
    onTertiary = onTertiaryLight.urbanAsh,
    tertiaryContainer = tertiaryContainerLight.urbanAsh,
    onTertiaryContainer = onTertiaryContainerLight.urbanAsh,
    error = errorLight.urbanAsh,
    onError = onErrorLight.urbanAsh,
    errorContainer = errorContainerLight.urbanAsh,
    onErrorContainer = onErrorContainerLight.urbanAsh,
    background = backgroundLight.urbanAsh,
    onBackground = onBackgroundLight.urbanAsh,
    surface = surfaceLight.urbanAsh,
    onSurface = onSurfaceLight.urbanAsh,
    surfaceVariant = surfaceVariantLight.urbanAsh,
    onSurfaceVariant = onSurfaceVariantLight.urbanAsh,
    outline = outlineLight.urbanAsh,
    outlineVariant = outlineVariantLight.urbanAsh,
    scrim = scrimLight.urbanAsh,
    inverseSurface = inverseSurfaceLight.urbanAsh,
    inverseOnSurface = inverseOnSurfaceLight.urbanAsh,
    inversePrimary = inversePrimaryLight.urbanAsh,
    surfaceDim = surfaceDimLight.urbanAsh,
    surfaceBright = surfaceBrightLight.urbanAsh,
    surfaceContainerLowest = surfaceContainerLowestLight.urbanAsh,
    surfaceContainerLow = surfaceContainerLowLight.urbanAsh,
    surfaceContainer = surfaceContainerLight.urbanAsh,
    surfaceContainerHigh = surfaceContainerHighLight.urbanAsh,
    surfaceContainerHighest = surfaceContainerHighestLight.urbanAsh,
)

private val urbanAshDark = darkColorScheme(
    primary = primaryDark.urbanAsh,
    onPrimary = onPrimaryDark.urbanAsh,
    primaryContainer = primaryContainerDark.urbanAsh,
    onPrimaryContainer = onPrimaryContainerDark.urbanAsh,
    secondary = secondaryDark.urbanAsh,
    onSecondary = onSecondaryDark.urbanAsh,
    secondaryContainer = secondaryContainerDark.urbanAsh,
    onSecondaryContainer = onSecondaryContainerDark.urbanAsh,
    tertiary = tertiaryDark.urbanAsh,
    onTertiary = onTertiaryDark.urbanAsh,
    tertiaryContainer = tertiaryContainerDark.urbanAsh,
    onTertiaryContainer = onTertiaryContainerDark.urbanAsh,
    error = errorDark.urbanAsh,
    onError = onErrorDark.urbanAsh,
    errorContainer = errorContainerDark.urbanAsh,
    onErrorContainer = onErrorContainerDark.urbanAsh,
    background = backgroundDark.urbanAsh,
    onBackground = onBackgroundDark.urbanAsh,
    surface = surfaceDark.urbanAsh,
    onSurface = onSurfaceDark.urbanAsh,
    surfaceVariant = surfaceVariantDark.urbanAsh,
    onSurfaceVariant = onSurfaceVariantDark.urbanAsh,
    outline = outlineDark.urbanAsh,
    outlineVariant = outlineVariantDark.urbanAsh,
    scrim = scrimDark.urbanAsh,
    inverseSurface = inverseSurfaceDark.urbanAsh,
    inverseOnSurface = inverseOnSurfaceDark.urbanAsh,
    inversePrimary = inversePrimaryDark.urbanAsh,
    surfaceDim = surfaceDimDark.urbanAsh,
    surfaceBright = surfaceBrightDark.urbanAsh,
    surfaceContainerLowest = surfaceContainerLowestDark.urbanAsh,
    surfaceContainerLow = surfaceContainerLowDark.urbanAsh,
    surfaceContainer = surfaceContainerDark.urbanAsh,
    surfaceContainerHigh = surfaceContainerHighDark.urbanAsh,
    surfaceContainerHighest = surfaceContainerHighestDark.urbanAsh,
)

private val verdantDawnLight = lightColorScheme(
    primary = primaryLight.verdantDawn,
    onPrimary = onPrimaryLight.verdantDawn,
    primaryContainer = primaryContainerLight.verdantDawn,
    onPrimaryContainer = onPrimaryContainerLight.verdantDawn,
    secondary = secondaryLight.verdantDawn,
    onSecondary = onSecondaryLight.verdantDawn,
    secondaryContainer = secondaryContainerLight.verdantDawn,
    onSecondaryContainer = onSecondaryContainerLight.verdantDawn,
    tertiary = tertiaryLight.verdantDawn,
    onTertiary = onTertiaryLight.verdantDawn,
    tertiaryContainer = tertiaryContainerLight.verdantDawn,
    onTertiaryContainer = onTertiaryContainerLight.verdantDawn,
    error = errorLight.verdantDawn,
    onError = onErrorLight.verdantDawn,
    errorContainer = errorContainerLight.verdantDawn,
    onErrorContainer = onErrorContainerLight.verdantDawn,
    background = backgroundLight.verdantDawn,
    onBackground = onBackgroundLight.verdantDawn,
    surface = surfaceLight.verdantDawn,
    onSurface = onSurfaceLight.verdantDawn,
    surfaceVariant = surfaceVariantLight.verdantDawn,
    onSurfaceVariant = onSurfaceVariantLight.verdantDawn,
    outline = outlineLight.verdantDawn,
    outlineVariant = outlineVariantLight.verdantDawn,
    scrim = scrimLight.verdantDawn,
    inverseSurface = inverseSurfaceLight.verdantDawn,
    inverseOnSurface = inverseOnSurfaceLight.verdantDawn,
    inversePrimary = inversePrimaryLight.verdantDawn,
    surfaceDim = surfaceDimLight.verdantDawn,
    surfaceBright = surfaceBrightLight.verdantDawn,
    surfaceContainerLowest = surfaceContainerLowestLight.verdantDawn,
    surfaceContainerLow = surfaceContainerLowLight.verdantDawn,
    surfaceContainer = surfaceContainerLight.verdantDawn,
    surfaceContainerHigh = surfaceContainerHighLight.verdantDawn,
    surfaceContainerHighest = surfaceContainerHighestLight.verdantDawn,
)

private val nevLavaLight = lightColorScheme(
    primary = primaryLight.nevLava,
    onPrimary = onPrimaryLight.nevLava,
    primaryContainer = primaryContainerLight.nevLava,
    onPrimaryContainer = onPrimaryContainerLight.nevLava,
    secondary = secondaryLight.nevLava,
    onSecondary = onSecondaryLight.nevLava,
    secondaryContainer = secondaryContainerLight.nevLava,
    onSecondaryContainer = onSecondaryContainerLight.nevLava,
    tertiary = tertiaryLight.nevLava,
    onTertiary = onTertiaryLight.nevLava,
    tertiaryContainer = tertiaryContainerLight.nevLava,
    onTertiaryContainer = onTertiaryContainerLight.nevLava,
    error = errorLight.nevLava,
    onError = onErrorLight.nevLava,
    errorContainer = errorContainerLight.nevLava,
    onErrorContainer = onErrorContainerLight.nevLava,
    background = backgroundLight.nevLava,
    onBackground = onBackgroundLight.nevLava,
    surface = surfaceLight.nevLava,
    onSurface = onSurfaceLight.nevLava,
    surfaceVariant = surfaceVariantLight.nevLava,
    onSurfaceVariant = onSurfaceVariantLight.nevLava,
    outline = outlineLight.nevLava,
    outlineVariant = outlineVariantLight.nevLava,
    scrim = scrimLight.nevLava,
    inverseSurface = inverseSurfaceLight.nevLava,
    inverseOnSurface = inverseOnSurfaceLight.nevLava,
    inversePrimary = inversePrimaryLight.nevLava,
    surfaceDim = surfaceDimLight.nevLava,
    surfaceBright = surfaceBrightLight.nevLava,
    surfaceContainerLowest = surfaceContainerLowestLight.nevLava,
    surfaceContainerLow = surfaceContainerLowLight.nevLava,
    surfaceContainer = surfaceContainerLight.nevLava,
    surfaceContainerHigh = surfaceContainerHighLight.nevLava,
    surfaceContainerHighest = surfaceContainerHighestLight.nevLava,
)

private val verdantDawnDark = darkColorScheme(
    primary = primaryDark.verdantDawn,
    onPrimary = onPrimaryDark.verdantDawn,
    primaryContainer = primaryContainerDark.verdantDawn,
    onPrimaryContainer = onPrimaryContainerDark.verdantDawn,
    secondary = secondaryDark.verdantDawn,
    onSecondary = onSecondaryDark.verdantDawn,
    secondaryContainer = secondaryContainerDark.verdantDawn,
    onSecondaryContainer = onSecondaryContainerDark.verdantDawn,
    tertiary = tertiaryDark.verdantDawn,
    onTertiary = onTertiaryDark.verdantDawn,
    tertiaryContainer = tertiaryContainerDark.verdantDawn,
    onTertiaryContainer = onTertiaryContainerDark.verdantDawn,
    error = errorDark.verdantDawn,
    onError = onErrorDark.verdantDawn,
    errorContainer = errorContainerDark.verdantDawn,
    onErrorContainer = onErrorContainerDark.verdantDawn,
    background = backgroundDark.verdantDawn,
    onBackground = onBackgroundDark.verdantDawn,
    surface = surfaceDark.verdantDawn,
    onSurface = onSurfaceDark.verdantDawn,
    surfaceVariant = surfaceVariantDark.verdantDawn,
    onSurfaceVariant = onSurfaceVariantDark.verdantDawn,
    outline = outlineDark.verdantDawn,
    outlineVariant = outlineVariantDark.verdantDawn,
    scrim = scrimDark.verdantDawn,
    inverseSurface = inverseSurfaceDark.verdantDawn,
    inverseOnSurface = inverseOnSurfaceDark.verdantDawn,
    inversePrimary = inversePrimaryDark.verdantDawn,
    surfaceDim = surfaceDimDark.verdantDawn,
    surfaceBright = surfaceBrightDark.verdantDawn,
    surfaceContainerLowest = surfaceContainerLowestDark.verdantDawn,
    surfaceContainerLow = surfaceContainerLowDark.verdantDawn,
    surfaceContainer = surfaceContainerDark.verdantDawn,
    surfaceContainerHigh = surfaceContainerHighDark.verdantDawn,
    surfaceContainerHighest = surfaceContainerHighestDark.verdantDawn,
)

private val nevLavaDark = darkColorScheme(
    primary = primaryDark.nevLava,
    onPrimary = onPrimaryDark.nevLava,
    primaryContainer = primaryContainerDark.nevLava,
    onPrimaryContainer = onPrimaryContainerDark.nevLava,
    secondary = secondaryDark.nevLava,
    onSecondary = onSecondaryDark.nevLava,
    secondaryContainer = secondaryContainerDark.nevLava,
    onSecondaryContainer = onSecondaryContainerDark.nevLava,
    tertiary = tertiaryDark.nevLava,
    onTertiary = onTertiaryDark.nevLava,
    tertiaryContainer = tertiaryContainerDark.nevLava,
    onTertiaryContainer = onTertiaryContainerDark.nevLava,
    error = errorDark.nevLava,
    onError = onErrorDark.nevLava,
    errorContainer = errorContainerDark.nevLava,
    onErrorContainer = onErrorContainerDark.nevLava,
    background = backgroundDark.nevLava,
    onBackground = onBackgroundDark.nevLava,
    surface = surfaceDark.nevLava,
    onSurface = onSurfaceDark.nevLava,
    surfaceVariant = surfaceVariantDark.nevLava,
    onSurfaceVariant = onSurfaceVariantDark.nevLava,
    outline = outlineDark.nevLava,
    outlineVariant = outlineVariantDark.nevLava,
    scrim = scrimDark.nevLava,
    inverseSurface = inverseSurfaceDark.nevLava,
    inverseOnSurface = inverseOnSurfaceDark.nevLava,
    inversePrimary = inversePrimaryDark.nevLava,
    surfaceDim = surfaceDimDark.nevLava,
    surfaceBright = surfaceBrightDark.nevLava,
    surfaceContainerLowest = surfaceContainerLowestDark.nevLava,
    surfaceContainerLow = surfaceContainerLowDark.nevLava,
    surfaceContainer = surfaceContainerDark.nevLava,
    surfaceContainerHigh = surfaceContainerHighDark.nevLava,
    surfaceContainerHighest = surfaceContainerHighestDark.nevLava,
)

private val cyberLight = lightColorScheme(
    primary = primaryLight.cyber,
    onPrimary = onPrimaryLight.cyber,
    primaryContainer = primaryContainerLight.cyber,
    onPrimaryContainer = onPrimaryContainerLight.cyber,
    secondary = secondaryLight.cyber,
    onSecondary = onSecondaryLight.cyber,
    secondaryContainer = secondaryContainerLight.cyber,
    onSecondaryContainer = onSecondaryContainerLight.cyber,
    tertiary = tertiaryLight.cyber,
    onTertiary = onTertiaryLight.cyber,
    tertiaryContainer = tertiaryContainerLight.cyber,
    onTertiaryContainer = onTertiaryContainerLight.cyber,
    error = errorLight.cyber,
    onError = onErrorLight.cyber,
    errorContainer = errorContainerLight.cyber,
    onErrorContainer = onErrorContainerLight.cyber,
    background = backgroundLight.cyber,
    onBackground = onBackgroundLight.cyber,
    surface = surfaceLight.cyber,
    onSurface = onSurfaceLight.cyber,
    surfaceVariant = surfaceVariantLight.cyber,
    onSurfaceVariant = onSurfaceVariantLight.cyber,
    outline = outlineLight.cyber,
    outlineVariant = outlineVariantLight.cyber,
    scrim = scrimLight.cyber,
    inverseSurface = inverseSurfaceLight.cyber,
    inverseOnSurface = inverseOnSurfaceLight.cyber,
    inversePrimary = inversePrimaryLight.cyber,
    surfaceDim = surfaceDimLight.cyber,
    surfaceBright = surfaceBrightLight.cyber,
    surfaceContainerLowest = surfaceContainerLowestLight.cyber,
    surfaceContainerLow = surfaceContainerLowLight.cyber,
    surfaceContainer = surfaceContainerLight.cyber,
    surfaceContainerHigh = surfaceContainerHighLight.cyber,
    surfaceContainerHighest = surfaceContainerHighestLight.cyber,
)

private val heroDark = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF2B2B2B),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFFD9D9D9),
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF3A3A3A),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = Color(0xFFB8B8B8),
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFF333333),
    onTertiaryContainer = Color(0xFFF2F2F2),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF000000),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF0A0A0A),
    onSurface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFF1F1F1F),
    onSurfaceVariant = Color(0xFFCFCFCF),
    outline = Color(0xFF8A8A8A),
    outlineVariant = Color(0xFF3A3A3A),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFFFFFFF),
    inverseOnSurface = Color(0xFF111111),
    inversePrimary = Color(0xFF000000),
    surfaceDim = Color(0xFF000000),
    surfaceBright = Color(0xFF2A2A2A),
    surfaceContainerLowest = Color(0xFF000000),
    surfaceContainerLow = Color(0xFF0A0A0A),
    surfaceContainer = Color(0xFF111111),
    surfaceContainerHigh = Color(0xFF1A1A1A),
    surfaceContainerHighest = Color(0xFF242424),
)

private val heroLight = lightColorScheme(
    primary = Color(0xFF000000),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE6E6E6),
    onPrimaryContainer = Color(0xFF000000),
    secondary = Color(0xFF2B2B2B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDADADA),
    onSecondaryContainer = Color(0xFF000000),
    tertiary = Color(0xFF555555),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE0E0E0),
    onTertiaryContainer = Color(0xFF111111),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFFAFAFA),
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFEDEDED),
    onSurfaceVariant = Color(0xFF444444),
    outline = Color(0xFF777777),
    outlineVariant = Color(0xFFCCCCCC),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF111111),
    inverseOnSurface = Color(0xFFFFFFFF),
    inversePrimary = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFE2E2E2),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF5F5F5),
    surfaceContainer = Color(0xFFEFEFEF),
    surfaceContainerHigh = Color(0xFFE8E8E8),
    surfaceContainerHighest = Color(0xFFE0E0E0),
)

private val liquidDark = darkColorScheme(
    primary = Color(0xFF5AC8FA),
    onPrimary = Color(0xFF00344A),
    primaryContainer = Color(0xFF0B4F6C),
    onPrimaryContainer = Color(0xFFC4EBFF),
    secondary = Color(0xFF9B8CFF),
    onSecondary = Color(0xFF261A6B),
    secondaryContainer = Color(0xFF3B3190),
    onSecondaryContainer = Color(0xFFE3DFFF),
    tertiary = Color(0xFF5EEAD4),
    onTertiary = Color(0xFF00382F),
    tertiaryContainer = Color(0xFF0E5247),
    onTertiaryContainer = Color(0xFFA0F5E4),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF060D18),
    onBackground = Color(0xFFE2EAF5),
    surface = Color(0xFF0A1422),
    onSurface = Color(0xFFE2EAF5),
    surfaceVariant = Color(0xFF1B2A3D),
    onSurfaceVariant = Color(0xFFB4C4D8),
    outline = Color(0xFF6E8199),
    outlineVariant = Color(0xFF2B3C52),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE2EAF5),
    inverseOnSurface = Color(0xFF1B2A3D),
    inversePrimary = Color(0xFF006A8E),
    surfaceDim = Color(0xFF060D18),
    surfaceBright = Color(0xFF22344B),
    surfaceContainerLowest = Color(0xFF040A12),
    surfaceContainerLow = Color(0xFF0A1422),
    surfaceContainer = Color(0xFF0F1B2D),
    surfaceContainerHigh = Color(0xFF16243A),
    surfaceContainerHighest = Color(0xFF1D2E47),
)

private val liquidLight = lightColorScheme(
    primary = Color(0xFF006A8E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC4EBFF),
    onPrimaryContainer = Color(0xFF001E2C),
    secondary = Color(0xFF5B4FC4),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFE3DFFF),
    onSecondaryContainer = Color(0xFF16004F),
    tertiary = Color(0xFF00695C),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFA0F5E4),
    onTertiaryContainer = Color(0xFF00201A),
    background = Color(0xFFEEF6FD),
    onBackground = Color(0xFF0B1826),
    surface = Color(0xFFF4F9FE),
    onSurface = Color(0xFF0B1826),
    surfaceVariant = Color(0xFFDCE8F4),
    onSurfaceVariant = Color(0xFF3F5166),
    outline = Color(0xFF71859C),
    outlineVariant = Color(0xFFC0D0E2),
)

private val cyberDark = darkColorScheme(
    primary = primaryDark.cyber,
    onPrimary = onPrimaryDark.cyber,
    primaryContainer = primaryContainerDark.cyber,
    onPrimaryContainer = onPrimaryContainerDark.cyber,
    secondary = secondaryDark.cyber,
    onSecondary = onSecondaryDark.cyber,
    secondaryContainer = secondaryContainerDark.cyber,
    onSecondaryContainer = onSecondaryContainerDark.cyber,
    tertiary = tertiaryDark.cyber,
    onTertiary = onTertiaryDark.cyber,
    tertiaryContainer = tertiaryContainerDark.cyber,
    onTertiaryContainer = onTertiaryContainerDark.cyber,
    error = errorDark.cyber,
    onError = onErrorDark.cyber,
    errorContainer = errorContainerDark.cyber,
    onErrorContainer = onErrorContainerDark.cyber,
    background = backgroundDark.cyber,
    onBackground = onBackgroundDark.cyber,
    surface = surfaceDark.cyber,
    onSurface = onSurfaceDark.cyber,
    surfaceVariant = surfaceVariantDark.cyber,
    onSurfaceVariant = onSurfaceVariantDark.cyber,
    outline = outlineDark.cyber,
    outlineVariant = outlineVariantDark.cyber,
    scrim = scrimDark.cyber,
    inverseSurface = inverseSurfaceDark.cyber,
    inverseOnSurface = inverseOnSurfaceDark.cyber,
    inversePrimary = inversePrimaryDark.cyber,
    surfaceDim = surfaceDimDark.cyber,
    surfaceBright = surfaceBrightDark.cyber,
    surfaceContainerLowest = surfaceContainerLowestDark.cyber,
    surfaceContainerLow = surfaceContainerLowDark.cyber,
    surfaceContainer = surfaceContainerDark.cyber,
    surfaceContainerHigh = surfaceContainerHighDark.cyber,
    surfaceContainerHighest = surfaceContainerHighestDark.cyber,
)

private fun customLight(
    color: Color,
    style: PaletteStyle,
): ColorScheme {
    return dynamicColorScheme(
        primary = color,
        isDark = false,
        style = style,
    )
}

private fun customDark(
    color: Color,
    style: PaletteStyle,
): ColorScheme {
    return dynamicColorScheme(
        primary = color,
        isDark = true,
        style = style,
    )
}

@Composable
fun NevLauncherTheme(
    darkTheme: Boolean = isLauncherInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    applyUiScale: Boolean = true,
    backgroundViewModel: BackgroundViewModel? = null,
    festivals: List<Festival> = emptyList(),
    content: @Composable () -> Unit
) {
    val colorTheme = AllSettings.launcherColorTheme.state
    val nevStyle = nevStyleFor(AllSettings.launcherDesignStyle.state)
    val customColorInt = AllSettings.launcherCustomColor.state
    val customColor = Color(customColorInt)
    val customPaletteStyle = AllSettings.launcherCustomPaletteStyle.state

    val context = LocalContext.current

    val targetColorScheme = remember(darkTheme, colorTheme, customColor, customPaletteStyle) {
        when {
            colorTheme == ColorThemeType.DYNAMIC && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> when (colorTheme) {
                ColorThemeType.EMBERMIRE -> customDark(Color(0xFFE2641F), PaletteStyle.TonalSpot)
                ColorThemeType.VELVET_ROSE -> velvetRoseDark
                ColorThemeType.MISTWAVE -> mistwaveDark
                ColorThemeType.GLACIER -> glacierDark
                ColorThemeType.VERDANTFIELD -> verdantFieldDark
                ColorThemeType.URBAN_ASH -> urbanAshDark
                ColorThemeType.VERDANT_DAWN -> verdantDawnDark
                ColorThemeType.NEV_LAVA -> nevLavaDark
                ColorThemeType.CRYOGENIC -> cryogenicDark
                ColorThemeType.CYBER -> cyberDark
                ColorThemeType.LIQUID -> liquidDark
                ColorThemeType.HERO -> heroDark
                ColorThemeType.CUSTOM -> customDark(
                    color = customColor,
                    style = customPaletteStyle
                )
                else -> seedThemeOf(colorTheme)?.let { customDark(it.seed, it.style) } ?: embermireDark
            }

            else -> when (colorTheme) {
                ColorThemeType.EMBERMIRE -> customLight(Color(0xFFE2641F), PaletteStyle.TonalSpot)
                ColorThemeType.VELVET_ROSE -> velvetRoseLight
                ColorThemeType.MISTWAVE -> mistwaveLight
                ColorThemeType.GLACIER -> glacierLight
                ColorThemeType.VERDANTFIELD -> verdantFieldLight
                ColorThemeType.URBAN_ASH -> urbanAshLight
                ColorThemeType.VERDANT_DAWN -> verdantDawnLight
                ColorThemeType.NEV_LAVA -> nevLavaLight
                ColorThemeType.CRYOGENIC -> cryogenicLight
                ColorThemeType.CYBER -> cyberLight
                ColorThemeType.LIQUID -> liquidLight
                ColorThemeType.HERO -> heroLight
                ColorThemeType.CUSTOM -> customLight(
                    color = customColor,
                    style = customPaletteStyle
                )
                else -> seedThemeOf(colorTheme)?.let { customLight(it.seed, it.style) } ?: embermireLight
            }
        }
    }

    var currentDarkTheme by remember { mutableStateOf(darkTheme) }
    var currentDisplayScheme by remember { mutableStateOf(targetColorScheme) }

    LaunchedEffect(darkTheme, targetColorScheme) {
        if (darkTheme != currentDarkTheme) {
            context.activeMaskView(
                maskComplete = {
                    currentDarkTheme = darkTheme
                    currentDisplayScheme = targetColorScheme
                },
                maskAnimFinish = {

                }
            )
        } else {
            currentDarkTheme = darkTheme
            currentDisplayScheme = targetColorScheme
        }
    }

    //界面缩放：通过缩放 Density 让整个界面整体变大或变小
    val uiScale = if (applyUiScale) AllSettings.launcherUiScale.state / 100f else 1f
    val baseDensity = LocalDensity.current
    val scaledDensity = remember(baseDensity, uiScale) {
        Density(baseDensity.density * uiScale, baseDensity.fontScale)
    }

    //satu animasi putar bersama untuk garis tepi semua tombol Liquid (hanya berjalan bila fitur aktif)
    val liquidOn = AllSettings.controlLiquid.state
    val liquidSpin: androidx.compose.runtime.State<Float>? = if (liquidOn || nevStyle.liquid) {
        androidx.compose.animation.core.rememberInfiniteTransition(label = "liquidSpin").animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = androidx.compose.animation.core.infiniteRepeatable(
                androidx.compose.animation.core.tween(5000, easing = androidx.compose.animation.core.LinearEasing)
            ),
            label = "liquidSpinAngle"
        )
    } else null

    CompositionLocalProvider(
        LocalBackgroundViewModel provides backgroundViewModel,
        LocalFestivals provides festivals,
        LocalDensity provides scaledDensity,
        LocalNevStyle provides nevStyle,
        com.movtery.layer_controller.utils.LocalControlAccent provides
            (if (AllSettings.controlFollowTheme.state) currentDisplayScheme.primary else null),
        com.movtery.layer_controller.utils.LocalControlLiquid provides liquidOn,
        com.movtery.layer_controller.utils.LocalLiquidSpin provides liquidSpin
    ) {
        MaterialExpressiveTheme(
            colorScheme = currentDisplayScheme,
            motionScheme = MotionScheme.expressive(),
            shapes = if (nevStyle.cyber) CyberShapes else if (isBoxy()) BoxyShapes else Shapes(),
            typography = if (nevStyle.cyber) CyberTypography else AppTypography,
            content = content
        )
    }
}
