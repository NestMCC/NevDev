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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.ui.components.SliderValueEditDialog
import com.movtery.zalithlauncher.ui.screens.content.elements.DisabledAlpha
import com.movtery.zalithlauncher.ui.theme.nevShape
import java.util.Locale
import kotlin.math.roundToInt

/** Pilihan cepat RAM (MB). Yang melebihi batas RAM perangkat tidak ditampilkan. */
private val QuickPicksMb = listOf(512, 1024, 2048, 3072, 4096)

private fun formatRam(mb: Int): String =
    String.format(Locale.getDefault(), "%d MB (%.1f GB)", mb, mb / 1024f)

private fun quickLabel(mb: Int): String = if (mb < 1024) "${mb}M" else "${mb / 1024}G"

/**
 * Kartu alokasi RAM desain baru: judul + chip nilai (ketuk untuk mengetik angka),
 * slider pil bertitik dengan penanda vertikal, dan baris "Pilihan Cepat".
 *
 * @param appendContent konten tambahan di kanan chip (mis. kotak centang "ikut global")
 * @param previewContent isi di bawah (pratinjau pemakaian RAM)
 */
@Composable
fun RamAllocationSettingsCard(
    value: Int,
    position: CardPosition,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Int) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.settings_game_ram_max_title),
    enabled: Boolean = true,
    appendContent: @Composable () -> Unit = {},
    previewContent: @Composable ColumnScope.() -> Unit = {}
) {
    SettingsCard(
        modifier = modifier,
        position = position,
        outerShape = 28.dp,
        innerShape = 4.dp
    ) {
        var showEditDialog by remember { mutableStateOf(false) }
        val primary = MaterialTheme.colorScheme.primary

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            //judul + chip nilai
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .alpha(if (enabled) 1f else DisabledAlpha),
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                val chipShape = nevShape(12.dp)
                Box(
                    modifier = Modifier
                        .alpha(if (enabled) 1f else DisabledAlpha)
                        .clip(chipShape)
                        .background(primary.copy(alpha = 0.14f))
                        .border(1.dp, primary.copy(alpha = 0.7f), chipShape)
                        .clickable(enabled = enabled) { showEditDialog = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = formatRam(value),
                        style = MaterialTheme.typography.labelLarge,
                        color = primary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
                appendContent()
            }

            DottedRamSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(if (enabled) 1f else DisabledAlpha),
                value = value.toFloat(),
                range = valueRange,
                enabled = enabled,
                onValueChange = { onValueChange(it.roundToInt()) },
                onValueChangeFinished = onValueChangeFinished
            )

            //pilihan cepat
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    modifier = Modifier.alpha(0.75f),
                    text = stringResource(R.string.settings_game_ram_quick_title),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                val picks = QuickPicksMb.filter {
                    it >= valueRange.start && it <= valueRange.endInclusive
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (enabled) 1f else DisabledAlpha),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    picks.forEach { mb ->
                        val selected = value == mb
                        val shape = nevShape(10.dp)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(shape)
                                .background(
                                    if (selected) primary.copy(alpha = 0.18f) else androidx.compose.ui.graphics.Color.Transparent
                                )
                                .border(
                                    1.dp,
                                    if (selected) primary else MaterialTheme.colorScheme.outlineVariant,
                                    shape
                                )
                                .clickable(enabled = enabled) {
                                    onValueChange(mb)
                                    onValueChangeFinished()
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = quickLabel(mb),
                                style = MaterialTheme.typography.labelLarge,
                                color = if (selected) primary else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            previewContent()
        }

        if (showEditDialog) {
            SliderValueEditDialog(
                onDismissRequest = { showEditDialog = false },
                title = title,
                valueRange = valueRange,
                value = value.toFloat(),
                onValueChange = { onValueChange(it.toInt()) },
                onValueChangeFinished = onValueChangeFinished,
                intCheck = true
            )
        }
    }
}

/**
 * Slider pil bertitik: bagian terisi berwarna tema dengan titik terang, penanda vertikal,
 * bagian kosong gelap dengan titik ungu dan satu titik penanda di ujung.
 */
@Composable
private fun DottedRamSlider(
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val emptyTrack = MaterialTheme.colorScheme.surfaceVariant
    val emptyDot = MaterialTheme.colorScheme.tertiary

    val curRange by rememberUpdatedState(range)
    val curChange by rememberUpdatedState(onValueChange)
    val curFinish by rememberUpdatedState(onValueChangeFinished)

    val fraction = if (range.endInclusive > range.start) {
        ((value - range.start) / (range.endInclusive - range.start)).coerceIn(0f, 1f)
    } else 0f

    Canvas(
        modifier = modifier
            .height(44.dp)
            .pointerInput(enabled) {
                if (enabled) {
                    detectTapGestures { offset ->
                        val f = (offset.x / size.width).coerceIn(0f, 1f)
                        curChange(curRange.start + f * (curRange.endInclusive - curRange.start))
                        curFinish()
                    }
                }
            }
            .pointerInput(enabled) {
                if (enabled) {
                    fun update(x: Float) {
                        val f = (x / size.width).coerceIn(0f, 1f)
                        curChange(curRange.start + f * (curRange.endInclusive - curRange.start))
                    }
                    detectHorizontalDragGestures(
                        onDragStart = { offset -> update(offset.x) },
                        onDragEnd = { curFinish() },
                        onDragCancel = { curFinish() }
                    ) { change, _ ->
                        change.consume()
                        update(change.position.x)
                    }
                }
            }
    ) {
        val w = size.width
        val cy = size.height / 2f
        val trackH = 28.dp.toPx()
        val gap = 6.dp.toPx()
        val r = trackH / 2f
        val thumbX = (fraction * w).coerceIn(2.dp.toPx(), w - 2.dp.toPx())

        //bagian terisi
        val filledW = (thumbX - gap).coerceAtLeast(0f)
        if (filledW > 0f) {
            drawRoundRect(
                color = primary,
                topLeft = Offset(0f, cy - trackH / 2f),
                size = Size(filledW, trackH),
                cornerRadius = CornerRadius(r, r)
            )
        }
        //bagian kosong
        val emptyX = thumbX + gap
        if (emptyX < w) {
            drawRoundRect(
                color = emptyTrack,
                topLeft = Offset(emptyX, cy - trackH / 2f),
                size = Size(w - emptyX, trackH),
                cornerRadius = CornerRadius(r, r)
            )
        }
        //penanda vertikal
        drawRoundRect(
            color = primary,
            topLeft = Offset(thumbX - 2.dp.toPx(), cy - 20.dp.toPx()),
            size = Size(4.dp.toPx(), 40.dp.toPx()),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        //titik-titik
        val step = 22.dp.toPx()
        var x = 14.dp.toPx()
        while (x < w - 14.dp.toPx()) {
            if (x < thumbX - gap - 6.dp.toPx()) {
                drawCircle(onPrimary.copy(alpha = 0.92f), 2.5.dp.toPx(), Offset(x, cy))
            } else if (x > thumbX + gap + 6.dp.toPx()) {
                drawCircle(emptyDot.copy(alpha = 0.75f), 2.dp.toPx(), Offset(x, cy))
            }
            x += step
        }
        //titik penanda ujung kanan
        if (w - 12.dp.toPx() > thumbX + gap) {
            drawCircle(primary, 3.dp.toPx(), Offset(w - 12.dp.toPx(), cy))
        }
    }
}
