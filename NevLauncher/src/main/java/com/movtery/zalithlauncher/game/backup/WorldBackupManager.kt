package com.movtery.zalithlauncher.game.backup

import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionFolders
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.utils.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.RandomAccessFile
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

private const val TAG = "WorldBackup"

/** Jenis cadangan, ikut tertulis di nama berkas. */
enum class BackupKind(val tag: String) {
    EXIT("keluar"), AUTO("berkala"), MANUAL("manual");

    companion object {
        fun fromName(name: String): BackupKind =
            entries.find { name.contains("_${it.tag}") } ?: MANUAL
    }
}

data class WorldBackup(
    val worldName: String,
    val file: File,
    val time: Long,
    val size: Long,
    val kind: BackupKind
)

/**
 * Pencadangan world. Hasilnya disimpan di `<folder versi>/backup-world/<nama world>/`,
 * mis. `NevLauncher/.minecraft/versions/<instance>/backup-world/`.
 */
object WorldBackupManager {
    const val FOLDER_NAME = "backup-world"
    private val fmt get() = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US)

    fun backupRoot(version: Version): File = File(version.getVersionPath(), FOLDER_NAME)

    private fun savesDir(version: Version): File = VersionFolders.SAVES.getDir(version.getGameDir())

    /** Semua world (folder yang berisi level.dat). */
    fun listWorlds(version: Version): List<File> =
        savesDir(version).listFiles { f -> f.isDirectory && File(f, "level.dat").exists() }?.toList() ?: emptyList()

    /** World yang paling baru dimainkan. */
    fun latestWorld(version: Version): File? =
        listWorlds(version).maxByOrNull { File(it, "level.dat").lastModified() }

    fun listBackups(version: Version): List<WorldBackup> {
        val root = backupRoot(version)
        val dirs = root.listFiles { f -> f.isDirectory } ?: return emptyList()
        return dirs.flatMap { dir ->
            dir.listFiles { f -> f.isFile && f.extension == "zip" }?.map { f ->
                WorldBackup(dir.name, f, f.lastModified(), f.length(), BackupKind.fromName(f.name))
            } ?: emptyList()
        }.sortedByDescending { it.time }
    }

    /**
     * Cadangkan satu world. Mengembalikan berkas zip, atau null kalau dilewati/gagal.
     * @param skipIfUnchanged lewati bila level.dat tidak berubah sejak cadangan terakhir
     */
    suspend fun backupWorld(
        version: Version,
        world: File,
        kind: BackupKind,
        skipIfUnchanged: Boolean = false
    ): File? = withContext(Dispatchers.IO) {
        runCatching {
            if (!world.isDirectory) return@runCatching null
            val dir = File(backupRoot(version), world.name).apply { mkdirs() }
            if (skipIfUnchanged) {
                val last = dir.listFiles { f -> f.extension == "zip" }?.maxOfOrNull { it.lastModified() } ?: 0L
                val changed = File(world, "level.dat").lastModified()
                if (last >= changed) return@runCatching null
            }
            val out = File(dir, "${fmt.format(Date())}_${kind.tag}.zip")
            val tmp = File(dir, out.name + ".part")
            ZipOutputStream(tmp.outputStream().buffered()).use { zos ->
                world.walkTopDown().filter { it.isFile && it.name != "session.lock" }.forEach { f ->
                    val name = world.name + "/" + f.relativeTo(world).path.replace("\\", "/")
                    runCatching {
                        FileInputStream(f).use { input ->
                            zos.putNextEntry(ZipEntry(name))
                            input.copyTo(zos)
                            zos.closeEntry()
                        }
                    }.onFailure { e ->
                        //berkas sedang ditulis game: lewati saja
                        Logger.warning(TAG, "Skip ${f.name}: ${e.message}")
                        runCatching { zos.closeEntry() }
                    }
                }
            }
            if (!tmp.renameTo(out)) { tmp.delete(); return@runCatching null }
            prune(dir)
            Logger.info(TAG, "Backup created: ${out.absolutePath}")
            out
        }.onFailure { Logger.error(TAG, "Backup failed", it) }.getOrNull()
    }

    private fun prune(dir: File) {
        val max = AllSettings.worldBackupMax.getValue()
        if (max <= 0) return
        dir.listFiles { f -> f.extension == "zip" }
            ?.sortedByDescending { it.lastModified() }
            ?.drop(max)
            ?.forEach { it.delete() }
    }

    /** Cadangkan semua world sekarang. Mengembalikan jumlah yang berhasil. */
    suspend fun backupAll(version: Version, kind: BackupKind = BackupKind.MANUAL): Int =
        listWorlds(version).count { backupWorld(version, it, kind) != null }

    /**
     * Pulihkan cadangan ke saves. World yang sedang ada diganti nama dulu menjadi
     * `<nama>_sebelum-pulih` agar tidak hilang.
     */
    suspend fun restore(version: Version, backup: WorldBackup): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val saves = savesDir(version).apply { mkdirs() }
            val target = File(saves, backup.worldName)
            if (target.exists()) {
                var safe = File(saves, backup.worldName + "_sebelum-pulih")
                var i = 2
                while (safe.exists()) safe = File(saves, "${backup.worldName}_sebelum-pulih$i").also { i++ }
                if (!target.renameTo(safe)) return@runCatching false
            }
            ZipFile(backup.file).use { zip ->
                val base = saves.canonicalPath + File.separator
                zip.entries().asSequence().filter { !it.isDirectory }.forEach { e ->
                    val outFile = File(saves, e.name)
                    if (!outFile.canonicalPath.startsWith(base)) return@forEach //cegah zip-slip
                    outFile.parentFile?.mkdirs()
                    zip.getInputStream(e).use { input -> outFile.outputStream().use { input.copyTo(it) } }
                }
            }
            true
        }.onFailure { Logger.error(TAG, "Restore failed", it) }.getOrDefault(false)
    }

    fun delete(backup: WorldBackup) = backup.file.delete()

    // ===== Pemantau saat bermain (jalan di proses game) =====

    /**
     * Mulai memantau: cadangan saat keluar dari world (dibaca dari log game) dan berkala.
     */
    fun startWatching(scope: CoroutineScope, version: Version, logFile: File): Job = scope.launch(Dispatchers.IO) {
        launch { watchExit(version, logFile) }
        launch { periodic(version) }
    }

    private suspend fun periodic(version: Version) {
        while (currentCoroutineContext().isActive) {
            val minutes = AllSettings.worldBackupIntervalMin.getValue().coerceAtLeast(5)
            delay(minutes * 60_000L)
            if (!AllSettings.worldBackupPeriodic.getValue()) continue
            latestWorld(version)?.let { backupWorld(version, it, BackupKind.AUTO, skipIfUnchanged = true) }
        }
    }

    private suspend fun watchExit(version: Version, logFile: File) {
        var pos = if (logFile.exists()) logFile.length() else 0L
        while (currentCoroutineContext().isActive) {
            delay(2_000L)
            if (!AllSettings.worldBackupOnExit.getValue()) {
                pos = if (logFile.exists()) logFile.length() else pos
                continue
            }
            if (!logFile.exists()) continue
            val len = logFile.length()
            if (len < pos) pos = 0L //log dirotasi
            if (len == pos) continue
            val text = runCatching {
                RandomAccessFile(logFile, "r").use { raf ->
                    raf.seek(pos)
                    val buf = ByteArray((len - pos).coerceAtMost(512_000L).toInt())
                    raf.readFully(buf)
                    pos += buf.size
                    String(buf, Charsets.UTF_8)
                }
            }.getOrNull() ?: continue
            val left = text.contains("Stopping singleplayer server as player logged out") ||
                text.contains("Stopping server") ||
                text.contains("All dimensions are saved")
            if (left) {
                //beri waktu game selesai menyimpan world
                delay(6_000L)
                latestWorld(version)?.let { backupWorld(version, it, BackupKind.EXIT, skipIfUnchanged = true) }
            }
        }
    }
}
