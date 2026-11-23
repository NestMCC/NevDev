package com.movtery.zalithlauncher.ui.theme

import android.app.ActivityManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.movtery.zalithlauncher.setting.AllSettings

/**
 * true: mode ringan aktif, baik karena diaktifkan pengguna maupun karena perangkat
 * berRAM rendah (terdeteksi otomatis). Efek berat seperti glow bayangan dan blur kaca dimatikan.
 */
@Composable
fun isLiteMode(): Boolean {
    val context = LocalContext.current
    val lowRam = remember {
        runCatching {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            am.isLowRamDevice || am.memoryClass <= 128
        }.getOrDefault(false)
    }
    return lowRam || AllSettings.launcherLiteMode.state
}
