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


package com.movtery.zalithlauncher.ui.theme

import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.setting.AllSettings
import androidx.compose.foundation.shape.RoundedCornerShape as BaseRoundedCornerShape

/*
 * Gaya Hero: KOTAK MURNI tanpa lengkungan sedikit pun (radius 0) di semua aspek; Nev, Futuristik, Liquid tetap bulat.
 * File-file UI meng-import RoundedCornerShape dari sini, jadi semua sudut membulat berubah
 * serentak (Unduh, Modpack/Mod/Snapshot, Kelola Versi, Pengaturan, dialog, dst.).
 * Membaca state Compose, sehingga ikut berubah saat saklar diubah.
 */

/** true bila tema desain aktif adalah Nev (klasik): bukan Cryogenic/Futuristik, Cyber, Liquid, maupun Hero. */
@Composable
fun isNevDesign(): Boolean = AllSettings.launcherDesignStyle.state == DesignStyleType.NEV

/** Radius kartu daftar: persegi panjang (kecil) pada tema Nev, [default] pada tema lain. */
@Composable
fun nevRectRadius(default: androidx.compose.ui.unit.Dp, rect: androidx.compose.ui.unit.Dp = 8.dp): androidx.compose.ui.unit.Dp =
    if (isNevDesign()) rect else default

/** Apakah bentuk kotak berlengkung tipis sedang aktif. */
fun isBoxy(): Boolean =
    AllSettings.launcherDesignStyle.state == DesignStyleType.HERO

/** Radius pada tema Hero selalu 0 (kotak murni); tema lain memakai ukuran aslinya. */
fun boxyRadius(size: Dp): Dp =
    if (!isBoxy() || size <= 0.dp) size else 0.dp

fun RoundedCornerShape(size: Dp): BaseRoundedCornerShape = BaseRoundedCornerShape(boxyRadius(size))

fun RoundedCornerShape(percent: Int): BaseRoundedCornerShape =
    if (isBoxy()) BaseRoundedCornerShape(0.dp) else BaseRoundedCornerShape(percent)

fun RoundedCornerShape(
    topStart: Dp,
    topEnd: Dp,
    bottomEnd: Dp,
    bottomStart: Dp
): BaseRoundedCornerShape = BaseRoundedCornerShape(
    topStart = boxyRadius(topStart),
    topEnd = boxyRadius(topEnd),
    bottomEnd = boxyRadius(bottomEnd),
    bottomStart = boxyRadius(bottomStart)
)

/** Bentuk Material (Surface, tombol, dialog) untuk mode kotak berlengkung tipis. */
val BoxyShapes = Shapes(
    extraSmall = BaseRoundedCornerShape(0.dp),
    small = BaseRoundedCornerShape(0.dp),
    medium = BaseRoundedCornerShape(0.dp),
    large = BaseRoundedCornerShape(0.dp),
    extraLarge = BaseRoundedCornerShape(0.dp)
)

/** Pengganti CircleShape: tetap bulat di gaya lain, jadi kotak berlengkung tipis di mode hero. */
val CircleShape: BaseRoundedCornerShape
    get() = if (isBoxy()) BaseRoundedCornerShape(0.dp) else androidx.compose.foundation.shape.CircleShape

/** Garis tepi untuk permukaan (Surface) di gaya kotak; null di gaya lain. */
@androidx.compose.runtime.Composable
fun boxyBorder(): androidx.compose.foundation.BorderStroke? = if (isBoxy()) NevCardBorder else null
