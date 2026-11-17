package com.movtery.zalithlauncher.components

import android.content.Context
import com.movtery.zalithlauncher.components.jre.Jre
import com.movtery.zalithlauncher.components.jre.UnpackJnaTask
import com.movtery.zalithlauncher.components.jre.UnpackJreTask
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.utils.logging.Logger
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

private const val TAG = "ComponentInstaller"

/**
 * Pemasang komponen launcher (runtime Java, LWJGL, authlib-injector, dll.) yang berjalan di
 * latar belakang, sehingga tidak perlu layar khusus: pemasangan otomatis dimulai dan
 * kemajuannya ditampilkan di beranda.
 */
object ComponentInstaller {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var job: Job? = null

    private val _items = ArrayList<InstallableItem>()
    val items: List<InstallableItem> get() = _items

    private val _ready = MutableStateFlow(false)
    /** true bila semua komponen sudah terpasang dan terbaru */
    val ready: StateFlow<Boolean> = _ready.asStateFlow()

    private val _running = MutableStateFlow(false)
    val running: StateFlow<Boolean> = _running.asStateFlow()

    private val _failed = MutableStateFlow(false)
    val failed: StateFlow<Boolean> = _failed.asStateFlow()

    /** Jumlah komponen yang sudah selesai / total, untuk indikator kemajuan. */
    private val _progress = MutableStateFlow(0 to 0)
    val progress: StateFlow<Pair<Int, Int>> = _progress.asStateFlow()

    /** Nama komponen yang sedang dipasang. */
    private val _currentName = MutableStateFlow<String?>(null)
    val currentName: StateFlow<String?> = _currentName.asStateFlow()

    @Synchronized
    fun init(context: Context) {
        if (_items.isNotEmpty()) return
        val app = context.applicationContext
        Components.entries.forEach { component ->
            _items.add(
                InstallableItem(
                    component.displayName,
                    app.getString(component.summary),
                    UnpackComponentsTask(app, component)
                )
            )
        }
        Jre.entries.forEach { jre ->
            val task = UnpackJreTask(app, jre)
            if (!task.isCheckFailed()) {
                _items.add(InstallableItem(jre.jreName, app.getString(jre.summary), task))
            }
        }
        _items.add(InstallableItem("JNA", app.getString(com.movtery.zalithlauncher.R.string.unpack_screen_jna), UnpackJnaTask(app)))
        _items.sort()
    }

    /** Periksa status tiap komponen (apakah perlu dipasang/diperbarui). */
    suspend fun checkAll() {
        _items.forEach { item -> item.updateState(item.task.checkState()) }
        refresh()
    }

    fun areAllFinished(): Boolean =
        _items.isNotEmpty() && _items.all { it.state.value == InstallableItem.State.FINISHED }

    private fun refresh() {
        val done = _items.count { it.state.value == InstallableItem.State.FINISHED }
        _progress.value = done to _items.size
        _failed.value = _items.any { it.state.value == InstallableItem.State.FAILED }
        _ready.value = areAllFinished()
    }

    /** Mulai memasang semua komponen yang belum selesai. Aman dipanggil berulang. */
    fun start() {
        if (job?.isActive == true || _items.isEmpty()) return
        job = scope.launch {
            _running.value = true
            _failed.value = false
            val todo = _items.filter {
                val s = it.state.value
                s == InstallableItem.State.NOT_STARTED ||
                    s == InstallableItem.State.PENDING ||
                    s == InstallableItem.State.FAILED
            }
            todo.map { item ->
                launch {
                    item.updateState(InstallableItem.State.RUNNING)
                    item.task.updateMessage(null)
                    _currentName.value = item.name
                    try {
                        item.task.run()
                        item.updateState(InstallableItem.State.FINISHED)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Logger.error(TAG, "Failed to install component: ${item.name}", e)
                        item.task.updateMessage(e.message ?: e.javaClass.simpleName)
                        item.updateState(InstallableItem.State.FAILED)
                    }
                    refresh()
                }
            }.joinAll()
            if (areAllFinished()) {
                AllSettings.javaRuntime.apply {
                    //pilih Java bawaan jika belum ada
                    if (getValue().isEmpty()) save(Jre.JRE_8.jreName)
                }
            }
            _currentName.value = null
            _running.value = false
            refresh()
        }
    }
}
