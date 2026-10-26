package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.GitaViewModel
import com.example.util.HapticHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChapterListScreen(
    viewModel: GitaViewModel,
    modifier: Modifier = Modifier
) {
    val allProgress by viewModel.allChapterProgress.collectAsState()
    val overallProgress by viewModel.overallGitaProgress.collectAsState()
    val settings by viewModel.userSettings.collectAsState()
    val progressMap = allProgress.associateBy { it.chapterNumber }

    var filterSelection by remember { mutableIntStateOf(0) } // 0: All, 1: Completed, 2: In Progress, 3: Unread
    val filteredChapters = when (filterSelection) {
        1 -> viewModel.chapters.filter {
            val p = progressMap[it.number]
            p?.isCompleted == true || ((p?.readVersesCount ?: 0) >= it.verseCount && it.verseCount > 0)
        }
        2 -> viewModel.chapters.filter {
            val p = progressMap[it.number]
            val rc = p?.readVersesCount ?: 0
            rc > 0 && !(p?.isCompleted == true || rc >= it.verseCount)
        }
        3 -> viewModel.chapters.filter {
            val p = progressMap[it.number]
            (p?.readVersesCount ?: 0) == 0 && p?.isCompleted != true
        }
        else -> viewModel.chapters
    }

    Scaffold(
        modifier = modifier.testTag("chapter_list_screen"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "অধ্যায় সমূহ (১৮টি)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Overall Gita Reading Progress Tracker Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("overall_progress_card"),
                    shape = RoundedCornerShape(20.dp),
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.WorkspacePremium,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "সমগ্র গীতা পাঠের অগ্রগতি",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "রুম ডাটাবেসে রিয়েল-টাইমে সংরক্ষিত",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                    )
                                }
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "${String.format("%.1f", overallProgress.overallPercentage)}%",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val animatedOverallProgress by animateFloatAsState(
                            targetValue = overallProgress.overallPercentage / 100f,
                            animationSpec = tween(durationMillis = 700),
                            label = "overall_progress_anim"
                        )

                        LinearProgressIndicator(
                            progress = { animatedOverallProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .testTag("overall_progress_bar"),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${overallProgress.completedChaptersCount}/১৮টি অধ্যায় সম্পূর্ণ",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${overallProgress.totalVersesRead}/${overallProgress.totalVerses}টি শ্লোক পঠিত",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }

            // Filter Chips for Progress Status
            item {
                val unreadCount = (18 - overallProgress.completedChaptersCount - overallProgress.inProgressChaptersCount).coerceAtLeast(0)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("chapter_progress_filter_row"),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterSelection == 0,
                        onClick = {
                            HapticHelper.performClick(null)
                            filterSelection = 0
                        },
                        label = { Text("সকল (১৮)", fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_all_chapters")
                    )
                    FilterChip(
                        selected = filterSelection == 1,
                        onClick = {
                            HapticHelper.performClick(null)
                            filterSelection = 1
                        },
                        label = { Text("সম্পূর্ণ (${overallProgress.completedChaptersCount})", fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_completed_chapters")
                    )
                    FilterChip(
                        selected = filterSelection == 2,
                        onClick = {
                            HapticHelper.performClick(null)
                            filterSelection = 2
                        },
                        label = { Text("চলমান (${overallProgress.inProgressChaptersCount})", fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_in_progress_chapters")
                    )
                    FilterChip(
                        selected = filterSelection == 3,
                        onClick = {
                            HapticHelper.performClick(null)
                            filterSelection = 3
                        },
                        label = { Text("অপঠিত ($unreadCount)", fontSize = 11.sp) },
                        modifier = Modifier.testTag("filter_unread_chapters")
                    )
                }
            }

            // Chapters List with Individual Chapter Progress Trackers
            items(filteredChapters) { chapter ->
                val progress = progressMap[chapter.number]
                val readCount = progress?.readVersesCount ?: 0
                val totalCount = chapter.verseCount
                val completionPct = progress?.completionPercentage ?: 0f
                val isCompleted = progress?.isCompleted == true || (readCount >= totalCount && totalCount > 0)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            HapticHelper.performClick(null)
                            viewModel.openChapter(chapter.number)
                        }
                        .testTag("chapter_card_${chapter.number}"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                    ),
                    border = com.example.ui.theme.blueCardBorder(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Chapter Number or Completion Icon Badge
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCompleted) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "সম্পূর্ণ",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(26.dp)
                                    )
                                } else {
                                    Text(
                                        text = "${chapter.number}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = chapter.nameBengali,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = chapter.nameSanskrit,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = chapter.meaningBengali,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            val isDownloaded = settings.isChapterDownloaded(chapter.number)
                            if (isDownloaded) {
                                Surface(
                                    color = Color(0xFFE8F5E9),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("chapter_offline_badge_${chapter.number}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CloudDone,
                                            contentDescription = "অফলাইনে সংরক্ষিত",
                                            tint = Color(0xFF2E7D32),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "অফলাইন",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF2E7D32)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }

                            // Status Pill / Badge
                            Surface(
                                color = when {
                                    isCompleted -> Color(0xFFE8F5E9)
                                    readCount > 0 -> MaterialTheme.colorScheme.primaryContainer
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = when {
                                        isCompleted -> "সম্পূর্ণ ✓"
                                        readCount > 0 -> "${String.format("%.0f", completionPct)}%"
                                        else -> "০%"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when {
                                        isCompleted -> Color(0xFF2E7D32)
                                        readCount > 0 -> MaterialTheme.colorScheme.primary
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Visual Progress Bar for this Chapter
                        val animatedChapterProgress by animateFloatAsState(
                            targetValue = (completionPct / 100f).coerceIn(0f, 1f),
                            animationSpec = tween(durationMillis = 500),
                            label = "chapter_progress_bar_anim"
                        )

                        LinearProgressIndicator(
                            progress = { animatedChapterProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .testTag("chapter_progress_bar_${chapter.number}"),
                            color = if (isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Progress Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (readCount == 0) "এখনো পাঠ শুরু হয়নি" else "${String.format("%.1f", completionPct)}% পাঠ সম্পন্ন",
                                fontSize = 11.sp,
                                color = if (isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isCompleted) FontWeight.SemiBold else FontWeight.Normal
                            )

                            Text(
                                text = "$readCount / $totalCount শ্লোক",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
