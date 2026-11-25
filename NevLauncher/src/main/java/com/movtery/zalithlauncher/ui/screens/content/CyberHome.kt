package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.movtery.zalithlauncher.ui.screens.content.elements.CommonVersionInfoLayout
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
    var showVersions by remember { mutableStateOf(false) }
    val versions by VersionsManager.versions.collectAsStateWithLifecycle()
    val primary = MaterialTheme.colorScheme.primary
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

        //kanan atas: chip versi (dropdown versi terpasang + kelola versi)
        Box(modifier = Modifier.align(Alignment.TopEnd).padding(end = 12.dp, top = 8.dp)) {
        Box {
            Row(
                modifier = Modifier
                    .clip(chipShape)
                    .background(glass)
                    .border(neon, chipShape)
                    .clickable { showVersions = true }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = version?.getVersionName() ?: stringResource(R.string.hero_empty_title),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(R.drawable.ic_keyboard_arrow_down),
                    contentDescription = stringResource(R.string.hero_switch_version)
                )
            }
            DropdownMenu(
                expanded = showVersions,
                onDismissRequest = { showVersions = false },
                modifier = Modifier.width(280.dp),
                shape = chipShape
            ) {
                versions.forEach { v ->
                    DropdownMenuItem(
                        text = {
                            CommonVersionInfoLayout(
                                modifier = Modifier.fillMaxWidth(),
                                version = v,
                                iconSize = 28.dp
                            )
                        },
                        onClick = {
                            if (version != v) VersionsManager.saveVersion(v)
                            showVersions = false
                        }
                    )
                }
                HorizontalDivider()
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(R.string.hero_manage_versions),
                            fontWeight = FontWeight.SemiBold,
                            color = primary
                        )
                    },
                    onClick = {
                        showVersions = false
                        toVersionManageScreen()
                    }
                )
            }
        }
        }

        //kanan bawah: nomor versi launcher
        Box(modifier = Modifier.align(Alignment.BottomEnd).padding(end = 14.dp, bottom = 2.dp)) {
            Text(
                modifier = Modifier.alpha(0.6f),
                text = "v${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
