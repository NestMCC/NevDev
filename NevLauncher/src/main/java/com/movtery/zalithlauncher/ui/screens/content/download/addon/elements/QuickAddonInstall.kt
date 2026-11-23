package com.movtery.zalithlauncher.ui.screens.content.download.addon.elements

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.download.addon.isAutoInstallable
import com.movtery.zalithlauncher.game.download.addon.platform.Platform
import com.movtery.zalithlauncher.game.download.addon.platform.PlatformClasses
import com.movtery.zalithlauncher.game.download.addon.platform.PlatformReleaseType
import com.movtery.zalithlauncher.game.download.addon.platform.PlatformVersion
import com.movtery.zalithlauncher.game.download.addon.platform.getVersions
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.utils.network.isUsingMobileData
import kotlinx.coroutines.launch

/**
 * Pemasang cepat (tombol hijau "+ INSTAL" di daftar hasil pencarian tema Hero):
 * (platform, projectId, kelas, fallback) -> memasang versi terbaru yang cocok dengan versi game aktif.
 * [fallback] dipanggil bila tidak ada versi cocok (buka halaman proyek agar dipilih manual).
 */
typealias QuickAddonInstaller = (Platform, String, PlatformClasses, () -> Unit) -> Unit

val LocalQuickAddonInstall = staticCompositionLocalOf<QuickAddonInstaller?> { null }

@Composable
fun rememberQuickAddonInstaller(
    changeOperation: (DownloadSingleOperation) -> Unit
): QuickAddonInstaller {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val change by rememberUpdatedState(changeOperation)
    return remember {
        { platform, projectId, classes, fallback ->
            scope.launch {
                val target = VersionsManager.currentVersion.value?.takeIf { it.isValid() }
                if (target == null) {
                    Toast.makeText(context, R.string.download_addon_no_installed_versions, Toast.LENGTH_SHORT).show()
                    return@launch
                }
                Toast.makeText(context, R.string.download_addon_quick_preparing, Toast.LENGTH_SHORT).show()
                val best = runCatching {
                    getVersions(projectId, platform)
                        .initAllGeneric(projectId)
                        .filter { it.isAutoInstallable(target, classes) }
                        .sortedWith(
                            compareByDescending<PlatformVersion> { it.platformReleaseType() == PlatformReleaseType.RELEASE }
                                .thenByDescending { it.platformDatePublished() }
                        )
                        .firstOrNull()
                }.getOrNull()
                if (best == null) {
                    Toast.makeText(context, R.string.download_addon_quick_no_compatible, Toast.LENGTH_LONG).show()
                    fallback()
                    return@launch
                }
                change(
                    if (isUsingMobileData(context)) {
                        DownloadSingleOperation.WarningForMobileData(classes, best, emptyList())
                    } else {
                        DownloadSingleOperation.Install(
                            classes = classes,
                            version = best,
                            versions = listOf(target),
                            dependencies = emptyList(),
                            autoDependencies = true
                        )
                    }
                )
            }
        }
    }
}
