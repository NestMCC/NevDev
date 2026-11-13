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

package com.movtery.zalithlauncher.ui.components

import androidx.compose.foundation.layout.Arrangement
import com.movtery.zalithlauncher.ui.theme.NevActionButton
import com.movtery.zalithlauncher.ui.theme.NevRed
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.ui.theme.cardColor
import com.movtery.zalithlauncher.ui.theme.onCardColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * 返回对话框内容可用的最大高度，以窗口容器高度为上界
 *
 * 输入法动画期间窗口高度可能被系统短暂压缩，这里记录最大值，避免对话框高度随之抖动
 */
@Composable
fun rememberDialogMaxHeight(): Dp {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current
    var maxHeight by remember(windowInfo) { mutableIntStateOf(windowInfo.containerSize.height) }
    maxHeight = maxOf(maxHeight, windowInfo.containerSize.height)
    return with(density) { maxHeight.toDp() }
}

/**
 * 展示警告对话框
 *
 * @param title 对话框标题
 * @param text 对话框内容
 * @param dismissByDialog 是否允许Dialog自己请求关闭
 * @param onConfirm 点击确认按钮的回调
 * @param onDismiss 点击取消或对话框外部的回调
 */
@Composable
fun SimpleAlertDialog(
    title: String,
    text: String,
    confirmText: String = stringResource(R.string.generic_confirm),
    dismissText: String = stringResource(R.string.generic_cancel),
    dismissByDialog: Boolean = true,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (useCryoDialog()) {
        CryoDialog(
            title = title,
            confirmText = confirmText,
            dismissText = dismissText,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
            onDismissRequest = onDismiss,
            dismissOnOutside = dismissByDialog
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    NevAlertDialog(
        title = title,
        onDismissRequest = { if (dismissByDialog) onDismiss() },
        confirmText = confirmText,
        onConfirm = onConfirm,
        dismissText = dismissText,
        onDismiss = onDismiss
    ) {
        Text(text = text)
    }
}

/**
 * 展示警告对话框
 *
 * @param title 对话框标题
 * @param text 对话框内容
 * @param dismissByDialog 是否允许Dialog自己请求关闭
 * @param onDismiss 点击确认或对话框外部的回调
 */
@Composable
fun SimpleAlertDialog(
    title: String,
    text: String,
    confirmText: String = stringResource(R.string.generic_confirm),
    dismissByDialog: Boolean = true,
    onDismiss: () -> Unit
) {
    if (useCryoDialog()) {
        CryoDialog(
            title = title,
            confirmText = confirmText,
            onConfirm = onDismiss,
            onDismissRequest = onDismiss,
            dismissOnOutside = dismissByDialog
        ) {
            Text(text = text, style = MaterialTheme.typography.bodyLarge)
        }
        return
    }
    NevAlertDialog(
        title = title,
        onDismissRequest = { if (dismissByDialog) onDismiss() },
        confirmText = confirmText,
        onConfirm = onDismiss
    ) {
        Text(text = text)
    }
}

@Composable
fun SimpleAlertDialog(
    title: String,
    text: @Composable () -> Unit = {},
    confirmText: String = stringResource(R.string.generic_confirm),
    dismissText: String = stringResource(R.string.generic_cancel),
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit = {}
) {
    if (useCryoDialog()) {
        CryoDialog(
            title = title,
            confirmText = confirmText,
            dismissText = dismissText,
            onConfirm = onConfirm,
            onDismiss = onCancel,
            onDismissRequest = onDismissRequest,
            content = text
        )
        return
    }
    NevAlertDialog(
        title = title,
        onDismissRequest = onDismissRequest,
        confirmText = confirmText,
        onConfirm = onConfirm,
        dismissText = dismissText,
        onDismiss = onCancel
    ) {
        text()
    }
}

/**
 * 展示仅带确认按钮的警告对话框
 *
 * @param title 对话框标题
 * @param text 对话框内容
 * @param onDismiss 点击确认或对话框外部的回调
 */
@Composable
fun SimpleAlertDialog(
    title: String,
    text: @Composable () -> Unit = {},
    confirmText: String = stringResource(R.string.generic_confirm),
    onDismiss: () -> Unit
) {
    if (useCryoDialog()) {
        CryoDialog(
            title = title,
            confirmText = confirmText,
            onConfirm = onDismiss,
            onDismissRequest = onDismiss,
            content = text
        )
        return
    }
    NevAlertDialog(
        title = title,
        onDismissRequest = onDismiss,
        confirmText = confirmText,
        onConfirm = onDismiss
    ) {
        text()
    }
}

@Composable
fun SimpleEditDialog(
    title: String,
    value: String,
    onValueChange: (newValue: String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = 3,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    extraBody: @Composable (() -> Unit)? = null,
    extraContent: @Composable (() -> Unit)? = null,
    onDismissRequest: () -> Unit = {},
    onCancel: () -> Unit = onDismissRequest,
    onConfirm: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        ImePanContainer(
            modifier = Modifier
                .heightIn(max = rememberDialogMaxHeight())
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .padding(all = 6.dp)
                    .heightIn(max = (maxHeight - 12.dp).coerceAtMost(rememberDialogMaxHeight()))
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.extraLarge,
                color = cardColor(false),
                contentColor = onCardColor(),
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    simpleEditDialogBody(
                        title = title,
                        value = value,
                        onValueChange = onValueChange,
                        label = label,
                        isError = isError,
                        supportingText = supportingText,
                        singleLine = singleLine,
                        maxLines = maxLines,
                        keyboardOptions = keyboardOptions,
                        extraBody = extraBody,
                        extraContent = extraContent
                    ).invoke(this)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        FilledTonalButton(
                            modifier = Modifier.weight(1f),
                            onClick = onCancel
                        ) {
                            MarqueeText(text = stringResource(R.string.generic_cancel))
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = onConfirm
                        ) {
                            MarqueeText(text = stringResource(R.string.generic_confirm))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleEditDialog(
    title: String,
    value: String,
    onValueChange: (newValue: String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = 3,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    extraBody: @Composable (() -> Unit)? = null,
    extraContent: @Composable (() -> Unit)? = null,
    onConfirm: () -> Unit = {},
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(decorFitsSystemWindows = false)
    ) {
        ImePanContainer(
            modifier = Modifier
                .heightIn(max = rememberDialogMaxHeight())
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .padding(all = 6.dp)
                    .heightIn(max = (maxHeight - 12.dp).coerceAtMost(rememberDialogMaxHeight()))
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.extraLarge,
                color = cardColor(false),
                contentColor = onCardColor(),
                shadowElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    simpleEditDialogBody(
                        title = title,
                        value = value,
                        onValueChange = onValueChange,
                        label = label,
                        isError = isError,
                        supportingText = supportingText,
                        singleLine = singleLine,
                        maxLines = maxLines,
                        keyboardOptions = keyboardOptions,
                        extraBody = extraBody,
                        extraContent = extraContent
                    ).invoke(this)

                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = onConfirm
                    ) {
                        MarqueeText(text = stringResource(R.string.generic_confirm))
                    }
                }
            }
        }
    }
}

@Composable
private fun simpleEditDialogBody(
    title: String,
    value: String,
    onValueChange: (newValue: String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = 3,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    extraBody: @Composable (() -> Unit)? = null,
    extraContent: @Composable (() -> Unit)? = null,
    onConfirm: () -> Unit = {}
): @Composable ColumnScope.() -> Unit = {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium
    )

    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fadeEdge(state = scrollState)
            .weight(1f, fill = false)
            .verticalScrollWithBar(state = scrollState)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        extraBody?.let {
            it.invoke()
            Spacer(modifier = Modifier.size(8.dp))
        }

        val focusManager = LocalFocusManager.current

        if (singleLine) {
            SingleLineTextCheck(
                text = value,
                onSingleLined = onValueChange
            )
        }

        OwnOutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            label = label,
            isError = isError,
            supportingText = supportingText,
            singleLine = singleLine,
            maxLines = maxLines,
            keyboardOptions = keyboardOptions.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus(true)
                    onConfirm()
                }
            ),
            shape = MaterialTheme.shapes.large
        )
        extraContent?.invoke()
    }
}

@Composable
fun SimpleCheckEditDialog(
    title: String,
    text: String,
    value: String,
    checked: Boolean,
    checkBoxText: String? = null,
    onValueChange: (newValue: String) -> Unit,
    onCheckedChange: (newValue: Boolean) -> Unit,
    label: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = 3,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    SimpleEditDialog(
        title = title,
        value = value,
        onValueChange = onValueChange,
        label = label,
        isError = isError,
        supportingText = supportingText,
        singleLine = singleLine,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        extraBody = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall
            )
        },
        extraContent = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.End
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    checkBoxText?.let{ Text(text = it, style = MaterialTheme.typography.labelMedium) }
                    Checkbox(
                        checked = checked,
                        onCheckedChange = onCheckedChange
                    )
                }
            }
        },
        onDismissRequest = onDismissRequest,
        onConfirm = onConfirm
    )
}

/**
 * 一个很简单的列表Dialog
 * @param items 需要列出的items
 * @param onItemSelected item被点击的回调
 * @param onDismissRequest dialog被关闭的回调
 * @param showConfirm 是否通过确认按钮来触发item的点击回调
 */
@Composable
fun <T> SimpleListDialog(
    title: String,
    items: List<T>,
    onItemSelected: (T) -> Unit,
    onDismissRequest: (selected: Boolean) -> Unit,
    current: T? = null,
    itemLayout: @Composable (item: T, isCurrent: Boolean, onClick: () -> Unit) -> Unit,
    showConfirm: Boolean = false,
    confirmText: @Composable RowScope.() -> Unit = {
        MarqueeText(text = stringResource(R.string.generic_confirm))
    }
) {
    var selectedItem: T? by remember { mutableStateOf(current) }

    Dialog(
        onDismissRequest = {
            onDismissRequest(false)
        }
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .heightIn(max = rememberDialogMaxHeight())
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .padding(all = 3.dp)
                    .heightIn(max = (maxHeight - 6.dp).coerceAtMost(rememberDialogMaxHeight()))
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.extraLarge,
                color = cardColor(false),
                contentColor = onCardColor(),
                shadowElevation = 3.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .wrapContentHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.size(16.dp))

                    val state = rememberLazyListState()
                    LazyColumn(
                        modifier = Modifier
                            .fadeEdge(state = state)
                            .weight(1f, fill = false),
                        state = state
                    ) {
                        items(items) { item ->
                            val isCurrent = selectedItem == item
                            itemLayout(item, isCurrent) {
                                selectedItem = item
                                if (!showConfirm && !isCurrent) {
                                    onItemSelected(item)
                                    onDismissRequest(true)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.size(4.dp))

                    if (showConfirm) {
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (selectedItem != null) {
                                    onItemSelected(selectedItem!!)
                                    onDismissRequest(true)
                                }
                            }
                        ) {
                            confirmText()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleTaskDialog(
    title: String,
    text: String? = null,
    task: suspend () -> Unit,
    context: CoroutineContext = Dispatchers.Default,
    onDismiss: () -> Unit,
    onError: (Throwable) -> Unit = {}
) {
    var inProgress by rememberSaveable { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    if (inProgress) {
        ProgressDialog(
            title = title,
            text = text
        )
    } else {
        onDismiss()
    }

    LaunchedEffect(Unit) {
        inProgress = true
        scope.launch(context) {
            try {
                task()
            } catch (e: Throwable) {
                onError(e)
            } finally {
                inProgress = false
            }
        }
    }
}

@Composable
fun ProgressDialog(
    title: String = stringResource(R.string.generic_in_progress),
    text: String? = null
) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            color = cardColor(false),
            contentColor = onCardColor(),
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                text?.let {
                    Text(text = it, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.height(16.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * Dialog bergaya Nev: bingkai gelap bergaris oranye dengan aksen sudut, label sistem,
 * judul dua warna (kata terakhir beraksen), garis pemisah, isi, lalu tombol bergradasi.
 * Bila [dismissText] dan [onDismiss] diberikan, tombol sekunder bergaris ditampilkan di samping tombol utama.
 */
@Composable
fun NevAlertDialog(
    title: String,
    onDismissRequest: () -> Unit,
    confirmText: String,
    onConfirm: () -> Unit,
    dismissText: String? = null,
    onDismiss: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val corner = 28.dp
        val shape = androidx.compose.foundation.shape.RoundedCornerShape(corner)
        val bracket = NevRed.GradientEnd
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .androidx_widthIn(560.dp)
                .heightIn(max = rememberDialogMaxHeight() - 32.dp)
                .androidx_border(shape)
                .androidx_panelBackground(shape)
                .androidx_cornerBrackets(corner, bracket)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 22.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .size(60.dp)
                            .androidx_border(androidx.compose.foundation.shape.RoundedCornerShape(18.dp), 1.5.dp, NevRed.OutlineBright)
                            .androidx_fill(androidx.compose.foundation.shape.RoundedCornerShape(18.dp), NevRed.PanelDeep),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.Icon(
                            modifier = Modifier.size(30.dp),
                            painter = androidx.compose.ui.res.painterResource(R.drawable.ic_info_filled),
                            tint = NevRed.GradientEnd,
                            contentDescription = null
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "NEV // SYSTEM",
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = NevRed.GradientEnd,
                            maxLines = 1
                        )
                        val accentColor = androidx.compose.ui.graphics.lerp(NevRed.GradientEnd, androidx.compose.ui.graphics.Color.White, 0.35f)
                        val head = title.substringBeforeLast(' ', "")
                        val tail = title.substringAfterLast(' ')
                        Text(
                            text = androidx.compose.ui.text.buildAnnotatedString {
                                if (head.isNotEmpty()) append("$head ")
                                withStyle(androidx.compose.ui.text.SpanStyle(color = accentColor)) { append(tail) }
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }

                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .androidx_fillBrush(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                listOf(NevRed.OutlineBright, NevRed.Outline.copy(alpha = 0.2f))
                            )
                        )
                )

                val scrollState = rememberScrollState()
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .fadeEdge(state = scrollState)
                        .verticalScrollWithBar(state = scrollState)
                ) {
                    androidx.compose.runtime.CompositionLocalProvider(
                        androidx.compose.material3.LocalContentColor provides NevRed.OnPanel
                    ) {
                        content()
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (dismissText != null && onDismiss != null) {
                        NevOutlineButton(
                            modifier = Modifier.weight(1f),
                            text = dismissText,
                            onClick = onDismiss
                        )
                    }
                    NevActionButton(
                        modifier = Modifier.weight(1f),
                        text = confirmText,
                        onClick = onConfirm
                    )
                }
            }
        }
    }
}

@Composable
private fun NevOutlineButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = shape,
        color = androidx.compose.ui.graphics.Color.Transparent,
        contentColor = NevRed.OnPanel,
        border = androidx.compose.foundation.BorderStroke(1.dp, NevRed.OutlineBright.copy(alpha = 0.8f))
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .androidx_fill(shape, NevRed.SurfaceHigh.copy(alpha = 0.6f))
                .padding(horizontal = 18.dp, vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

private fun Modifier.androidx_widthIn(max: Dp): Modifier = this.then(Modifier.widthIn(max = max))

@Composable
private fun Modifier.androidx_border(
    shape: androidx.compose.ui.graphics.Shape,
    width: Dp = 1.2.dp,
    color: androidx.compose.ui.graphics.Color = NevRed.OutlineBright.copy(alpha = 0.85f)
): Modifier = this.then(Modifier.border(width, color, shape))

private fun Modifier.androidx_fill(
    shape: androidx.compose.ui.graphics.Shape,
    color: androidx.compose.ui.graphics.Color
): Modifier = this.then(Modifier.background(color, shape))

private fun Modifier.androidx_fillBrush(brush: androidx.compose.ui.graphics.Brush): Modifier =
    this.then(Modifier.background(brush))

@Composable
private fun Modifier.androidx_panelBackground(shape: androidx.compose.ui.graphics.Shape): Modifier =
    this.then(
        Modifier.background(
            androidx.compose.ui.graphics.Brush.verticalGradient(
                listOf(NevRed.Panel, NevRed.PanelDeep)
            ),
            shape
        )
    )

/** Aksen sudut kanan-atas dan kiri-bawah: busur sesuai lengkung bingkai plus garis pendek. */
private fun Modifier.androidx_cornerBrackets(corner: Dp, color: androidx.compose.ui.graphics.Color): Modifier =
    this.then(
        Modifier.drawBehind {
            val stroke = 3.dp.toPx()
            val inset = 6.dp.toPx()
            val ri = corner.toPx() - inset
            val w = size.width
            val h = size.height
            val line = 28.dp.toPx()
            val style = androidx.compose.ui.graphics.drawscope.Stroke(width = stroke, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            //Kanan-atas
            drawArc(
                color = color, startAngle = 270f, sweepAngle = 90f, useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(w - inset - 2 * ri, inset),
                size = androidx.compose.ui.geometry.Size(2 * ri, 2 * ri), style = style
            )
            drawLine(color, androidx.compose.ui.geometry.Offset(w - inset - ri - line, inset), androidx.compose.ui.geometry.Offset(w - inset - ri, inset), stroke, androidx.compose.ui.graphics.StrokeCap.Round)
            drawLine(color, androidx.compose.ui.geometry.Offset(w - inset, inset + ri), androidx.compose.ui.geometry.Offset(w - inset, inset + ri + line), stroke, androidx.compose.ui.graphics.StrokeCap.Round)
            //Kiri-bawah
            drawArc(
                color = color, startAngle = 90f, sweepAngle = 90f, useCenter = false,
                topLeft = androidx.compose.ui.geometry.Offset(inset, h - inset - 2 * ri),
                size = androidx.compose.ui.geometry.Size(2 * ri, 2 * ri), style = style
            )
            drawLine(color, androidx.compose.ui.geometry.Offset(inset + ri, h - inset), androidx.compose.ui.geometry.Offset(inset + ri + line, h - inset), stroke, androidx.compose.ui.graphics.StrokeCap.Round)
            drawLine(color, androidx.compose.ui.geometry.Offset(inset, h - inset - ri), androidx.compose.ui.geometry.Offset(inset, h - inset - ri - line), stroke, androidx.compose.ui.graphics.StrokeCap.Round)
        }
    )
