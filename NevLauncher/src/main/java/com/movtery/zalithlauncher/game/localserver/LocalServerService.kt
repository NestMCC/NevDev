package com.movtery.zalithlauncher.game.localserver

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.compose.ui.unit.IntSize
import androidx.core.app.NotificationCompat
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.bridge.LoggerBridge
import com.movtery.zalithlauncher.bridge.NativeLibraryLoader
import com.movtery.zalithlauncher.game.launch.Launcher
import com.movtery.zalithlauncher.game.multirt.RuntimesManager
import com.movtery.zalithlauncher.notification.NOTIFICATION_ID_LOCAL_SERVER
import com.movtery.zalithlauncher.notification.NotificationChannelData
import com.movtery.zalithlauncher.utils.logging.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "LocalServerService"

const val SERVICE_SERVER_DIR = "server.dir"
const val SERVICE_SERVER_JRE = "server.jre"
const val SERVICE_SERVER_RAM = "server.ram"
const val SERVICE_SERVER_NAME = "server.name"

/** Menjalankan JVM server Minecraft di proses `:server` (tanpa grafis) */
class LocalServerService : Service() {
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val name = intent?.getStringExtra(SERVICE_SERVER_NAME) ?: "Server"
        postNotification(name)

        val dir = intent?.getStringExtra(SERVICE_SERVER_DIR)
        val jre = intent?.getStringExtra(SERVICE_SERVER_JRE)
        val ram = intent?.getIntExtra(SERVICE_SERVER_RAM, 1024) ?: 1024
        if (dir == null || jre == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        scope.launch {
            val code = run(dir, jre, ram)
            runCatching {
                sendBroadcast(
                    Intent(packageName + "." + ACTION_LOCAL_SERVER_EXIT)
                        .setPackage(packageName)
                        .putExtra(EXTRA_EXIT_CODE, code)
                )
            }
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    private suspend fun run(dir: String, jre: String, ram: Int): Int = withContext(Dispatchers.IO) {
        runCatching {
            withContext(Dispatchers.Main) { NativeLibraryLoader.loadPojavLib() }
            val log = File(dir, "nev-console.log")
            if (!log.exists() && !log.createNewFile()) error("Failed to create console log")
            LoggerBridge.start(log.absolutePath)
            Logger.info(TAG, "start server jvm in $dir")

            ServerLauncher(
                context = applicationContext,
                dir = File(dir),
                jreName = jre,
                ramMb = ram,
                logFile = log
            ).launch(IntSize(1280, 720))
        }.onFailure { e ->
            Logger.warning(TAG, "server jvm crashed", e)
            runCatching { LoggerBridge.append(e.stackTraceToString()) }
        }.getOrElse { 1 }
    }

    override fun onDestroy() {
        super.onDestroy()
        Thread.sleep(300)
        //JVM tidak bisa dimuat ulang di proses yang sama: matikan proses agar start berikutnya bersih
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    private fun postNotification(name: String) {
        val data = NotificationChannelData.JVM_SERVICE_CHANNEL
        val notification: Notification = NotificationCompat.Builder(this, data.channelId)
            .setContentTitle(getString(R.string.local_server_notification, name))
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID_LOCAL_SERVER, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(NOTIFICATION_ID_LOCAL_SERVER, notification)
        }
    }
}

private class ServerLauncher(
    private val context: Context,
    private val dir: File,
    private val jreName: String,
    private val ramMb: Int,
    private val logFile: File
) : Launcher(onExit = { _, _ -> }, openPath = {}) {

    override suspend fun launch(screenSize: IntSize): Int {
        initLwjglComponent(context, 0)
        runtime = RuntimesManager.forceReload(jreName)

        LoggerBridge.appendTitle("Launch Local Server")
        return launchJvm(
            context = context,
            jvmArgs = listOf(
                "-Djava.awt.headless=true",
                "-jar", File(dir, "server.jar").absolutePath,
                "nogui"
            ),
            userHome = dir.absolutePath,
            userArgs = "",
            screenSize = screenSize,
            useLocalLanguage = false
        )
    }

    override fun progressFinalUserArgs(args: MutableList<String>, ramAllocation: Int) {
        super.progressFinalUserArgs(args, ramMb)
        //heap awal kecil: server tidak perlu langsung mengambil seluruh RAM
        args.purgeArg("-Xms")
        args.add("-Xms${minOf(ramMb, 256)}M")
    }

    override fun chdir(): String = dir.absolutePath
    override fun getLogFile(): File = logFile
    override fun exit() {}
}
