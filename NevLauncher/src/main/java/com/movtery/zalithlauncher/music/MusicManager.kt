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

package com.movtery.zalithlauncher.music

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.movtery.zalithlauncher.game.path.getAssetsHome
import com.movtery.zalithlauncher.path.PathManager
import com.movtery.zalithlauncher.setting.AllSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File

/** Satu lagu di daftar putar. [id] stabil: "mc:<nama>" atau "user:<namaFile>". */
data class MusicTrack(
    val id: String,
    val title: String,
    val path: String,
    val fromMinecraft: Boolean
)

/**
 * Pemutar musik Nev Launcher. Satu instance per proses
 * (peluncur dan game ":game" masing-masing punya sendiri; pengaturan dibagi lewat MMKV).
 * Semua fungsi publik dipanggil dari thread utama.
 */
object MusicManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _tracks = MutableStateFlow<List<MusicTrack>>(emptyList())
    val tracks: StateFlow<List<MusicTrack>> = _tracks

    private val _current = MutableStateFlow<MusicTrack?>(null)
    val current: StateFlow<MusicTrack?> = _current

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private var player: MediaPlayer? = null
    private var prepared = false
    /** Aplikasi sedang di depan (di proses ini) */
    private var foreground = false
    /** Pengguna menekan jeda sendiri */
    private var userPaused = false
    private var errorStreak = 0
    private var scanned = false

    /** Folder musik milik pengguna; taruh file .ogg/.mp3/.wav/.m4a/.flac di sini. */
    fun userMusicDir(): File = File(PathManager.DIR_FILES_EXTERNAL, "music").also { it.mkdirs() }

    private val audioExt = setOf("ogg", "mp3", "wav", "m4a", "flac", "aac")

    /** Pindai ulang daftar lagu (Minecraft + folder pengguna). */
    fun refreshTracks(onDone: () -> Unit = {}) {
        scope.launch {
            val list = withContext(Dispatchers.IO) { scanTracks() }
            _tracks.value = list
            scanned = true
            val savedId = AllSettings.musicTrackId.getValue()
            val keep = _current.value?.let { cur -> list.firstOrNull { it.id == cur.id } }
            _current.value = keep ?: list.firstOrNull { it.id == savedId } ?: list.firstOrNull()
            onDone()
        }
    }

    private fun scanTracks(): List<MusicTrack> {
        val result = ArrayList<MusicTrack>()
        //1) folder pengguna
        runCatching {
            userMusicDir().listFiles { f -> f.isFile && f.extension.lowercase() in audioExt }
                ?.sortedBy { it.name.lowercase() }
                ?.forEach { f ->
                    result += MusicTrack("user:${f.name}", prettify(f.nameWithoutExtension), f.absolutePath, false)
                }
        }
        //2) musik bawaan Minecraft dari aset yang sudah diunduh game (tidak disertakan di APK)
        runCatching {
            val assets = File(getAssetsHome())
            val indexes = File(assets, "indexes").listFiles { f -> f.extension == "json" }
                ?.sortedByDescending { it.lastModified() } ?: emptyList()
            val seen = HashSet<String>()
            val mc = ArrayList<MusicTrack>()
            for (index in indexes) {
                val objects = JSONObject(index.readText()).optJSONObject("objects") ?: continue
                val keys = objects.keys()
                while (keys.hasNext()) {
                    val name = keys.next()
                    if (!name.startsWith("minecraft/sounds/music/") || !name.endsWith(".ogg")) continue
                    if (!seen.add(name)) continue
                    val hash = objects.getJSONObject(name).optString("hash")
                    if (hash.length < 2) continue
                    val file = File(assets, "objects/${hash.substring(0, 2)}/$hash")
                    if (!file.isFile) continue
                    val stem = name.removePrefix("minecraft/sounds/music/").removeSuffix(".ogg")
                    mc += MusicTrack("mc:$stem", prettify(stem.substringAfterLast('/')), file.absolutePath, true)
                }
            }
            //lagu menu dulu (tema), lalu sisanya
            result += mc.sortedWith(compareBy({ !it.id.startsWith("mc:menu") }, { it.title }))
        }
        return result
    }

    private fun prettify(raw: String): String {
        val spaced = raw.replace('_', ' ').replace('-', ' ')
            .replace(Regex("(?<=[a-zA-Z])(?=\\d)"), " ").trim()
        return spaced.split(' ').filter { it.isNotEmpty() }
            .joinToString(" ") { w -> w.replaceFirstChar { it.uppercase() } }
    }

    // ---------- Siklus hidup ----------

    /** Panggil saat layar peluncur / game tampil. [inGame] true dari proses game. */
    fun onForeground(inGame: Boolean) {
        foreground = true
        if (!AllSettings.musicEnabled.getValue()) return
        if (inGame && !AllSettings.musicInGame.getValue()) return
        if (!scanned) {
            refreshTracks { resume() }
        } else {
            resume()
        }
    }

    /** Panggil saat layar tidak lagi tampil. */
    fun onBackground() {
        foreground = false
        pausePlayer()
    }

    /** Dipanggil UI saat saklar musik berubah. */
    fun onEnabledChanged(enabled: Boolean) {
        if (enabled) {
            userPaused = false
            if (foreground) onForeground(inGame = false)
        } else {
            stop()
        }
    }

    // ---------- Kontrol ----------

    fun togglePlayPause() {
        if (_isPlaying.value) {
            userPaused = true
            pausePlayer()
        } else {
            userPaused = false
            resume()
        }
    }

    fun next() = step(+1)

    fun previous() = step(-1)

    fun select(track: MusicTrack) {
        userPaused = false
        play(track)
    }

    fun setVolume(percent: Int) {
        applyVolume(player, percent)
    }

    private fun step(delta: Int) {
        val list = _tracks.value
        if (list.isEmpty()) return
        val idx = list.indexOfFirst { it.id == _current.value?.id }
        val target = list[((if (idx < 0) 0 else idx) + delta).mod(list.size)]
        userPaused = false
        play(target)
    }

    private fun resume() {
        if (userPaused || !foreground) return
        val mp = player
        if (mp != null && prepared) {
            if (!mp.isPlaying) {
                mp.start()
                _isPlaying.value = true
            }
            return
        }
        if (mp != null) return //sedang menyiapkan
        val track = _current.value ?: _tracks.value.firstOrNull() ?: return
        play(track)
    }

    private fun play(track: MusicTrack) {
        releasePlayer()
        _current.value = track
        runCatching { AllSettings.musicTrackId.save(track.id) }
        if (!foreground) return
        try {
            val mp = MediaPlayer()
            mp.setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            mp.setDataSource(track.path)
            mp.setOnPreparedListener {
                prepared = true
                errorStreak = 0
                applyVolume(it, AllSettings.musicVolume.getValue())
                if (foreground && !userPaused) {
                    it.start()
                    _isPlaying.value = true
                }
            }
            mp.setOnCompletionListener { step(+1) }
            mp.setOnErrorListener { _, _, _ ->
                errorStreak++
                releasePlayer()
                //lompati file rusak, tapi jangan berputar tanpa henti
                if (errorStreak < _tracks.value.size.coerceAtLeast(1)) step(+1)
                true
            }
            player = mp
            mp.prepareAsync()
        } catch (_: Exception) {
            releasePlayer()
        }
    }

    private fun pausePlayer() {
        val mp = player
        if (mp != null && prepared && mp.isPlaying) mp.pause()
        _isPlaying.value = false
    }

    private fun stop() {
        releasePlayer()
    }

    private fun releasePlayer() {
        player?.let {
            runCatching { it.setOnCompletionListener(null) }
            runCatching { it.setOnErrorListener(null) }
            runCatching { it.release() }
        }
        player = null
        prepared = false
        _isPlaying.value = false
    }

    private fun applyVolume(mp: MediaPlayer?, percent: Int = AllSettings.musicVolume.getValue()) {
        val v = (percent.coerceIn(0, 100) / 100f).let { it * it } //kurva kuadrat terasa lebih alami
        runCatching { mp?.setVolume(v, v) }
    }
}
