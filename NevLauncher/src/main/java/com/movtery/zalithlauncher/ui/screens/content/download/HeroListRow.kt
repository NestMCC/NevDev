package com.movtery.zalithlauncher.ui.screens.content.download

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.ui.theme.NevRed

/**
 * Baris daftar khusus tema Hero (kotak, polos tanpa gradasi): ubin ikon bergaris, judul + "by penulis",
 * deskripsi satu baris, chip kecil bergaris, dan tombol hijau "+ INSTAL" di kanan.
 * Seluruh baris bisa diklik.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HeroListRow(
    onClick: () -> Unit,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    description: String? = null,
    downloadsText: String? = null,
    chips: List<String> = emptyList(),
    actionText: String,
    icon: @Composable () -> Unit,
    extra: (@Composable RowScope.() -> Unit)? = null
) {
    val line = NevRed.Outline
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RectangleShape,
        color = NevRed.PanelDeep,
        contentColor = NevRed.OnPanel,
        border = BorderStroke(1.dp, line)
    ) {
        Row(
            modifier = Modifier.padding(all = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            //ubin ikon bergaris
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .border(1.dp, line),
                contentAlignment = Alignment.Center
            ) { icon() }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        modifier = Modifier.weight(1f, fill = false),
                        text = title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!subtitle.isNullOrBlank()) {
                        Text(
                            modifier = Modifier.alpha(0.65f),
                            text = "by $subtitle",
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                if (!description.isNullOrBlank()) {
                    Text(
                        modifier = Modifier.alpha(0.75f),
                        text = description,
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (downloadsText != null) {
                        Row(
                            modifier = Modifier
                                .border(1.dp, line)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(12.dp),
                                painter = painterResource(R.drawable.ic_download_2_filled),
                                contentDescription = null
                            )
                            Text(
                                text = downloadsText,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }
                    chips.forEach { chip ->
                        Box(
                            modifier = Modifier
                                .border(1.dp, line)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                modifier = Modifier.alpha(0.8f),
                                text = chip,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            extra?.invoke(this)

            //tombol hijau "+ INSTAL" (seluruh baris yang menangani klik)
            Box(
                modifier = Modifier
                    .background(Color(0xFF10B981), RoundedCornerShape(0.dp))
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+ ${actionText.uppercase()}",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
            }
        }
    }
}
