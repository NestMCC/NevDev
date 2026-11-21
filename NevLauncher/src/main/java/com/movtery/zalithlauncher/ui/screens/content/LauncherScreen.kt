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

package com.movtery.zalithlauncher.ui.screens.content

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import com.movtery.zalithlauncher.ui.theme.OutlinedButton
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.Surface
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import com.movtery.zalithlauncher.ui.theme.NevGradientButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.Image
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.movtery.cardgrid.state.rememberCardGridState
import com.movtery.guide.GuideSide
import com.movtery.guide.guideNode
import com.movtery.petlib.PetAnimation
import com.movtery.petlib.PetTapReaction
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.game.account.Account
import com.movtery.zalithlauncher.game.account.AccountsManager
import com.movtery.zalithlauncher.game.account.getAccountTypeName
import com.movtery.zalithlauncher.game.account.wardrobe.toArmModel
import com.movtery.zalithlauncher.game.version.installed.Version
import com.movtery.zalithlauncher.game.version.installed.VersionsManager
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.setting.enums.ActionMenuSide
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.theme.LocalNevStyle
import com.movtery.zalithlauncher.ui.components.AnimatedDirection
import com.movtery.zalithlauncher.ui.components.BackgroundCard
import com.movtery.zalithlauncher.ui.components.MarqueeText
import com.movtery.zalithlauncher.ui.components.PetModelView
import com.movtery.zalithlauncher.ui.components.ScalingActionButton
import com.movtery.zalithlauncher.ui.components.animated
import com.movtery.zalithlauncher.ui.components.rememberAnimatedContext
import com.movtery.zalithlauncher.ui.guide.GuideKeys
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.theme.NevRed
import com.movtery.zalithlauncher.ui.theme.nevShape
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.content.elements.CommonVersionInfoLayout
import com.movtery.zalithlauncher.ui.screens.content.elements.PlayerFace
import com.movtery.zalithlauncher.ui.screens.content.elements.VersionIconImage
import com.movtery.zalithlauncher.ui.screens.content.home.HomeGrid
import com.movtery.zalithlauncher.ui.screens.content.home.LocalActionMenuDrag
import com.movtery.zalithlauncher.ui.screens.content.home.actionMenuDragAnchor
import com.movtery.zalithlauncher.ui.screens.content.home.actionMenuDragExclusion
import com.movtery.zalithlauncher.ui.screens.content.home.rememberActionMenuDragState
import com.movtery.zalithlauncher.ui.screens.content.home.server.LocalServerCardQuickPlay
import com.movtery.zalithlauncher.ui.screens.content.home.version.LocalHomeCardLauncher
import com.movtery.zalithlauncher.ui.screens.content.home.version.LocalHomeCardVersionSettings
import com.movtery.zalithlauncher.viewmodel.ScreenBackStackViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

private const val ContentWeight = 7f
private const val ActionMenuWeight = 3f
/** 紧凑（横向）操作菜单的占比 */
private const val ActionMenuCompactWeight = 4.6f
private const val ContentCompactWeight = 5.4f
private val ActionMenuCompactHeight = 124.dp

/**
 * 操作菜单停泊槽位与屏幕边缘的间距
 */
private val ActionMenuOuterPadding = 12.dp

@Composable
fun LauncherScreen(
    backStackViewModel: ScreenBackStackViewModel,
    navigateToVersions: (Version) -> Unit,
    onLaunchGame: (Version?) -> Unit,
    onQuickPlayServer: (Version?, String) -> Unit,
    onOpenLink: (String) -> Unit,
    onOpenFileManager: () -> Unit = {},
    startGuideOnce: (GuideKeys.Keys) -> Unit,
) {
    //game baru boleh dijalankan setelah komponen launcher selesai dipasang
    val installerReady by com.movtery.zalithlauncher.components.ComponentInstaller.ready.collectAsStateWithLifecycle()
    val launchContext = androidx.compose.ui.platform.LocalContext.current
    val onLaunchGame: (Version?) -> Unit = { v ->
        if (installerReady) onLaunchGame(v) else {
            android.widget.Toast.makeText(
                launchContext,
                launchContext.getString(R.string.components_wait_toast),
                android.widget.Toast.LENGTH_SHORT
            ).show()
        }
    }
    LaunchedEffect(Unit) {
        //发起新手引导
        startGuideOnce(GuideKeys.Main)
    }

    BaseScreen(
        screenKey = NormalNavKey.LauncherMain,
        currentKey = backStackViewModel.mainScreen.currentKey
    ) { isVisible ->
        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
        val dockedSide = AllSettings.launcherActionMenuSide.state
        val dragState = rememberActionMenuDragState(
            onCommit = { side -> AllSettings.launcherActionMenuSide.save(side) }
        )
        dragState.dockedSide = dockedSide
        dragState.isRtl = isRtl

        LaunchedEffect(isVisible) {
            //屏幕切走时打断进行中的拖拽
            if (!isVisible) dragState.onDragCancel()
        }

        if (LocalNevStyle.current.futuristic) {
            FuturisticHome(
                isVisible = isVisible,
                onLaunchGame = onLaunchGame,
                toAccountManageScreen = {
                    backStackViewModel.mainScreen.navigateTo(
                        screenKey = NormalNavKey.AccountManager(FirstLoginMenu.NONE)
                    )
                },
                toVersionSettingsScreen = {
                    VersionsManager.currentVersion.value?.let { navigateToVersions(it) }
                },
                toVersionManageScreen = {
                    backStackViewModel.mainScreen.removeAndNavigateTo(
                        remove = NestedNavKey.VersionSettings::class,
                        screenKey = NormalNavKey.VersionsManager
                    )
                }
            )
            return@BaseScreen
        }

        if (AllSettings.launcherDesignStyle.state != com.movtery.zalithlauncher.ui.theme.DesignStyleType.HERO &&
            !LocalNevStyle.current.cyber
        ) {
            DashboardHome(
                isVisible = isVisible,
                onLaunchGame = onLaunchGame,
                toAccountManageScreen = {
                    backStackViewModel.mainScreen.navigateTo(
                        screenKey = NormalNavKey.AccountManager(FirstLoginMenu.NONE)
                    )
                },
                toVersionManageScreen = {
                    backStackViewModel.mainScreen.removeAndNavigateTo(
                        remove = NestedNavKey.VersionSettings::class,
                        screenKey = NormalNavKey.VersionsManager
                    )
                }
            )
            return@BaseScreen
        }

        if (AllSettings.launcherDesignStyle.state == com.movtery.zalithlauncher.ui.theme.DesignStyleType.HERO) {
            HeroHome(
                isVisible = isVisible,
                onLaunchGame = onLaunchGame,
                toAccountManageScreen = {
                    backStackViewModel.mainScreen.navigateTo(
                        screenKey = NormalNavKey.AccountManager(FirstLoginMenu.NONE)
                    )
                },
                onOpenFileManager = onOpenFileManager,
                toVersionSettingsScreen = {
                    VersionsManager.currentVersion.value?.let { navigateToVersions(it) }
                },
                toVersionManageScreen = {
                    backStackViewModel.mainScreen.removeAndNavigateTo(
                        remove = NestedNavKey.VersionSettings::class,
                        screenKey = NormalNavKey.VersionsManager
                    )
                }
            )
            return@BaseScreen
        }

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    dragState.parentOrigin = coordinates.positionInRoot()
                }
        ) {
            val parentWidthPx = constraints.maxWidth.toFloat()
            dragState.parentWidthPx = parentWidthPx
            dragState.outerPaddingPx = with(LocalDensity.current) { ActionMenuOuterPadding.toPx() }
            //Cyber: bilah perintah (akun, versi, Main) selalu mendatar di atas, grid kartu di bawahnya
            val isActionMenuTaller = maxHeight >= 600.dp && !LocalNevStyle.current.cyber
            val menuWeight = if (isActionMenuTaller) ActionMenuWeight else ActionMenuCompactWeight
            val contentWeight = if (isActionMenuTaller) ContentWeight else ContentCompactWeight
            //紧凑模式：操作卡片横向铺满整行，内容区域放到卡片下方
            //两侧停泊位置重合（都铺满），所以拖拽归位后依旧是铺满的
            dragState.menuSpanPx = if (isActionMenuTaller) {
                parentWidthPx * (menuWeight / (menuWeight + contentWeight))
            } else {
                parentWidthPx - dragState.outerPaddingPx
            }

            //拖拽期间以预览侧为准
            val effectiveSide = dragState.previewSide ?: dockedSide

            //卡片尺寸与停泊槽内容区保持一致
            val cardWidth = if (isActionMenuTaller) {
                maxWidth * (menuWeight / (menuWeight + contentWeight)) - ActionMenuOuterPadding
            } else {
                maxWidth - ActionMenuOuterPadding * 2
            }
            val cardHeight = if (isActionMenuTaller) maxHeight - ActionMenuOuterPadding * 2 else ActionMenuCompactHeight

            val toAccountManageScreen: () -> Unit = {
                backStackViewModel.mainScreen.navigateTo(
                    screenKey = NormalNavKey.AccountManager(FirstLoginMenu.NONE)
                )
            }
            val toVersionManageScreen: () -> Unit = {
                backStackViewModel.mainScreen.removeAndNavigateTo(
                    remove = NestedNavKey.VersionSettings::class,
                    screenKey = NormalNavKey.VersionsManager
                )
            }
            val toVersionSettingsScreen: () -> Unit = {
                VersionsManager.currentVersion.value?.let { version ->
                    navigateToVersions(version)
                }
            }

            // 内容区域
            // 紧凑模式下，内容在铺满整行的操作卡片下方；否则与操作卡片左右并排
            val contentLayout: @Composable (@Composable (Modifier) -> Unit) -> Unit = { content ->
                if (isActionMenuTaller) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        // ActionMenu 对接到了 Start，留出空位
                        if (dockedSide == ActionMenuSide.START) {
                            Spacer(modifier = Modifier.weight(menuWeight))
                        }

                        content(Modifier.weight(contentWeight))

                        // ActionMenu 对接到了 End，留出空位
                        if (dockedSide == ActionMenuSide.END) {
                            Spacer(modifier = Modifier.weight(menuWeight))
                        }
                    }
                } else {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.height(ActionMenuCompactHeight + ActionMenuOuterPadding))
                        content(Modifier.weight(1f))
                    }
                }
            }

            contentLayout { contentModifier ->
                CompositionLocalProvider(
                    LocalUriHandler provides object : UriHandler {
                        override fun openUri(uri: String) {
                            onOpenLink(uri)
                        }
                    }
                ) {
                ContentMenu(
                    modifier = Modifier
                        .guideNode(
                            key = GuideKeys.Main.Step.CardTip,
                            holeRadius = 0.dp
                        )
                        .then(contentModifier)
                        .offset {
                            IntOffset(
                                x = if (isActionMenuTaller) dragState.previewShift.value.roundToInt() else 0,
                                y = 0
                            )
                        },
                    isVisible = isVisible,
                    onLaunchGame = { version ->
                        onLaunchGame(version)
                    },
                    onQuickPlayServer = onQuickPlayServer,
                    onOpenVersionSettings = navigateToVersions
                )
                }
            }

            MusicMiniPlayer(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 12.dp)
            )

            CompositionLocalProvider(LocalActionMenuDrag provides dragState) {
                Box(
                    modifier = Modifier
                        .offset {
                            val translation = if (dragState.floating) {
                                dragState.cardPosition
                            } else {
                                dragState.landingOf(effectiveSide) + dragState.settleOffset.value
                            }
                            val x = if (isRtl) parentWidthPx - cardWidth.toPx() - translation.x else translation.x
                            IntOffset(x.roundToInt(), translation.y.roundToInt())
                        }
                        .size(cardWidth, cardHeight)
                ) {
                    ActionMenu(
                        modifier = Modifier.fillMaxSize(),
                        isVisible = isVisible,
                        isTaller = isActionMenuTaller,
                        dockedSide = dockedSide,
                        onLaunchGame = onLaunchGame,
                        swapDirection = if (dockedSide == ActionMenuSide.END) AnimatedDirection.Right else AnimatedDirection.Left,
                        pickUpScale = { dragState.scale },
                        toAccountManageScreen = toAccountManageScreen,
                        toVersionManageScreen = toVersionManageScreen,
                        toVersionSettingsScreen = toVersionSettingsScreen
                    )
                }
            }
        }
    }
}

@Composable
private fun ContentMenu(
    isVisible: Boolean,
    onLaunchGame: (Version) -> Unit,
    onQuickPlayServer: (Version?, String) -> Unit,
    onOpenVersionSettings: (Version) -> Unit,
    modifier: Modifier = Modifier,
) {
    val ctx = rememberAnimatedContext(isVisible = isVisible)
    val gridState = rememberCardGridState()

    CompositionLocalProvider(
        LocalHomeCardLauncher provides onLaunchGame,
        LocalHomeCardVersionSettings provides onOpenVersionSettings,
        LocalServerCardQuickPlay provides onQuickPlayServer
    ) {
        HomeGrid(
            state = gridState,
            isVisible = isVisible,
            modifier = modifier
                .fillMaxSize()
                .animated(ctx)
        )
    }
}

@Composable
private fun AccountAvatarCenter(
    modifier: Modifier = Modifier,
    account: Account?,
    refreshKey: Any? = null,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clip(shape = MaterialTheme.shapes.extraLarge)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(all = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (account != null) {
                PlayerFace(
                    account = account,
                    avatarSize = 64.dp,
                    refreshKey = refreshKey
                )
            } else {
                Icon(
                    modifier = Modifier.size(40.dp),
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = null
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = account?.username ?: stringResource(R.string.account_add_new_account),
                    maxLines = 1,
                    style = MaterialTheme.typography.titleSmall
                )
                if (account != null) {
                    Text(
                        text = getAccountTypeName(account),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun AccountAvatarRow(
    modifier: Modifier = Modifier,
    account: Account?,
    refreshKey: Any? = null,
) {
    Row(
        modifier = modifier.padding(all = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (account != null) {
            PlayerFace(
                modifier = Modifier.padding(start = 4.dp),
                account = account,
                avatarSize = 48.dp,
                refreshKey = refreshKey
            )
        } else {
            Icon(
                modifier = Modifier.size(40.dp),
                painter = painterResource(R.drawable.ic_add),
                contentDescription = null
            )
        }

        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = account?.username ?: stringResource(R.string.account_add_new_account),
                maxLines = 1,
                style = MaterialTheme.typography.titleSmall
            )
            if (account != null) {
                Text(
                    text = getAccountTypeName(account),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun VersionsContent(
    horizontal: Boolean = false,
    onLaunchGame: (Version?) -> Unit,
    toVersionManageScreen: () -> Unit,
    toVersionSettingsScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val version by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val isRefreshing by VersionsManager.isRefreshing.collectAsStateWithLifecycle()

    var showList by remember { mutableStateOf(false) }
    var versionManagerRow by remember { mutableStateOf<LayoutCoordinates?>(null) }
    val versionPart: @Composable (Modifier) -> Unit = { boxModifier ->
        Box(
            modifier = boxModifier,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .onGloballyPositioned { coordinates ->
                            versionManagerRow = coordinates
                        }
                ) {
                    VersionManagerLayout(
                        isRefreshing = isRefreshing,
                        version = version,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        swapToVersionManage = toVersionManageScreen,
                        openListMenu = { showList = true },
                    )
                }
                version?.takeIf { !isRefreshing && it.isValid() }?.let {
                    IconButton(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = toVersionSettingsScreen
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_settings_filled),
                            contentDescription = stringResource(R.string.versions_manage_settings)
                        )
                    }
                }
            }

            val menuAnchor = versionManagerRow
            val menuAnchorBounds = menuAnchor?.boundsInParent()
            val menuAnchorX = menuAnchorBounds?.left ?: 0f
            val menuAnchorHeight = menuAnchorBounds?.height ?: 0f

            DropdownMenu(
                border = com.movtery.zalithlauncher.ui.theme.boxyBorder(),
                expanded = showList && menuAnchor != null,
                onDismissRequest = { showList = false },
                modifier = Modifier.width(260.dp),
                offset = DpOffset(
                    x = with(LocalDensity.current) { menuAnchorX.toDp() },
                    y = with(LocalDensity.current) { (-menuAnchorHeight).toDp() } - 8.dp
                ),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                val versions by VersionsManager.versions.collectAsStateWithLifecycle()
                versions.forEach { version0 ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CommonVersionInfoLayout(
                                    modifier = Modifier.weight(1f),
                                    version = version0,
                                    iconSize = 28.dp
                                )
                                IconButton(
                                    onClick = {
                                        onLaunchGame(version0)
                                        showList = false
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_play_arrow_filled),
                                        contentDescription = stringResource(R.string.main_launch_game),
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        },
                        onClick = {
                            if (version == version0) return@DropdownMenuItem
                            VersionsManager.saveVersion(version0)
                            showList = false
                        }
                    )
                }
            }
        }

    }

    val playPart: @Composable (Modifier) -> Unit = { buttonModifier ->
        NevGradientButton(
            modifier = buttonModifier,
            onClick = {
                onLaunchGame(null)
            }
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_play_arrow_filled),
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.main_play),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }

    if (horizontal) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {
            versionPart(Modifier.weight(1f))
            playPart(Modifier.padding(end = 16.dp))
        }
    } else {
        Column(modifier = modifier) {
            versionPart(Modifier.fillMaxWidth())
            playPart(
                Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(horizontal = 12.dp))
                    .padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
private fun ActionMenuCardContent(
    modifier: Modifier = Modifier,
    account: Account?,
    onLaunchGame: (Version?) -> Unit,
    toAccountManageScreen: () -> Unit,
    toVersionManageScreen: () -> Unit,
    toVersionSettingsScreen: () -> Unit,
) {
    BackgroundCard(
        modifier = Modifier
            .actionMenuDragAnchor()
            .guideNode(GuideKeys.Main.Step.CardDrag)
            .then(modifier),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
        //logo N besar samar di sisi kanan banner, di belakang tombol Main
        Image(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 120.dp)
                .fillMaxHeight(0.9f)
                .alpha(0.30f),
            painter = painterResource(R.drawable.img_nev_logo),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.70f),
                            MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.55f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.45f)
                        )
                    )
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AccountAvatarRow(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.extraLarge)
                    .clickable(onClick = toAccountManageScreen)
                    .guideNode(
                        key = GuideKeys.Main.Step.Account,
                        preferSide = GuideSide.Below
                    ),
                account = account,
            )

            VersionsContent(
                modifier = Modifier.weight(1f),
                horizontal = true,
                onLaunchGame = onLaunchGame,
                toVersionManageScreen = toVersionManageScreen,
                toVersionSettingsScreen = toVersionSettingsScreen,
            )
        }
        }
    }
}

@Composable
private fun ActionMenuTallerContent(
    modifier: Modifier = Modifier,
    account: Account?,
    dockedSide: ActionMenuSide,
    onLaunchGame: (Version?) -> Unit,
    toAccountManageScreen: () -> Unit,
    toVersionManageScreen: () -> Unit,
    toVersionSettingsScreen: () -> Unit,
) {
    val refreshWardrobe by AccountsManager.refreshWardrobe.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .actionMenuDragAnchor()
            .guideNode(
                key = GuideKeys.Main.Step.CardDrag,
                holeRadius = 0.dp,
            )
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
        // 偏航角符号与停泊侧相反，让模型面向屏幕内容一侧
        val yaw = Math.toRadians(
            ((if (dockedSide == ActionMenuSide.START) 35.0 else -(35.0)) * (if (isRtl) -1 else 1))
        ).toFloat()

        val armModel = account?.skinModelType?.toArmModel()
        val skinStamp = account?.getSkinFile()?.lastModified()
        val capeStamp = account?.getCapeFile()?.lastModified()
        val skinBytes by produceState<ByteArray?>(null, account, refreshWardrobe, skinStamp) {
            value = withContext(Dispatchers.IO) {
                account?.getSkinFile()?.takeIf { it.exists() }
                    ?.inputStream()?.use { it.readBytes() }
            }
        }
        val capeBytes by produceState<ByteArray?>(null, account, refreshWardrobe, capeStamp) {
            value = withContext(Dispatchers.IO) {
                account?.getCapeFile()?.takeIf { it.exists() }
                    ?.inputStream()?.use { it.readBytes() }
            }
        }

        PetModelView(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            skin = skinBytes,
            cape = capeBytes,
            arm = armModel,
            yaw = yaw,
            animation = PetAnimation.Idle,
            tapReaction = PetTapReaction.Interact,
            dragYaw = false
        )

        BackgroundCard(
            modifier = Modifier
                .guideNode(
                    key = GuideKeys.Main.Step.Account,
                    preferSide = GuideSide.Above,
                    holeRadius = 28.dp
                )
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            onClick = toAccountManageScreen
        ) {
            AccountAvatarRow(
                modifier = Modifier.fillMaxWidth(),
                account = account,
            )
        }

        BackgroundCard(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            VersionsContent(
                modifier = Modifier.fillMaxWidth(),
                onLaunchGame = onLaunchGame,
                toVersionManageScreen = toVersionManageScreen,
                toVersionSettingsScreen = toVersionSettingsScreen,
            )
        }
    }
}

@Composable
private fun ActionMenu(
    isVisible: Boolean,
    isTaller: Boolean,
    dockedSide: ActionMenuSide,
    onLaunchGame: (Version?) -> Unit,
    swapDirection: AnimatedDirection,
    pickUpScale: () -> Float,
    modifier: Modifier = Modifier,
    toAccountManageScreen: () -> Unit = {},
    toVersionManageScreen: () -> Unit = {},
    toVersionSettingsScreen: () -> Unit = {}
) {
    val ctx = rememberAnimatedContext(isVisible = isVisible)

    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()

    val contentModifier = modifier.graphicsLayer {
        val scale = pickUpScale()
        scaleX = scale
        scaleY = scale
    }.animated(ctx, from = swapDirection)

    if (isTaller) {
        ActionMenuTallerContent(
            modifier = contentModifier,
            account = account,
            dockedSide = dockedSide,
            onLaunchGame = onLaunchGame,
            toAccountManageScreen = toAccountManageScreen,
            toVersionManageScreen = toVersionManageScreen,
            toVersionSettingsScreen = toVersionSettingsScreen,
        )
    } else {
        ActionMenuCardContent(
            modifier = contentModifier,
            account = account,
            onLaunchGame = onLaunchGame,
            toAccountManageScreen = toAccountManageScreen,
            toVersionManageScreen = toVersionManageScreen,
            toVersionSettingsScreen = toVersionSettingsScreen,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun VersionManagerLayout(
    isRefreshing: Boolean,
    version: Version?,
    swapToVersionManage: () -> Unit,
    openListMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .actionMenuDragExclusion()
            .clip(shape = MaterialTheme.shapes.large)
            .combinedClickable(
                role = Role.Button,
                onClick = swapToVersionManage,
                onLongClick = {
                    if (version != null) openListMenu()
                }
            ).guideNode(
                key = GuideKeys.Main.Step.VersionList,
                preferSide = GuideSide.Above,
            ).padding(PaddingValues(all = 8.dp))
    ) {
        if (isRefreshing) {
            Box(modifier = Modifier.fillMaxWidth()) {
                LoadingIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
        } else {
            VersionIconImage(
                version = version,
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(8.dp))

            if (version == null) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .basicMarquee(iterations = Int.MAX_VALUE),
                    text = stringResource(R.string.versions_manage_no_versions),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1
                )
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                ) {
                    Text(
                        modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
                        text = version.getVersionName(),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1
                    )
                    if (version.isValid()) {
                        Text(
                            modifier = Modifier.basicMarquee(iterations = Int.MAX_VALUE),
                            text = version.getVersionSummary(),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}


/**
 * Beranda bergaya hero: judul versi besar di kiri bawah, tombol Main besar di kanan bawah,
 * kartu akun kecil di pojok kanan atas. Seluruh latar peluncur tetap terlihat di belakangnya.
 */
@Composable
private fun HeroHome(
    isVisible: Boolean,
    onLaunchGame: (Version?) -> Unit,
    toAccountManageScreen: () -> Unit,
    onOpenFileManager: () -> Unit,
    toVersionSettingsScreen: () -> Unit,
    toVersionManageScreen: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val ctx = rememberAnimatedContext(isVisible = isVisible)
    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val refreshWardrobe by AccountsManager.refreshWardrobe.collectAsStateWithLifecycle()
    val version by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val isRefreshing by VersionsManager.isRefreshing.collectAsStateWithLifecycle()
    val versions by VersionsManager.versions.collectAsStateWithLifecycle()
    val ready = !isRefreshing && version?.isValid() == true
    var showList by remember { mutableStateOf(false) }

    val chipShape = MaterialTheme.shapes.small

    @Composable
    fun Chip(text: String, highlight: Boolean = false) {
        Surface(
            shape = chipShape,
            color = Color.Black.copy(alpha = 0.45f),
            border = BorderStroke(
                1.dp,
                if (highlight) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                else Color.White.copy(alpha = 0.18f)
            )
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (highlight) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp,
                    color = Color.White.copy(alpha = 0.92f),
                    maxLines = 1
                )
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        //peredam gelap di bagian bawah supaya teks tetap terbaca di atas latar apa pun
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.35f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.78f)
                    )
                )
        )

        //kemajuan pemasangan komponen launcher (otomatis, di latar belakang)
        ComponentInstallCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                //di atas mini player musik bila musik aktif
                .padding(bottom = if (AllSettings.musicEnabled.state) 84.dp else 16.dp, start = 24.dp, end = 24.dp)
        )

        //kartu akun kecil, pojok kanan atas
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 12.dp)
                .animated(ctx),
            shape = MaterialTheme.shapes.large,
            color = Color.Black.copy(alpha = 0.45f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
            onClick = toAccountManageScreen
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                account?.let {
                    PlayerFace(account = it, avatarSize = 32.dp, refreshKey = refreshWardrobe)
                } ?: Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(R.drawable.ic_add),
                    contentDescription = null,
                    tint = Color.White
                )
                Column {
                    Text(
                        text = account?.username ?: stringResource(R.string.account_add_new_account),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        maxLines = 1
                    )
                    account?.let {
                        Text(
                            text = getAccountTypeName(it),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 1
                        )
                    }
                }
            }
        }

        //mini player musik (hanya tampil bila musik diaktifkan)
        MusicMiniPlayer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )

        //judul + info di kiri bawah
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(0.62f)
                .padding(start = 24.dp, bottom = 24.dp)
                .animated(ctx),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip(
                    text = stringResource(
                        when {
                            isRefreshing -> R.string.hero_status_loading
                            ready -> R.string.hero_status_ready
                            else -> R.string.hero_status_empty
                        }
                    ),
                    highlight = ready
                )
                if (ready) {
                    version?.getVersionInfo()?.let { info ->
                        Chip(info.minecraftVersion)
                        info.primaryLoader?.let { Chip(it.loader.displayName) }
                    }
                }
            }

            Box {
                Row(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { showList = true },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f, fill = false),
                        text = if (ready) version!!.getVersionName() else stringResource(R.string.hero_empty_title),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (versions.isNotEmpty()) {
                        Icon(
                            modifier = Modifier.padding(start = 6.dp).size(28.dp),
                            painter = painterResource(R.drawable.ic_keyboard_arrow_down),
                            contentDescription = stringResource(R.string.hero_switch_version),
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
                DropdownMenu(
                    border = com.movtery.zalithlauncher.ui.theme.boxyBorder(),
                    expanded = showList,
                    onDismissRequest = { showList = false },
                    modifier = Modifier.width(280.dp),
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    versions.forEach { v ->
                        DropdownMenuItem(
                            text = {
                                CommonVersionInfoLayout(
                                    modifier = Modifier.fillMaxWidth(),
                                    version = v,
                                    iconSize = 28.dp
                                )
                            },
                            onClick = {
                                if (version != v) VersionsManager.saveVersion(v)
                                showList = false
                            }
                        )
                    }
                    androidx.compose.material3.HorizontalDivider()
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = stringResource(R.string.hero_manage_versions),
                                fontWeight = FontWeight.SemiBold,
                                color = NevRed.GradientStart
                            )
                        },
                        onClick = {
                            showList = false
                            toVersionManageScreen()
                        }
                    )
                }
            }

            Text(
                text = if (ready) "MINECRAFT JAVA" else stringResource(R.string.hero_empty_desc),
                style = if (ready) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodyMedium,
                letterSpacing = if (ready) 2.sp else 0.sp,
                fontWeight = if (ready) FontWeight.SemiBold else null,
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onOpenFileManager,
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.4f),
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(R.drawable.ic_folder_outlined),
                        contentDescription = null
                    )
                    Text(
                        modifier = Modifier.padding(start = 6.dp),
                        text = stringResource(R.string.hero_open_folder),
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 1.sp
                    )
                }
                IconButton(
                    onClick = { VersionsManager.refresh("[HeroHome] manual refresh") },
                    enabled = !isRefreshing
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_refresh),
                        contentDescription = stringResource(R.string.hero_refresh),
                        tint = Color.White
                    )
                }
            }
        }

        //kanan bawah: pengaturan versi + tombol Main besar
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 28.dp)
                .animated(ctx),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (ready) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = Color.Black.copy(alpha = 0.45f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
                    onClick = toVersionSettingsScreen
                ) {
                    Icon(
                        modifier = Modifier.padding(14.dp).size(22.dp),
                        painter = painterResource(R.drawable.ic_settings_filled),
                        contentDescription = stringResource(R.string.versions_manage_settings),
                        tint = Color.White
                    )
                }
            }
            NevGradientButton(onClick = { onLaunchGame(null) }) {
                Row(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(26.dp),
                        painter = painterResource(R.drawable.ic_play_arrow_filled),
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(R.string.main_play).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

/** Kartu kemajuan pemasangan komponen; hilang sendiri setelah semuanya terpasang. */
@Composable
private fun ComponentInstallCard(modifier: Modifier = Modifier) {
    val installer = com.movtery.zalithlauncher.components.ComponentInstaller
    val ready by installer.ready.collectAsStateWithLifecycle()
    val running by installer.running.collectAsStateWithLifecycle()
    val failed by installer.failed.collectAsStateWithLifecycle()
    val progress by installer.progress.collectAsStateWithLifecycle()
    val current by installer.currentName.collectAsStateWithLifecycle()

    androidx.compose.animation.AnimatedVisibility(visible = !ready && progress.second > 0, modifier = modifier) {
        val accent = NevRed.GradientStart
        Surface(
            modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth(),
            shape = nevShape(16.dp),
            color = Color.Black.copy(alpha = 0.6f),
            border = BorderStroke(1.dp, accent.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = when {
                            failed && !running -> stringResource(R.string.components_failed)
                            else -> stringResource(R.string.components_installing)
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${progress.first} / ${progress.second}",
                        style = MaterialTheme.typography.labelMedium,
                        color = accent,
                        fontWeight = FontWeight.Bold
                    )
                    if (failed && !running) {
                        androidx.compose.material3.TextButton(onClick = { installer.start() }) {
                            Text(stringResource(R.string.generic_retry), color = accent)
                        }
                    }
                }
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { if (progress.second == 0) 0f else progress.first / progress.second.toFloat() },
                    modifier = Modifier.fillMaxWidth(),
                    color = accent,
                    trackColor = Color.White.copy(alpha = 0.15f)
                )
                current?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}


/** Kata waktu untuk salam: pagi / siang / sore / malam. */
@Composable
private fun greetingPart(): String {
    //diperbarui tiap menit supaya kata salam ikut berganti saat jam berubah
    val hour by produceState(java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) {
        while (true) {
            value = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
            kotlinx.coroutines.delay(60_000L)
        }
    }
    return stringResource(
        when (hour) {
            in 4..10 -> R.string.dash_part_morning
            in 11..14 -> R.string.dash_part_noon
            in 15..17 -> R.string.dash_part_afternoon
            else -> R.string.dash_part_night
        }
    )
}

/**
 * Beranda dashboard: salam di atas; kiri banner "MINECRAFT JAVA" + kartu Waktu Bermain;
 * kanan kartu akun + Main + pilih versi, lalu daftar Paket & Versi. Warna tetap mengikuti tema (Nev Lava).
 */
@Composable
private fun DashboardHome(
    isVisible: Boolean,
    onLaunchGame: (Version?) -> Unit,
    toAccountManageScreen: () -> Unit,
    toVersionManageScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = rememberAnimatedContext(isVisible = isVisible)
    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val refreshWardrobe by AccountsManager.refreshWardrobe.collectAsStateWithLifecycle()
    val version by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val isRefreshing by VersionsManager.isRefreshing.collectAsStateWithLifecycle()
    val versions by VersionsManager.versions.collectAsStateWithLifecycle()
    val ready = !isRefreshing && version?.isValid() == true
    var showList by remember { mutableStateOf(false) }
    val primary = MaterialTheme.colorScheme.primary

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 12.dp)
                .animated(ctx),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                //kiri: banner + waktu bermain
                Column(
                    modifier = Modifier.weight(1.5f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BackgroundCard(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        0f to primary.copy(alpha = 0.40f),
                                        1f to Color.Transparent
                                    )
                                )
                        ) {
                            //skala teks mengikuti ukuran banner (ikut ukuran launcher), supaya tidak kotak besar dengan teks kecil
                            val s = minOf(maxHeight / 190.dp, maxWidth / 520.dp).coerceIn(1f, 2.4f)
                            val unset = androidx.compose.ui.unit.TextUnit.Unspecified
                            Column(
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(horizontal = 18.dp * s, vertical = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp * s)
                            ) {
                                Text(
                                    text = stringResource(R.string.dash_greeting, greetingPart()),
                                    style = MaterialTheme.typography.labelLarge.copy(fontSize = (14f * s).sp, lineHeight = unset),
                                    modifier = Modifier.alpha(0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "MINECRAFT JAVA",
                                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = (28f * s).sp, lineHeight = unset),
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = (2f * s).sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = stringResource(R.string.dash_tagline_1),
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = (16f * s).sp, lineHeight = unset),
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                                Text(
                                    text = stringResource(R.string.dash_tagline_2),
                                    style = MaterialTheme.typography.titleMedium.copy(fontSize = (16f * s).sp, lineHeight = unset),
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                OutlinedButton(
                                    onClick = { onLaunchGame(null) },
                                    shape = MaterialTheme.shapes.small,
                                    border = BorderStroke(1.dp, primary),
                                    contentPadding = PaddingValues(horizontal = 16.dp * s, vertical = 6.dp * s)
                                ) {
                                    Text(
                                        text = stringResource(R.string.dash_start_playing),
                                        style = MaterialTheme.typography.labelLarge.copy(fontSize = (14f * s).sp, lineHeight = unset)
                                    )
                                }
                            }
                        }
                    }

                    com.movtery.zalithlauncher.ui.screens.content.home.PlayTimeStatsCard(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        compact = true
                    )
                }

                //kanan: akun + Main + versi, lalu Paket & Versi
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    BackgroundCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(MaterialTheme.shapes.medium)
                                    .clickable(onClick = toAccountManageScreen),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                account?.let {
                                    PlayerFace(account = it, avatarSize = 34.dp, refreshKey = refreshWardrobe)
                                } ?: Icon(
                                    modifier = Modifier.size(28.dp),
                                    painter = painterResource(R.drawable.ic_add),
                                    contentDescription = null
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = account?.username ?: stringResource(R.string.account_add_new_account),
                                        style = MaterialTheme.typography.titleSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    account?.let {
                                        Text(
                                            modifier = Modifier.alpha(0.7f),
                                            text = getAccountTypeName(it),
                                            style = MaterialTheme.typography.labelSmall,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            NevGradientButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { onLaunchGame(null) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        modifier = Modifier.size(22.dp),
                                        painter = painterResource(R.drawable.ic_play_arrow_filled),
                                        contentDescription = null
                                    )
                                    Text(
                                        modifier = Modifier.padding(start = 6.dp),
                                        text = stringResource(R.string.main_play),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }

                            Box {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.small,
                                    color = Color.Transparent,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)),
                                    onClick = { showList = true }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            modifier = Modifier.weight(1f),
                                            text = if (ready) version!!.getVersionName() else stringResource(R.string.hero_empty_title),
                                            style = MaterialTheme.typography.labelLarge,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Icon(
                                            modifier = Modifier.size(20.dp),
                                            painter = painterResource(R.drawable.ic_keyboard_arrow_down),
                                            contentDescription = stringResource(R.string.hero_switch_version)
                                        )
                                    }
                                }
                                DropdownMenu(
                                    border = com.movtery.zalithlauncher.ui.theme.boxyBorder(),
                                    expanded = showList,
                                    onDismissRequest = { showList = false },
                                    modifier = Modifier.width(260.dp),
                                    shape = MaterialTheme.shapes.extraLarge
                                ) {
                                    versions.forEach { v ->
                                        DropdownMenuItem(
                                            text = {
                                                CommonVersionInfoLayout(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    version = v,
                                                    iconSize = 28.dp
                                                )
                                            },
                                            onClick = {
                                                if (version != v) VersionsManager.saveVersion(v)
                                                showList = false
                                            }
                                        )
                                    }
                                    androidx.compose.material3.HorizontalDivider()
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = stringResource(R.string.hero_manage_versions),
                                                fontWeight = FontWeight.SemiBold,
                                                color = NevRed.GradientStart
                                            )
                                        },
                                        onClick = {
                                            showList = false
                                            toVersionManageScreen()
                                        }
                                    )
                                }
                            }
                        }
                    }

                    //tinggi kartu mengikuti jumlah instance (tidak memenuhi sisa layar), tetap bisa discroll bila banyak
                    BackgroundCard(
                        modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                        shape = MaterialTheme.shapes.extraLarge
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.dash_packs_versions),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Column(
                                modifier = Modifier
                                    .weight(1f, fill = false)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                versions.forEach { v ->
                                    val selected = version == v
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CommonVersionInfoLayout(
                                            modifier = Modifier.weight(1f),
                                            version = v,
                                            iconSize = 28.dp
                                        )
                                        OutlinedButton(
                                            onClick = { if (!selected) VersionsManager.saveVersion(v) },
                                            shape = MaterialTheme.shapes.small,
                                            border = BorderStroke(1.dp, if (selected) primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = stringResource(if (selected) R.string.dash_selected else R.string.dash_choose),
                                                style = MaterialTheme.typography.labelMedium,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //kemajuan pemasangan komponen + mini player musik tetap ada
        ComponentInstallCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (AllSettings.musicEnabled.state) 84.dp else 16.dp, start = 24.dp, end = 24.dp)
        )
        MusicMiniPlayer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )
    }
}


/** Kartu kaca beranda Futuristik: judul ber-ikon bulat emas + isi. */
@Composable
private fun FuturisticCard(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    BackgroundCard(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        content = content
    )
}

@Composable
private fun FuturisticCardTitle(text: String, modifier: Modifier = Modifier, trailing: @Composable () -> Unit = {}) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        trailing()
    }
}

/**
 * Beranda tema Futuristik: kartu sambutan + kartu versi dengan tombol MAIN emas di atas,
 * lalu daftar versi, akun, dan statistik waktu bermain. Dapat digulir ke bawah.
 */
@Composable
private fun FuturisticHome(
    isVisible: Boolean,
    onLaunchGame: (Version?) -> Unit,
    toAccountManageScreen: () -> Unit,
    toVersionSettingsScreen: () -> Unit,
    toVersionManageScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val ctx = rememberAnimatedContext(isVisible = isVisible)
    val account by AccountsManager.currentAccountFlow.collectAsStateWithLifecycle()
    val refreshWardrobe by AccountsManager.refreshWardrobe.collectAsStateWithLifecycle()
    val version by VersionsManager.currentVersion.collectAsStateWithLifecycle()
    val isRefreshing by VersionsManager.isRefreshing.collectAsStateWithLifecycle()
    val versions by VersionsManager.versions.collectAsStateWithLifecycle()
    val ready = !isRefreshing && version?.isValid() == true
    val primary = MaterialTheme.colorScheme.primary
    val chipShape = com.movtery.zalithlauncher.ui.theme.nevPillShape()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 8.dp, end = 12.dp, top = 4.dp, bottom = 12.dp)
                .animated(ctx),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            //baris atas: sambutan + versi Minecraft
            Row(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FuturisticCard(modifier = Modifier.weight(1.6f).fillMaxHeight()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.fut_welcome),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Minecraft Java",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            modifier = Modifier.alpha(0.8f),
                            text = stringResource(R.string.fut_subtitle),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (ready) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                version?.getVersionInfo()?.let { info ->
                                    listOf(
                                        version!!.getVersionName(),
                                        info.primaryLoader?.loader?.displayName
                                    ).filterNotNull().forEach { label ->
                                        Surface(
                                            shape = chipShape,
                                            color = Color.Transparent,
                                            border = BorderStroke(1.dp, NevRed.Outline)
                                        ) {
                                            Text(
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                                text = label,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.SemiBold,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        NevGradientButton(onClick = { onLaunchGame(null) }) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 9.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    modifier = Modifier.size(20.dp),
                                    painter = painterResource(R.drawable.ic_play_arrow_filled),
                                    contentDescription = null
                                )
                                Text(
                                    text = stringResource(R.string.fut_play_now),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                FuturisticCard(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FuturisticCardTitle(stringResource(R.string.fut_version_card))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            version?.let { v ->
                                CommonVersionInfoLayout(
                                    modifier = Modifier.weight(1f),
                                    version = v,
                                    iconSize = 40.dp
                                )
                            } ?: Text(
                                modifier = Modifier.weight(1f),
                                text = stringResource(R.string.hero_empty_title),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            NevGradientButton(
                                modifier = Modifier.weight(1f),
                                onClick = { onLaunchGame(null) }
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        modifier = Modifier.size(18.dp),
                                        painter = painterResource(R.drawable.ic_play_arrow_filled),
                                        contentDescription = null
                                    )
                                    Text(
                                        modifier = Modifier.padding(start = 6.dp),
                                        text = stringResource(R.string.main_play).uppercase(),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                            if (ready) {
                                Surface(
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    color = Color.Transparent,
                                    border = BorderStroke(1.5.dp, primary),
                                    onClick = toVersionSettingsScreen
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(9.dp).size(20.dp),
                                        painter = painterResource(R.drawable.ic_settings_filled),
                                        contentDescription = stringResource(R.string.versions_manage_settings)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            //baris tengah: daftar versi + akun
            Row(
                modifier = Modifier.fillMaxWidth().height(208.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FuturisticCard(modifier = Modifier.weight(1.4f).fillMaxHeight()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FuturisticCardTitle(stringResource(R.string.fut_your_versions)) {
                            Text(
                                modifier = Modifier
                                    .clip(chipShape)
                                    .clickable(onClick = toVersionManageScreen)
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                text = stringResource(R.string.fut_see_all) + "  \u203A",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = primary,
                                maxLines = 1
                            )
                        }
                        versions.take(3).forEach { v ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CommonVersionInfoLayout(
                                    modifier = Modifier.weight(1f),
                                    version = v,
                                    iconSize = 32.dp
                                )
                                Surface(
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    color = Color.Transparent,
                                    border = BorderStroke(1.5.dp, primary),
                                    onClick = {
                                        if (version != v) VersionsManager.saveVersion(v)
                                        onLaunchGame(v)
                                    }
                                ) {
                                    Icon(
                                        modifier = Modifier.padding(8.dp).size(20.dp),
                                        painter = painterResource(R.drawable.ic_play_arrow_filled),
                                        contentDescription = stringResource(R.string.main_play)
                                    )
                                }
                            }
                        }
                    }
                }

                FuturisticCard(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FuturisticCardTitle(stringResource(R.string.fut_account))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            account?.let {
                                PlayerFace(account = it, avatarSize = 44.dp, refreshKey = refreshWardrobe)
                            } ?: Icon(
                                modifier = Modifier.size(32.dp),
                                painter = painterResource(R.drawable.ic_add),
                                contentDescription = null
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = account?.username ?: stringResource(R.string.account_add_new_account),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                account?.let {
                                    Text(
                                        modifier = Modifier.alpha(0.75f),
                                        text = getAccountTypeName(it),
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        OutlinedButton(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = toAccountManageScreen,
                            border = BorderStroke(1.dp, primary)
                        ) {
                            Text(
                                text = stringResource(R.string.fut_manage_account),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            //waktu bermain
            com.movtery.zalithlauncher.ui.screens.content.home.PlayTimeStatsCard(
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(if (AllSettings.musicEnabled.state) 72.dp else 0.dp))
        }

        ComponentInstallCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = if (AllSettings.musicEnabled.state) 84.dp else 16.dp, start = 24.dp, end = 24.dp)
        )
        MusicMiniPlayer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp)
        )
    }
}
