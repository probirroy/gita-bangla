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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
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
 * Animated devotional audio pulse waveform bars.
 */
@Composable
fun VerseAudioWaveform(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "verse_audio_wave")
    val barDelays = listOf(350, 500, 280, 620, 420)
    val animations = barDelays.mapIndexed { index, dur ->
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = if (isPlaying) 1.0f else 0.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = dur, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "wave_bar_$index"
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        animations.forEach { anim ->
            val height = if (isPlaying) (6 + anim.value * 14).dp else 6.dp
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(height)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isPlaying) SaffronPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
            )
        }
    }
}

/**
 * VerseAudioPlayer component using an AudioStateManager that allows playing,
 * pausing, seeking, and switching recitation audio modes linked to specific Bengali verses.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerseAudioPlayer(
    verse: Verse,
    audioStateManager: AudioStateManager,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    showVerseInfo: Boolean = true,
    showModeSelector: Boolean = true
) {
    val context = LocalContext.current
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

    var isUserSeeking by remember { mutableStateOf(false) }
    var userSeekPos by remember { mutableFloatStateOf(0f) }

    val effectivePosition = if (isUserSeeking) userSeekPos else (if (isThisVerseActive) currentPosition else 0f)
    val effectiveDuration = if (isThisVerseActive) duration.coerceAtLeast(1f) else audioFile.estimatedDurationSeconds
    val sliderValue = (effectivePosition / effectiveDuration).coerceIn(0f, 1f)

    val containerBg by animateColorAsState(
        targetValue = if (isThisVersePlaying) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
        } else if (isThisVersePaused) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        } else {
            MaterialTheme.colorScheme.surface
        },
        label = "audio_player_bg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("verse_audio_player")
            .testTag("verse_audio_player_card")
            .testTag("audio_player_component"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        border = com.example.ui.theme.blueCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isThisVersePlaying) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (isCompact) 12.dp else 16.dp)
        ) {
            // Header: Verse info & Audio status badge
            if (showVerseInfo) {
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (isThisVersePlaying) SaffronPrimary else MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isThisVersePlaying) Icons.Default.GraphicEq else Icons.Default.Headphones,
                                contentDescription = null,
                                tint = if (isThisVersePlaying) Color.White else SaffronPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = audioFile.audioFileName,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when {
                            isThisVersePlaying -> SaffronPrimary.copy(alpha = 0.15f)
                            isThisVersePaused -> MaterialTheme.colorScheme.secondaryContainer
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        },
                        modifier = Modifier.testTag("verse_audio_status_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            VerseAudioWaveform(isPlaying = isThisVersePlaying)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = when {
                                    isThisVersePlaying -> "আবৃত্তি চলছে"
                                    isThisVersePaused -> "স্থগিত"
                                    else -> "প্রস্তুত"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isThisVersePlaying) SaffronPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bangla Shlok display linked to this audio file
                Surface(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "॥ ${verse.pronunciationBengali} ॥",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        maxLines = if (isCompact) 2 else 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Seek Slider & Time display
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = sliderValue,
                    onValueChange = { fraction ->
                        isUserSeeking = true
                        userSeekPos = fraction * effectiveDuration
                    },
                    onValueChangeFinished = {
                        isUserSeeking = false
                        audioStateManager.seekTo(userSeekPos)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = SaffronPrimary,
                        activeTrackColor = SaffronPrimary,
                        inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                        .testTag("verse_audio_seek_slider")
                        .testTag("verse_audio_seek_bar")
                        .testTag("progress_seek_bar")
                        .testTag("audio_seek_bar")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatAudioTime(effectivePosition),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${formatAudioTime(effectivePosition)} / ${formatAudioTime(effectiveDuration)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.testTag("verse_audio_time_text")
                    )
                    Text(
                        text = formatAudioTime(effectiveDuration),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Audio Control Buttons (Rewind -5s, Play/Pause, Fast Forward +5s, Stop, Loop)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Loop / Repeat toggle
                IconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        audioStateManager.toggleLoop()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("verse_audio_loop_button")
                ) {
                    Icon(
                        imageVector = if (isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = if (isLooping) "পুনরাবৃত্তি বন্ধ করুন" else "পুনরাবৃত্তি চালু করুন",
                        tint = if (isLooping) SaffronPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Rewind 5s
                IconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        audioStateManager.seekBy(-5f)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("verse_audio_rewind_button")
                        .testTag("verse_audio_rewind_5s")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastRewind,
                        contentDescription = "৫ সেকেন্ড পেছনে",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Primary Play / Pause button
                FilledIconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        audioStateManager.togglePlayPause(verse)
                    },
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = SaffronPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .size(54.dp)
                        .testTag("verse_audio_play_pause_button")
                        .testTag("play_pause_button")
                        .testTag("play_daily_verse_audio_button")
                        .testTag("verse_of_the_day_audio_button")
                        .testTag(if (isThisVersePlaying) "verse_audio_pause_button" else "verse_audio_play_button")
                ) {
                    Icon(
                        imageVector = if (isThisVersePlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isThisVersePlaying) "আবৃত্তি থামান" else "আবৃত্তি চালান",
                        modifier = Modifier.size(30.dp)
                    )
                }

                // Fast Forward 5s
                IconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        audioStateManager.seekBy(5f)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("verse_audio_forward_button")
                        .testTag("verse_audio_forward_5s")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "৫ সেকেন্ড সামনে",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Stop button
                IconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        audioStateManager.stop()
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("verse_audio_stop_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "বন্ধ করুন",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Recitation Mode Selector Chips
            if (showModeSelector && !isCompact) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = recitationMode == RecitationMode.SHLOKA_ONLY,
                        onClick = {
                            HapticHelper.performClick(null)
                            audioStateManager.setRecitationMode(RecitationMode.SHLOKA_ONLY)
                            if (isThisVersePlaying) audioStateManager.play(verse, RecitationMode.SHLOKA_ONLY)
                        },
                        label = { Text("শ্লোক আবৃত্তি", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("recitation_mode_shloka_chip")
                    )

                    FilterChip(
                        selected = recitationMode == RecitationMode.TRANSLATION_ONLY,
                        onClick = {
                            HapticHelper.performClick(null)
                            audioStateManager.setRecitationMode(RecitationMode.TRANSLATION_ONLY)
                            if (isThisVersePlaying) audioStateManager.play(verse, RecitationMode.TRANSLATION_ONLY)
                        },
                        label = { Text("সরল বঙ্গানুবাদ", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("recitation_mode_translation_chip")
                    )

                    FilterChip(
                        selected = recitationMode == RecitationMode.BOTH,
                        onClick = {
                            HapticHelper.performClick(null)
                            audioStateManager.setRecitationMode(RecitationMode.BOTH)
                            if (isThisVersePlaying) audioStateManager.play(verse, RecitationMode.BOTH)
                        },
                        label = { Text("উভয়ই", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SaffronPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("recitation_mode_both_chip")
                    )
                }
            }
        }
    }
}

/**
 * Dedicated VerseCardAudioPlayer component embedded into each verse card.
 * Enables users to listen to the recitation of the selected Bhagavad Gita verse,
 * scrub via seekbar, toggle playback modes (শ্লোক / বঙ্গানুবাদ / উভয়ই), rewind/forward 5s,
 * and view live animated audio waveforms.
 */
@Composable
fun VerseCardAudioPlayer(
    verse: Verse,
    audioStateManager: AudioStateManager,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val playbackState by audioStateManager.playbackState.collectAsState()
    val playingVerseId by audioStateManager.currentPlayingVerseId.collectAsState()
    val currentPosition by audioStateManager.currentPositionSeconds.collectAsState()
    val duration by audioStateManager.durationSeconds.collectAsState()
    val recitationMode by audioStateManager.recitationMode.collectAsState()
    val isLooping by audioStateManager.isLooping.collectAsState()

    val isThisVersePlaying = playingVerseId == verse.verseId && playbackState == PlaybackState.PLAYING
    val isThisVersePaused = playingVerseId == verse.verseId && playbackState == PlaybackState.PAUSED
    val isThisVerseActive = playingVerseId == verse.verseId && playbackState != PlaybackState.IDLE

    var isExpandedManually by remember { mutableStateOf(false) }
    val isExpanded = isThisVerseActive || isExpandedManually

    val audioFile = remember(verse) { audioStateManager.getAudioFileForVerse(verse) }

    var isUserSeeking by remember { mutableStateOf(false) }
    var userSeekPos by remember { mutableFloatStateOf(0f) }

    val effectivePosition = if (isUserSeeking) userSeekPos else (if (isThisVerseActive) currentPosition else 0f)
    val effectiveDuration = if (isThisVerseActive) duration.coerceAtLeast(1f) else audioFile.estimatedDurationSeconds
    val sliderValue = (effectivePosition / effectiveDuration).coerceIn(0f, 1f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("verse_card_audio_player_${verse.verseId}")
            .testTag("verse_card_audio_player"),
        shape = RoundedCornerShape(14.dp),
        color = if (isThisVersePlaying) {
            SaffronPrimary.copy(alpha = 0.08f)
        } else if (isThisVersePaused) {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        },
        border = com.example.ui.theme.blueCardBorder(width = if (isThisVersePlaying) 1.5.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            // Header bar: Play/Pause button + Status + Waveform + Expand/Collapse
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    FilledIconButton(
                        onClick = {
                            HapticHelper.performClick(context)
                            audioStateManager.togglePlayPause(verse)
                        },
                        shape = CircleShape,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = if (isThisVersePlaying) SaffronPrimary else MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("card_play_audio_${verse.verseId}")
                            .testTag("verse_card_play_button_${verse.verseId}")
                            .testTag("verse_card_play_pause_button")
                            .testTag("play_pause_button")
                    ) {
                        Icon(
                            imageVector = if (isThisVersePlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isThisVersePlaying) "আবৃত্তি থামান" else "আবৃত্তি শুনুন",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = when {
                                    isThisVersePlaying -> "আবৃত্তি চলছে..."
                                    isThisVersePaused -> "আবৃত্তি স্থগিত"
                                    else -> "শ্লোক আবৃত্তি শুনুন"
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isThisVersePlaying) SaffronPrimary else MaterialTheme.colorScheme.onSurface
                            )
                            if (isThisVersePlaying) {
                                Spacer(modifier = Modifier.width(6.dp))
                                VerseAudioWaveform(isPlaying = true)
                            }
                        }
                        Text(
                            text = if (isThisVerseActive) {
                                "${formatAudioTime(effectivePosition)} / ${formatAudioTime(effectiveDuration)}"
                            } else {
                                "সময়সীমা: ${formatAudioTime(audioFile.estimatedDurationSeconds)}"
                            },
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Mode quick indicator
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Text(
                            text = when (recitationMode) {
                                RecitationMode.SHLOKA_ONLY -> "শ্লোক"
                                RecitationMode.TRANSLATION_ONLY -> "অনুবাদ"
                                RecitationMode.BOTH -> "উভয়ই"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            HapticHelper.performClick(context)
                            isExpandedManually = !isExpandedManually
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("toggle_expand_audio_${verse.verseId}")
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = if (isExpanded) "প্লেয়ার সংকোচন করুন" else "প্লেয়ার বিস্তারিত দেখুন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Expanded / Active Controls: Seek slider, Rewind/Forward, Stop, Mode Selector
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    // Seek Slider
                    Slider(
                        value = sliderValue,
                        onValueChange = { fraction ->
                            isUserSeeking = true
                            userSeekPos = fraction * effectiveDuration
                        },
                        onValueChangeFinished = {
                            isUserSeeking = false
                            audioStateManager.seekTo(userSeekPos)
                        },
                        colors = SliderDefaults.colors(
                            thumbColor = SaffronPrimary,
                            activeTrackColor = SaffronPrimary,
                            inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                            .testTag("seek_slider_${verse.verseId}")
                            .testTag("verse_card_seek_bar")
                            .testTag("progress_seek_bar")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatAudioTime(effectivePosition),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatAudioTime(effectiveDuration),
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Control buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Loop toggle
                        IconButton(
                            onClick = {
                                HapticHelper.performClick(context)
                                audioStateManager.toggleLoop()
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("loop_audio_${verse.verseId}")
                        ) {
                            Icon(
                                imageVector = if (isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                contentDescription = "পুনরাবৃত্তি",
                                tint = if (isLooping) SaffronPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Rewind 5s
                        IconButton(
                            onClick = {
                                HapticHelper.performClick(context)
                                audioStateManager.seekBy(-5f)
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("rewind_5s_${verse.verseId}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastRewind,
                                contentDescription = "৫ সেকেন্ড পেছনে",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Stop
                        IconButton(
                            onClick = {
                                HapticHelper.performClick(context)
                                audioStateManager.stop()
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("stop_audio_${verse.verseId}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "বন্ধ করুন",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Fast Forward 5s
                        IconButton(
                            onClick = {
                                HapticHelper.performClick(context)
                                audioStateManager.seekBy(5f)
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("forward_5s_${verse.verseId}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.FastForward,
                                contentDescription = "৫ সেকেন্ড সামনে",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Mode Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val modes = listOf(
                            RecitationMode.SHLOKA_ONLY to "শ্লোক",
                            RecitationMode.TRANSLATION_ONLY to "অনুবাদ",
                            RecitationMode.BOTH to "উভয়ই"
                        )
                        modes.forEach { (mode, label) ->
                            FilterChip(
                                selected = recitationMode == mode,
                                onClick = {
                                    HapticHelper.performClick(context)
                                    audioStateManager.setRecitationMode(mode)
                                    if (isThisVersePlaying) audioStateManager.play(verse, mode)
                                },
                                label = { Text(label, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SaffronPrimary,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .testTag("mode_${mode.name.lowercase()}_${verse.verseId}")
                            )
                        }
                    }
                }
            }
        }
    }
}

