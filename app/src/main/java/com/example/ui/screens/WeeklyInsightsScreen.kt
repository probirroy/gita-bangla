package com.example.ui.screens

import android.annotation.SuppressLint
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.DayHabitStat
import com.example.data.model.WeeklyInsightsSummary
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GitaViewModel
import com.example.util.HapticHelper

enum class InsightMetricType {
    STUDY_TIME,
    VERSE_COUNT,
    COMBINED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyInsightsScreen(
    viewModel: GitaViewModel,
    modifier: Modifier = Modifier
) {
    val weeklyInsights by viewModel.weeklyInsights.collectAsState()
    val progress by viewModel.readingProgress.collectAsState()
    val context = LocalContext.current

    BackHandler {
        viewModel.navigateTo(AppScreen.HOME)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("weekly_insights_view"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "সাপ্তাহিক অন্তর্দৃষ্টি",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "গত ৭ দিনের পাঠ ও ধারাবাহিকতা বিশ্লেষণ",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            HapticHelper.performClick(context)
                            viewModel.navigateTo(AppScreen.HOME)
                        },
                        modifier = Modifier.testTag("insights_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "ফিরে যান"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${weeklyInsights.consistencyScorePercent}% ধারাবাহিকতা",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. High-level Consistency & Habit Stats Row
            item {
                WeeklyOverviewStatsSection(summary = weeklyInsights)
            }

            // 2. Main Weekly Consistency Graph Component
            item {
                WeeklyConsistencyGraphCard(
                    summary = weeklyInsights,
                    onStartReading = {
                        viewModel.openVerseReader(progress.lastChapter, progress.lastVerse)
                    }
                )
            }

            // 3. Gita Spiritual Inspiration on Consistency
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoStories,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "শ্রীমদ্ভগবদ্গীতা সাধনা বাণী",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "“অভ্যাসেন তু কৌন্তেয় বৈরাগ্যেন চ গৃহ্যতে।” (৬/৩৫)",
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "হে অর্জুন! নিরন্তর একাগ্র অনুশীলন এবং অনাসক্তির দ্বারাই চঞ্চল মনকে সম্পূর্ণ বশে আনা সম্ভব। প্রতিদিনের নিয়মিত অধ্যয়নই প্রকৃত সাধনা।",
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 4. Day-by-Day Consistency Breakdown
            item {
                Text(
                    text = "দৈনিক পাঠ ও লক্ষ্য বিবরণী (৭ দিন)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(weeklyInsights.days) { dayStat ->
                DayHabitRowItem(dayStat = dayStat)
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun WeeklyOverviewStatsSection(
    summary: WeeklyInsightsSummary,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stat 1: Total Study Time
            StatBoxItem(
                icon = Icons.Default.Timer,
                title = "মোট পাঠ সময়",
                value = formatMinutesToBn(summary.totalStudySeconds / 60),
                subtitle = "গড় ${formatMinutesToBn(summary.totalStudySeconds / (60 * 7))}/দিন",
                iconTint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            // Stat 2: Total Verses Read
            StatBoxItem(
                icon = Icons.Default.MenuBook,
                title = "পঠিত শ্লোক",
                value = "${summary.totalVersesRead}টি",
                subtitle = "লক্ষ্য: ${summary.dailyVerseGoal * 7}টি",
                iconTint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stat 3: Goals Met Days
            StatBoxItem(
                icon = Icons.Default.Flag,
                title = "লক্ষ্য অর্জিত",
                value = "${summary.daysGoalCompletedCount}/৭ দিন",
                subtitle = "${summary.consistencyScorePercent}% সাফল্য",
                iconTint = Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )

            // Stat 4: Active Streak
            StatBoxItem(
                icon = Icons.Default.LocalFireDepartment,
                title = "পাঠের ধারাবাহিকতা",
                value = "${summary.currentStreak} দিন",
                subtitle = "সেরা দিন: ${summary.bestDayLabel}",
                iconTint = Color(0xFFF59E0B),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatBoxItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    subtitle: String,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WeeklyConsistencyGraphCard(
    summary: WeeklyInsightsSummary,
    modifier: Modifier = Modifier,
    onStartReading: (() -> Unit)? = null,
    showRechartsByDefault: Boolean = false
) {
    var selectedMetric by remember { mutableStateOf(InsightMetricType.STUDY_TIME) }
    var selectedDayIndex by remember {
        mutableIntStateOf(if (summary.days.isNotEmpty()) summary.days.size - 1 else 0)
    }
    var useRechartsWebView by remember { mutableStateOf(showRechartsByDefault) }

    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_consistency_graph"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header & Graph Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShowChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ধারাবাহিকতা চার্ট",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Visualizer Switcher: Native Compose vs Recharts (HTML5/SVG)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.clickable {
                        HapticHelper.performClick(context)
                        useRechartsWebView = !useRechartsWebView
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Equalizer,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (useRechartsWebView) "Recharts মোড" else "নেটিভ চার্ট",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Metric Tabs: Study Time vs Verse Count vs Combined
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedMetric == InsightMetricType.STUDY_TIME,
                    onClick = {
                        HapticHelper.performClick(context)
                        selectedMetric = InsightMetricType.STUDY_TIME
                    },
                    label = { Text("পাঠ সময়", fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("metric_study_time_chip")
                )

                FilterChip(
                    selected = selectedMetric == InsightMetricType.VERSE_COUNT,
                    onClick = {
                        HapticHelper.performClick(context)
                        selectedMetric = InsightMetricType.VERSE_COUNT
                    },
                    label = { Text("শ্লোক সংখ্যা", fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("metric_verses_chip")
                )

                FilterChip(
                    selected = selectedMetric == InsightMetricType.COMBINED,
                    onClick = {
                        HapticHelper.performClick(context)
                        selectedMetric = InsightMetricType.COMBINED
                    },
                    label = { Text("ধারাবাহিকতা", fontSize = 11.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Flag,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.testTag("metric_consistency_chip")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Graph Visualization Area
            if (useRechartsWebView) {
                RechartsSummaryWebView(
                    summary = summary,
                    metricType = selectedMetric,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .testTag("recharts_container")
                )
            } else {
                NativeComposeConsistencyChart(
                    summary = summary,
                    metricType = selectedMetric,
                    selectedIndex = selectedDayIndex,
                    onSelectDay = { index ->
                        HapticHelper.performClick(context)
                        selectedDayIndex = index
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("recharts_graph")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selected Day Deep-Dive Detail Card
            if (summary.days.isNotEmpty() && selectedDayIndex in summary.days.indices) {
                val day = summary.days[selectedDayIndex]
                SelectedDayInsightCard(day = day)
            }

            // Quick Call-to-Action
            if (onStartReading != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = {
                        HapticHelper.performClick(context)
                        onStartReading()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("আজকের শ্লোক পাঠ করুন", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun NativeComposeConsistencyChart(
    summary: WeeklyInsightsSummary,
    metricType: InsightMetricType,
    selectedIndex: Int,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = summary.days
    if (days.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text("কোনো পাঠের তথ্য পাওয়া যায়নি", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    // Determine target threshold and max value for scaling
    val (targetValue, maxValue) = when (metricType) {
        InsightMetricType.STUDY_TIME -> {
            val goal = summary.dailyGoalMinutes.toFloat()
            val max = maxOf(goal * 1.4f, days.maxOfOrNull { it.studyMinutes } ?: goal)
            Pair(goal, max.coerceAtLeast(5f))
        }
        InsightMetricType.VERSE_COUNT -> {
            val goal = summary.dailyVerseGoal.toFloat()
            val max = maxOf(goal * 1.4f, days.maxOfOrNull { it.versesCount.toFloat() } ?: goal)
            Pair(goal, max.coerceAtLeast(3f))
        }
        InsightMetricType.COMBINED -> {
            Pair(100f, 100f)
        }
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val successColor = Color(0xFF10B981)
    val amberColor = Color(0xFFF59E0B)
    val outlineColor = MaterialTheme.colorScheme.outlineVariant

    Column(modifier = modifier) {
        // Target Reference Line Indicator (Recharts-style reference line label)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (metricType) {
                    InsightMetricType.STUDY_TIME -> "লক্ষ্য: ${summary.dailyGoalMinutes} মিনিট"
                    InsightMetricType.VERSE_COUNT -> "লক্ষ্য: ${summary.dailyVerseGoal} শ্লোক"
                    InsightMetricType.COMBINED -> "লক্ষ্য: ১০০% সাধনা"
                },
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = amberColor
            )

            Text(
                text = "দিন স্পর্শ করে বিবরণ দেখুন",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Main Bar Visualization Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // Dotted reference threshold line
            val thresholdRatio = (targetValue / maxValue).coerceIn(0.1f, 0.95f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(1f - thresholdRatio)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .align(Alignment.BottomCenter)
                    .background(amberColor.copy(alpha = 0.45f))
            )

            // Bars Row
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEachIndexed { index, day ->
                    val (currentValue, isGoalMet) = when (metricType) {
                        InsightMetricType.STUDY_TIME -> Pair(day.studyMinutes, day.isTimeGoalMet)
                        InsightMetricType.VERSE_COUNT -> Pair(day.versesCount.toFloat(), day.isVerseGoalMet)
                        InsightMetricType.COMBINED -> {
                            val timeProg = (day.studyMinutes / summary.dailyGoalMinutes.toFloat()).coerceIn(0f, 1f)
                            val verseProg = (day.versesCount / summary.dailyVerseGoal.toFloat()).coerceIn(0f, 1f)
                            val combined = ((timeProg + verseProg) / 2f) * 100f
                            Pair(combined, day.isAnyGoalMet)
                        }
                    }

                    val heightFraction by animateFloatAsState(
                        targetValue = (currentValue / maxValue).coerceIn(0.06f, 1f),
                        animationSpec = spring(stiffness = Spring.StiffnessLow),
                        label = "bar_height_$index"
                    )

                    val isSelected = (index == selectedIndex)

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onSelectDay(index) }
                            .padding(horizontal = 3.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        // Value label pill on top of bar
                        Text(
                            text = when (metricType) {
                                InsightMetricType.STUDY_TIME -> "${day.studyMinutes.toInt()}m"
                                InsightMetricType.VERSE_COUNT -> "${day.versesCount}v"
                                InsightMetricType.COMBINED -> "${(currentValue).toInt()}%"
                            },
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) primaryColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        // Vertical Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(heightFraction)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = if (isGoalMet) {
                                            listOf(successColor, primaryColor)
                                        } else if (currentValue > 0) {
                                            listOf(amberColor, primaryColor.copy(alpha = 0.7f))
                                        } else {
                                            listOf(
                                                MaterialTheme.colorScheme.surfaceVariant,
                                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                            )
                                        }
                                    )
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) primaryColor else Color.Transparent,
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        // Day label & goal indicator dot
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = day.dayLabelBn,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected || day.isToday) FontWeight.Bold else FontWeight.Normal,
                                color = if (day.isToday) primaryColor else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        // Dot indicating Goal Achieved
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(
                                    if (day.isAnyGoalMet) successColor else Color.Transparent
                                )
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun RechartsSummaryWebView(
    summary: WeeklyInsightsSummary,
    metricType: InsightMetricType,
    modifier: Modifier = Modifier
) {
    val days = summary.days
    val primaryHex = "#EA580C"
    val successHex = "#10B981"
    val amberHex = "#F59E0B"
    val bgHex = "#FFFFFF"

    // Generate JSON dataset for Recharts rendering
    val dataJson = buildString {
        append("[")
        days.forEachIndexed { i, day ->
            val value = when (metricType) {
                InsightMetricType.STUDY_TIME -> day.studyMinutes.toInt()
                InsightMetricType.VERSE_COUNT -> day.versesCount
                InsightMetricType.COMBINED -> ((day.studyMinutes / summary.dailyGoalMinutes) * 50 + (day.versesCount / summary.dailyVerseGoal.toFloat()) * 50).toInt().coerceIn(0, 100)
            }
            val goal = when (metricType) {
                InsightMetricType.STUDY_TIME -> summary.dailyGoalMinutes
                InsightMetricType.VERSE_COUNT -> summary.dailyVerseGoal
                InsightMetricType.COMBINED -> 100
            }
            append("""{"day":"${day.dayLabelBn}","value":$value,"goal":$goal,"isMet":${day.isAnyGoalMet}}""")
            if (i < days.size - 1) append(",")
        }
        append("]")
    }

    val metricName = when (metricType) {
        InsightMetricType.STUDY_TIME -> "পাঠের সময় (মিনিট)"
        InsightMetricType.VERSE_COUNT -> "পঠিত শ্লোক (সংখ্যা)"
        InsightMetricType.COMBINED -> "ধারাবাহিকতা স্কোর (%)"
    }

    // High quality offline HTML5 with SVG replicating Recharts components & classes
    val htmlContent = """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, user-scalable=no"/>
            <style>
                * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, sans-serif; }
                body { background: transparent; padding: 6px; overflow: hidden; }
                .recharts-wrapper { position: relative; width: 100%; height: 180px; }
                .recharts-surface { width: 100%; height: 100%; overflow: visible; }
                .recharts-cartesian-grid line { stroke: #E2E8F0; stroke-dasharray: 3 3; }
                .recharts-bar-rectangle { rx: 4; ry: 4; transition: height 0.4s ease; }
                .recharts-reference-line line { stroke: $amberHex; stroke-width: 1.5; stroke-dasharray: 4 4; }
                .recharts-text { font-size: 11px; fill: #64748B; text-anchor: middle; }
                .recharts-tooltip-box {
                    font-size: 10px; font-weight: bold; fill: #0F172A; text-anchor: middle;
                }
            </style>
        </head>
        <body>
            <div id="recharts-container" class="recharts-wrapper">
                <svg class="recharts-surface" viewBox="0 0 350 170">
                    <!-- Cartesian Grid -->
                    <g class="recharts-cartesian-grid">
                        <line x1="20" y1="30" x2="330" y2="30"/>
                        <line x1="20" y1="75" x2="330" y2="75"/>
                        <line x1="20" y1="120" x2="330" y2="120"/>
                        <line x1="20" y1="140" x2="330" y2="140" stroke="#CBD5E1"/>
                    </g>

                    <!-- Reference Goal Line -->
                    <g class="recharts-reference-line">
                        <line x1="20" y1="55" x2="330" y2="55"/>
                        <text x="325" y="50" font-size="9" fill="$amberHex" text-anchor="end">লক্ষ্য রেখা</text>
                    </g>

                    <!-- Recharts Bars rendered dynamically -->
                    <g class="recharts-bar" id="barsGroup"></g>

                    <!-- X-Axis Labels -->
                    <g class="recharts-xAxis" id="xAxisGroup"></g>
                </svg>
            </div>
            <script>
                const data = $dataJson;
                const barsGroup = document.getElementById('barsGroup');
                const xAxisGroup = document.getElementById('xAxisGroup');
                const maxVal = Math.max(...data.map(d => d.value), data[0]?.goal || 15) * 1.3 || 20;

                const startX = 35;
                const step = 42;
                const baseY = 140;
                const maxBarH = 105;

                data.forEach((d, idx) => {
                    const x = startX + idx * step;
                    const barH = Math.max(6, (d.value / maxVal) * maxBarH);
                    const y = baseY - barH;
                    const fill = d.isMet ? '$successHex' : (d.value > 0 ? '$primaryHex' : '#E2E8F0');

                    // Bar Rectangle
                    const rect = document.createElementNS('http://www.w3.org/2000/svg', 'rect');
                    rect.setAttribute('class', 'recharts-bar-rectangle');
                    rect.setAttribute('x', x - 12);
                    rect.setAttribute('y', y);
                    rect.setAttribute('width', 24);
                    rect.setAttribute('height', barH);
                    rect.setAttribute('fill', fill);
                    barsGroup.appendChild(rect);

                    // Value Label
                    const valText = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                    valText.setAttribute('class', 'recharts-tooltip-box');
                    valText.setAttribute('x', x);
                    valText.setAttribute('y', y - 4);
                    valText.textContent = d.value;
                    barsGroup.appendChild(valText);

                    // X-Axis Day Label
                    const label = document.createElementNS('http://www.w3.org/2000/svg', 'text');
                    label.setAttribute('class', 'recharts-text');
                    label.setAttribute('x', x);
                    label.setAttribute('y', 156);
                    label.textContent = d.day;
                    xAxisGroup.appendChild(label);
                });
            </script>
        </body>
        </html>
    """.trimIndent()

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            WebView(ctx).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                setBackgroundColor(0)
                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        }
    )
}

@Composable
fun SelectedDayInsightCard(
    day: DayHabitStat,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${day.dayLabelBn}বার (${day.date})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (day.isToday) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "আজ",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (day.isAnyGoalMet) Color(0xFF10B981).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (day.isAnyGoalMet) "লক্ষ্য অর্জিত ✓" else "সাধনা চলমান",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (day.isAnyGoalMet) Color(0xFF047857) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Time Progress
                Column(modifier = Modifier.weight(1f)) {
                    Text("অধ্যয়ন সময়", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${day.studyMinutes.toInt()} / ${day.timeGoalMinutes} মিনিট",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (day.isTimeGoalMet) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (day.studyMinutes / day.timeGoalMinutes.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (day.isTimeGoalMet) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                    )
                }

                // Verse Progress
                Column(modifier = Modifier.weight(1f)) {
                    Text("পঠিত শ্লোক", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = "${day.versesCount} / ${day.verseGoal} শ্লোক",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (day.isVerseGoalMet) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (day.versesCount / day.verseGoal.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (day.isVerseGoalMet) Color(0xFF10B981) else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun DayHabitRowItem(
    dayStat: DayHabitStat,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (dayStat.isAnyGoalMet) Color(0xFF10B981).copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.surfaceVariant
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (dayStat.isAnyGoalMet) Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (dayStat.isAnyGoalMet) Color(0xFF10B981) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${dayStat.dayLabelBn}বার",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        if (dayStat.isToday) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• আজ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Text(
                        text = "${dayStat.studyMinutes.toInt()} মিনিট পাঠ • ${dayStat.versesCount}টি শ্লোক",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (dayStat.isAnyGoalMet) Color(0xFF10B981).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = if (dayStat.isAnyGoalMet) "অর্জিত" else "বাকি",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (dayStat.isAnyGoalMet) Color(0xFF047857) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

private fun formatMinutesToBn(minutes: Long): String {
    return if (minutes >= 60) {
        val h = minutes / 60
        val m = minutes % 60
        if (m > 0) "${h} ঘণ্টা ${m} মি." else "${h} ঘণ্টা"
    } else {
        "${minutes} মিনিট"
    }
}
