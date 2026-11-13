/*
 * Nev Launcher
 * Based on Zalith Launcher 2
 * Copyright (C) 2025-2026 MovTery <movtery228@qq.com> and contributors
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

package com.movtery.zalithlauncher.game.renderer.renderers

import com.movtery.zalithlauncher.game.renderer.RendererInterface

/**
 * Nev Renderer: renderer Vulkan-first untuk Minecraft Java terbaru (26.3 dan seterusnya).
 *
 * Memakai jalur Mesa Zink + Kopper (Vulkan -> OpenGL) yang sudah ada di bridge native
 * (POJAV_RENDERER = opengles3_desktopgl_zink_kopper), sehingga tidak perlu library native baru.
 * Bedanya dengan [KopperZinkRenderer]: tidak ada batas versi maksimum, jadi rilis 26.3
 * (dan 26.4+) tidak ditolak oleh pengecekan "renderer tidak didukung".
 */
object NevRenderer : RendererInterface {
    override fun getRendererId(): String = "opengles3_desktopgl_zink_kopper"

    override fun getUniqueIdentifier(): String = "c01e0617-e6d3-4df6-9855-a771eaa3f10b"

    override val mesaBased: Boolean = true

    override val needsZinkDriverOverride: Boolean = true

    override fun getRendererName(): String = "Nev Renderer (Vulkan)"

    override fun getRendererSummary(): String =
        "Renderer berbasis Vulkan untuk Minecraft Java terbaru (26.3+). Butuh GPU/driver dengan Vulkan 1.2+."

    // Tanpa getMaxMCVersion: tidak ada batas atas versi.

    override fun getRendererEnv(): Lazy<Map<String, String>> = lazy {
        mapOf(
            "LIBGL_ES" to "3"
        )
    }

    override fun getDlopenLibrary(): Lazy<List<String>> = lazy { emptyList() }

    override fun getRendererLibrary(): String = "libglxshim.so"

    override fun getRendererEGL(): String = "libEGL_mesa.so"
}
