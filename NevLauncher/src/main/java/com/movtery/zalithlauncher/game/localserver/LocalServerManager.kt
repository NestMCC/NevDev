package com.movtery.zalithlauncher.game.localserver

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.annotation.Keep
import androidx.core.content.ContextCompat
import com.google.gson.JsonParser
import com.movtery.zalithlauncher.game.multirt.RuntimesManager
import com.movtery.zalithlauncher.game.versioninfo.MinecraftVersions
import com.movtery.zalithlauncher.path.PathManager
import com.movtery.zalithlauncher.utils.GSON
import com.movtery.zalithlauncher.utils.logging.Logger
import com.movtery.zalithlauncher.utils.network.downloadFile
import com.movtery.zalithlauncher.utils.network.fetchStringFromUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.RandomAccessFile
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.UUID

private const val TAG = "LocalServer"

const val ACTION_LOCAL_SERVER_EXIT = "local.server.exit"
const val EXTRA_EXIT_CODE = "exit.code"
const val ACTION_GEYSER_EXIT = "geyser.exit"
const val BEDROCK_PORT = 19132
private const val GEYSER_URL = "https://download.geysermc.org/v2/projects/geyser/versions/latest/builds/latest/downloads/standalone"
const val SERVER_PROCESS_SUFFIX = ":server"
private const val MAX_CONSOLE_LINES = 400

/** Konfigurasi satu server lokal (disimpan sebagai config.json di folder servernya) */
@Keep
data class LocalServerConfig(
    val id: String,
    val name: String,
    val mcVersion: String,
    val javaMajor: Int,
    val ramMb: Int,
    val port: Int,
    val onlineMode: Boolean,
    val rconPort: Int,
    val rconPassword: String,
    /** Izinkan pemain Bedrock lewat Geyser (UDP 19132) */
    val bedrock: Boolean = false
)

enum class LocalServerStatus { STOPPED, STARTING, RUNNING, STOPPING }

/**
 * Mengelola server Minecraft yang berjalan di perangkat pengguna sendiri.
 * Mesinnya memakai JVM bawaan launcher (proses terpisah `:server`), tanpa grafis.
 */
object LocalServerManager {
    private val scope = CoroutineScope(Dispatchers.IO)
    private var registered = false
    private var appContext: Context? = null
    private var tailJob: Job? = null

    private val _servers = MutableStateFlow<List<LocalServerConfig>>(emptyList())
    val servers: StateFlow<List<LocalServerConfig>> = _servers.asStateFlow()

    private val _status = MutableStateFlow(LocalServerStatus.STOPPED)
    val status: StateFlow<LocalServerStatus> = _status.asStateFlow()

    /** id server yang sedang berjalan */
    private val _runningId = MutableStateFlow<String?>(null)
    val runningId: StateFlow<String?> = _runningId.asStateFlow()

    private val _console = MutableStateFlow<List<String>>(emptyList())
    val console: StateFlow<List<String>> = _console.asStateFlow()

    val rootDir: File get() = File(PathManager.DIR_FILES_EXTERNAL, "local_servers")
    fun serverDir(id: String) = File(rootDir, id)
    fun consoleLog(id: String) = File(serverDir(id), "nev-console.log")

    fun init(context: Context) {
        val app = context.applicationContext
        appContext = app
        if (!registered) {
            registered = true
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context?, intent: Intent?) {
                    Logger.info(TAG, "Server process exit: ${intent?.getIntExtra(EXTRA_EXIT_CODE, -1)}")
                    onProcessExit()
                }
            }
            ContextCompat.registerReceiver(
                app,
                receiver,
                IntentFilter(app.packageName + "." + ACTION_LOCAL_SERVER_EXIT),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
        reload()
        syncWithProcess(app)
    }

    /** Jika proses :server masih hidup (mis. UI dibuka ulang), tandai sebagai berjalan */
    private fun syncWithProcess(context: Context) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val alive = am.runningAppProcesses?.any { it.processName == context.packageName + SERVER_PROCESS_SUFFIX } == true
        if (alive && _status.value == LocalServerStatus.STOPPED) {
            val id = _servers.value.firstOrNull { consoleLog(it.id).exists() }?.id
            if (id != null) {
                _runningId.value = id
                _status.value = LocalServerStatus.RUNNING
                startTail(id, fromStart = true)
            }
        }
    }

    fun reload() {
        val list = rootDir.listFiles()?.mapNotNull { dir ->
            runCatching {
                GSON.fromJson(File(dir, "config.json").readText(), LocalServerConfig::class.java)
            }.getOrNull()
        }?.sortedBy { it.name.lowercase() } ?: emptyList()
        _servers.value = list
    }

    /** Alamat IPv4 perangkat di jaringan (Wi-Fi/hotspot) agar bisa dihubungi dari perangkat lain */
    fun localAddresses(): List<String> = runCatching {
        NetworkInterface.getNetworkInterfaces().toList()
            .filter { it.isUp && !it.isLoopback }
            .flatMap { it.inetAddresses.toList() }
            .filterIsInstance<Inet4Address>()
            .mapNotNull { it.hostAddress }
    }.getOrDefault(emptyList())

    /** Daftar versi rilis Minecraft yang bisa dibuat servernya */
    suspend fun releaseVersions(): List<String> = withContext(Dispatchers.IO) {
        MinecraftVersions.getVersionManifest().versions
            .filter { it.type == "release" }
            .map { it.id }
    }

    /**
     * Membuat server baru: unduh server.jar resmi Mojang, tulis eula + server.properties.
     * @param onProgress pesan kemajuan singkat
     */
    suspend fun create(
        name: String,
        mcVersion: String,
        ramMb: Int,
        port: Int,
        onlineMode: Boolean,
        bedrock: Boolean = false,
        onProgress: (String) -> Unit
    ): LocalServerConfig = withContext(Dispatchers.IO) {
        val entry = MinecraftVersions.getVersionManifest().versions.firstOrNull { it.id == mcVersion }
            ?: error("Versi $mcVersion tidak ditemukan")

        onProgress("manifest")
        val json = JsonParser.parseString(fetchStringFromUrl(entry.url)).asJsonObject
        val serverDl = json.getAsJsonObject("downloads")?.getAsJsonObject("server")
            ?: error("Versi $mcVersion tidak punya server resmi")
        val javaMajor = json.getAsJsonObject("javaVersion")?.get("majorVersion")?.asInt ?: 8

        val id = name.lowercase().replace(Regex("[^a-z0-9]+"), "-").trim('-').take(24)
            .ifEmpty { "server" } + "-" + UUID.randomUUID().toString().take(6)
        val dir = serverDir(id).also { it.mkdirs() }

        runCatching {
            onProgress("jar")
            downloadFile(
                url = serverDl.get("url").asString,
                outputFile = File(dir, "server.jar"),
                sha1 = serverDl.get("sha1")?.asString
            )

            val cfg = LocalServerConfig(
                id = id,
                name = name,
                mcVersion = mcVersion,
                javaMajor = javaMajor,
                ramMb = ramMb,
                port = port,
                onlineMode = onlineMode,
                rconPort = port + 10,
                rconPassword = UUID.randomUUID().toString().replace("-", "").take(16),
                bedrock = bedrock
            )
            File(dir, "eula.txt").writeText("eula=true\n")
            File(dir, "server.properties").writeText(buildProperties(cfg))
            File(dir, "config.json").writeText(GSON.toJson(cfg))
            cfg
        }.onFailure {
            dir.deleteRecursively()
        }.getOrThrow()
    }.also { reload() }

    private fun buildProperties(cfg: LocalServerConfig) = """
        server-port=${cfg.port}
        online-mode=${cfg.onlineMode}
        enable-rcon=true
        rcon.port=${cfg.rconPort}
        rcon.password=${cfg.rconPassword}
        motd=${cfg.name} - Nev Launcher
        view-distance=6
        simulation-distance=5
    """.trimIndent() + "\n"

    /** Ubah izin Bedrock server yang sudah ada (hanya saat server berhenti) */
    fun setBedrock(cfg: LocalServerConfig, enabled: Boolean) {
        if (_status.value != LocalServerStatus.STOPPED) return
        val dir = serverDir(cfg.id)
        runCatching { File(dir, "config.json").writeText(GSON.toJson(cfg.copy(bedrock = enabled))) }
        reload()
    }

    private fun geyserDir(id: String) = File(serverDir(id), "geyser")

    /** Unduh Geyser Standalone (sekali) dan tulis konfigurasinya. @return false bila tidak ada Java 17+ */
    suspend fun prepareBedrock(cfg: LocalServerConfig): Boolean = withContext(Dispatchers.IO) {
        if (pickJre(17) == null) return@withContext false
        val gd = geyserDir(cfg.id).also { it.mkdirs() }
        val jar = File(gd, "Geyser-Standalone.jar")
        if (!jar.exists() || jar.length() < 1_000_000) {
            downloadFile(url = GEYSER_URL, outputFile = jar)
        }
        File(gd, "config.yml").writeText(
            """
            bedrock:
              address: 0.0.0.0
              port: $BEDROCK_PORT
              clone-remote-port: false
              motd1: "${cfg.name.replace('"', '\'')}"
              motd2: "Nev Launcher"
              server-name: "${cfg.name.replace('"', '\'')}"
            remote:
              address: 127.0.0.1
              port: ${cfg.port}
              auth-type: ${if (cfg.onlineMode) "online" else "offline"}
            passthrough-motd: false
            passthrough-player-counts: false
            """.trimIndent() + "\n"
        )
        true
    }

    private fun startGeyser(cfg: LocalServerConfig) {
        val ctx = appContext ?: return
        val jre = pickJre(17) ?: return
        val gd = geyserDir(cfg.id)
        if (!File(gd, "Geyser-Standalone.jar").exists()) return
        runCatching { File(gd, "nev-geyser.log").delete() }
        val intent = Intent(ctx, GeyserService::class.java).apply {
            putExtra(SERVICE_SERVER_DIR, gd.absolutePath)
            putExtra(SERVICE_SERVER_JRE, jre)
            putExtra(SERVICE_SERVER_RAM, 384)
            putExtra(SERVICE_SERVER_NAME, "Geyser (Bedrock)")
            putExtra(SERVICE_SERVER_JAR, "Geyser-Standalone.jar")
            putExtra(SERVICE_SERVER_ARGS, "--nogui")
            putExtra(SERVICE_SERVER_LOG, "nev-geyser.log")
            putExtra(SERVICE_EXIT_ACTION, ACTION_GEYSER_EXIT)
        }
        runCatching { ctx.startForegroundService(intent) }
        _console.update { (it + "[Nev] Bedrock aktif di UDP $BEDROCK_PORT (Geyser)").takeLast(MAX_CONSOLE_LINES) }
    }

    private fun stopGeyser() {
        val ctx = appContext ?: return
        runCatching { ctx.stopService(Intent(ctx, GeyserService::class.java)) }
    }

    /** Nama JRE terkecil yang memenuhi syarat Java versi server */
    fun pickJre(javaMajor: Int): String? =
        RuntimesManager.getRuntimes()
            .filter { it.javaVersion >= javaMajor }
            .minByOrNull { it.javaVersion }?.name

    /** @return pesan error bila gagal mulai, null bila berhasil dimulai */
    fun start(context: Context, cfg: LocalServerConfig): String? {
        if (_status.value != LocalServerStatus.STOPPED) return "busy"
        val jre = pickJre(cfg.javaMajor) ?: return "java:${cfg.javaMajor}"
        val dir = serverDir(cfg.id)
        if (!File(dir, "server.jar").exists()) return "nojar"

        val log = consoleLog(cfg.id)
        runCatching { log.delete() }
        _console.value = emptyList()
        _runningId.value = cfg.id
        _status.value = LocalServerStatus.STARTING

        val intent = Intent(context, LocalServerService::class.java).apply {
            putExtra(SERVICE_SERVER_DIR, dir.absolutePath)
            putExtra(SERVICE_SERVER_JRE, jre)
            putExtra(SERVICE_SERVER_RAM, cfg.ramMb)
            putExtra(SERVICE_SERVER_NAME, cfg.name)
        }
        return runCatching {
            context.startForegroundService(intent)
            startTail(cfg.id, fromStart = true)
            null
        }.getOrElse {
            onProcessExit()
            it.message ?: "error"
        }
    }

    /** Hentikan dengan perintah `stop` lewat RCON agar dunia tersimpan rapi */
    suspend fun stop(context: Context) {
        val cfg = _servers.value.firstOrNull { it.id == _runningId.value } ?: return
        if (_status.value == LocalServerStatus.STOPPED) return
        _status.value = LocalServerStatus.STOPPING
        stopGeyser()
        val ok = runCatching { RconClient.exec(cfg.rconPort, cfg.rconPassword, "stop") }.isSuccess
        if (!ok) {
            //RCON belum siap: hentikan paksa layanan
            context.stopService(Intent(context, LocalServerService::class.java))
            delay(500)
            onProcessExit()
        }
    }

    suspend fun sendCommand(command: String): Result<String> {
        val cfg = _servers.value.firstOrNull { it.id == _runningId.value }
            ?: return Result.failure(IllegalStateException("not running"))
        _console.update { (it + "> $command").takeLast(MAX_CONSOLE_LINES) }
        return runCatching { RconClient.exec(cfg.rconPort, cfg.rconPassword, command) }
            .onSuccess { reply ->
                if (reply.isNotBlank()) _console.update { (it + reply.trim().lines()).takeLast(MAX_CONSOLE_LINES) }
            }
    }

    fun delete(id: String) {
        if (_runningId.value == id && _status.value != LocalServerStatus.STOPPED) return
        serverDir(id).deleteRecursively()
        reload()
    }

    private fun onProcessExit() {
        tailJob?.cancel()
        stopGeyser()
        _status.value = LocalServerStatus.STOPPED
        _runningId.value = null
    }

    private fun startTail(id: String, fromStart: Boolean) {
        tailJob?.cancel()
        tailJob = scope.launch {
            val file = consoleLog(id)
            var offset = if (fromStart) 0L else (file.takeIf { it.exists() }?.length() ?: 0L)
            var remainder = ByteArray(0)
            while (isActive) {
                delay(300)
                if (!file.exists()) continue
                val length = file.length()
                if (length < offset) { offset = 0; remainder = ByteArray(0) }
                if (length <= offset) continue
                val bytes = runCatching {
                    RandomAccessFile(file, "r").use { raf ->
                        raf.seek(offset)
                        ByteArray((length - offset).toInt()).also { raf.readFully(it) }
                    }
                }.getOrNull() ?: continue
                offset = length

                val all = remainder + bytes
                val last = all.lastIndexOf('\n'.code.toByte())
                if (last < 0) { remainder = all; continue }
                val text = String(all, 0, last + 1, Charsets.UTF_8)
                remainder = all.copyOfRange(last + 1, all.size)
                val lines = text.split('\n').map { it.removeSuffix("\r") }.filter { it.isNotEmpty() }
                if (lines.isEmpty()) continue
                _console.update { (it + lines).takeLast(MAX_CONSOLE_LINES) }
                if (_status.value == LocalServerStatus.STARTING && lines.any { it.contains("Done (") }) {
                    _status.value = LocalServerStatus.RUNNING
                    _servers.value.firstOrNull { it.id == id }?.takeIf { it.bedrock }?.let { startGeyser(it) }
                }
            }
        }
    }
}
