package com.movtery.zalithlauncher.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.composed
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Gaya tema merah Nev Launcher: gradasi merah-oranye, latar merah gelap, garis tepi oranye.
 */
object NevRed {
    /** Awal gradasi: warna utama tema aktif. */
    val GradientStart: Color
        @Composable get() = MaterialTheme.colorScheme.primary
    /** Akhir gradasi: warna tersier tema aktif. */
    val GradientEnd: Color
        @Composable get() = MaterialTheme.colorScheme.tertiary
    /** Teks/ikon di atas gradasi. */
    val OnGradient: Color
        @Composable get() = MaterialTheme.colorScheme.onPrimary
    val Panel: Color
        @Composable get() = glass(MaterialTheme.colorScheme.surfaceContainer, 0.08f)
    val PanelDeep: Color
        @Composable get() = glass(MaterialTheme.colorScheme.surfaceContainerLow, 0.10f)
    val SurfaceHigh: Color
        @Composable get() = glass(MaterialTheme.colorScheme.surfaceContainerHigh, 0.12f)
    val SurfaceHighest: Color
        @Composable get() = glass(MaterialTheme.colorScheme.surfaceContainerHighest, 0.14f)

    /** Pada gaya Liquid warna panel dibuat tembus pandang seperti kaca; gaya lain tetap solid. */
    @Composable
    private fun glass(base: Color, alpha: Float): Color =
        if (LocalNevStyle.current.liquid) lerp(base, Color.White, 0.07f).copy(alpha = alpha) else base
    val Outline: Color
        @Composable get() = MaterialTheme.colorScheme.outlineVariant
    val OutlineBright: Color
        @Composable get() = lerp(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.tertiary,
            0.5f
        )
    val OnPanel: Color
        @Composable get() = MaterialTheme.colorScheme.onSurface
    val DangerTile: Color
        @Composable get() = MaterialTheme.colorScheme.errorContainer
    val DangerContent: Color
        @Composable get() = MaterialTheme.colorScheme.error
    val Muted: Color
        @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

    val Gradient: Brush
        @Composable get() = Brush.horizontalGradient(listOf(GradientStart, GradientEnd))

    val GradientDiagonal: Brush
        @Composable get() = Brush.linearGradient(listOf(GradientStart, GradientEnd))

    val PanelBrush: Brush
        @Composable get() = if (isNevGradient()) NevCardFill else if (isBoxy()) NevRed.solid(PanelDeep) else Brush.verticalGradient(listOf(Panel, PanelDeep))

    /** Brush satu warna (untuk latar polos yang tetap bertipe Brush). */
    @Composable
    fun solid(color: Color): Brush = Brush.horizontalGradient(listOf(color, color))
}

/**
 * Wadah popup bergaya Nev (dipakai untuk popup Pengaturan / Klon / Hapus).
 */
@Composable
fun NevPopupContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .border(
                border = BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(NevRed.Outline, NevRed.OutlineBright))
                ),
                shape = nevShape(24.dp)
            )
            .background(NevRed.PanelBrush, nevShape(24.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        content()
    }
}

/**
 * Ubin ikon bergradasi dengan label di bawahnya.
 * @param danger true untuk aksi berbahaya (Hapus), memakai warna merah muda redup
 */
@Composable
fun NevIconTile(
    painter: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    danger: Boolean = false
) {
    Column(
        modifier = modifier
            .semantics { role = Role.Button }
            .padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Surface(
            onClick = onClick,
            shape = nevShape(14.dp),
            color = Color.Transparent,
            border = if (danger) BorderStroke(1.dp, NevRed.DangerContent.copy(alpha = 0.6f)) else null
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        if (danger) NevRed.solid(NevRed.DangerTile)
                        else NevRed.GradientDiagonal
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painter,
                    contentDescription = text,
                    tint = if (danger) NevRed.DangerContent else NevRed.OnGradient
                )
            }
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (danger) NevRed.DangerContent else NevRed.OnPanel,
            maxLines = 1
        )
    }
}

/** Pemisah vertikal tipis antar ubin di popup. */
@Composable
fun NevPopupDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(44.dp)
            .background(NevRed.Outline.copy(alpha = 0.6f))
    )
}

/**
 * Baris popup: beberapa ubin ikon dengan pemisah.
 */
@Composable
fun NevPopupRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        content()
    }
}

enum class NevPillStyle { Gradient, Neutral, Danger }

/**
 * Tombol pil bergaya Nev dengan lingkaran ikon di kiri.
 */
@Composable
fun NevPillButton(
    text: String,
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: NevPillStyle = NevPillStyle.Neutral
) {
    val contentColor = when (style) {
        NevPillStyle.Gradient -> NevRed.OnGradient
        NevPillStyle.Neutral -> NevRed.OnPanel
        NevPillStyle.Danger -> NevRed.DangerContent
    }
    val circleColor = when (style) {
        NevPillStyle.Gradient -> NevRed.OnGradient
        NevPillStyle.Neutral -> NevRed.SurfaceHighest
        NevPillStyle.Danger -> NevRed.DangerTile
    }
    val circleIcon = when (style) {
        NevPillStyle.Gradient -> NevRed.GradientEnd
        else -> contentColor
    }
    val shape = nevPillShape()
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        shape = shape,
        color = Color.Transparent,
        border = when (style) {
            NevPillStyle.Gradient -> BorderStroke(1.dp, NevRed.OutlineBright.copy(alpha = 0.8f))
            NevPillStyle.Neutral -> BorderStroke(1.dp, NevRed.Outline)
            NevPillStyle.Danger -> BorderStroke(1.dp, NevRed.DangerContent.copy(alpha = 0.5f))
        }
    ) {
        Row(
            modifier = Modifier
                .background(
                    when (style) {
                        NevPillStyle.Gradient -> NevRed.Gradient
                        NevPillStyle.Neutral -> NevRed.solid(NevRed.SurfaceHigh)
                        NevPillStyle.Danger -> NevRed.solid(NevRed.DangerTile.copy(alpha = 0.6f))
                    }
                )
                .padding(start = 6.dp, end = 18.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(circleColor, nevPillShape()),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painter,
                    contentDescription = null,
                    tint = circleIcon
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}

/**
 * Item tab sisi bernomor ("01 Informasi"); yang terpilih memakai gradasi merah-oranye.
 */
@Composable
fun NevNumberedTab(
    number: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = nevShape(18.dp)
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Tab },
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(
            1.dp,
            if (selected) NevRed.OutlineBright else NevRed.Outline.copy(alpha = 0.7f)
        )
    ) {
        Row(
            modifier = Modifier
                .background(
                    if (selected) NevRed.Gradient
                    else NevRed.solid(NevRed.Panel)
                )
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.material3.LocalContentColor provides
                        if (selected) NevRed.OnGradient else NevRed.OnPanel
            ) {
                content()
            }
        }
    }
}

/**
 * Tab atas (ikon + label di bawahnya) bergaya Nev; yang terpilih memakai gradasi merah-oranye.
 * Dipakai di layar Pengaturan dan Unduh.
 */
@Composable
fun NevTopTab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit
) {
    val shape = nevShape(16.dp)
    Surface(
        onClick = onClick,
        modifier = (if (selected) modifier.nevGlow(shape, 10.dp, 0.9f) else modifier)
            .semantics { role = Role.Tab },
        shape = shape,
        color = Color.Transparent,
        border = if (selected) BorderStroke(1.dp, NevRed.OutlineBright) else null
    ) {
        Column(
            modifier = Modifier
                .then(
                    if (selected) Modifier.background(NevRed.GradientDiagonal) else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.material3.LocalContentColor provides
                        if (selected) NevRed.OnGradient else NevRed.Muted
            ) {
                icon()
                label()
            }
        }
    }
}

/**
 * Tombol kecil bergradasi merah-oranye (mis. "Main" di kartu server).
 */
@Composable
fun NevGradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    val shape = nevPillShape()
    Surface(
        onClick = onClick,
        modifier = modifier
            .nevGlow(shape, 10.dp, 0.9f)
            .semantics { role = Role.Button },
        shape = shape,
        color = Color.Transparent,
        contentColor = NevRed.OnGradient
    ) {
        Row(
            modifier = Modifier
                .background(NevRed.Gradient)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            content = content
        )
    }
}

/** Warna kolom teks bergaris oranye gelap (kolom cari, dialog tambah server, dll). */
@Composable
fun nevTextFieldColors() = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
    focusedBorderColor = NevRed.OutlineBright,
    unfocusedBorderColor = NevRed.Outline,
    cursorColor = NevRed.GradientEnd,
    focusedContainerColor = NevRed.Panel,
    unfocusedContainerColor = NevRed.Panel
)

/** Border gradasi merah menyala untuk kartu. */
val NevCardBorder: BorderStroke
    @Composable get() = if (LocalNevStyle.current.liquid) {
        //Liquid: tepi kaca putih bening
        BorderStroke(
            1.dp,
            Brush.linearGradient(
                colorStops = arrayOf(
                    0f to Color.White.copy(alpha = 0.85f),
                    0.4f to Color.White.copy(alpha = 0.12f),
                    0.7f to Color.White.copy(alpha = 0.12f),
                    1f to Color.White.copy(alpha = 0.55f)
                )
            )
        )
    } else if (LocalNevStyle.current.cyber) {
        //Cyber: garis tepi samar; penanda sudut (cyberFrame) yang menonjol
        BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.28f))
    } else if (isBoxy()) {
        //Hero (kotak): garis tepi polos, tanpa gradasi
        BorderStroke(LocalNevStyle.current.cardBorderWidth, NevRed.OutlineBright)
    } else if (isNevGradient()) {
        //Nev: bingkai gradasi aksen (sama dengan kartu daftar mod/versi)
        if (LocalNevStyle.current.futuristic) {
            //Futuristik: bingkai emas tipis, terang di sudut lalu meredup
            BorderStroke(
                1.2.dp,
                Brush.linearGradient(
                    listOf(
                        NevRed.GradientEnd.copy(alpha = 0.95f),
                        NevRed.GradientStart.copy(alpha = 0.55f),
                        NevRed.GradientStart.copy(alpha = 0.28f)
                    )
                )
            )
        } else BorderStroke(
            1.5.dp,
            Brush.linearGradient(
                listOf(NevRed.GradientStart.copy(alpha = 0.9f), NevRed.GradientStart.copy(alpha = 0.35f))
            )
        )
    } else BorderStroke(
        LocalNevStyle.current.cardBorderWidth,
        Brush.linearGradient(
            listOf(
                NevRed.GradientStart.copy(alpha = 0.85f),
                NevRed.Outline,
                NevRed.GradientEnd.copy(alpha = 0.85f)
            )
        )
    )

/** false: layar tertentu (mis. daftar versi game & layar Pasang) memakai tampilan default tanpa gradasi aksen. */
//Edisi Rush: kartu polos tanpa gradasi (lebih ringan)
val LocalNevGradientCards = androidx.compose.runtime.compositionLocalOf { !com.movtery.zalithlauncher.BuildConfig.RUSH }

/** true: desain Nev dan gradasi aksen kartu/dialog/panel sedang aktif. */
@Composable
fun isNevGradient(): Boolean = (isNevDesign() || LocalNevStyle.current.futuristic || LocalNevStyle.current.liquid) && LocalNevGradientCards.current

/** Warna dasar kartu gradasi: tembus pandang (kaca) pada tema Futuristik, solid pada Nev. */
val NevCardBase: Color
    @Composable get() = if (LocalNevStyle.current.futuristic) NevRed.PanelDeep.copy(alpha = 0.62f) else NevRed.PanelDeep

/** Isi kartu gradasi: bagian atas berpendar warna aksen lalu memudar ke gelap (sama dengan kartu daftar mod/versi). */
val NevCardFill: Brush
    @Composable get() = if (LocalNevStyle.current.liquid) {
        //Liquid Glass: kilau kaca diagonal, terang di sudut atas-kiri, bening di tengah, kilau tipis di bawah-kanan
        Brush.linearGradient(
            colorStops = arrayOf(
                0f to Color.White.copy(alpha = 0.14f),
                0.35f to Color.White.copy(alpha = 0.03f),
                0.7f to Color.White.copy(alpha = 0.01f),
                1f to Color.White.copy(alpha = 0.07f)
            )
        )
    } else if (LocalNevStyle.current.futuristic) {
        //Futuristik: kaca gelap dengan pendar emas dari kiri
        Brush.horizontalGradient(
            listOf(
                NevRed.GradientStart.copy(alpha = 0.20f),
                NevRed.PanelDeep.copy(alpha = 0.55f),
                NevRed.Panel.copy(alpha = 0.45f)
            )
        )
    } else Brush.verticalGradient(
        listOf(NevRed.GradientStart.copy(alpha = 0.28f), NevRed.PanelDeep, NevRed.Panel)
    )

/**
 * Pengganti [androidx.compose.material3.Surface] untuk dialog/panel: pada desain Nev tampil dengan
 * bingkai + isi gradasi aksen, pada desain lain identik dengan Surface biasa.
 */
@Composable
fun NevGradientSurface(
    modifier: Modifier = Modifier,
    shape: Shape = androidx.compose.ui.graphics.RectangleShape,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = androidx.compose.material3.contentColorFor(color),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    if (isNevGradient()) {
        androidx.compose.material3.Surface(
            modifier = modifier,
            shape = shape,
            color = NevCardBase,
            contentColor = NevRed.OnPanel,
            tonalElevation = 0.dp,
            shadowElevation = if (LocalNevStyle.current.liquid || LocalNevStyle.current.futuristic) 0.dp else shadowElevation,
            border = border ?: NevCardBorder
        ) {
            Box(modifier = Modifier.background(NevCardFill), propagateMinConstraints = true) {
                content()
            }
        }
    } else {
        androidx.compose.material3.Surface(
            modifier = modifier,
            shape = shape,
            color = color,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            shadowElevation = if (LocalNevStyle.current.liquid || LocalNevStyle.current.futuristic) 0.dp else shadowElevation,
            border = border,
            content = content
        )
    }
}

/** Versi yang bisa diklik dari [NevGradientSurface]. */
@Composable
fun NevGradientSurface(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = androidx.compose.ui.graphics.RectangleShape,
    color: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = androidx.compose.material3.contentColorFor(color),
    tonalElevation: Dp = 0.dp,
    shadowElevation: Dp = 0.dp,
    border: BorderStroke? = null,
    content: @Composable () -> Unit
) {
    if (isNevGradient()) {
        androidx.compose.material3.Surface(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            color = NevCardBase,
            contentColor = NevRed.OnPanel,
            tonalElevation = 0.dp,
            shadowElevation = if (LocalNevStyle.current.liquid || LocalNevStyle.current.futuristic) 0.dp else shadowElevation,
            border = border ?: NevCardBorder
        ) {
            Box(modifier = Modifier.background(NevCardFill), propagateMinConstraints = true) {
                content()
            }
        }
    } else {
        androidx.compose.material3.Surface(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            shape = shape,
            color = color,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            shadowElevation = if (LocalNevStyle.current.liquid || LocalNevStyle.current.futuristic) 0.dp else shadowElevation,
            border = border,
            content = content
        )
    }
}

/**
 * Efek glow merah: bayangan berwarna tema di sekeliling bentuk (di luar isi, tidak menutupi konten).
 * @param radius jarak sebar cahaya
 * @param intensity 0..1, kekuatan cahaya
 */
fun Modifier.nevGlow(
    shape: Shape,
    radius: Dp = 8.dp,
    intensity: Float = 0.6f
): Modifier = this //Glow bayangan dimatikan di semua tema: berat saat scroll dan tembus ke panel kaca

/**
 * Tab atas bergaya kapsul untuk layar Unduh: lingkaran ikon di kiri, nomor kecil ("01") di atas label.
 * Yang terpilih memakai gradasi merah-oranye.
 */
@Composable
fun NevPillTab(
    number: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit
) {
    if (isBoxy()) {
        //gaya kotak: chip kotak berlengkung tipis, ikon + teks sebaris, semua bergaris tepi
        val chipShape = nevShape(10.dp)
        val content = if (selected) NevRed.OnGradient else NevRed.OnPanel
        Surface(
            onClick = onClick,
            modifier = modifier.semantics { role = Role.Tab },
            shape = chipShape,
            color = Color.Transparent,
            border = BorderStroke(
                1.dp,
                if (selected) NevRed.OutlineBright else NevRed.Outline
            )
        ) {
            Row(
                modifier = Modifier
                    .background(
                        if (selected) NevRed.Gradient
                        else androidx.compose.ui.graphics.SolidColor(NevRed.Panel.glassAlpha(0.8f))
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides
                            if (selected) content else NevRed.Muted
                ) {
                    Box(modifier = Modifier.size(20.dp), contentAlignment = Alignment.Center) { icon() }
                }
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides content
                ) {
                    androidx.compose.material3.ProvideTextStyle(
                        MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    ) {
                        label()
                    }
                }
            }
        }
        return
    }

    val shape = nevPillShape()
    val iconShape = nevPillShape()
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Tab },
        shape = shape,
        color = Color.Transparent,
        border = if (selected) BorderStroke(1.dp, NevRed.OutlineBright) else null
    ) {
        Row(
            modifier = Modifier
                .then(if (selected) Modifier.background(NevRed.Gradient) else Modifier)
                .padding(start = 6.dp, end = 18.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val content = if (selected) NevRed.OnGradient else NevRed.OnPanel
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(
                        1.dp,
                        if (selected) NevRed.OnGradient.copy(alpha = 0.35f) else NevRed.Outline,
                        iconShape
                    )
                    .background(
                        if (selected) NevRed.OnGradient.copy(alpha = 0.18f)
                        else NevRed.SurfaceHigh.glassAlpha(0.6f),
                        iconShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides
                            if (selected) content else NevRed.Muted
                ) {
                    icon()
                }
            }
            Column {
                androidx.compose.runtime.CompositionLocalProvider(
                    androidx.compose.material3.LocalContentColor provides content
                ) {
                    androidx.compose.material3.ProvideTextStyle(
                        MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    ) {
                        label()
                    }
                }
            }
        }
    }
}

/** Wadah kapsul bergaris gradasi untuk deretan tab atas. */
@Composable
fun NevPillBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (isBoxy()) {
        //gaya kotak: tanpa kapsul pembungkus, chip berdiri sendiri
        Box(modifier = modifier) { content() }
        return
    }
    if (isNevDesign()) {
        //tema Nev: tanpa garis/kapsul pembungkus, tab berdiri sendiri
        Box(modifier = modifier) { content() }
        return
    }
    val shape = nevPillShape()
    Box(
        modifier = modifier
            .border(
                BorderStroke(
                    1.dp,
                    Brush.horizontalGradient(listOf(NevRed.OutlineBright.copy(alpha = 0.8f), NevRed.Outline))
                ),
                shape
            )
            .background(NevRed.Panel.glassAlpha(0.72f), shape)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        content()
    }
}

/** Ikon berlian (kotak diputar 45°) kecil untuk chip filter. */
@Composable
fun NevDiamondIcon(
    color: Color,
    modifier: Modifier = Modifier,
    filled: Boolean = false
) {
    val round = isNevDesign()
    androidx.compose.foundation.Canvas(modifier = modifier.size(18.dp)) {
        val s = size.minDimension
        if (round) {
            //tema Nev: ikon bundar menggantikan berlian
            drawCircle(
                color = color,
                radius = s / 2f - s * 0.06f,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = s * 0.12f)
            )
            if (filled) drawCircle(color = color, radius = s * 0.24f)
            return@Canvas
        }
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(s / 2f, 0f)
            lineTo(s, s / 2f)
            lineTo(s / 2f, s)
            lineTo(0f, s / 2f)
            close()
        }
        drawPath(
            path = path,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = s * 0.12f)
        )
        if (filled) {
            val inner = androidx.compose.ui.graphics.Path().apply {
                moveTo(s / 2f, s * 0.28f)
                lineTo(s * 0.72f, s / 2f)
                lineTo(s / 2f, s * 0.72f)
                lineTo(s * 0.28f, s / 2f)
                close()
            }
            drawPath(inner, color)
        }
    }
}

/**
 * Chip filter bergaya Nev: berlian + teks kapital monospasi.
 * Terpilih: gradasi merah-oranye; tidak terpilih: panel gelap bergaris oranye.
 */
@Composable
fun NevFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = if (isNevDesign()) nevPillShape() else nevShape(16.dp)
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Checkbox },
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(
            1.dp,
            if (selected) NevRed.OutlineBright else NevRed.Outline.copy(alpha = 0.8f)
        )
    ) {
        Row(
            modifier = Modifier
                .background(
                    if (selected) NevRed.GradientDiagonal
                    else NevRed.solid(NevRed.Panel.glassAlpha(0.72f))
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NevDiamondIcon(
                color = if (selected) NevRed.OnGradient else NevRed.GradientEnd.copy(alpha = 0.9f),
                filled = selected
            )
            Text(
                text = text.uppercase(),
                style = MaterialTheme.typography.titleSmall,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (selected) NevRed.OnGradient else NevRed.OnPanel,
                maxLines = 1
            )
        }
    }
}

/** Bingkai besar bergaris gradasi oranye untuk satu layar penuh (dialog-like). */
@Composable
fun NevPanelFrame(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 36.dp,
    content: @Composable () -> Unit
) {
    val shape = nevShape(cornerRadius)
    Box(
        modifier = modifier
            .border(
                BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(listOf(NevRed.OutlineBright.copy(alpha = 0.8f), NevRed.Outline))
                ),
                shape
            )
            .background(
                Brush.verticalGradient(
                    listOf(NevRed.Panel.glassAlpha(0.82f), NevRed.PanelDeep.glassAlpha(0.9f))
                ),
                shape
            )
            .padding(12.dp)
    ) {
        content()
    }
}

/** Panel dalam (mis. panel info di kiri) dengan garis tepi berwarna aksen. */
@Composable
fun NevInnerPanel(
    modifier: Modifier = Modifier,
    accent: Color = NevRed.OutlineBright,
    cornerRadius: androidx.compose.ui.unit.Dp = 26.dp,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    val shape = nevShape(cornerRadius)
    Column(
        modifier = modifier
            .border(1.dp, accent.copy(alpha = 0.75f), shape)
            .background(
                Brush.verticalGradient(
                    listOf(accent.copy(alpha = 0.14f), NevRed.PanelDeep.glassAlpha(0.85f))
                ),
                shape
            )
            .padding(16.dp),
        content = content
    )
}

/**
 * Tombol aksi bergradasi merah-oranye (mis. "Pasang"), ikon opsional di kiri teks.
 */
@Composable
fun NevActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    enabled: Boolean = true,
    cornerRadius: androidx.compose.ui.unit.Dp = 20.dp
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.semantics { role = Role.Button },
        shape = nevShape(cornerRadius),
        color = Color.Transparent,
        contentColor = NevRed.OnGradient
    ) {
        Row(
            modifier = Modifier
                .background(
                    if (enabled) NevRed.Gradient
                    else NevRed.solid(NevRed.SurfaceHigh)
                )
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
        ) {
            if (painter != null) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painter,
                    contentDescription = null
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

/**
 * Atur opasitas warna panel NevRed. Gaya lain: opasitas persis [alpha].
 * Gaya Liquid: panel tetap berupa kaca tembus pandang, jadi opasitas hanya boleh lebih rendah dari nilai kaca bawaan.
 */
@Composable
fun Color.glassAlpha(alpha: Float): Color =
    if (LocalNevStyle.current.liquid) copy(alpha = minOf(this.alpha, alpha)) else copy(alpha = alpha)
