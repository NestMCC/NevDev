package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.zalithlauncher.BuildConfig
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.account.getAccountTypeName
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.ui.components.animated
import com.movtery.zalithlauncher.ui.components.rememberAnimatedContext
import com.movtery.zalithlauncher.ui.screens.content.elements.PlayerFace

/**
 * Beranda khusus tema Cyber: latar penuh (gambar latar pengguna terlihat jelas),
 * akun di kiri atas, dan bilah kaca melayang di bawah: menu cepat, tombol MAIN besar di tengah,
 * serta versi dan akun di kanan.
 */
@Composable
fun CyberHome(
    isVisible: Boolean,
    onLaunchGame: (Version?) -> Unit,
    onOpenFileManager: () -> Unit,
    toAccountManageScreen: () -> Unit,
    toVersionManageScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = rememberAnimatedContext(isVisible = isVisible)
    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val refreshWardrobe by AccountsManager.refreshWardrobe.collectAsStateWithLifecycle()
    val version by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val primary = MaterialTheme.colorScheme.primary
    val tertiary = MaterialTheme.colorScheme.tertiary
    val barShape = CutCornerShape(topStart = 18.dp, bottomEnd = 18.dp)
    val chipShape = CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp)
    val glass = Color(0xFF05080F).copy(alpha = 0.55f)
    val neon = BorderStroke(1.dp, primary.copy(alpha = 0.75f))

    Box(modifier = modifier.fillMaxSize().animated(ctx)) {
        //peredam gelap di bawah agar bilah dan teks tetap terbaca di atas gambar apa pun
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(190.dp)
                .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.55f))))
        )

        //kiri atas: akun
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 12.dp, top = 8.dp)
                .clip(chipShape)
                .background(glass)
                .clickable(onClick = toAccountManageScreen)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            account?.let { PlayerFace(account = it, avatarSize = 26.dp, refreshKey = refreshWardrobe) }
            Column {
                Text(
                    text = account?.username ?: stringResource(R.string.account_add_new_account),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                account?.let {
                    Text(
                        modifier = Modifier.alpha(0.75f),
                        text = getAccountTypeName(it),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                }
            }
        }

        //bilah kaca di bawah
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 14.dp, end = 14.dp, bottom = 16.dp)
                .clip(barShape)
                .background(glass)
                .background(
                    Brush.horizontalGradient(
                        listOf(primary.copy(alpha = 0.16f), Color.Transparent, tertiary.copy(alpha = 0.16f))
                    )
                )
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            //kiri: menu cepat
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CyberBarItem(R.drawable.ic_home_filled, stringResource(R.string.hero_nav_home), selected = true) {}
                CyberBarItem(R.drawable.ic_extension_outlined, stringResource(R.string.cyber_nav_versions), onClick = toVersionManageScreen)
                CyberBarItem(R.drawable.ic_folder_outlined, stringResource(R.string.cyber_nav_files), onClick = onOpenFileManager)
            }

            //tengah: MAIN
            Box(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .width(150.dp)
                    .height(46.dp)
                    .clip(CutCornerShape(topStart = 14.dp, bottomEnd = 14.dp))
                    .background(Brush.horizontalGradient(listOf(primary, tertiary)))
                    .clickable { onLaunchGame(null) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(
                        modifier = Modifier.size(20.dp),
                        painter = painterResource(R.drawable.ic_play_arrow_filled),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = stringResource(R.string.main_play).uppercase(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        letterSpacing = 1.5.sp,
                        maxLines = 1
                    )
                }
            }

            //kanan: versi dan akun
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clip(chipShape)
                        .background(Color.White.copy(alpha = 0.06f))
                        .clickable(onClick = toVersionManageScreen)
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = version?.getVersionName() ?: stringResource(R.string.hero_empty_title),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                account?.let {
                    Row(
                        modifier = Modifier
                            .clip(chipShape)
                            .background(Color.White.copy(alpha = 0.06f))
                            .clickable(onClick = toAccountManageScreen)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        PlayerFace(account = it, avatarSize = 26.dp, refreshKey = refreshWardrobe)
                        Column {
                            Text(
                                text = it.username,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                modifier = Modifier.alpha(0.7f),
                                text = getAccountTypeName(it),
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        //kanan bawah: nomor versi launcher
        Text(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 2.dp)
                .alpha(0.55f),
            text = "v${BuildConfig.VERSION_NAME}",
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun CyberBarItem(
    iconRes: Int,
    label: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier
            .clip(CutCornerShape(topStart = 10.dp, bottomEnd = 10.dp))
            .background(if (selected) primary.copy(alpha = 0.18f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = if (selected) primary else MaterialTheme.colorScheme.onSurface
        )
        Spacer(Modifier.height(1.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) primary else MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
    }
}
