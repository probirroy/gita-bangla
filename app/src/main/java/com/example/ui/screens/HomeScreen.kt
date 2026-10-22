package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import com.example.ui.components.AudioWaveformVisualizer
import com.example.ui.components.DailyVerseComponent
import com.example.ui.components.VerseAudioPlayer
import com.example.ui.components.formatAudioTime
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GitaViewModel
import com.example.util.HapticHelper
import com.example.util.ShareHelper
import com.example.util.TimeHelper

@Composable
fun HomeScreen(
    viewModel: GitaViewModel,
    modifier: Modifier = Modifier
) {
    val progress by viewModel.readingProgress.collectAsState()
    val dailyVerse by viewModel.dailyVerse.collectAsState()
    val playbackState by viewModel.audioPlayer.playbackState.collectAsState()
    val playingVerseId by viewModel.audioPlayer.currentPlayingVerseId.collectAsState()
    val audioCurrentPosition by viewModel.audioCurrentPosition.collectAsState()
    val audioDuration by viewModel.audioDuration.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val sessionSeconds by viewModel.sessionSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val weeklyInsights by viewModel.weeklyInsights.collectAsState()
    val allChapterProgress by viewModel.allChapterProgress.collectAsState()
    val overallGitaProgress by viewModel.overallGitaProgress.collectAsState()
    val progressMap = allChapterProgress.associateBy { it.chapterNumber }
    val context = LocalContext.current

    val isDailyBookmarked = bookmarks.any {
        it.chapterNumber == dailyVerse.chapterNumber && it.verseNumber == dailyVerse.verseNumber
    }
    val dailyNote = notes.find {
        it.chapterNumber == dailyVerse.chapterNumber && it.verseNumber == dailyVerse.verseNumber
    }
    val isDailyVerseRead = progressMap[dailyVerse.chapterNumber]?.readVersesSet?.contains(dailyVerse.verseNumber) == true
    val dailyVerseChapter = viewModel.chapters.find { it.number == dailyVerse.chapterNumber }

    var selectedDashboardTab by remember { mutableIntStateOf(0) }
    var showCustomGoalDialog by remember { mutableStateOf(false) }
    var customGoalInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Header
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolunteerActivism,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "শ্রীমদ্ভগবদ্গীতা বাংলা",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "যদা যদা হি ধর্মস্য গ্লানির্ভবতি ভারত • সনাতন আত্মদর্শনের আলো",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Streak & Quick Stats
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Streak Card
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${progress.streakDays} দিন",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ধারাবাহিক পাঠ সাধনা",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Reminder Trigger Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_daily_reminder_card")
                        .clickable {
                            HapticHelper.performSuccess(context)
                            viewModel.triggerDailyNotification()
                            android.widget.Toast.makeText(context, "আজকের বাংলা শ্লোক নোটিফিকেশন পাঠানো হয়েছে!", android.widget.Toast.LENGTH_SHORT).show()
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "দৈনিক স্মরণ",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "মনোযোগ অভ্যাস করুন",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // --- DAILY VERSE (দৈনিক শ্লোক) COMPONENT ---
        item {
            DailyVerseComponent(viewModel = viewModel)
        }

        // Comprehensive Habit Tracking Dashboard (দৈনিক পাঠ সাধনা ও অভ্যাস ড্যাশবোর্ড)
        item {
            val totalTodaySeconds = progress.todayStudySeconds + (if (isTimerRunning) (sessionSeconds % 10L) else 0L)
            val timeGoalMinutes = progress.dailyGoalMinutes.coerceAtLeast(1)
            val timeGoalProgress = (totalTodaySeconds.toFloat() / (timeGoalMinutes * 60).toFloat()).coerceIn(0f, 1f)
            val timeGoalPercent = (timeGoalProgress * 100).toInt()

            val verseGoal = progress.dailyVerseGoal.coerceAtLeast(1)
            val versesReadToday = progress.todayVersesCount
            val verseGoalProgress = (versesReadToday.toFloat() / verseGoal.toFloat()).coerceIn(0f, 1f)
            val verseGoalPercent = (verseGoalProgress * 100).toInt()
            val isVerseGoalCompleted = versesReadToday >= verseGoal

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("habit_tracking_dashboard"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Dashboard Title Header
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
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "পাঠ সাধনা ড্যাশবোর্ড",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "দৈনিক আধ্যাত্মিক অভ্যাস পর্যবেক্ষণ",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isVerseGoalCompleted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isVerseGoalCompleted) Icons.Default.CheckCircle else Icons.Default.Flag,
                                    contentDescription = null,
                                    tint = if (isVerseGoalCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isVerseGoalCompleted) "লক্ষ্য অর্জিত" else "$versesReadToday/$verseGoal শ্লোক",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isVerseGoalCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Segmented Tabs: Verse Goal vs Study Time
                    TabRow(
                        selectedTabIndex = selectedDashboardTab,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        contentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedDashboardTab == 0,
                            onClick = {
                                HapticHelper.performClick(null)
                                selectedDashboardTab = 0
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("শ্লোক লক্ষ্য", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            },
                            modifier = Modifier.testTag("verse_goal_tab")
                        )
                        Tab(
                            selected = selectedDashboardTab == 1,
                            onClick = {
                                HapticHelper.performClick(null)
                                selectedDashboardTab = 1
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("পাঠ সময়", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            },
                            modifier = Modifier.testTag("time_goal_tab")
                        )
                        Tab(
                            selected = selectedDashboardTab == 2,
                            onClick = {
                                HapticHelper.performClick(null)
                                selectedDashboardTab = 2
                            },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ShowChart,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("সাপ্তাহিক চার্ট", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            },
                            modifier = Modifier.testTag("weekly_insights_tab")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Content
                    when (selectedDashboardTab) {
                        0 -> {
                        // --- DAILY VERSE READING GOAL VIEW ---
                        Column(modifier = Modifier.testTag("daily_verse_goal_card")) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "আজকের পঠিত শ্লোক",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Row(verticalAlignment = Alignment.Bottom) {
                                        Text(
                                            text = "$versesReadToday",
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = " / $verseGoal শ্লোক",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(bottom = 4.dp, start = 2.dp)
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer
                                    ) {
                                        Text(
                                            text = "$verseGoalPercent%",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (isVerseGoalCompleted) "১০০% সম্পন্ন 🎉" else "আর ${verseGoal - versesReadToday}টি বাকি",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Verse Goal Progress Bar
                            LinearProgressIndicator(
                                progress = { verseGoalProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Motivational Banner
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isVerseGoalCompleted) {
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isVerseGoalCompleted) Icons.Default.CheckCircle else Icons.Default.AutoStories,
                                        contentDescription = null,
                                        tint = if (isVerseGoalCompleted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isVerseGoalCompleted) {
                                            "অভিনন্দন! আজকের ${verseGoal}টি শ্লোক পাঠের লক্ষ্য সফলভাবে অর্জিত হয়েছে! 🌸"
                                        } else if (versesReadToday == 0) {
                                            "আজ এখনও কোনো শ্লোক পড়া হয়নি। জ্ঞান অর্জনের শুভ সূচনা করুন।"
                                        } else {
                                            "চমৎকার অগ্রগতি! আজকের লক্ষ্য পূরণ করতে আর মাত্র ${verseGoal - versesReadToday}টি শ্লোক বাকি।"
                                        },
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Goal Selector Row (Set daily verse goals)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "দৈনিক লক্ষ্য:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    IconButton(
                                        onClick = {
                                            customGoalInput = verseGoal.toString()
                                            showCustomGoalDialog = true
                                        },
                                        modifier = Modifier
                                            .size(24.dp)
                                            .testTag("edit_custom_goal_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "কাস্টম লক্ষ্য",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf(3, 5, 10, 15).forEach { target ->
                                        FilterChip(
                                            selected = verseGoal == target,
                                            onClick = {
                                                HapticHelper.performTick(context)
                                                viewModel.updateDailyVerseGoal(target)
                                            },
                                            label = { Text("${target}টি", fontSize = 10.sp) },
                                            modifier = Modifier.testTag("verse_goal_chip_$target")
                                        )
                                    }

                                    FilterChip(
                                        selected = verseGoal !in listOf(3, 5, 10, 15),
                                        onClick = {
                                            HapticHelper.performClick(null)
                                            customGoalInput = verseGoal.toString()
                                            showCustomGoalDialog = true
                                        },
                                        label = {
                                            Text(
                                                if (verseGoal !in listOf(3, 5, 10, 15)) "${verseGoal}টি" else "কাস্টম",
                                                fontSize = 10.sp
                                            )
                                        },
                                        modifier = Modifier.testTag("custom_goal_chip")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Quick Action Button
                            Button(
                                onClick = {
                                    HapticHelper.performClick(null)
                                    viewModel.openVerseReader(progress.lastChapter, progress.lastVerse)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("read_verse_goal_action_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("শ্লোক পাঠে এগিয়ে যান", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    1 -> {
                        // --- DAILY STUDY TIME GOAL VIEW ---
                        Column(modifier = Modifier.testTag("daily_study_tracker_card")) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "আজকের সাধনার সময়",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = TimeHelper.formatTodayStudySummary(totalTodaySeconds),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = if (isTimerRunning) "⏱️ সাধনা চলছে: ${TimeHelper.formatSecondsToTimer(sessionSeconds)}" else "লক্ষ্য: $timeGoalMinutes মিনিট",
                                        fontSize = 11.sp,
                                        color = if (isTimerRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Button(
                                    onClick = {
                                        HapticHelper.performClick(null)
                                        viewModel.toggleStudyTimer()
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isTimerRunning) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.testTag("toggle_study_timer_button")
                                ) {
                                    Icon(
                                        imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(if (isTimerRunning) "বিরতি" else "সাধনা শুরু")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "সময় অগ্রগতি",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "$timeGoalPercent%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            LinearProgressIndicator(
                                progress = { timeGoalProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Time Goal selector chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "সময় লক্ষ্য:",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf(5, 10, 15, 20, 30).forEach { mins ->
                                        FilterChip(
                                            selected = timeGoalMinutes == mins,
                                            onClick = {
                                                HapticHelper.performTick(context)
                                                viewModel.updateDailyGoalMinutes(mins)
                                            },
                                            label = { Text("${mins}মি", fontSize = 10.sp) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    2 -> {
                        // --- WEEKLY INSIGHTS & RECHARTS GRAPH VIEW ---
                        Column(modifier = Modifier.testTag("weekly_insights_view")) {
                            WeeklyConsistencyGraphCard(
                                summary = weeklyInsights,
                                onStartReading = {
                                    viewModel.openVerseReader(progress.lastChapter, progress.lastVerse)
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = {
                                    HapticHelper.performClick(context)
                                    viewModel.navigateTo(AppScreen.WEEKLY_INSIGHTS)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("open_weekly_insights_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShowChart,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("সম্পূর্ণ সাপ্তাহিক অন্তর্দৃষ্টি ও চার্ট দেখুন →", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        }

        // Continue Reading Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable {
                        HapticHelper.performClick(null)
                        viewModel.openVerseReader(progress.lastChapter, progress.lastVerse)
                    }
                    .testTag("continue_reading_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "সর্বশেষ পঠিত শ্লোক",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "অধ্যায় ${progress.lastChapter}, শ্লোক ${progress.lastVerse}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Button(
                        onClick = {
                            HapticHelper.performClick(null)
                            viewModel.openVerseReader(progress.lastChapter, progress.lastVerse)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("চালিয়ে যান")
                    }
                }
            }
        }



        // Visual Progress Tracker Card for all 18 Gita Chapters (Room DB)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("home_visual_progress_tracker_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                    text = "গীতা অধ্যায় পাঠ ট্র্যাকার",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Room ডাটাবেসে প্রতিটি অধ্যায়ের সমাপ্তি সংরক্ষিত",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = "${String.format("%.1f", overallGitaProgress.overallPercentage)}%",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { (overallGitaProgress.overallPercentage / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .testTag("home_overall_progress_bar"),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${overallGitaProgress.completedChaptersCount}/১৮ অধ্যায় সম্পন্ন",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "${overallGitaProgress.totalVersesRead}/${overallGitaProgress.totalVerses} শ্লোক পাঠ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Horizontal Scrollable Row of all 18 Chapters with mini progress
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.testTag("home_chapters_progress_row")
                    ) {
                        items(viewModel.chapters) { ch ->
                            val chProg = progressMap[ch.number]
                            val chPct = chProg?.completionPercentage ?: 0f
                            val isDone = chProg?.isCompleted == true || (chProg?.readVersesCount ?: 0) >= ch.verseCount

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isDone) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surface,
                                shadowElevation = 1.dp,
                                modifier = Modifier
                                    .clickable {
                                        HapticHelper.performClick(null)
                                        viewModel.openChapter(ch.number)
                                    }
                                    .testTag("home_chapter_chip_${ch.number}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        if (isDone) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = null,
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                        }
                                        Text(
                                            text = "অঃ ${ch.number}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDone) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isDone) "১০০%" else "${String.format("%.0f", chPct)}%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isDone) Color(0xFF2E7D32) else if (chPct > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Chapters Preview
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "শ্রীমদ্ভগবদ্গীতার অধ্যায়সমূহ (১৮টি)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "সকল অধ্যায়",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        HapticHelper.performClick(null)
                        viewModel.navigateTo(AppScreen.CHAPTER_LIST)
                    }
                )
            }
        }

        // Chapters list preview (first 4) with visual progress
        items(viewModel.chapters.take(4)) { chapter ->
            val progress = progressMap[chapter.number]
            val pct = progress?.completionPercentage ?: 0f
            val isCompleted = progress?.isCompleted == true
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 5.dp)
                    .clickable {
                        HapticHelper.performClick(null)
                        viewModel.openChapter(chapter.number)
                    }
                    .testTag("home_chapter_card_${chapter.number}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
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
                                    modifier = Modifier.size(20.dp)
                                )
                            } else {
                                Text(
                                    text = "${chapter.number}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = chapter.nameBengali,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = chapter.meaningBengali,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isCompleted) "১০০% সম্পূর্ণ" else "${String.format("%.0f", pct)}%",
                                fontSize = 12.sp,
                                color = if (isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${progress?.readVersesCount ?: 0}/${chapter.verseCount} শ্লোক",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { (pct / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }

    // Custom Daily Verse Goal Setting Dialog
    if (showCustomGoalDialog) {
        AlertDialog(
            onDismissRequest = { showCustomGoalDialog = false },
            title = {
                Text(
                    text = "দৈনিক শ্লোক পাঠের লক্ষ্য নির্ধারণ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "প্রতিদিন কতগুলো শ্লোক পাঠ করতে চান তা লিখুন (১ থেকে ১০০):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = customGoalInput,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() } && input.length <= 3) {
                                customGoalInput = input
                            }
                        },
                        label = { Text("শ্লোক সংখ্যা") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("custom_goal_text_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = customGoalInput.toIntOrNull()
                        if (parsed != null && parsed in 1..100) {
                            HapticHelper.performSuccess(context)
                            viewModel.updateDailyVerseGoal(parsed)
                            showCustomGoalDialog = false
                        }
                    },
                    modifier = Modifier.testTag("save_custom_goal_button")
                ) {
                    Text("নির্ধারণ করুন")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showCustomGoalDialog = false }
                ) {
                    Text("বাতিল")
                }
            }
        )
    }
}
