package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.ui.components.VerseCardAudioPlayer
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GitaViewModel
import com.example.util.HapticHelper
import com.example.util.ShareHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterDetailScreen(
    viewModel: GitaViewModel,
    modifier: Modifier = Modifier
) {
    val chapter = viewModel.getSelectedChapter()
    val verses = viewModel.getVersesForCurrentChapter()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val allProgress by viewModel.allChapterProgress.collectAsState()
    val chapterProgress = allProgress.find { it.chapterNumber == chapter.number }
    val readCount = chapterProgress?.readVersesCount ?: 0
    val completionPct = chapterProgress?.completionPercentage ?: 0f
    val isCompleted = chapterProgress?.isCompleted == true || (readCount >= chapter.verseCount && chapter.verseCount > 0)
    val readVersesSet = chapterProgress?.readVersesSet ?: emptySet()
    val playbackState by viewModel.audioPlayer.playbackState.collectAsState()
    val playingVerseId by viewModel.audioPlayer.currentPlayingVerseId.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val context = LocalContext.current

    BackHandler {
        viewModel.navigateTo(AppScreen.CHAPTER_LIST)
    }

    Scaffold(
        modifier = modifier.testTag("chapter_detail_screen"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "অধ্যায় ${chapter.number}: ${chapter.nameBengali}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            HapticHelper.performClick(null)
                            viewModel.navigateTo(AppScreen.CHAPTER_LIST)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                actions = {
                    val isDownloaded = settings.isChapterDownloaded(chapter.number)
                    IconButton(
                        onClick = {
                            HapticHelper.performClick(null)
                            viewModel.toggleChapterDownload(chapter.number)
                        },
                        modifier = Modifier.testTag("chapter_detail_offline_toggle")
                    ) {
                        Icon(
                            imageVector = if (isDownloaded) Icons.Default.CloudDone else Icons.Default.CloudDownload,
                            contentDescription = if (isDownloaded) "অফলাইনে সংরক্ষিত" else "অফলাইনে ডাউনলোড করুন",
                            tint = if (isDownloaded) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Chapter Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    border = com.example.ui.theme.blueCardBorder(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = chapter.nameSanskrit,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${chapter.verseCount}টি শ্লোক",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = chapter.meaningBengali,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f),
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = chapter.summaryBengali,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 22.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            // Visual Chapter Progress Tracker Card (Stored in Room)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chapter_detail_progress_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    border = com.example.ui.theme.blueCardBorder(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "অধ্যায় পাঠের অগ্রগতি (Room DB)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "$readCount / ${chapter.verseCount}টি শ্লোক পাঠ সম্পন্ন",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                color = if (isCompleted) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (isCompleted) "১০০% সম্পূর্ণ ✓" else "${String.format("%.1f", completionPct)}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val animatedChapterProgress by animateFloatAsState(
                            targetValue = (completionPct / 100f).coerceIn(0f, 1f),
                            animationSpec = tween(durationMillis = 600),
                            label = "chapter_detail_progress_anim"
                        )

                        LinearProgressIndicator(
                            progress = { animatedChapterProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .testTag("chapter_detail_progress_bar"),
                            color = if (isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val nextUnread = (1..chapter.verseCount).firstOrNull { !readVersesSet.contains(it) }

                            if (nextUnread != null) {
                                Button(
                                    onClick = {
                                        HapticHelper.performClick(null)
                                        viewModel.openVerseReader(chapter.number, nextUnread)
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("jump_next_unread_button"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary
                                    )
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("পরবর্তী অপঠিত ($nextUnread)", fontSize = 12.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    HapticHelper.performSuccess(context)
                                    viewModel.markChapterAsCompleted(chapter.number, !isCompleted)
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("toggle_chapter_completion_button"),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCompleted) Icons.Default.RestartAlt else Icons.Default.CheckCircleOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (isCompleted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isCompleted) "পুনরায় শুরু" else "সব সমাপ্ত",
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "শ্লোকসমূহ (${chapter.verseCount}টি):",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Verses List
            items(verses) { verse ->
                val isBookmarked = bookmarks.any {
                    it.chapterNumber == verse.chapterNumber && it.verseNumber == verse.verseNumber
                }
                val isVerseRead = readVersesSet.contains(verse.verseNumber)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            HapticHelper.performClick(null)
                            viewModel.openVerseReader(verse.chapterNumber, verse.verseNumber)
                        }
                        .testTag("verse_item_${verse.verseNumber}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isVerseRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface
                    ),
                    border = com.example.ui.theme.blueCardBorder(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isVerseRead) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isVerseRead) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "পঠিত",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${verse.verseNumber}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = verse.pronunciationBengali.lines().firstOrNull() ?: "",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f, fill = false)
                                    )
                                    if (isVerseRead) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = Color(0xFFE8F5E9),
                                            shape = RoundedCornerShape(6.dp)
                                        ) {
                                            Text(
                                                text = "পঠিত",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2E7D32),
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = verse.translationBengali,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Read Toggle Button
                                IconButton(
                                    onClick = {
                                        HapticHelper.performTick(context)
                                        viewModel.toggleVerseRead(verse.chapterNumber, verse.verseNumber)
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("verse_read_toggle_${verse.verseNumber}")
                                ) {
                                    Icon(
                                        imageVector = if (isVerseRead) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline,
                                        contentDescription = if (isVerseRead) "অপঠিত করুন" else "পঠিত চিহ্নিত করুন",
                                        tint = if (isVerseRead) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                if (isBookmarked) {
                                    Icon(
                                        imageVector = Icons.Default.Bookmark,
                                        contentDescription = "সংরক্ষিত",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                }

                                IconButton(
                                    onClick = {
                                        HapticHelper.performClick(null)
                                        viewModel.openShareVerseDialog(verse)
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("share_verse_${verse.verseNumber}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "শ্লোক শেয়ার করুন",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dedicated Audio Player Component for each verse card
                        VerseCardAudioPlayer(
                            verse = verse,
                            audioStateManager = viewModel.audioStateManager
                        )
                    }
                }
            }
        }
    }
}
