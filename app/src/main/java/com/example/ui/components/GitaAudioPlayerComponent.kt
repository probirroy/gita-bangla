package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.audio.RecitationMode
import com.example.data.model.Verse
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LotusTeal
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.GitaViewModel
import com.example.util.HapticHelper
import java.util.Locale

fun formatAudioTime(seconds: Float): String {
    val totalSec = seconds.toInt().coerceAtLeast(0)
    val mins = totalSec / 60
    val secs = totalSec % 60
    return String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
}

@Composable
fun AudioWaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "audio_bars")
    val heights = (0..5).map { index ->
        val duration = 400 + index * 120
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = if (isPlaying) 1.0f else 0.25f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = duration, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar_$index"
        )
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        heights.forEachIndexed { idx, animHeight ->
            val barHeight = if (isPlaying) (8 + animHeight.value * 16).dp else 8.dp
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(barHeight)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isPlaying) SaffronPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            )
        }
    }
}

/**
 * Reusable full Audio Player Card with Play/Pause and Seek Controls.
 * Can be embedded in VerseReaderScreen, HomeScreen, or expanded bottom sheet.
 */
@Composable
fun GitaAudioPlayerCard(
    viewModel: GitaViewModel,
    verse: Verse,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val context = LocalContext.current
    val playbackState by viewModel.audioPlayer.playbackState.collectAsState()
    val playingVerseId by viewModel.audioPlayer.currentPlayingVerseId.collectAsState()
    val currentPosition by viewModel.audioCurrentPosition.collectAsState()
    val duration by viewModel.audioDuration.collectAsState()
    val recitationMode by viewModel.recitationMode.collectAsState()
    val isLooping by viewModel.isAudioLooping.collectAsState()
    val settings by viewModel.userSettings.collectAsState()

    val isThisVerseActive = playingVerseId == verse.verseId
    val isPlaying = isThisVerseActive && playbackState == PlaybackState.PLAYING
    val isPaused = isThisVerseActive && playbackState == PlaybackState.PAUSED

    var isUserSeeking by remember { mutableStateOf(false) }
    var userSeekPosition by remember { mutableFloatStateOf(0f) }

    val effectivePosition = if (isUserSeeking) userSeekPosition else (if (isThisVerseActive) currentPosition else 0f)
    val effectiveDuration = if (isThisVerseActive) duration.coerceAtLeast(1f) else 15f
    val progressFraction = (effectivePosition / effectiveDuration).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("gita_audio_player_card")
            .testTag("audio_player_component")
            .testTag("verse_recitation_audio_player"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
        ),
        border = com.example.ui.theme.blueCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Visualizer, Status & Loop Mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isPlaying) SaffronPrimary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.primaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPlaying) {
                            AudioWaveformVisualizer(isPlaying = true)
                        } else {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "আবৃত্তি প্লেয়ার",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isPlaying) LotusTeal.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = when {
                                        isPlaying -> "বাজছে • ${formatAudioTime(effectivePosition)}"
                                        isPaused -> "সাময়িক স্থগিত"
                                        else -> "প্রস্তুত"
                                    },
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPlaying) LotusTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Loop / Japa toggle chip
                FilterChip(
                    selected = isLooping,
                    onClick = {
                        HapticHelper.performTick(context)
                        viewModel.toggleRecitationLoop()
                    },
                    label = {
                        Text(
                            text = if (isLooping) "জপ লুপ চালু" else "লুপ বন্ধ",
                            fontSize = 10.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isLooping) Icons.Default.RepeatOne else Icons.Default.Repeat,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SaffronPrimary.copy(alpha = 0.2f),
                        selectedLabelColor = SaffronPrimary
                    ),
                    modifier = Modifier.testTag("loop_recitation_button")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- SEEK CONTROLS (Slider + Elapsed Time / Duration) ---
            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = progressFraction,
                    onValueChange = { fraction ->
                        isUserSeeking = true
                        userSeekPosition = fraction * effectiveDuration
                    },
                    onValueChangeFinished = {
                        isUserSeeking = false
                        HapticHelper.performTick(context)
                        if (!isThisVerseActive) {
                            viewModel.togglePlayRecitation(verse)
                        }
                        viewModel.seekRecitationTo(userSeekPosition)
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = SaffronPrimary,
                        activeTrackColor = SaffronPrimary,
                        inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .testTag("audio_seek_slider")
                )

                // Timestamp Indicators (Elapsed / Total)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatAudioTime(effectivePosition),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("audio_current_time_text")
                    )

                    Text(
                        text = "-${formatAudioTime((effectiveDuration - effectivePosition).coerceAtLeast(0f))}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    Text(
                        text = formatAudioTime(effectiveDuration),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.testTag("audio_total_duration_text")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // --- MAIN PLAYBACK CONTROLS ROW ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Seek Backward 5s
                IconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        viewModel.seekRecitationBy(-5f)
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("audio_skip_backward_button")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FastRewind,
                            contentDescription = "৫ সেকেন্ড পিছনে যান",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "-5s",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Previous Verse
                IconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        viewModel.playPreviousVerse()
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("audio_prev_verse_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "পূর্ববর্তী শ্লোক",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Large Main Play / Pause Button
                Surface(
                    onClick = {
                        HapticHelper.performClick(null)
                        viewModel.togglePlayRecitation(verse)
                    },
                    shape = CircleShape,
                    color = SaffronPrimary,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(56.dp)
                        .testTag("audio_play_pause_button")
                ) {
                    Box(
                        modifier = Modifier.size(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "পজ করুন" else "প্লে করুন",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Next Verse
                IconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        viewModel.playNextVerse()
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("audio_next_verse_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "পরবর্তী শ্লোক",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Seek Forward 5s
                IconButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        viewModel.seekRecitationBy(5f)
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("audio_skip_forward_button")
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = "৫ সেকেন্ড এগিয়ে যান",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "+5s",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (!isCompact) {
                Spacer(modifier = Modifier.height(14.dp))

                // Recitation Mode Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "আবৃত্তি ধরন:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    listOf(
                        RecitationMode.TRANSLATION_ONLY to "বঙ্গানুবাদ",
                        RecitationMode.SHLOKA_ONLY to "সংস্কৃত শ্লোক",
                        RecitationMode.BOTH to "উভয়ই"
                    ).forEach { (mode, label) ->
                        FilterChip(
                            selected = recitationMode == mode,
                            onClick = {
                                HapticHelper.performClick(null)
                                viewModel.setRecitationMode(mode)
                            },
                            label = { Text(label, fontSize = 10.sp) },
                            modifier = Modifier.testTag("recitation_mode_${mode.name}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Recitation Speed Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "গতি:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    listOf(0.8f, 0.95f, 1.1f, 1.25f).forEach { spd ->
                        FilterChip(
                            selected = settings.recitationSpeed == spd,
                            onClick = {
                                HapticHelper.performClick(null)
                                viewModel.updateReadingSettings(settings.copy(recitationSpeed = spd))
                            },
                            label = { Text("${spd}x", fontSize = 10.sp) },
                            modifier = Modifier.testTag("audio_speed_chip_${spd}x")
                        )
                    }
                }
            }
        }
    }
}

/**
 * Modal Bottom Sheet Audio Player when tapped from Floating Bar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitaAudioPlayerBottomSheet(
    viewModel: GitaViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val playingVerse = viewModel.currentPlayingVerse.collectAsState().value ?: viewModel.getSelectedVerse()
    val chapter = viewModel.chapters.find { it.number == playingVerse.chapterNumber }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header: Close / Down icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "মিনিমাইজ করুন",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "শ্রীমদ্ভগবদ্গীতা আবৃত্তি প্লেয়ার",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "বন্ধ করুন",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sacred Chariot / Shloka Art Box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(18.dp),
                color = SaffronPrimary.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "অধ্যায় ${playingVerse.chapterNumber}: ${chapter?.nameBengali ?: "গীতা"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "॥ ${playingVerse.pronunciationBengali} ॥",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Audio Player Card with full Seek & Navigation Controls
            GitaAudioPlayerCard(
                viewModel = viewModel,
                verse = playingVerse,
                isCompact = false
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
