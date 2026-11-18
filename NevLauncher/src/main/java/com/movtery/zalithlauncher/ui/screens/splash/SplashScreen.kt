/*
 * Zalith Launcher 2
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

package com.movtery.zalithlauncher.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.movtery.zalithlauncher.ui.screens.content.elements.NevWordmark
import com.movtery.zalithlauncher.ui.screens.content.elements.nevLoadingBackground
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.entryProvider
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.components.InstallableItem
import com.movtery.zalithlauncher.legal.AvailableAgreement
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.AnimatedDirection
import com.movtery.zalithlauncher.ui.components.animated
import com.movtery.zalithlauncher.ui.components.rememberAnimatedContext
import com.movtery.zalithlauncher.ui.screens.NavStateHost
import com.movtery.zalithlauncher.ui.screens.NestedNavDisplay
import com.movtery.zalithlauncher.ui.screens.rememberTransitionSpec
import com.movtery.zalithlauncher.viewmodel.SplashBackStackViewModel
import kotlinx.coroutines.flow.StateFlow

/**
 * @param startAllTask 开启全部的解压任务
 * @param onWelcomeFinished 完成欢迎页
 * @param availableAgreement 完整的可用协议数据
 * @param unpackItems 解压任务列表
 */
@Composable
fun SplashScreen(
    startAllTask: () -> Unit,
    onWelcomeFinished: () -> Unit,
    availableAgreement: StateFlow<AvailableAgreement?>,
    unpackItems: List<InstallableItem>,
    screenViewModel: SplashBackStackViewModel
) {
    NavStateHost(rootKey = screenViewModel.splashScreen) {
        NavigationUI(
            modifier = Modifier.fillMaxSize(),
            startAllTask = startAllTask,
            onWelcomeFinished = onWelcomeFinished,
            availableAgreement = availableAgreement,
            unpackItems = unpackItems,
            screenViewModel = screenViewModel
        )
    }
}

@Composable
private fun NavigationUI(
    modifier: Modifier = Modifier,
    startAllTask: () -> Unit,
    onWelcomeFinished: () -> Unit,
    availableAgreement: StateFlow<AvailableAgreement?>,
    unpackItems: List<InstallableItem>,
    screenViewModel: SplashBackStackViewModel
) {
    val backStack = screenViewModel.splashScreen.backStack

    val currentKey = backStack.lastOrNull()
    LaunchedEffect(currentKey) {
        screenViewModel.splashScreen.currentKey = currentKey
    }

    if (backStack.isNotEmpty()) {
        NestedNavDisplay(
            ownerKey = screenViewModel.splashScreen,
            backStack = backStack,
            modifier = modifier,
            onBack = {},
            transitionSpec = rememberTransitionSpec(),
            popTransitionSpec = rememberTransitionSpec(),
            entryProvider = entryProvider {
                entry<SplashOperation.Loading> {
                    LoadingScreen(screenViewModel = screenViewModel)
                }
                entry<SplashOperation.Welcome> {
                    WelcomeScreen(
                        modifier = Modifier.fillMaxSize(),
                        onFinished = onWelcomeFinished,
                        availableAgreement = availableAgreement,
                        screenViewModel = screenViewModel
                    )
                }
                entry<SplashOperation.Unpacking> {
                    UnpackScreen(
                        items = unpackItems,
                        screenViewModel = screenViewModel,
                        onAgreeClick = startAllTask
                    )
                }
            }
        )
    } else {
        Box(modifier)
    }
}

@Composable
private fun LoadingScreen(
    screenViewModel: SplashBackStackViewModel
) {
    BaseScreen(
        screenKey = SplashOperation.Loading,
        currentKey = screenViewModel.splashScreen.currentKey
    ) { isVisible ->
        val ctx = rememberAnimatedContext(isVisible = isVisible)

        val accent = MaterialTheme.colorScheme.primary
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(nevLoadingBackground(accent))
                .animated(ctx, from = AnimatedDirection.Bottom)
        ) {
            NevWordmark(modifier = Modifier.align(Alignment.Center), accent = accent)

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 32.dp, vertical = 28.dp)
                    .widthIn(max = 420.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.game_loading_text),
                    color = Color.White.copy(alpha = 0.75f),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                    color = accent,
                    trackColor = Color.White.copy(alpha = 0.12f)
                )
            }
        }
    }
}
