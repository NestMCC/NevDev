package com.movtery.zalithlauncher.ui.screens.content.elements

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape as BaseRoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.coroutine.TaskStage
import com.movtery.zalithlauncher.coroutine.TitledTask
import com.movtery.zalithlauncher.ui.AndroidStringText
import com.movtery.zalithlauncher.ui.theme.TextButton

/**
 * Layar loading penuh saat masuk game: wordmark "NEV LAUNCHER" bergaya blok 3D,
 * pelat tagline, dan bar progres dari rangkaian tugas peluncuran.
 */
@Composable
fun GameLoadingOverlay(
    tasks: List<TitledTask>,
    onCancel: () -> Unit
) {
    val stages = tasks.map { key(it.task.id) { it.task.stage.collectAsStateWithLifecycle() } }
    val progresses = tasks.map { key(it.task.id) { it.task.progress.collectAsStateWithLifecycle() } }
    val total = tasks.size.coerceAtLeast(1)
    val overall = tasks.indices.sumOf { i ->
        when (stages[i].value) {
            TaskStage.COMPLETED -> 1.0
            TaskStage.RUNNING -> progresses[i].value.coerceAtLeast(0f).toDouble()
            TaskStage.PREPARING -> 0.0
        }
    }.toFloat() / total
    val animated by animateFloatAsState(overall.coerceIn(0f, 1f), label = "GameLoadingProgress")
    val activeIndex = stages.indexOfFirst { it.value == TaskStage.RUNNING }.takeIf { it >= 0 }
        ?: stages.indexOfFirst { it.value == TaskStage.PREPARING }.takeIf { it >= 0 }
        ?: tasks.lastIndex
    val active = tasks.getOrNull(activeIndex)

    val scheme = MaterialTheme.colorScheme
    val accent = scheme.primary
    val deep = lerp(Color.Black, accent, 0.28f)
    val face = lerp(Color.White, accent, 0.12f)

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(lerp(Color(0xFF0B0B14), accent, 0.18f), Color(0xFF07070D))
                    )
                )
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                //wordmark: lapisan bayangan bergeser = efek ekstrusi 3D
                Box(contentAlignment = Alignment.Center) {
                    for (d in 6 downTo 1) {
                        WordmarkText(
                            modifier = Modifier.offset(x = d.dp, y = d.dp),
                            color = lerp(Color.Black, deep, 1f - d / 8f)
                        )
                    }
                    WordmarkText(color = face)
                }

                //pelat tagline
                Box(
                    modifier = Modifier
                        .clip(BaseRoundedCornerShape(4.dp))
                        .background(Color(0xFF14141F))
                        .border(2.dp, accent, BaseRoundedCornerShape(4.dp))
                        .padding(horizontal = 18.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.game_loading_tagline),
                        color = Color.White,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        letterSpacing = 3.sp
                    )
                }
            }

            //bar progres + teks di bawah
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 32.dp, vertical = 28.dp)
                    .widthIn(max = 420.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.game_loading_text),
                        color = Color.White.copy(alpha = 0.75f),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${(animated * 100).toInt()}%",
                        color = accent,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animated)
                            .background(accent)
                    )
                }
                if (active != null) {
                    AndroidStringText(
                        text = active.title,
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.55f)),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
                TextButton(onClick = onCancel) {
                    Text(
                        text = stringResource(R.string.generic_cancel),
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
private fun WordmarkText(color: Color, modifier: Modifier = Modifier) {
    Text(
        modifier = modifier,
        text = "NEV LAUNCHER",
        color = color,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Black,
        fontSize = 44.sp,
        letterSpacing = 2.sp,
        maxLines = 1
    )
}
