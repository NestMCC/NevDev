package com.movtery.layer_controller.utils

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.drawscope.drawOutline
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.movtery.layer_controller.data.toAndroidShape
import com.movtery.layer_controller.observable.ObservableButtonStyle
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

/** Efek "Liquid" tombol kontrol aktif/nonaktif (diatur dari pengaturan launcher). */
val LocalControlLiquid = staticCompositionLocalOf { false }

/** Sudut putar (derajat) garis tepi berputar; dibagi ke semua tombol agar hanya ada satu animasi. */
val LocalLiquidSpin = staticCompositionLocalOf<State<Float>?> { null }

private val DefaultLiquidColor = Color(0xFF5AC8FA)

/**
 * Tombol bergaya kaca bening; saat ditekan, cairan naik berombak dari bawah dan
 * tombol "memipih" mengenyal. Ringan: hanya satu Path kecil, dan animasi gelombang
 * hanya berjalan selama tombol ditekan (plus sesaat setelah dilepas).
 */
@Composable
internal fun Modifier.liquidGlass(
    style: ObservableButtonStyle,
    isDark: Boolean = isSystemInDarkTheme(),
    isPressed: Boolean
): Modifier {
    val themeStyle = if (style.commonStyle || !isDark) style.lightStyle else style.darkStyle
    val shape = remember(themeStyle, themeStyle.borderRadius) { themeStyle.borderRadius.toAndroidShape() }
    val accent = LocalControlAccent.current
    val liquid = remember(accent, themeStyle.pressedBorderColor) {
        accent ?: themeStyle.pressedBorderColor.takeIf { it.alpha > 0.05f } ?: DefaultLiquidColor
    }

    //pegas dengan sedikit pantulan = kesan cairan kenyal
    val fill = animateFloatAsState(
        targetValue = if (isPressed) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
        label = "liquidFill"
    )

    //gelombang hanya dianimasikan saat dibutuhkan
    var active by remember { mutableStateOf(false) }
    LaunchedEffect(isPressed) {
        if (isPressed) active = true else {
            delay(700)
            active = false
        }
    }
    val phase: State<Float> = if (active) {
        rememberInfiniteTransition(label = "liquidWave").animateFloat(
            initialValue = 0f,
            targetValue = (2 * PI).toFloat(),
            animationSpec = infiniteRepeatable(tween(1100, easing = LinearEasing), RepeatMode.Restart),
            label = "liquidPhase"
        )
    } else remember { mutableStateOf(0f) }

    val path = remember { Path() }
    val glass = remember {
        Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.42f), Color.White.copy(alpha = 0.16f)))
    }
    val spin = LocalLiquidSpin.current
    val density = androidx.compose.ui.platform.LocalDensity.current
    val strokePx = with(density) { 1.6.dp.toPx() }
    val rimColors = remember(liquid) {
        listOf(
            Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.18f),
            Color.White.copy(alpha = 0.98f), liquid.copy(alpha = 0.9f),
            Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.18f)
        )
    }
    val rimStops = remember { listOf(0f, 0.15f, 0.32f, 0.42f, 0.6f, 1f) }

    return this
        .graphicsLayer {
            //mengenyal: melebar sedikit & memipih saat ditekan
            val f = fill.value
            scaleX = 1f + 0.05f * f
            scaleY = 1f - 0.04f * f
        }
        .clip(shape)
        .background(glass)
        .drawBehind {
            val f = fill.value.coerceAtLeast(0f)
            if (f < 0.01f) return@drawBehind
            val w = size.width
            val h = size.height
            val level = h * (1f - f.coerceAtMost(1f) * 0.92f)
            val amp = h * 0.07f * f.coerceAtMost(1f)
            val steps = 14
            fun wave(p: Float, freq: Float, base: Float) {
                path.rewind()
                path.moveTo(0f, h)
                for (i in 0..steps) {
                    val x = w * i / steps
                    val y = base + amp * sin(freq * i / steps * 2f * PI.toFloat() + p)
                    path.lineTo(x, y)
                }
                path.lineTo(w, h)
                path.close()
            }
            //lapisan belakang (lebih transparan, fase berlawanan)
            wave(-phase.value * 0.8f + 1.7f, 1.2f, level - amp * 0.6f)
            drawPath(path, liquid.copy(alpha = 0.28f * f.coerceAtMost(1f)))
            //lapisan depan
            wave(phase.value, 1.6f, level)
            drawPath(
                path,
                Brush.verticalGradient(
                    listOf(liquid.copy(alpha = 0.85f), liquid.copy(alpha = 0.5f)),
                    startY = level - amp,
                    endY = h
                )
            )
            //kilau pada permukaan cairan
            drawLine(
                color = Color.White.copy(alpha = 0.35f * f.coerceAtMost(1f)),
                start = Offset(w * 0.18f, level + amp * 0.3f),
                end = Offset(w * 0.5f, level + amp * 0.3f),
                strokeWidth = 1.5f
            )
        }
        .drawWithContent {
            drawContent()
            //garis tepi: gradasi sapuan yang berputar pelan
            val angle = spin?.value ?: 0f
            val c = center
            val shader = androidx.compose.ui.graphics.SweepGradientShader(c, rimColors, rimStops)
            shader.setLocalMatrix(android.graphics.Matrix().apply { postRotate(angle, c.x, c.y) })
            drawOutline(
                outline = shape.createOutline(size, layoutDirection, this),
                brush = androidx.compose.ui.graphics.ShaderBrush(shader),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokePx)
            )
        }
}
