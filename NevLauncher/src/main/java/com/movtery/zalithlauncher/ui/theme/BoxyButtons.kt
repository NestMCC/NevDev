package com.movtery.zalithlauncher.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Pengganti tombol Material3 (dipakai lewat import): bentuk bawaan tombol adalah pil penuh
 * dan tidak mengikuti tema. Di gaya kotak, bentuknya diganti kotak berlengkung tipis.
 */
@Composable
private fun resolveButtonShape(shape: Shape?): Shape =
    shape ?: if (isBoxy()) RoundedCornerShape(12.dp) else ButtonDefaults.shape

@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape? = null,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val src = rememberButtonSource(interactionSource)
    androidx.compose.material3.Button(
    onClick = onClick,
    modifier = modifier.liquidButton(src, shape),
    enabled = enabled,
    shape = resolveButtonShape(shape),
    colors = colors,
    elevation = elevation,
    border = border,
    contentPadding = contentPadding,
    interactionSource = src,
    content = content
    )
}

@Composable
fun FilledTonalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape? = null,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.filledTonalButtonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val src = rememberButtonSource(interactionSource)
    androidx.compose.material3.FilledTonalButton(
    onClick = onClick,
    modifier = modifier.liquidButton(src, shape),
    enabled = enabled,
    shape = resolveButtonShape(shape),
    colors = colors,
    elevation = elevation,
    border = border,
    contentPadding = contentPadding,
    interactionSource = src,
    content = content
    )
}

@Composable
fun OutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape? = null,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val src = rememberButtonSource(interactionSource)
    androidx.compose.material3.OutlinedButton(
    onClick = onClick,
    modifier = modifier.liquidButton(src, shape),
    enabled = enabled,
    shape = resolveButtonShape(shape),
    colors = colors,
    elevation = elevation,
    border = border,
    contentPadding = contentPadding,
    interactionSource = src,
    content = content
    )
}

@Composable
fun TextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape? = null,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val src = rememberButtonSource(interactionSource)
    androidx.compose.material3.TextButton(
    onClick = onClick,
    modifier = modifier.liquidButton(src, shape),
    enabled = enabled,
    shape = resolveButtonShape(shape),
    colors = colors,
    elevation = elevation,
    border = border,
    contentPadding = contentPadding,
    interactionSource = src,
    content = content
    )
}

@Composable
private fun rememberButtonSource(source: MutableInteractionSource?): MutableInteractionSource =
    source ?: androidx.compose.runtime.remember { MutableInteractionSource() }

/** Pada gaya Liquid, tombol mendapat efek gelombang cairan saat ditekan. */
@Composable
private fun Modifier.liquidButton(source: MutableInteractionSource, shape: Shape?): Modifier {
    if (!LocalNevStyle.current.liquid) return this
    return this.liquidPress(source, resolveButtonShape(shape), androidx.compose.material3.MaterialTheme.colorScheme.primary)
}
