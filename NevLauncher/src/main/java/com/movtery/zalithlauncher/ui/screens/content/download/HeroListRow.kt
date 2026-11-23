package com.movtery.zalithlauncher.ui.screens.content.download

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.height
import androidx.compose.animation.animateContentSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
    plusPrefix: Boolean = true,
    onActionClick: (() -> Unit)? = null,
    icon: @Composable () -> Unit,
    extra: (@Composable RowScope.() -> Unit)? = null,
    /** Bila tidak null, baris bisa dibuka (dropdown): tombol hijau diganti panah dan isi tampil di bawah. */
    expandedContent: (@Composable ColumnScope.() -> Unit)? = null
) {
    var expanded by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }
    val line = NevRed.Outline
    Surface(
        modifier = modifier.fillMaxWidth(),
        onClick = if (expandedContent != null) ({ expanded = !expanded }) else onClick,
        shape = RectangleShape,
        color = NevRed.PanelDeep,
        contentColor = NevRed.OnPanel,
        border = BorderStroke(1.dp, line)
    ) {
      Column(modifier = Modifier.animateContentSize()) {
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

            if (expandedContent != null) {
                //panah dropdown
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, line),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(28.dp),
                        painter = painterResource(
                            if (expanded) R.drawable.ic_arrow_drop_up_rounded else R.drawable.ic_arrow_drop_down_rounded
                        ),
                        contentDescription = null
                    )
                }
            } else {
                //tombol hijau "+ INSTAL" (seluruh baris yang menangani klik)
                Box(
                    modifier = Modifier
                        .background(Color(0xFF10B981), RoundedCornerShape(0.dp))
                        .then(if (onActionClick != null) Modifier.clickable(onClick = onActionClick) else Modifier)
                        .padding(horizontal = 14.dp, vertical = 9.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (plusPrefix) "+ ${actionText.uppercase()}" else actionText.uppercase(),
                        color = Color.White,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                }
            }
        }
        if (expandedContent != null && expanded) {
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(line))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                content = expandedContent
            )
        }
      }
    }
}
