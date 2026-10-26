package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.blueCardBorder
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GitaViewModel
import com.example.util.BengaliNumberHelper
import com.example.util.HapticHelper
import java.util.Locale

/**
 * Filter mode for the top progress tracker.
 */
enum class ProgressTrackerMode {
    VERSES,
    CHAPTERS
}

/**
 * Comprehensive progress tracker displayed at the top of the main screen.
 * Shows the user how many chapters or verses they have read, displayed as a progress bar.
 */
@Composable
fun TopReadingProgressTracker(
    viewModel: GitaViewModel,
    modifier: Modifier = Modifier
) {
    val overallProgress by viewModel.overallGitaProgress.collectAsState()
    val readingProgress by viewModel.readingProgress.collectAsState()

    var selectedMode by remember { mutableStateOf(ProgressTrackerMode.VERSES) }

    val totalVerses = if (overallProgress.totalVerses > 0) overallProgress.totalVerses else 700
    val versesRead = overallProgress.totalVersesRead
    val totalChapters = if (overallProgress.totalChapters > 0) overallProgress.totalChapters else 18
    val chaptersCompleted = overallProgress.completedChaptersCount
    val remainingVerses = (totalVerses - versesRead).coerceAtLeast(0)

    val verseProgressFraction = if (totalVerses > 0) {
        (versesRead.toFloat() / totalVerses.toFloat()).coerceIn(0f, 1f)
    } else 0f
    val chapterProgressFraction = if (totalChapters > 0) {
        (chaptersCompleted.toFloat() / totalChapters.toFloat()).coerceIn(0f, 1f)
    } else 0f

    val activeFraction = when (selectedMode) {
        ProgressTrackerMode.VERSES -> verseProgressFraction
        ProgressTrackerMode.CHAPTERS -> chapterProgressFraction
    }

    val animatedFraction by animateFloatAsState(
        targetValue = activeFraction,
        label = "TopProgressBarAnimation"
    )

    val activePercentage = (activeFraction * 100f)
    val percentageString = String.format(Locale.US, "%.1f", activePercentage)
    val percentageBengali = BengaliNumberHelper.toBengali(percentageString)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("main_screen_progress_tracker")
            .testTag("chapter_verse_progress_tracker"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = blueCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Title, Icon, and Percentage Badge
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
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "গীতাপাঠ অগ্রগতি ট্র্যাকার",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "কতগুলো অধ্যায় ও শ্লোক পাঠ হয়েছে তার খতিয়ান",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Percentage Badge
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("top_progress_percentage_badge")
                ) {
                    Text(
                        text = "$percentageBengali% সমাপ্ত",
                        modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mode Selector FilterChips: Verses vs Chapters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedMode == ProgressTrackerMode.VERSES,
                    onClick = {
                        HapticHelper.performClick(null)
                        selectedMode = ProgressTrackerMode.VERSES
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "শ্লোক ভিত্তিক (${BengaliNumberHelper.toBengali(versesRead)}/${BengaliNumberHelper.toBengali(totalVerses)})",
                            fontSize = 11.sp,
                            fontWeight = if (selectedMode == ProgressTrackerMode.VERSES) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("progress_tracker_verses_chip")
                )

                FilterChip(
                    selected = selectedMode == ProgressTrackerMode.CHAPTERS,
                    onClick = {
                        HapticHelper.performClick(null)
                        selectedMode = ProgressTrackerMode.CHAPTERS
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "অধ্যায় ভিত্তিক (${BengaliNumberHelper.toBengali(chaptersCompleted)}/${BengaliNumberHelper.toBengali(totalChapters)})",
                            fontSize = 11.sp,
                            fontWeight = if (selectedMode == ProgressTrackerMode.CHAPTERS) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("progress_tracker_chapters_chip")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Progress Bar
            LinearProgressIndicator(
                progress = { animatedFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(9.dp)
                    .clip(RoundedCornerShape(4.5.dp))
                    .testTag("top_progress_bar")
                    .testTag(
                        if (selectedMode == ProgressTrackerMode.VERSES) "verses_progress_bar" else "chapters_progress_bar"
                    ),
                color = if (activeFraction >= 1f) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Progress labels under progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (selectedMode) {
                        ProgressTrackerMode.VERSES -> "পঠিত শ্লোক: ${BengaliNumberHelper.toBengali(versesRead)} / ${BengaliNumberHelper.toBengali(totalVerses)}"
                        ProgressTrackerMode.CHAPTERS -> "সম্পূর্ণ অধ্যায়: ${BengaliNumberHelper.toBengali(chaptersCompleted)} / ${BengaliNumberHelper.toBengali(totalChapters)}"
                    },
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when (selectedMode) {
                        ProgressTrackerMode.VERSES -> "অবশিষ্ট: ${BengaliNumberHelper.toBengali(remainingVerses)} শ্লোক"
                        ProgressTrackerMode.CHAPTERS -> "পাঠরত অধ্যায়: ${BengaliNumberHelper.toBengali(overallProgress.inProgressChaptersCount)}"
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dual Stats Cards: Chapters Read and Verses Read side-by-side
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Chapters Completed Box
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tracker_chapters_stat_box")
                        .clickable {
                            HapticHelper.performClick(null)
                            selectedMode = ProgressTrackerMode.CHAPTERS
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedMode == ProgressTrackerMode.CHAPTERS) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        }
                    ),
                    border = blueCardBorder(width = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "অধ্যায় সমাপ্তি",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${BengaliNumberHelper.toBengali(chaptersCompleted)} / ${BengaliNumberHelper.toBengali(totalChapters)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { chapterProgressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (chaptersCompleted > 0) {
                                "${BengaliNumberHelper.toBengali(chaptersCompleted)}টি অধ্যায়ের সব শ্লোক পঠিত"
                            } else {
                                "অধ্যায় শুরু করুন"
                            },
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Verses Read Box
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tracker_verses_stat_box")
                        .clickable {
                            HapticHelper.performClick(null)
                            selectedMode = ProgressTrackerMode.VERSES
                        },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedMode == ProgressTrackerMode.VERSES) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                        }
                    ),
                    border = blueCardBorder(width = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "শ্লোক পাঠ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${BengaliNumberHelper.toBengali(versesRead)} / ${BengaliNumberHelper.toBengali(totalVerses)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { verseProgressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color(0xFF2E7D32),
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${BengaliNumberHelper.toBengali(remainingVerses)} শ্লোক বাকি",
                            fontSize = 9.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Navigation Actions: Resume Reading or Go to Chapters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        viewModel.navigateTo(AppScreen.CHAPTER_LIST)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tracker_view_chapters_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "অধ্যায় সূচি",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                FilledTonalButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        viewModel.openVerseReader(
                            readingProgress.lastChapter.coerceIn(1, 18),
                            readingProgress.lastVerse.coerceAtLeast(1)
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("tracker_continue_reading_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "পাঠ অব্যাহত রাখুন",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
