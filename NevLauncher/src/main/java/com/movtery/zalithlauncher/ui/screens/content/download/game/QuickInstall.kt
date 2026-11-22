package com.movtery.zalithlauncher.ui.screens.content.download.game

import androidx.compose.runtime.staticCompositionLocalOf
import com.movtery.zalithlauncher.game.addons.modloader.ModLoader
import com.movtery.zalithlauncher.game.addons.modloader.fabriclike.fabric.FabricAPIVersions
import com.movtery.zalithlauncher.game.addons.modloader.fabriclike.fabric.FabricVersions
import com.movtery.zalithlauncher.game.addons.modloader.fabriclike.legacyfabric.LegacyFabricAPIVersions
import com.movtery.zalithlauncher.game.addons.modloader.fabriclike.legacyfabric.LegacyFabricVersions
import com.movtery.zalithlauncher.game.addons.modloader.fabriclike.quilt.QuiltAPIVersions
import com.movtery.zalithlauncher.game.addons.modloader.fabriclike.quilt.QuiltVersions
import com.movtery.zalithlauncher.game.addons.modloader.forgelike.forge.ForgeVersions
import com.movtery.zalithlauncher.game.addons.modloader.forgelike.neoforge.NeoForgeVersions
import com.movtery.zalithlauncher.game.addons.modloader.optifine.OptiFineVersions
import com.movtery.zalithlauncher.game.download.game.GameDownloadInfo
import com.movtery.zalithlauncher.game.versioninfo.MinecraftVersion

/**
 * Pemasangan cepat dari dropdown baris versi (Hero): versi game + loader terbaru langsung dipasang
 * tanpa membuka layar Pasang. Disediakan oleh [com.movtery.zalithlauncher.ui.screens.content.download.DownloadGameScreen].
 */
val LocalQuickInstall = staticCompositionLocalOf<((MinecraftVersion, ModLoader?) -> Unit)?> { null }

/**
 * Susun [GameDownloadInfo] memakai versi terbaru dari loader yang dipilih.
 * Nama versi mengikuti nama otomatis layar Pasang ("1.21 Fabric 0.16.0").
 * @return null bila daftar versi loader tidak bisa diambil / kosong
 */
suspend fun buildQuickInstallInfo(gameVersion: String, loader: ModLoader?): GameDownloadInfo? {
    return runCatching {
        when (loader) {
            null -> GameDownloadInfo(gameVersion = gameVersion, customVersionName = gameVersion)
            ModLoader.OPTIFINE -> {
                val v = OptiFineVersions.fetchOptiFineList(gameVersion = gameVersion)?.firstOrNull() ?: return null
                GameDownloadInfo(gameVersion, "$gameVersion ${ModLoader.OPTIFINE.displayName} ${v.realVersion}", optifine = v)
            }
            ModLoader.FORGE -> {
                val v = ForgeVersions.fetchForgeList(gameVersion)?.firstOrNull() ?: return null
                GameDownloadInfo(gameVersion, "$gameVersion ${ModLoader.FORGE.displayName} ${v.versionName}", forge = v)
            }
            ModLoader.NEOFORGE -> {
                val v = NeoForgeVersions.fetchNeoForgeList(gameVersion = gameVersion)?.firstOrNull() ?: return null
                GameDownloadInfo(gameVersion, "$gameVersion ${ModLoader.NEOFORGE.displayName} ${v.versionName}", neoforge = v)
            }
            ModLoader.FABRIC -> {
                val v = FabricVersions.fetchFabricLoaderList(gameVersion)?.firstOrNull() ?: return null
                val api = runCatching { FabricAPIVersions.fetchVersionList(gameVersion)?.firstOrNull() }.getOrNull()
                GameDownloadInfo(gameVersion, "$gameVersion ${ModLoader.FABRIC.displayName} ${v.version}", fabric = v, fabricAPI = api)
            }
            ModLoader.QUILT -> {
                val v = QuiltVersions.fetchQuiltLoaderList(gameVersion)?.firstOrNull() ?: return null
                val api = runCatching { QuiltAPIVersions.fetchVersionList(gameVersion)?.firstOrNull() }.getOrNull()
                GameDownloadInfo(gameVersion, "$gameVersion ${ModLoader.QUILT.displayName} ${v.version}", quilt = v, quiltAPI = api)
            }
            ModLoader.LEGACY_FABRIC -> {
                val v = LegacyFabricVersions.fetchFabricLoaderList(gameVersion)?.firstOrNull() ?: return null
                val api = runCatching { LegacyFabricAPIVersions.fetchVersionList(gameVersion)?.firstOrNull() }.getOrNull()
                GameDownloadInfo(gameVersion, "$gameVersion ${ModLoader.LEGACY_FABRIC.displayName} ${v.version}", legacyFabric = v, legacyFabricAPI = api)
            }
            else -> null
        }
    }.getOrNull()
}
