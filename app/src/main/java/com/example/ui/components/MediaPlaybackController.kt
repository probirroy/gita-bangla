package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioStateManager
import com.example.audio.PlaybackState
import com.example.audio.RecitationMode
import com.example.data.model.Verse
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LotusTeal
import com.example.ui.theme.SaffronPrimary
import com.example.util.HapticHelper

/**
 * Animated audio pulse waveform bars for media playback.
 */
@Composable
fun MediaPlaybackWaveform(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 5,
    activeColor: Color = SaffronPrimary,
    inactiveColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "media_playback_wave")
    val barDelays = listOf(320, 520, 260, 640, 400, 360, 480).take(barCount)
    val animations = barDelays.mapIndexed { index, dur ->
        infiniteTransition.animateFloat(
            initialValue = 0.25f,
            targetValue = if (isPlaying) 1.0f else 0.25f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = dur, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "media_wave_bar_$index"
        )
    }

    Row(
        modifier = modifier.testTag("playback_waveform"),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        animations.forEach { anim ->
            val height = if (isPlaying) (6 + anim.value * 16).dp else 6.dp
            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(height)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isPlaying) activeColor else inactiveColor)
            )
        }
    }
}

/**
 * Media Playback Controller component connected to AudioStateManager.
 * Provides play, pause, seek, jump forward/rewind, looping, and recitation modes
 * for listening to Bengali verse recitations.
 */
@Composable
fun MediaPlaybackController(
    verse: Verse,
    audioStateManager: AudioStateManager,
    modifier: Modifier = Modifier,
    showVerseHeader: Boolean = true,
    showRecitationModes: Boolean = true,
    onClose: (() -> Unit)? = null
) {
    val playbackState by audioStateManager.playbackState.collectAsState()
    val playingVerseId by audioStateManager.currentPlayingVerseId.collectAsState()
    val currentPosition by audioStateManager.currentPositionSeconds.collectAsState()
    val duration by audioStateManager.durationSeconds.collectAsState()
    val recitationMode by audioStateManager.recitationMode.collectAsState()
    val isLooping by audioStateManager.isLooping.collectAsState()

    val isThisVersePlaying = playingVerseId == verse.verseId && playbackState == PlaybackState.PLAYING
    val isThisVersePaused = playingVerseId == verse.verseId && playbackState == PlaybackState.PAUSED
    val isThisVerseActive = playingVerseId == verse.verseId && playbackState != PlaybackState.IDLE

    val audioFile = remember(verse) { audioStateManager.getAudioFileForVerse(verse) }
    val effectivePosition = if (isThisVerseActive) currentPosition else 0f
    val effectiveDuration = if (isThisVerseActive && duration > 0f) duration else audioFile.estimatedDurationSeconds

    MediaPlaybackController(
        isPlaying = isThisVersePlaying,
        isPaused = isThisVersePaused,
        currentPosition = effectivePosition,
        duration = effectiveDuration,
        verseTitle = "অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber}",
        verseShlokaBengali = "॥ ${verse.pronunciationBengali} ॥",
        audioFileName = audioFile.audioFileName,
        isLooping = isLooping,
        recitationMode = recitationMode,
        showVerseHeader = showVerseHeader,
        showRecitationModes = showRecitationModes,
        onPlayPause = {
            audioStateManager.togglePlayPause(verse)
        },
        onSeek = { targetSeconds ->
            audioStateManager.seekTo(targetSeconds)
        },
        onRewind5s = {
            audioStateManager.seekBy(-5f)
        },
        onForward5s = {
            audioStateManager.seekBy(5f)
        },
        onStop = {
            audioStateManager.stop()
        },
        onToggleLoop = {
            audioStateManager.toggleLoop()
        },
        onRecitationModeChanged = { mode ->
            audioStateManager.setRecitationMode(mode)
            if (isThisVersePlaying) {
                audioStateManager.play(verse, mode)
            }
        },
        onClose = onClose,
        modifier = modifier
    )
}

/**
 * Universal Stateless Media Playback Controller Composable with play, pause, and seek.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPlaybackController(
    isPlaying: Boolean,
    isPaused: Boolean,
    currentPosition: Float,
    duration: Float,
    verseTitle: String,
    verseShlokaBengali: String,
    audioFileName: String,
    onPlayPause: () -> Unit,
    onSeek: (Float) -> Unit,
    onRewind5s: () -> Unit,
    onForward5s: () -> Unit,
    modifier: Modifier = Modifier,
    isLooping: Boolean = false,
    recitationMode: RecitationMode = RecitationMode.BOTH,
    showVerseHeader: Boolean = true,
    showRecitationModes: Boolean = true,
    onStop: (() -> Unit)? = null,
    onToggleLoop: (() -> Unit)? = null,
    onRecitationModeChanged: ((RecitationMode) -> Unit)? = null,
    onClose: (() -> Unit)? = null
) {
    val context = LocalContext.current
    var isUserSeeking by remember { mutableStateOf(false) }
    var userSeekPos by remember { mutableFloatStateOf(0f) }

    val safeDuration = duration.coerceAtLeast(1f)
    val displayPosition = if (isUserSeeking) userSeekPos else currentPosition
    val sliderValue = (displayPosition / safeDuration).coerceIn(0f, 1f)

    val containerColor by animateColorAsState(
        targetValue = if (isPlaying) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        } else if (isPaused) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "media_controller_bg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("media_playback_controller")
            .testTag("verse_media_playback_controller"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(
            width = if (isPlaying) 1.5.dp else 1.dp,
            color = if (isPlaying) SaffronPrimary.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlaying) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title, Waveform Status Badge, and optional Close
            if (showVerseHeader) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (isPlaying) SaffronPrimary else MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.GraphicEq else Icons.Default.Headphones,
                                contentDescription = null,
                                tint = if (isPlaying) Color.White else SaffronPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = verseTitle,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = audioFileName,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Playback Status Indicator with animated waveform
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = when {
                                isPlaying -> SaffronPrimary.copy(alpha = 0.15f)
                                isPaused -> MaterialTheme.colorScheme.secondaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            },
                            modifier = Modifier.testTag("playback_status_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MediaPlaybackWaveform(isPlaying = isPlaying)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when {
                                        isPlaying -> "আবৃত্তি চলছে"
                                        isPaused -> "স্থগিত"
                                        else -> "প্রস্তুত"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isPlaying) SaffronPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        if (onClose != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            IconButton(
                                onClick = onClose,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("playback_close_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "বন্ধ করুন",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                if (verseShlokaBengali.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = verseShlokaBengali,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Seek Slider and Timing display
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = sliderValue,
                    onValueChange = { fraction ->
                        isUserSeeking = true
                        userSeekPos = fraction * safeDuration
                    },
                    onValueChangeFinished = {
                        isUserSeeking = false
                        onSeek(userSeekPos)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = SaffronPrimary,
                        activeTrackColor = SaffronPrimary,
                        inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .testTag("playback_seek_slider")
                        .testTag("media_seek_slider")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatAudioTime(displayPosition),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("playback_current_time")
                    )

                    Text(
                        text = "${formatAudioTime(displayPosition)} / ${formatAudioTime(safeDuration)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("playback_time_ratio")
                    )

                    Text(
                        text = formatAudioTime(safeDuration),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("playback_duration_time")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Media Playback Controls: Loop, Rewind 5s, Play/Pause, Fast-Forward 5s, Stop
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Loop / Repeat Toggle
                IconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        onToggleLoop?.invoke()
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("playback_loop_button")
                ) {
                    Icon(
                        imageVector = if (isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = if (isLooping) "পুনরাবৃত্তি বন্ধ করুন" else "পুনরাবৃত্তি চালু করুন",
                        tint = if (isLooping) SaffronPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // 5-Second Rewind
                IconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        onRewind5s()
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("playback_rewind_button")
                        .testTag("playback_rewind_5s")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "৫ সেকেন্ড পেছনে",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Primary Play / Pause Button
                FilledIconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        onPlayPause()
                    },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = SaffronPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("playback_play_pause_button")
                        .testTag(if (isPlaying) "playback_pause_button" else "playback_play_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "আবৃত্তি থামান" else "আবৃত্তি চালান",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // 5-Second Fast Forward
                IconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        onForward5s()
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("playback_forward_button")
                        .testTag("playback_forward_5s")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "৫ সেকেন্ড সামনে",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Stop Button
                IconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        onStop?.invoke()
                    },
                    modifier = Modifier
                        .size(46.dp)
                        .testTag("playback_stop_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "বন্ধ করুন",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Recitation Mode Selector Chips
            if (showRecitationModes && onRecitationModeChanged != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("playback_mode_chips"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = recitationMode == RecitationMode.SHLOKA_ONLY,
                        onClick = {
                            HapticHelper.performClick(null)
                            onRecitationModeChanged(RecitationMode.SHLOKA_ONLY)
                        },
                        label = { Text("শ্লোক আবৃত্তি", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("playback_mode_shloka_chip")
                    )

                    FilterChip(
                        selected = recitationMode == RecitationMode.TRANSLATION_ONLY,
                        onClick = {
                            HapticHelper.performClick(null)
                            onRecitationModeChanged(RecitationMode.TRANSLATION_ONLY)
                        },
                        label = { Text("সরল বঙ্গানুবাদ", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("playback_mode_translation_chip")
                    )

                    FilterChip(
                        selected = recitationMode == RecitationMode.BOTH,
                        onClick = {
                            HapticHelper.performClick(null)
                            onRecitationModeChanged(RecitationMode.BOTH)
                        },
                        label = { Text("উভয়ই", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("playback_mode_both_chip")
                    )
                }
            }
        }
    }
}
