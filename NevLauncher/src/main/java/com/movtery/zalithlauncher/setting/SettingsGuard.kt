package com.movtery.zalithlauncher.setting

import android.app.Application
import android.content.Context
import android.os.Build
import com.movtery.zalithlauncher.BuildKeys
import java.io.File

/**
 * Menjaga pengaturan pengguna agar tidak hilang/ter-reset saat update:
 * 1. Saat versi aplikasi berubah, salinan file pengaturan (MMKV) dibuat lebih dulu (cadangan "sebelum update").
 * 2. Jika file pengaturan ternyata kosong/hilang sementara cadangan ada, cadangan dipulihkan
 *    sebelum pengaturan dibaca.
 * Dipanggil sekali di proses utama, setelah MMKV.initialize dan sebelum loadAllSettings.
 */
object SettingsGuard {
    private const val PREFS = "nev_settings_guard"
    private const val KEY_LAST_VERSION = "last_version_code"
    private const val BACKUP_DIR = "settings_backup"
    private const val KEEP_BACKUPS = 3

    fun run(app: Application, mmkvRoot: String) {
        runCatching {
            if (Build.VERSION.SDK_INT >= 28 && Application.getProcessName() != app.packageName) return
            val id = BuildKeys.LAUNCHER_IDENTIFIER
            val dataFile = File(mmkvRoot, id)
            val crcFile = File(mmkvRoot, "$id.crc")
            val backupRoot = File(app.filesDir, BACKUP_DIR)

            //1. pulihkan bila pengaturan kosong tetapi cadangan ada
            if (!dataFile.exists() || dataFile.length() <= 4L) {
                val latest = backupRoot.listFiles { f -> f.isDirectory }?.maxByOrNull { it.name }
                val bData = latest?.let { File(it, id) }
                if (bData != null && bData.exists() && bData.length() > 4L) {
                    bData.copyTo(dataFile, overwrite = true)
                    File(latest, "$id.crc").takeIf { it.exists() }?.copyTo(crcFile, overwrite = true)
                }
            }

            //2. cadangkan saat versi aplikasi berubah (update)
            val prefs = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val current = currentVersionCode(app)
            val last = prefs.getLong(KEY_LAST_VERSION, -1L)
            if (last != current) {
                if (dataFile.exists() && dataFile.length() > 4L) {
                    val dir = File(backupRoot, "%020d_v%d".format(System.currentTimeMillis(), last))
                    dir.mkdirs()
                    dataFile.copyTo(File(dir, id), overwrite = true)
                    if (crcFile.exists()) crcFile.copyTo(File(dir, "$id.crc"), overwrite = true)
                    //sisakan beberapa cadangan terbaru saja
                    backupRoot.listFiles { f -> f.isDirectory }
                        ?.sortedBy { it.name }
                        ?.dropLast(KEEP_BACKUPS)
                        ?.forEach { it.deleteRecursively() }
                }
                prefs.edit().putLong(KEY_LAST_VERSION, current).apply()
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun currentVersionCode(app: Application): Long = runCatching {
        val info = app.packageManager.getPackageInfo(app.packageName, 0)
        if (Build.VERSION.SDK_INT >= 28) info.longVersionCode else info.versionCode.toLong()
    }.getOrDefault(0L)
}
