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

import androidx.compose.ui.graphics.Color
import com.materialkolor.PaletteStyle
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.setting.AllSettings

/**
 * Tema warna tambahan yang dibangkitkan dari satu warna dasar (seed) memakai Material You.
 * Skema terang/gelap dihasilkan otomatis oleh materialkolor.
 */
class SeedTheme(
    val type: ColorThemeType,
    val nameRes: Int,
    val seed: Color,
    val style: PaletteStyle = PaletteStyle.TonalSpot
)

val seedThemes: List<SeedTheme> = listOf(
    SeedTheme(ColorThemeType.SEED_OCEAN, R.string.theme_seed_ocean, Color(0xFF1E88E5), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_SUNSET, R.string.theme_seed_sunset, Color(0xFFFF7043), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_EMERALD, R.string.theme_seed_emerald, Color(0xFF00A86B), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_AMETHYST, R.string.theme_seed_amethyst, Color(0xFF9C5CFF), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_SAKURA, R.string.theme_seed_sakura, Color(0xFFFF8FB1), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_GOLD, R.string.theme_seed_gold, Color(0xFFFFC107), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_TEAL, R.string.theme_seed_teal, Color(0xFF00897B), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_CRIMSON, R.string.theme_seed_crimson, Color(0xFFDC143C), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_LIME, R.string.theme_seed_lime, Color(0xFFAEEA00), PaletteStyle.Vibrant),
    SeedTheme(ColorThemeType.SEED_INDIGO, R.string.theme_seed_indigo, Color(0xFF3F51B5), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_MOCHA, R.string.theme_seed_mocha, Color(0xFF8D6E63), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_MINT, R.string.theme_seed_mint, Color(0xFF3EB489), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_SKY, R.string.theme_seed_sky, Color(0xFF4FC3F7), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_MAGENTA, R.string.theme_seed_magenta, Color(0xFFE91E8C), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_LAVENDER, R.string.theme_seed_lavender, Color(0xFFB39DDB), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_PEACH, R.string.theme_seed_peach, Color(0xFFFFAB91), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_SLATE, R.string.theme_seed_slate, Color(0xFF607D8B), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_NEON_GREEN, R.string.theme_seed_neon_green, Color(0xFF39FF14), PaletteStyle.Vibrant),
    SeedTheme(ColorThemeType.SEED_CHERRY, R.string.theme_seed_cherry, Color(0xFFC2185B), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_COPPER, R.string.theme_seed_copper, Color(0xFFB87333), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_ARCTIC, R.string.theme_seed_arctic, Color(0xFF80DEEA), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_FOREST, R.string.theme_seed_forest, Color(0xFF2E7D32), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_SAND, R.string.theme_seed_sand, Color(0xFFD7B98E), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_MIDNIGHT, R.string.theme_seed_midnight, Color(0xFF1A237E), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_ORANGE, R.string.theme_seed_orange, Color(0xFFFB8C00), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_GRAPE, R.string.theme_seed_grape, Color(0xFF6A1B9A), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_CORAL, R.string.theme_seed_coral, Color(0xFFFF6F61), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_OLIVE, R.string.theme_seed_olive, Color(0xFF808000), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_ELECTRIC, R.string.theme_seed_electric, Color(0xFF2979FF), PaletteStyle.Vibrant),
    SeedTheme(ColorThemeType.SEED_NEON_PURPLE, R.string.theme_seed_neon_purple, Color(0xFFD500F9), PaletteStyle.Vibrant),
    SeedTheme(ColorThemeType.SEED_MONO, R.string.theme_seed_mono, Color(0xFF9E9E9E), PaletteStyle.Monochrome),
    SeedTheme(ColorThemeType.SEED_RUBY, R.string.theme_seed_ruby, Color(0xFFE0115F), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_AQUA, R.string.theme_seed_aqua, Color(0xFF00BCD4), PaletteStyle.TonalSpot),
    SeedTheme(ColorThemeType.SEED_SUNFLOWER, R.string.theme_seed_sunflower, Color(0xFFFFD600), PaletteStyle.Vibrant),
    SeedTheme(ColorThemeType.SEED_BERRY, R.string.theme_seed_berry, Color(0xFF8E24AA), PaletteStyle.TonalSpot),
)

fun seedThemeOf(type: ColorThemeType): SeedTheme? = seedThemes.firstOrNull { it.type == type }

/** Warna contoh yang ditampilkan di pemilih tema. */
fun themeSwatchColor(type: ColorThemeType): Color = when (type) {
    ColorThemeType.DYNAMIC -> Color(0xFF8AB4F8)
    ColorThemeType.EMBERMIRE -> Color(0xFFE2641F)
    ColorThemeType.VELVET_ROSE -> Color(0xFFF9B2D2)
    ColorThemeType.MISTWAVE -> Color(0xFFCEF3F9)
    ColorThemeType.GLACIER -> Color(0xFF73D2FB)
    ColorThemeType.VERDANTFIELD -> Color(0xFFD2C972)
    ColorThemeType.URBAN_ASH -> Color(0xFFC7C6C6)
    ColorThemeType.VERDANT_DAWN -> Color(0xFF8ED88E)
    ColorThemeType.NEV_LAVA -> Color(0xFFFF5A3D)
    ColorThemeType.CRYOGENIC -> Color(0xFF4DD8F0)
    ColorThemeType.CYBER -> Color(0xFF00E5FF)
    ColorThemeType.CUSTOM -> Color(AllSettings.launcherCustomColor.state)
    else -> seedThemeOf(type)?.seed ?: Color.Gray
}
