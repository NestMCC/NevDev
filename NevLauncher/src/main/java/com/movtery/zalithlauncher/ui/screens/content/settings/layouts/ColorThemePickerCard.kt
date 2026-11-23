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


package com.movtery.zalithlauncher.ui.screens.content.settings.layouts

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.components.TitleAndSummary
import com.movtery.zalithlauncher.ui.theme.CircleShape
import com.movtery.zalithlauncher.ui.theme.ColorThemeType
import com.movtery.zalithlauncher.ui.theme.RoundedCornerShape
import com.movtery.zalithlauncher.ui.theme.seedThemeOf
import com.movtery.zalithlauncher.ui.theme.themeSwatchColor

/** Urutan tampil: tema bawaan dulu, lalu tema tambahan, "Kustom" di akhir. */
private val builtInOrder = listOf(
    ColorThemeType.DYNAMIC,
    ColorThemeType.EMBERMIRE,
    ColorThemeType.VELVET_ROSE,
    ColorThemeType.MISTWAVE,
    ColorThemeType.GLACIER,
    ColorThemeType.VERDANTFIELD,
    ColorThemeType.URBAN_ASH,
    ColorThemeType.VERDANT_DAWN,
    ColorThemeType.NEV_LAVA,
    ColorThemeType.CRYOGENIC,
    ColorThemeType.CYBER,
    ColorThemeType.HERO
)

@Composable
private fun themeLabel(type: ColorThemeType): String = when (type) {
    ColorThemeType.DYNAMIC -> stringResource(R.string.theme_color_dynamic)
    ColorThemeType.EMBERMIRE -> stringResource(R.string.theme_color_embermire)
    ColorThemeType.VELVET_ROSE -> stringResource(R.string.theme_color_velvet_rose)
    ColorThemeType.MISTWAVE -> stringResource(R.string.theme_color_mistwave)
    ColorThemeType.GLACIER -> stringResource(R.string.theme_color_glacier)
    ColorThemeType.VERDANTFIELD -> stringResource(R.string.theme_color_verdant_field)
    ColorThemeType.URBAN_ASH -> stringResource(R.string.theme_color_urban_ash)
    ColorThemeType.VERDANT_DAWN -> stringResource(R.string.theme_color_verdant_dawn)
    ColorThemeType.NEV_LAVA -> stringResource(R.string.theme_color_nev_lava)
    ColorThemeType.CRYOGENIC -> stringResource(R.string.theme_color_cryogenic)
    ColorThemeType.CYBER -> stringResource(R.string.theme_color_cyber)
    ColorThemeType.LIQUID -> stringResource(R.string.theme_color_liquid)
    ColorThemeType.HERO -> stringResource(R.string.theme_color_hero)
    ColorThemeType.CUSTOM -> stringResource(R.string.generic_custom)
    else -> seedThemeOf(type)?.let { stringResource(it.nameRes) } ?: type.name
}

/**
 * Pemilih tema warna berupa deretan chip dengan titik warna contoh.
 * Menggantikan daftar radio agar puluhan tema tetap mudah dilihat dan dipilih.
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)
@Composable
fun ColorThemePickerCard(
    position: CardPosition,
    onCustomClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selected = AllSettings.launcherColorTheme.state
    val order = builtInOrder +
            com.movtery.zalithlauncher.ui.theme.seedThemes.map { it.type } +
            ColorThemeType.CUSTOM

    SettingsCard(modifier = modifier, position = position) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TitleAndSummary(
                title = stringResource(R.string.settings_launcher_color_theme_title),
                summary = stringResource(R.string.settings_launcher_color_theme_summary)
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                order.forEach { type ->
                    val enabled = type != ColorThemeType.DYNAMIC || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    ThemeChip(
                        type = type,
                        label = themeLabel(type),
                        selected = selected == type,
                        enabled = enabled,
                        onClick = {
                            AllSettings.launcherColorTheme.save(type)
                            if (type == ColorThemeType.CUSTOM) onCustomClick()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeChip(
    type: ColorThemeType,
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(50)
    val primary = MaterialTheme.colorScheme.primary
    val swatch = themeSwatchColor(type)
    Row(
        modifier = Modifier
            .alpha(if (enabled) 1f else 0.4f)
            .clip(shape)
            .border(
                if (selected) BorderStroke(2.dp, primary)
                else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                shape
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(start = 8.dp, end = 14.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(
                    if (type == ColorThemeType.DYNAMIC) {
                        Brush.sweepGradient(
                            listOf(
                                Color(0xFF4285F4), Color(0xFF34A853),
                                Color(0xFFFBBC05), Color(0xFFEA4335), Color(0xFF4285F4)
                            )
                        )
                    } else {
                        Brush.linearGradient(listOf(swatch, swatch))
                    }
                )
        ) {}
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
