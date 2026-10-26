package com.example.ui.screens

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.audio.PlaybackState
import com.example.audio.RecitationMode
import com.example.ui.components.GitaAudioPlayerCard
import com.example.ui.components.MediaPlaybackController
import com.example.ui.components.VerseAudioPlayer
import com.example.ui.components.VerseJournalComponent
import com.example.ui.theme.blueCardBorder
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GitaViewModel
import com.example.util.HapticHelper
import com.example.util.ShareHelper
import com.example.util.TimeHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerseReaderScreen(
    viewModel: GitaViewModel,
    modifier: Modifier = Modifier
) {
    val chapter = viewModel.getSelectedChapter()
    val verse = viewModel.getSelectedVerse()
    val settings by viewModel.userSettings.collectAsState()
    val progress by viewModel.readingProgress.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val sessionSeconds by viewModel.sessionSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val playbackState by viewModel.audioPlayer.playbackState.collectAsState()
    val playingVerseId by viewModel.audioPlayer.currentPlayingVerseId.collectAsState()
    val recitationMode by viewModel.recitationMode.collectAsState()
    val offlineAudioStatus by viewModel.offlineAudioStatus.collectAsState()
    val allProgress by viewModel.allChapterProgress.collectAsState()
    val chapterProgress = allProgress.find { it.chapterNumber == chapter.number }
    val readCount = chapterProgress?.readVersesCount ?: 0
    val completionPct = chapterProgress?.completionPercentage ?: 0f
    val isCurrentVerseRead = chapterProgress?.readVersesSet?.contains(verse.verseNumber) == true
    val isPlaying = playingVerseId == verse.verseId && playbackState == PlaybackState.PLAYING
    val isBookmarked = viewModel.isCurrentVerseBookmarked()
    val currentNote = notes.find { it.chapterNumber == verse.chapterNumber && it.verseNumber == verse.verseNumber }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    BackHandler {
        viewModel.navigateTo(AppScreen.CHAPTER_DETAIL)
    }

    Scaffold(
        modifier = modifier.testTag("verse_reader_screen"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "অধ্যায় ${chapter.number}: ${chapter.nameBengali}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "শ্লোক ${verse.verseNumber} / ${chapter.verseCount}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            HapticHelper.performClick(null)
                            viewModel.navigateTo(AppScreen.CHAPTER_DETAIL)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "অধ্যায়ে ফিরুন"
                        )
                    }
                },
                actions = {
                    // Mark verse read toggle action (updates Room DB completion percentage)
                    IconButton(
                        onClick = {
                            HapticHelper.performTick(context)
                            viewModel.toggleVerseRead(verse.chapterNumber, verse.verseNumber)
                        },
                        modifier = Modifier.testTag("reader_verse_read_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isCurrentVerseRead) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                            contentDescription = if (isCurrentVerseRead) "অপঠিত করুন" else "পঠিত চিহ্নিত করুন",
                            tint = if (isCurrentVerseRead) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Bookmark action
                    IconButton(
                        onClick = {
                            HapticHelper.performSuccess(context)
                            viewModel.toggleBookmarkForCurrentVerse()
                        },
                        modifier = Modifier.testTag("bookmark_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "বুকমার্ক",
                            tint = if (isBookmarked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Share action
                    IconButton(
                        onClick = {
                            HapticHelper.performClick(null)
                            viewModel.openShareVerseDialog(verse)
                        },
                        modifier = Modifier.testTag("top_bar_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "শেয়ার করুন"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Visual Chapter Reading Progress Indicator at top of reader (stored in Room DB)
            val currentProgress = (completionPct / 100f).coerceIn(0f, 1f)
            val animatedProgress by animateFloatAsState(
                targetValue = currentProgress,
                animationSpec = spring(stiffness = Spring.StiffnessLow),
                label = "chapter_progress_animation"
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("chapter_progress_indicator"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = blueCardBorder(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (completionPct >= 100f) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "অধ্যায় পাঠের অগ্রগতি",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Current Verse Read Status Pill
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        HapticHelper.performTick(context)
                                        viewModel.toggleVerseRead(verse.chapterNumber, verse.verseNumber)
                                    }
                                    .testTag("reader_current_verse_status_pill"),
                                color = if (isCurrentVerseRead) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isCurrentVerseRead) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                                        contentDescription = null,
                                        tint = if (isCurrentVerseRead) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (isCurrentVerseRead) "পঠিত ✓" else "অপঠিত",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrentVerseRead) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Daily Verse Goal Pill
                            val isGoalMet = progress.todayVersesCount >= progress.dailyVerseGoal
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        HapticHelper.performClick(null)
                                        viewModel.navigateTo(AppScreen.HOME)
                                    }
                                    .testTag("reader_verse_goal_pill"),
                                color = if (isGoalMet) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isGoalMet) Icons.Default.CheckCircle else Icons.Default.Flag,
                                        contentDescription = null,
                                        tint = if (isGoalMet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${progress.todayVersesCount}/${progress.dailyVerseGoal}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isGoalMet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Chapter percentage pill
                            Surface(
                                color = if (completionPct >= 100f) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "$readCount/${chapter.verseCount} (${String.format("%.1f", completionPct)}%)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (completionPct >= 100f) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .testTag("reader_chapter_progress_bar"),
                        color = if (completionPct >= 100f) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            // Main Verse Reading Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- MEDIA PLAYBACK CONTROLLER (AUDIO STATE MANAGER INTEGRATED) ---
                MediaPlaybackController(
                    verse = verse,
                    audioStateManager = viewModel.audioStateManager,
                    showVerseHeader = false,
                    showRecitationModes = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("media_playback_controller")
                        .testTag("verse_audio_player")
                        .testTag("bengali_audio_recitation_panel")
                )

                // Bangla Shlok (বাংলা শ্লোক)
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("shloka_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    border = blueCardBorder(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "॥ শ্রীমদ্ভগবদ্গীতা শ্লোক ॥",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            FilledTonalButton(
                                onClick = {
                                    HapticHelper.performClick(null)
                                    if (isPlaying && recitationMode == RecitationMode.SHLOKA_ONLY) {
                                        viewModel.stopRecitation()
                                    } else {
                                        viewModel.playBengaliShloka(verse)
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("shloka_audio_recitation_button")
                            ) {
                                Icon(
                                    imageVector = if (isPlaying && recitationMode == RecitationMode.SHLOKA_ONLY) Icons.Default.Pause else Icons.Default.VolumeUp,
                                    contentDescription = "শ্লোক আবৃত্তি শুনুন",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isPlaying && recitationMode == RecitationMode.SHLOKA_ONLY) "থামান" else "শ্লোক শুনুন",
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "॥ ${verse.pronunciationBengali} ॥",
                            fontSize = settings.shlokaFontSize.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = (settings.shlokaFontSize * 1.5f).sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Bengali Translation (বঙ্গানুবাদ)
                if (settings.showTranslation) {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("translation_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        ),
                        border = blueCardBorder(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "সরল বঙ্গানুবাদ:",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    FilledTonalButton(
                                        onClick = {
                                            HapticHelper.performClick(null)
                                            if (isPlaying && recitationMode == RecitationMode.TRANSLATION_ONLY) {
                                                viewModel.stopRecitation()
                                            } else {
                                                viewModel.playBengaliTranslation(verse)
                                            }
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("bengali_translation_audio_button")
                                    ) {
                                        Icon(
                                            imageVector = if (isPlaying && recitationMode == RecitationMode.TRANSLATION_ONLY) Icons.Default.Pause else Icons.Default.VolumeUp,
                                            contentDescription = "বাংলা অনুবাদ শুনুন",
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = if (isPlaying && recitationMode == RecitationMode.TRANSLATION_ONLY) "থামান" else "অনুবাদ শুনুন",
                                            fontSize = 11.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(4.dp))

                                    IconButton(
                                        onClick = {
                                            HapticHelper.performClick(null)
                                            viewModel.openShareVerseDialog(verse)
                                        },
                                        modifier = Modifier
                                            .size(30.dp)
                                            .testTag("share_translation_card_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "শ্লোক শেয়ার করুন",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(17.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = verse.translationBengali,
                                fontSize = settings.translationFontSize.sp,
                                lineHeight = (settings.translationFontSize * 1.5f).sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Bengali Purport (তাৎপর্য ও আধ্যাত্মিক ব্যাখ্যা)
                if (settings.showPurport) {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("purport_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = blueCardBorder(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = "তাৎপর্য ও আধ্যাত্মিক উপলব্ধি:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = verse.purportBengali,
                                fontSize = settings.purportFontSize.sp,
                                lineHeight = (settings.purportFontSize * 1.55f).sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                            )
                        }
                    }
                }

                // Full-Featured Audio Recitation Player with Seek Controls
                GitaAudioPlayerCard(
                    viewModel = viewModel,
                    verse = verse,
                    isCompact = false
                )

                // Dedicated Verse Journal Component (Room Database Persistence)
                VerseJournalComponent(
                    verse = verse,
                    currentNote = currentNote,
                    onSaveNote = { noteText ->
                        viewModel.saveVerseNote(verse.chapterNumber, verse.verseNumber, noteText)
                    },
                    onDeleteNote = {
                        viewModel.deleteNote(verse.chapterNumber, verse.verseNumber)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Bottom Navigation Controls: Previous / Next Verse
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp),
                border = blueCardBorder(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("verse_reader_bottom_bar")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            HapticHelper.performClick(null)
                            viewModel.previousVerse()
                        },
                        enabled = !(verse.chapterNumber == 1 && verse.verseNumber == 1),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("prev_verse_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("পূর্ববর্তী")
                    }

                    Text(
                        text = "${verse.chapterNumber}.${verse.verseNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Button(
                        onClick = {
                            HapticHelper.performClick(null)
                            viewModel.nextVerse()
                        },
                        enabled = !(verse.chapterNumber == 18 && verse.verseNumber == 78),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.testTag("next_verse_button")
                    ) {
                        Text("পরবর্তী")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
