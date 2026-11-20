package com.movtery.zalithlauncher.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SweepGradientShader
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.movtery.layer_controller.utils.LocalLiquidSpin
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/*
 * Tema desain Liquid: kaca bening, latar cairan yang bergerak pelan,
 * garis tepi kaca berputar, dan gelombang cairan saat tombol ditekan.
 * Semua animasi ringan: satu animasi bersama untuk garis tepi, satu untuk latar,
 * dan animasi gelombang hanya berjalan saat tombol ditekan.
 */

/** Apakah gaya Liquid sedang aktif. */
@Composable
fun isLiquid(): Boolean = LocalNevStyle.current.liquid

/**
 * Latar cairan: tiga gumpalan cahaya (aqua, ungu, mint) yang melayang pelan di atas warna dasar.
 * Digambar di belakang seluruh isi layar.
 */
fun Modifier.liquidBackdrop(): Modifier = composed {
    val scheme = MaterialTheme.colorScheme
    val c1 = scheme.primary
    val c2 = scheme.secondary
    val c3 = scheme.tertiary
    val phase = rememberInfiniteTransition(label = "liquidBackdrop").animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(tween(26000, easing = LinearEasing)),
        label = "liquidBackdropPhase"
    )
    drawBehind {
        val p = phase.value
        val w = size.width
        val h = size.height
        val r = size.maxDimension
        drawRect(
            Brush.radialGradient(
                listOf(c1.copy(alpha = 0.22f), Color.Transparent),
                center = Offset(w * (0.2f + 0.12f * sin(p)), h * (0.25f + 0.15f * cos(p * 1f))),
                radius = r * 0.55f
            )
        )
        drawRect(
            Brush.radialGradient(
                listOf(c2.copy(alpha = 0.20f), Color.Transparent),
                center = Offset(w * (0.8f + 0.1f * cos(p + 1.3f)), h * (0.35f + 0.18f * sin(p + 2f))),
                radius = r * 0.5f
            )
        )
        drawRect(
            Brush.radialGradient(
                listOf(c3.copy(alpha = 0.14f), Color.Transparent),
                center = Offset(w * (0.5f + 0.2f * sin(p + 3f)), h * (0.9f + 0.08f * cos(p + 0.5f))),
                radius = r * 0.5f
            )
        )
    }
}

/**
 * Panel kaca: isi putih bening bergradasi + garis tepi sapuan cahaya yang berputar pelan.
 * Dipakai pada kartu bergaya Liquid.
 */
fun Modifier.liquidGlassRim(shape: Shape): Modifier = composed {
    val spin = LocalLiquidSpin.current
    val accent = MaterialTheme.colorScheme.primary
    val strokePx = with(LocalDensity.current) { 1.4.dp.toPx() }
    val colors = remember(accent) {
        listOf(
            Color.White.copy(alpha = 0.14f), Color.White.copy(alpha = 0.14f),
            Color.White.copy(alpha = 0.85f), accent.copy(alpha = 0.8f),
            Color.White.copy(alpha = 0.14f), Color.White.copy(alpha = 0.14f)
        )
    }
    val stops = remember { listOf(0f, 0.15f, 0.32f, 0.42f, 0.6f, 1f) }
    val path = remember { Path() }
    drawWithContent {
        drawContent()
        val c = center
        val shader = SweepGradientShader(c, colors, stops)
        shader.setLocalMatrix(android.graphics.Matrix().apply { postRotate(spin?.value ?: 0f, c.x, c.y) })
        path.rewind()
        when (val o = shape.createOutline(size, layoutDirection, this)) {
            is Outline.Rounded -> path.addRoundRect(o.roundRect)
            is Outline.Rectangle -> path.addRect(o.rect)
            is Outline.Generic -> path.addPath(o.path)
        }
        drawPath(path, ShaderBrush(shader), style = Stroke(width = strokePx))
    }
}

/** Isi kaca bening: gradasi putih tipis di atas warna kartu. */
fun Modifier.liquidGlassFill(shape: Shape): Modifier =
    this.clip(shape).drawBehind {
        drawRect(
            Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.10f), Color.White.copy(alpha = 0.02f)))
        )
    }

/**
 * Efek tekan Liquid untuk tombol: cairan naik dari bawah dengan permukaan berombak,
 * dan tombol sedikit mengenyal. Gelombang hanya dianimasikan saat tombol ditekan.
 */
fun Modifier.liquidPress(
    interactionSource: MutableInteractionSource,
    shape: Shape,
    color: Color
): Modifier = composed {
    val pressed by interactionSource.collectIsPressedAsState()
    val fill = animateFloatAsState(
        targetValue = if (pressed) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow),
        label = "liquidBtnFill"
    )
    var active by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(pressed) {
        if (pressed) active = true else {
            kotlinx.coroutines.delay(700)
            active = false
        }
    }
    val phase: State<Float> = if (active) {
        rememberInfiniteTransition(label = "liquidBtnWave").animateFloat(
            initialValue = 0f,
            targetValue = (2 * PI).toFloat(),
            animationSpec = infiniteRepeatable(tween(1100, easing = LinearEasing)),
            label = "liquidBtnPhase"
        )
    } else remember { mutableStateOf(0f) }
    val path = remember { Path() }

    this
        .graphicsLayer {
            val f = fill.value
            scaleX = 1f + 0.04f * f
            scaleY = 1f - 0.03f * f
        }
        .clip(shape)
        .drawWithContent {
            drawContent()
            val f = fill.value.coerceAtLeast(0f)
            if (f < 0.01f) return@drawWithContent
            val w = size.width
            val h = size.height
            val level = h * (1f - f.coerceAtMost(1f) * 0.92f)
            val amp = h * 0.07f * f.coerceAtMost(1f)
            val steps = 14
            path.rewind()
            path.moveTo(0f, h)
            for (i in 0..steps) {
                val x = w * i / steps
                path.lineTo(x, level + amp * sin(1.6f * i / steps * 2f * PI.toFloat() + phase.value))
            }
            path.lineTo(w, h)
            path.close()
            drawPath(path, color.copy(alpha = 0.45f * f.coerceAtMost(1f)))
        }
}
