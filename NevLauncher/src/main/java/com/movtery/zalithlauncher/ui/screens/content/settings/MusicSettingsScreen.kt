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

package com.movtery.zalithlauncher.ui.screens.content.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.movtery.zalithlauncher.ui.theme.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.music.MusicManager
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.base.BaseScreen
import com.movtery.zalithlauncher.ui.components.AnimatedColumn
import com.movtery.zalithlauncher.ui.components.TitleAndSummary
import com.movtery.zalithlauncher.ui.components.animated
import com.movtery.zalithlauncher.ui.components.verticalScrollWithBar
import com.movtery.zalithlauncher.ui.screens.NestedNavKey
import com.movtery.zalithlauncher.ui.screens.NormalNavKey
import com.movtery.zalithlauncher.ui.screens.TitledNavKey
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.CardPosition
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.IntSliderSettingsCard
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.SettingsCard
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.SettingsCardColumn
import com.movtery.zalithlauncher.ui.screens.content.settings.layouts.SwitchSettingsCard

@Composable
fun MusicSettingsScreen(
    key: NestedNavKey.Settings,
    settingsScreenKey: TitledNavKey?,
    mainScreenKey: TitledNavKey?
) {
    BaseScreen(
        Triple(key, mainScreenKey, false),
        Triple(NormalNavKey.Settings.Music, settingsScreenKey, false)
    ) { isVisible ->
        val enabled = AllSettings.musicEnabled.state

        LaunchedEffect(Unit) { MusicManager.refreshTracks() }

        AnimatedColumn(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScrollWithBar(state = rememberScrollState())
                .padding(all = 12.dp),
            isVisible = isVisible
        ) {
            AnimatedItem { ctx ->
                SettingsCardColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animated(ctx)
                ) {
                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Top,
                        checked = enabled,
                        onCheckedChange = {
                            AllSettings.musicEnabled.save(it)
                            MusicManager.onForeground(inGame = false)
                            MusicManager.onEnabledChanged(it)
                        },
                        title = stringResource(R.string.music_enable_title),
                        summary = stringResource(R.string.music_enable_summary)
                    )

                    IntSliderSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        unit = AllSettings.musicVolume,
                        onValueChange = { MusicManager.setVolume(it) },
                        title = stringResource(R.string.music_volume_title),
                        valueRange = 0f..100f,
                        suffix = "%",
                        enabled = enabled
                    )

                    SwitchSettingsCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        checked = AllSettings.musicInGame.state,
                        onCheckedChange = { AllSettings.musicInGame.save(it) },
                        title = stringResource(R.string.music_in_game_title),
                        summary = stringResource(R.string.music_in_game_summary),
                        enabled = enabled
                    )

                    PlayerControlCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Middle,
                        enabled = enabled
                    )

                    PlaylistCard(
                        modifier = Modifier.fillMaxWidth(),
                        position = CardPosition.Bottom,
                        enabled = enabled
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerControlCard(
    position: CardPosition,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val current by MusicManager.current.collectAsState()
    val playing by MusicManager.isPlaying.collectAsState()

    SettingsCard(modifier = modifier, position = position) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.music_now_playing),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = current?.title ?: stringResource(R.string.music_none),
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            IconButton(enabled = enabled, onClick = { MusicManager.previous() }) {
                Icon(painterResource(R.drawable.ic_skip_previous_filled), stringResource(R.string.music_prev))
            }
            IconButton(enabled = enabled, onClick = { MusicManager.togglePlayPause() }) {
                Icon(
                    painterResource(if (playing) R.drawable.ic_pause_filled else R.drawable.ic_play_arrow_filled),
                    stringResource(R.string.music_play_pause)
                )
            }
            IconButton(enabled = enabled, onClick = { MusicManager.next() }) {
                Icon(painterResource(R.drawable.ic_skip_next_filled), stringResource(R.string.music_next))
            }
        }
    }
}

@Composable
private fun PlaylistCard(
    position: CardPosition,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val tracks by MusicManager.tracks.collectAsState()
    val current by MusicManager.current.collectAsState()

    SettingsCard(modifier = modifier, position = position) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TitleAndSummary(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.music_playlist_title),
                    summary = stringResource(
                        R.string.music_playlist_summary,
                        MusicManager.userMusicDir().absolutePath
                    )
                )
                TextButton(onClick = { MusicManager.refreshTracks() }) {
                    Text(stringResource(R.string.music_refresh))
                }
            }

            if (tracks.isEmpty()) {
                Text(
                    text = stringResource(R.string.music_empty_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().height(300.dp)) {
                    items(tracks, key = { it.id }) { track ->
                        val selected = track.id == current?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = enabled) { MusicManager.select(track) }
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                painter = painterResource(R.drawable.ic_music_note_filled),
                                contentDescription = null,
                                tint = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                modifier = Modifier.weight(1f),
                                text = track.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}
