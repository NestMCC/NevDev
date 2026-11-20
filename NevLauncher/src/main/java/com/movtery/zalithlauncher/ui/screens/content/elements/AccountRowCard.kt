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

package com.movtery.zalithlauncher.ui.screens.content.elements

import com.movtery.zalithlauncher.ui.theme.glassAlpha
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import com.movtery.zalithlauncher.ui.theme.CircleShape
import com.movtery.zalithlauncher.ui.theme.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.account.Account
import com.movtery.zalithlauncher.game.account.AccountType
import com.movtery.zalithlauncher.game.account.getAccountTypeName
import com.movtery.zalithlauncher.ui.theme.NevCardBorder
import com.movtery.zalithlauncher.ui.theme.NevRed
import com.movtery.zalithlauncher.ui.theme.nevShape

private val ActiveGreen = Color(0xFF5BE38B)

/**
 * Kartu akun gaya baru: avatar berbingkai, nama + lencana jenis akun / AKTIF,
 * lalu tombol bulat (muat ulang, salin UUID, hapus). Memakai warna tema launcher.
 */
@Composable
fun AccountRowCard(
    currentAccount: Account?,
    account: Account,
    modifier: Modifier = Modifier,
    refreshKey: Any? = null,
    onSelected: (Account) -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onCopyUUID: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    val selected = currentAccount?.uniqueUUID == account.uniqueUUID
    val shape = nevShape(22.dp)
    val accent = NevRed.GradientStart

    Surface(
        modifier = modifier,
        shape = shape,
        color = NevRed.PanelDeep.glassAlpha(0.92f),
        contentColor = NevRed.OnPanel,
        border = if (selected) BorderStroke(1.5.dp, NevRed.Gradient) else NevCardBorder,
        onClick = { if (!selected) onSelected(account) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val avatarShape = RoundedCornerShape(12.dp)
            PlayerFace(
                modifier = Modifier
                    .clip(avatarShape)
                    .border(
                        width = if (selected) 2.dp else 1.dp,
                        color = if (selected) accent else NevRed.Outline,
                        shape = avatarShape
                    ),
                account = account,
                avatarSize = 52.dp,
                refreshKey = refreshKey
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = account.username,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccountBadge(text = getAccountTypeName(account), color = accent)
                    if (selected) {
                        AccountBadge(text = stringResource(R.string.account_active_badge), color = ActiveGreen)
                    }
                }
            }

            CircleAction(
                painterRes = R.drawable.ic_refresh,
                description = stringResource(R.string.generic_refresh),
                enabled = account.accountType != AccountType.LOCAL.tag,
                tint = accent,
                onClick = onRefreshClick
            )
            CircleAction(
                painterRes = R.drawable.ic_copy_all_outlined,
                description = stringResource(R.string.account_local_uuid_copy),
                tint = accent,
                onClick = onCopyUUID
            )
            CircleAction(
                painterRes = R.drawable.ic_delete_outlined,
                description = stringResource(R.string.generic_delete),
                tint = NevRed.DangerContent,
                onClick = onDeleteClick
            )
        }
    }
}

@Composable
private fun AccountBadge(text: String, color: Color) {
    val shape = RoundedCornerShape(50)
    Text(
        modifier = Modifier
            .border(1.dp, color.copy(alpha = 0.7f), shape)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = color,
        maxLines = 1
    )
}

@Composable
private fun CircleAction(
    painterRes: Int,
    description: String,
    tint: Color,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val c = if (enabled) tint else tint.copy(alpha = 0.3f)
    IconButton(
        modifier = Modifier
            .size(44.dp)
            .border(1.dp, c.copy(alpha = 0.6f), CircleShape),
        onClick = onClick,
        enabled = enabled
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(painterRes),
            contentDescription = description,
            tint = c
        )
    }
}
