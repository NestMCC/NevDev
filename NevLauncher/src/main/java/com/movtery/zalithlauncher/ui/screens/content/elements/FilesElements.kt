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

package com.movtery.zalithlauncher.ui.screens.content.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.width
import com.movtery.zalithlauncher.ui.theme.LocalNevStyle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.ui.components.MarqueeText
import com.movtery.zalithlauncher.ui.components.SimpleEditDialog
import com.movtery.zalithlauncher.utils.file.InvalidFilenameException
import com.movtery.zalithlauncher.utils.file.checkFilenameValidity
import com.movtery.zalithlauncher.utils.file.formatFileSize
import com.movtery.zalithlauncher.utils.formatDate
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.Date

@Composable
fun BaseFileItem(
    file: File,
    modifier: Modifier = Modifier,
    suffix: (@Composable RowScope.() -> Unit)? = null
) {
    if (!file.exists()) throw IllegalArgumentException("File is not exists!")

    //Futuristik: ubin ikon emas, tag jenis, dan tombol panah (mengikuti referensi)
    if (LocalNevStyle.current.futuristic) {
        FuturisticFileRow(file = file, modifier = modifier, suffix = suffix)
        return
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(24.dp).align(Alignment.CenterVertically),
            painter = painterResource(
                if (file.isDirectory) {
                    R.drawable.ic_folder_outlined
                } else {
                    R.drawable.ic_description_outlined
                }
            ),
            contentDescription = null
        )
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            MarqueeText(
                text = file.name,
                style = MaterialTheme.typography.labelMedium
            )
            Row(
                modifier = Modifier.basicMarquee(Int.MAX_VALUE),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val date = Date(file.lastModified())
                Text(
                    modifier = Modifier.alpha(0.7f),
                    text = formatDate(
                        date = date,
                        pattern = stringResource(R.string.date_format)
                    ),
                    style = MaterialTheme.typography.labelSmall
                )
                if (file.isFile) {
                    Text(
                        text = formatFileSize(FileUtils.sizeOf(file)),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        suffix?.invoke(this@Row)
    }
}

@Composable
fun CreateNewDirDialog(
    onDismissRequest: () -> Unit = {},
    createDir: (name: String) -> Unit = {}
) {
    var value by remember { mutableStateOf("") }

    val filenameInvalidMessage = key(value) {
        isFilenameInvalid(value)
    }
    val isError = value.isEmpty() || filenameInvalidMessage != null

    SimpleEditDialog(
        title = stringResource(R.string.files_create_dir),
        value = value,
        onValueChange = { value = it },
        isError = isError,
        supportingText = {
            when {
                value.isEmpty() -> Text(text = stringResource(R.string.generic_cannot_empty))
                filenameInvalidMessage != null -> Text(text = filenameInvalidMessage)
            }
        },
        singleLine = true,
        onDismissRequest = onDismissRequest,
        onConfirm = { if (!isError) createDir(value) }
    )
}

@Composable
fun isFilenameInvalid(
    str: String
): String? {
    return try {
        checkFilenameValidity(str)
        null
    } catch (e: InvalidFilenameException) {
        e.getInvalidSummary()
    }
}

@Composable
fun InvalidFilenameException.getInvalidSummary(): String = when {
    containsIllegalCharacters() -> stringResource(R.string.generic_input_invalid_character, illegalCharacters)
    isInvalidLength -> stringResource(R.string.file_invalid_length, invalidLength, 255)
    isLeadingOrTrailingSpace -> stringResource(R.string.file_invalid_leading_or_trailing_space)
    else -> ""
}

@Composable
private fun FuturisticFileRow(
    file: File,
    modifier: Modifier = Modifier,
    suffix: (@Composable RowScope.() -> Unit)? = null
) {
    val gold = MaterialTheme.colorScheme.primary
    val goldLight = MaterialTheme.colorScheme.tertiary
    val tileShape = RoundedCornerShape(14.dp)
    val tag = if (file.isDirectory) "FOLDER" else file.extension.uppercase().ifEmpty { "FILE" }.take(8)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //ubin ikon berbingkai emas
        Box(
            modifier = Modifier
                .size(48.dp)
                .border(1.dp, Brush.linearGradient(listOf(goldLight.copy(alpha = 0.9f), gold.copy(alpha = 0.35f))), tileShape)
                .background(gold.copy(alpha = 0.14f), tileShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(26.dp),
                painter = painterResource(
                    if (file.isDirectory) R.drawable.ic_folder_filled else R.drawable.ic_description_outlined
                ),
                tint = if (file.isDirectory) gold else MaterialTheme.colorScheme.secondary,
                contentDescription = null
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            MarqueeText(
                text = file.name,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Row(
                modifier = Modifier.basicMarquee(Int.MAX_VALUE),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    modifier = Modifier.alpha(0.7f),
                    text = formatDate(
                        date = Date(file.lastModified()),
                        pattern = stringResource(R.string.date_format)
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace
                )
                if (file.isFile) {
                    Text(
                        modifier = Modifier.alpha(0.7f),
                        text = formatFileSize(FileUtils.sizeOf(file)),
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
        //tag jenis (FOLDER / LOG / ...)
        Box(
            modifier = Modifier
                .border(1.dp, gold.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = gold,
                maxLines = 1
            )
        }
        if (suffix != null) {
            suffix.invoke(this)
        } else if (file.isDirectory) {
            //tombol panah buka folder
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .border(1.dp, gold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .background(gold.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(22.dp),
                    painter = painterResource(R.drawable.ic_arrow_right_rounded),
                    tint = gold,
                    contentDescription = null
                )
            }
        }
    }
}
