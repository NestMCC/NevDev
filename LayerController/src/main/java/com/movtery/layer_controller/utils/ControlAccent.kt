package com.movtery.layer_controller.utils

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpaces
import androidx.compose.ui.graphics.lerp

/**
 * Warna aksen tema launcher untuk tombol kontrol. Null = nonaktif (pakai warna bawaan gaya).
 */
val LocalControlAccent = staticCompositionLocalOf<Color?> { null }

/** Apakah warna ini termasuk merah/oranye terang bawaan Nev (yang boleh diganti mengikuti tema). */
internal fun Color.isNevReddish(): Boolean {
    if (alpha < 0.05f) return false
    val r = red; val g = green; val b = blue
    val max = maxOf(r, g, b)
    val min = minOf(r, g, b)
    if (max < 0.55f) return false
    val sat = if (max == 0f) 0f else (max - min) / max
    if (sat < 0.55f || r < g || r < b) return false
    //hue merah..oranye (sampai ~40 derajat)
    val hue = if (max == min) 0f else 60f * ((g - b) / (max - min))
    return hue in -25f..40f
}

/** Latar gelap bawaan Nev (hampir hitam kemerahan). */
internal fun Color.isNevDarkReddish(): Boolean {
    if (alpha < 0.05f) return false
    val max = maxOf(red, green, blue)
    return max < 0.2f && red >= green && red >= blue && red > 0.02f
}

/** Latar tekan bawaan Nev (merah tua pekat). */
internal fun Color.isNevPressedReddish(): Boolean {
    if (alpha < 0.05f) return false
    val max = maxOf(red, green, blue)
    return max in 0.4f..0.85f && red >= green * 1.6f && red >= blue * 1.6f
}

internal fun Color.themedBorder(accent: Color): Color =
    if (isNevReddish()) accent.copy(alpha = alpha) else this

internal fun Color.themedPressedBorder(accent: Color): Color =
    if (isNevReddish()) lerp(accent, Color.White, 0.25f).copy(alpha = alpha) else this

internal fun Color.themedBackground(accent: Color): Color =
    if (isNevDarkReddish()) lerp(Color.Black, accent, 0.09f).copy(alpha = alpha) else this

internal fun Color.themedPressedBackground(accent: Color): Color =
    if (isNevPressedReddish()) lerp(Color.Black, accent, 0.7f).copy(alpha = alpha) else this
