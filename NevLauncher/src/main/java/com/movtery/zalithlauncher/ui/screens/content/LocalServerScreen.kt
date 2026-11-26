package com.movtery.zalithlauncher.ui.screens.content

import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.localserver.LocalServerConfig
import com.movtery.zalithlauncher.game.localserver.LocalServerManager
import com.movtery.zalithlauncher.game.localserver.LocalServerStatus
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.theme.NevRed
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import kotlinx.coroutines.launch

/**
 * Server Lokal: membuat dan menjalankan server Minecraft memakai mesin (JVM) perangkat ini.
 */
@Composable
fun LocalServerScreen(
    backScreenViewModel: ScreenBackStackViewModel
) {
    BaseScreen(
        screenKey = NormalNavKey.LocalServer,
        currentKey = backScreenViewModel.mainScreen.currentKey
    ) { _ ->
        LocalServerContent()
    }
}

@Composable
private fun LocalServerContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { LocalServerManager.init(context) }

    val servers by LocalServerManager.servers.collectAsState()
    val status by LocalServerManager.status.collectAsState()
    val runningId by LocalServerManager.runningId.collectAsState()
    val console by LocalServerManager.console.collectAsState()

    var selectedId by remember { mutableStateOf<String?>(null) }
    var showCreate by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf<LocalServerConfig?>(null) }

    val selected = servers.firstOrNull { it.id == selectedId } ?: servers.firstOrNull { it.id == runningId }

    val cardShape = RoundedCornerShape(14.dp)

    Row(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        //kiri: daftar server
        Column(
            modifier = Modifier.weight(0.38f).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.local_server_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedButton(
                    onClick = { showCreate = true },
                    border = BorderStroke(1.dp, NevRed.Outline)
                ) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        painter = painterResource(R.drawable.ic_add),
                        contentDescription = null
                    )
                    Text(stringResource(R.string.local_server_create))
                }
            }

            if (servers.isEmpty()) {
                Text(
                    text = stringResource(R.string.local_server_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = NevRed.OnPanel.copy(alpha = 0.7f)
                )
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(servers, key = { it.id }) { cfg ->
                    val isSel = cfg.id == selected?.id
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(NevRed.Panel, cardShape)
                            .border(
                                BorderStroke(if (isSel) 2.dp else 1.dp, if (isSel) NevRed.OutlineBright else NevRed.Outline),
                                cardShape
                            )
                            .clickable { selectedId = cfg.id }
                            .padding(12.dp)
                    ) {
                        Text(cfg.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(
                            text = "${cfg.mcVersion} · :${cfg.port} · ${cfg.ramMb} MB",
                            style = MaterialTheme.typography.bodySmall,
                            color = NevRed.OnPanel.copy(alpha = 0.7f)
                        )
                        if (cfg.id == runningId) {
                            Text(
                                text = statusText(status),
                                style = MaterialTheme.typography.labelMedium,
                                color = NevRed.GradientEnd,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        //kanan: detail + konsol
        Column(
            modifier = Modifier
                .weight(0.62f)
                .fillMaxHeight()
                .background(NevRed.Panel, cardShape)
                .border(BorderStroke(1.dp, NevRed.Outline), cardShape)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selected == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.local_server_pick), color = NevRed.OnPanel.copy(alpha = 0.7f))
                }
            } else {
                val isRunning = runningId == selected.id && status != LocalServerStatus.STOPPED
                val otherRunning = runningId != null && runningId != selected.id && status != LocalServerStatus.STOPPED

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(Modifier.weight(1f)) {
                        Text(selected.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1)
                        Text(
                            text = "Minecraft ${selected.mcVersion} · Java ${selected.javaMajor}+ · ${statusText(if (isRunning) status else LocalServerStatus.STOPPED)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = NevRed.OnPanel.copy(alpha = 0.7f)
                        )
                    }
                    if (isRunning) {
                        OutlinedButton(
                            enabled = status != LocalServerStatus.STOPPING,
                            onClick = { scope.launch { LocalServerManager.stop(context) } },
                            border = BorderStroke(1.dp, NevRed.Outline)
                        ) { Text(stringResource(R.string.local_server_stop)) }
                    } else {
                        OutlinedButton(
                            enabled = !otherRunning,
                            onClick = {
                                val err = LocalServerManager.start(context, selected)
                                if (err != null) {
                                    val msg = when {
                                        err.startsWith("java:") -> context.getString(R.string.local_server_no_java, err.removePrefix("java:"))
                                        else -> context.getString(R.string.local_server_start_failed, err)
                                    }
                                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                }
                            },
                            border = BorderStroke(1.dp, NevRed.OutlineBright)
                        ) { Text(stringResource(R.string.local_server_start), color = NevRed.GradientEnd, fontWeight = FontWeight.Bold) }
                        TextButton(onClick = { confirmDelete = selected }, enabled = !otherRunning) {
                            Text(stringResource(R.string.generic_delete))
                        }
                    }
                }

                //alamat untuk bergabung
                val addrs = remember(isRunning) { LocalServerManager.localAddresses() }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(stringResource(R.string.local_server_join), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    val lines = buildList {
                        add("localhost:${selected.port}  (${stringResource(R.string.local_server_this_device)})")
                        addrs.forEach { add("$it:${selected.port}") }
                    }
                    lines.forEach { line ->
                        Text(
                            modifier = Modifier.clickable {
                                val addr = line.substringBefore("  ")
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("address", addr))
                                Toast.makeText(context, R.string.local_server_copied, Toast.LENGTH_SHORT).show()
                            },
                            text = line,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text = stringResource(R.string.local_server_join_hint),
                        style = MaterialTheme.typography.labelSmall,
                        color = NevRed.OnPanel.copy(alpha = 0.6f)
                    )
                }

                //konsol
                val listState = rememberLazyListState()
                val shown = if (selected.id == runningId) console else emptyList()
                LaunchedEffect(shown.size) {
                    if (shown.isNotEmpty()) listState.scrollToItem(shown.lastIndex)
                }
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color.Black.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                        .padding(8.dp),
                    state = listState
                ) {
                    items(shown.size) { i ->
                        Text(
                            text = shown[i],
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 14.sp,
                            color = Color(0xFFD7E3EA)
                        )
                    }
                }

                var command by remember { mutableStateOf("") }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        modifier = Modifier.weight(1f),
                        value = command,
                        onValueChange = { command = it },
                        singleLine = true,
                        enabled = isRunning && status == LocalServerStatus.RUNNING,
                        placeholder = { Text(stringResource(R.string.local_server_command_hint)) }
                    )
                    OutlinedButton(
                        enabled = isRunning && status == LocalServerStatus.RUNNING && command.isNotBlank(),
                        onClick = {
                            val c = command.trim().removePrefix("/")
                            command = ""
                            scope.launch {
                                LocalServerManager.sendCommand(c).onFailure {
                                    Toast.makeText(context, R.string.local_server_command_failed, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        border = BorderStroke(1.dp, NevRed.Outline)
                    ) { Text(stringResource(R.string.local_server_send)) }
                }
            }
        }
    }

    if (showCreate) {
        CreateServerDialog(
            onDismiss = { showCreate = false },
            onCreated = { cfg ->
                showCreate = false
                selectedId = cfg.id
            }
        )
    }

    confirmDelete?.let { cfg ->
        AlertDialog(
            onDismissRequest = { confirmDelete = null },
            title = { Text(stringResource(R.string.local_server_delete_title, cfg.name)) },
            text = { Text(stringResource(R.string.local_server_delete_message)) },
            confirmButton = {
                TextButton(onClick = {
                    LocalServerManager.delete(cfg.id)
                    if (selectedId == cfg.id) selectedId = null
                    confirmDelete = null
                }) { Text(stringResource(R.string.generic_delete)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = null }) { Text(stringResource(R.string.generic_cancel)) }
            }
        )
    }
}

@Composable
private fun statusText(status: LocalServerStatus): String = stringResource(
    when (status) {
        LocalServerStatus.STOPPED -> R.string.local_server_status_stopped
        LocalServerStatus.STARTING -> R.string.local_server_status_starting
        LocalServerStatus.RUNNING -> R.string.local_server_status_running
        LocalServerStatus.STOPPING -> R.string.local_server_status_stopping
    }
)

@Composable
private fun CreateServerDialog(
    onDismiss: () -> Unit,
    onCreated: (LocalServerConfig) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var versions by remember { mutableStateOf<List<String>>(emptyList()) }
    var loadFailed by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("Server Nev") }
    var version by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var port by remember { mutableStateOf("25565") }
    var online by remember { mutableStateOf(false) }
    var eula by remember { mutableStateOf(false) }
    var working by remember { mutableStateOf(false) }

    //batas RAM: sampai 60% dari RAM perangkat, minimal 512 MB
    val maxRam = remember {
        val mi = ActivityManager.MemoryInfo()
        (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(mi)
        ((mi.totalMem / 1024 / 1024 * 0.6).toInt() / 256 * 256).coerceIn(1024, 8192)
    }
    var ram by remember { mutableStateOf(minOf(1536, maxRam).toFloat()) }

    LaunchedEffect(Unit) {
        runCatching { LocalServerManager.releaseVersions() }
            .onSuccess { versions = it; version = it.firstOrNull() }
            .onFailure { loadFailed = true }
    }

    val portValue = port.toIntOrNull()
    val portOk = portValue != null && portValue in 1024..65500

    AlertDialog(
        onDismissRequest = { if (!working) onDismiss() },
        title = { Text(stringResource(R.string.local_server_create)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name, onValueChange = { name = it.take(32) },
                    label = { Text(stringResource(R.string.local_server_name)) },
                    singleLine = true, enabled = !working
                )
                Box {
                    OutlinedButton(onClick = { expanded = true }, enabled = !working && versions.isNotEmpty()) {
                        Text(
                            when {
                                loadFailed -> stringResource(R.string.local_server_versions_failed)
                                version == null -> stringResource(R.string.local_server_loading)
                                else -> "Minecraft $version"
                            }
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        versions.take(60).forEach { v ->
                            DropdownMenuItem(text = { Text(v) }, onClick = { version = v; expanded = false })
                        }
                    }
                }
                Text(stringResource(R.string.local_server_ram, ram.toInt()), style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = ram, onValueChange = { ram = it },
                    valueRange = 512f..maxRam.toFloat(), enabled = !working
                )
                OutlinedTextField(
                    value = port, onValueChange = { port = it.filter(Char::isDigit).take(5) },
                    label = { Text(stringResource(R.string.local_server_port)) },
                    singleLine = true, enabled = !working, isError = !portOk
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(R.string.local_server_online_mode))
                        Text(
                            stringResource(R.string.local_server_online_mode_desc),
                            style = MaterialTheme.typography.labelSmall,
                            color = NevRed.OnPanel.copy(alpha = 0.6f)
                        )
                    }
                    Switch(checked = online, onCheckedChange = { online = it }, enabled = !working)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = eula, onCheckedChange = { eula = it }, enabled = !working)
                    Text(stringResource(R.string.local_server_eula), style = MaterialTheme.typography.bodySmall)
                }
                if (working) {
                    Text(stringResource(R.string.local_server_creating), style = MaterialTheme.typography.labelMedium)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !working && eula && version != null && portOk && name.isNotBlank(),
                onClick = {
                    working = true
                    scope.launch {
                        runCatching {
                            LocalServerManager.create(
                                name = name.trim(),
                                mcVersion = version!!,
                                ramMb = ram.toInt(),
                                port = portValue!!,
                                onlineMode = online,
                                onProgress = {}
                            )
                        }.onSuccess(onCreated).onFailure {
                            working = false
                            Toast.makeText(
                                context,
                                context.getString(R.string.local_server_create_failed, it.message ?: "error"),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            ) { Text(stringResource(R.string.local_server_create)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !working) { Text(stringResource(R.string.generic_cancel)) }
        }
    )
}
