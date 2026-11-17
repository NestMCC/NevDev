package com.movtery.zalithlauncher.ui.screens.content.versions

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import com.movtery.zalithlauncher.ui.theme.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.backup.BackupKind
import com.movtery.zalithlauncher.game.backup.WorldBackup
import com.movtery.zalithlauncher.game.backup.WorldBackupManager
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.SimpleAlertDialog
import com.movtery.zalithlauncher.ui.components.animated
import com.movtery.zalithlauncher.ui.components.rememberAnimatedContext
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.CardPosition
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.IntSliderSettingsCard
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.SwitchSettingsCard
import com.movtery.zalithlauncher.ui.screens.content.versions.layouts.VersionChunkBackground
import com.movtery.zalithlauncher.ui.theme.nevShape
import com.movtery.zalithlauncher.utils.file.formatFileSize
import com.movtery.zalithlauncher.utils.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

@OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)
/** Tab "Backup" di Kelola Versi › Dunia: pengaturan + daftar cadangan world. */
@Composable
fun WorldBackupScreen(
    mainScreenKey: TitledNavKey?,
    versionsScreenKey: TitledNavKey?,
    version: Version,
    backToMainScreen: () -> Unit
) {
    if (!version.isValid()) {
        backToMainScreen()
        return
    }

    BaseScreen(
        levels1 = listOf(
            Pair(NestedNavKey.VersionSettings::class.java, mainScreenKey)
        ),
        Triple(NormalNavKey.Versions.WorldBackup, versionsScreenKey, false),
    ) { isVisible ->
        val ctx = rememberAnimatedContext(isVisible = isVisible)
        val context = LocalContext.current
        val scope = rememberCoroutineScope()

        var backups by remember { mutableStateOf<List<WorldBackup>?>(null) }
        var working by remember { mutableStateOf(false) }
        var toRestore by remember { mutableStateOf<WorldBackup?>(null) }
        var toDelete by remember { mutableStateOf<WorldBackup?>(null) }

        fun reload() {
            scope.launch {
                backups = withContext(Dispatchers.IO) { WorldBackupManager.listBackups(version) }
            }
        }
        LaunchedEffect(version) { reload() }

        VersionChunkBackground(
            modifier = Modifier
                .fillMaxSize()
                .padding(all = 12.dp)
                .animated(ctx),
            paddingValues = PaddingValues()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        SwitchSettingsCard(
                            modifier = Modifier.fillMaxWidth(),
                            position = CardPosition.Top,
                            unit = AllSettings.worldBackupOnExit,
                            title = stringResource(R.string.backup_world_on_exit_title),
                            summary = stringResource(R.string.backup_world_on_exit_summary)
                        )
                        SwitchSettingsCard(
                            modifier = Modifier.fillMaxWidth(),
                            position = CardPosition.Middle,
                            unit = AllSettings.worldBackupPeriodic,
                            title = stringResource(R.string.backup_world_periodic_title),
                            summary = stringResource(R.string.backup_world_periodic_summary)
                        )
                        IntSliderSettingsCard(
                            modifier = Modifier.fillMaxWidth(),
                            position = CardPosition.Middle,
                            unit = AllSettings.worldBackupIntervalMin,
                            title = stringResource(R.string.backup_world_interval_title),
                            valueRange = 5f..120f,
                            steps = 22,
                            suffix = stringResource(R.string.backup_world_minutes)
                        )
                        IntSliderSettingsCard(
                            modifier = Modifier.fillMaxWidth(),
                            position = CardPosition.Bottom,
                            unit = AllSettings.worldBackupMax,
                            title = stringResource(R.string.backup_world_max_title),
                            summary = stringResource(R.string.backup_world_max_summary),
                            valueRange = 0f..50f,
                            steps = 49
                        )
                    }
                }

                item {
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !working,
                        shape = nevShape(16.dp),
                        onClick = {
                            scope.launch {
                                working = true
                                val n = WorldBackupManager.backupAll(version, BackupKind.MANUAL)
                                working = false
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.backup_world_done, n),
                                    Toast.LENGTH_SHORT
                                ).show()
                                reload()
                            }
                        }
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = painterResource(R.drawable.ic_archive_filled),
                            contentDescription = null
                        )
                        Text(
                            modifier = Modifier.padding(start = 8.dp),
                            text = stringResource(
                                if (working) R.string.backup_world_working else R.string.backup_world_now
                            )
                        )
                    }
                }

                val list = backups
                when {
                    list == null -> item {
                        Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            LoadingIndicator()
                        }
                    }
                    list.isEmpty() -> item {
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            text = stringResource(R.string.backup_world_empty),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    else -> items(list, key = { it.file.absolutePath }) { b ->
                        BackupRow(
                            backup = b,
                            onRestore = { toRestore = b },
                            onDelete = { toDelete = b }
                        )
                    }
                }
            }
        }

        toRestore?.let { b ->
            SimpleAlertDialog(
                title = stringResource(R.string.backup_world_restore_title),
                text = stringResource(R.string.backup_world_restore_message, b.worldName),
                onConfirm = {
                    toRestore = null
                    scope.launch {
                        working = true
                        val ok = WorldBackupManager.restore(version, b)
                        working = false
                        Toast.makeText(
                            context,
                            context.getString(
                                if (ok) R.string.backup_world_restore_ok else R.string.backup_world_restore_fail
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                onDismiss = { toRestore = null }
            )
        }
        toDelete?.let { b ->
            SimpleAlertDialog(
                title = stringResource(R.string.backup_world_delete_title),
                text = stringResource(R.string.backup_world_delete_message),
                onConfirm = {
                    toDelete = null
                    scope.launch(Dispatchers.IO) {
                        WorldBackupManager.delete(b)
                        reload()
                    }
                },
                onDismiss = { toDelete = null }
            )
        }
    }
}

@Composable
private fun BackupRow(
    backup: WorldBackup,
    onRestore: () -> Unit,
    onDelete: () -> Unit
) {
    val accent = MaterialTheme.colorScheme.primary
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = nevShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(start = 14.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = backup.worldName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${formatDate(Date(backup.time))} • ${formatFileSize(backup.size)} • " +
                        when (backup.kind) {
                            BackupKind.EXIT -> stringResource(R.string.backup_world_kind_exit)
                            BackupKind.AUTO -> stringResource(R.string.backup_world_kind_auto)
                            BackupKind.MANUAL -> stringResource(R.string.backup_world_kind_manual)
                        },
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onRestore) {
                Icon(
                    painter = painterResource(R.drawable.ic_restart_alt),
                    contentDescription = stringResource(R.string.backup_world_restore_title),
                    tint = accent
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete_outlined),
                    contentDescription = stringResource(R.string.generic_delete),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
