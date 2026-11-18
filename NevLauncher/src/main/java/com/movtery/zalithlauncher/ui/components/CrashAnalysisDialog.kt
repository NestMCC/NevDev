package com.movtery.zalithlauncher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.crash.AiCrashClient
import com.movtery.zalithlauncher.game.crash.CrashAnalyzer
import com.movtery.zalithlauncher.game.crash.CrashFinding
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.setting.enums.AiProvider
import com.movtery.zalithlauncher.ui.theme.OutlinedButton
import com.movtery.zalithlauncher.ui.theme.TextButton
import com.movtery.zalithlauncher.ui.theme.boxyBorder
import com.movtery.zalithlauncher.ui.theme.RoundedCornerShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.RandomAccessFile

private const val MAX_READ_BYTES = 600_000L

private fun readLogTail(file: File): String {
    if (!file.exists() || !file.isFile) return ""
    RandomAccessFile(file, "r").use { raf ->
        val len = raf.length()
        val start = (len - MAX_READ_BYTES).coerceAtLeast(0)
        raf.seek(start)
        val bytes = ByteArray((len - start).toInt())
        raf.readFully(bytes)
        return String(bytes, Charsets.UTF_8)
    }
}

/**
 * Dialog analisis crash: analisis cepat offline + tombol opsional "Analisis dengan AI".
 * Log hanya dikirim ke penyedia AI saat tombol AI ditekan.
 */
@Composable
fun CrashAnalysisDialog(
    logFile: File,
    context: String = "",
    onDismiss: () -> Unit
) {
    var logText by remember { mutableStateOf<String?>(null) }
    var findings by remember { mutableStateOf<List<CrashFinding>>(emptyList()) }
    var aiLoading by remember { mutableStateOf(false) }
    var aiResult by remember { mutableStateOf<String?>(null) }
    var aiError by remember { mutableStateOf<String?>(null) }
    var showAiSetup by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(logFile) {
        val text = withContext(Dispatchers.IO) { runCatching { readLogTail(logFile) }.getOrDefault("") }
        logText = text
        findings = withContext(Dispatchers.Default) { CrashAnalyzer.analyze(text) }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier,
        title = { Text(stringResource(R.string.crash_ai_dialog_title)) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 460.dp)
                    .verticalScrollWithBar(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (logText == null) {
                    Text(stringResource(R.string.crash_ai_reading))
                } else if (logText.isNullOrBlank()) {
                    Text(stringResource(R.string.crash_ai_no_log))
                } else {
                    Text(
                        text = stringResource(R.string.crash_ai_local_header),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    findings.forEach { f ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(stringResource(f.titleRes), style = MaterialTheme.typography.titleSmall)
                            Text(
                                stringResource(f.detailRes, f.arg ?: ""),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (!f.evidence.isNullOrBlank()) {
                                Text(
                                    f.evidence,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.crash_ai_ai_header),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        stringResource(R.string.crash_ai_privacy),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (showAiSetup || !AiCrashClient.isConfigured()) {
                        AiSetup()
                    }

                    if (aiLoading) Text(stringResource(R.string.crash_ai_loading))
                    aiError?.let {
                        Text(
                            stringResource(R.string.crash_ai_error, it),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    aiResult?.let {
                        SelectionContainer { Text(it, style = MaterialTheme.typography.bodyMedium) }
                    }
                }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!logText.isNullOrBlank()) {
                    OutlinedButton(
                        enabled = !aiLoading,
                        onClick = {
                            if (!AiCrashClient.isConfigured()) {
                                showAiSetup = true
                                return@OutlinedButton
                            }
                            aiLoading = true; aiError = null; aiResult = null
                            val text = logText.orEmpty()
                            scope.launch {
                                runCatching {
                                    val excerpt = withContext(Dispatchers.Default) { CrashAnalyzer.excerptForAi(text) }
                                    AiCrashClient.analyze(excerpt, context)
                                }.onSuccess { aiResult = it }
                                    .onFailure { aiError = it.message ?: it.javaClass.simpleName }
                                aiLoading = false
                            }
                        }
                    ) { Text(stringResource(R.string.crash_ai_analyze_button)) }
                }
                TextButton(onClick = onDismiss) { Text(stringResource(R.string.generic_close)) }
            }
        },
        dismissButton = {
            if (AiCrashClient.isConfigured() && !showAiSetup) {
                TextButton(onClick = { showAiSetup = true }) { Text(stringResource(R.string.crash_ai_settings)) }
            }
        }
    )
}

@Composable
private fun AiSetup() {
    val provider = AllSettings.aiCrashProvider.state
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            AiProvider.entries.forEach { p ->
                val selected = p == provider
                (if (selected) com.movtery.zalithlauncher.ui.theme.Button(
                    onClick = { AllSettings.aiCrashProvider.save(p) },
                    content = { Text(p.label, style = MaterialTheme.typography.labelMedium) }
                ) else OutlinedButton(
                    onClick = { AllSettings.aiCrashProvider.save(p) },
                    content = { Text(p.label, style = MaterialTheme.typography.labelMedium) }
                ))
            }
        }
        var key by remember { mutableStateOf(AllSettings.aiCrashApiKey.state) }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = key,
            onValueChange = { key = it; AllSettings.aiCrashApiKey.save(it.trim()) },
            label = { Text(stringResource(R.string.crash_ai_key_label)) },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        var model by remember { mutableStateOf(AllSettings.aiCrashModel.state) }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = model,
            onValueChange = { model = it; AllSettings.aiCrashModel.save(it.trim()) },
            label = { Text(stringResource(R.string.crash_ai_model_label)) },
            placeholder = { Text(provider.defaultModel) },
            singleLine = true
        )
        var url by remember { mutableStateOf(AllSettings.aiCrashBaseUrl.state) }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = url,
            onValueChange = { url = it; AllSettings.aiCrashBaseUrl.save(it.trim()) },
            label = { Text(stringResource(R.string.crash_ai_url_label)) },
            placeholder = { Text(provider.defaultBaseUrl) },
            singleLine = true
        )
    }
}
