package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.data.model.DailyVerseCurated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LotusTeal
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.GitaViewModel
import com.example.util.HapticHelper

/**
 * High-prominence, uniquely curated Daily Verse component on the Home screen.
 * Displays today's unique curated Bengali verse, inspiration quote, daily life wisdom,
 * streak commitment, and audio recitation to encourage consistent user engagement.
 */
@Composable
fun DailyVerseComponent(
    viewModel: GitaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val curatedDailyVerse by viewModel.curatedDailyVerse.collectAsState()
    val dailyVerse = curatedDailyVerse.verse
    val progress by viewModel.todayProgress.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val notes by viewModel.notes.collectAsState()

    val playingVerseId by viewModel.audioPlayer.currentPlayingVerseId.collectAsState()
    val playbackState by viewModel.audioPlayer.playbackState.collectAsState()

    val isPlaying = playingVerseId == dailyVerse.verseId && playbackState == PlaybackState.PLAYING
    val isDailyBookmarked = bookmarks.any { it.chapterNumber == dailyVerse.chapterNumber && it.verseNumber == dailyVerse.verseNumber }
    val dailyNote = notes.find { it.chapterNumber == dailyVerse.chapterNumber && it.verseNumber == dailyVerse.verseNumber }
    val isDailyVerseRead = progress.isVerseRead(dailyVerse.chapterNumber, dailyVerse.verseNumber)

    var showWisdomDetails by remember { mutableStateOf(true) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .testTag("daily_verse_feature")
            .testTag("daily_verse_card")
            .testTag("verse_of_the_day_card")
            .testTag("verse_of_the_day_component")
            .testTag("verse_of_the_day_feature"),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.5.dp, SaffronPrimary.copy(alpha = 0.35f)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Bar: Date, Sacred Badge, and Quick Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Sacred Badge + Bengali Date Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = SaffronPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("daily_verse_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoStories,
                                    contentDescription = null,
                                    tint = SaffronPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "আজকের শ্লোক",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronPrimary
                                )
                            }
                        }

                        // Bengali Calendar Date
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.testTag("daily_verse_date_text")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = curatedDailyVerse.dateDisplayBengali,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Chapter & Verse Location
                    val chapterObj = viewModel.chapters.find { it.number == dailyVerse.chapterNumber }
                    Text(
                        text = "অধ্যায় ${dailyVerse.chapterNumber}: ${chapterObj?.nameBengali ?: "গীতা"} • শ্লোক ${dailyVerse.verseNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("daily_verse_title")
                    )
                }

                // Header Action Buttons: Shuffle, Bookmark, Note, Share
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Explore/Shuffle Button
                    IconButton(
                        onClick = {
                            HapticHelper.performClick(null)
                            viewModel.loadRandomVerseOfTheDay()
                            Toast.makeText(context, "নতুন দৈব শ্লোক লোড করা হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("daily_verse_shuffle_button")
                            .testTag("refresh_verse_of_the_day_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "আরেকটি শ্লোক দেখুন",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Bookmark
                    IconButton(
                        onClick = {
                            HapticHelper.performTick(context)
                            viewModel.toggleBookmarkForVerse(
                                dailyVerse.chapterNumber,
                                dailyVerse.verseNumber,
                                "দৈনিক পাঠ"
                            )
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("bookmark_daily_verse_button")
                    ) {
                        Icon(
                            imageVector = if (isDailyBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "বুকমার্ক",
                            tint = if (isDailyBookmarked) SaffronPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    // Reflection Note
                    IconButton(
                        onClick = {
                            HapticHelper.performClick(null)
                            viewModel.openNoteEditor(dailyVerse.chapterNumber, dailyVerse.verseNumber)
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("note_daily_verse_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "নোট লিখুন",
                            tint = if (dailyNote != null) SaffronPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Two-Option Share (Screenshot or Text)
                    IconButton(
                        onClick = {
                            HapticHelper.performClick(null)
                            viewModel.openShareVerseDialog(dailyVerse)
                        },
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("share_daily_verse_button")
                            .testTag("verse_of_the_day_share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "শ্লোক শেয়ার করুন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Thematic Category & Inspiration Quote
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_verse_theme_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = SaffronPrimary,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = curatedDailyVerse.themeCategory,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = curatedDailyVerse.inspirationQuote,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bengali Shloka Recitation Card
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, GoldAccent.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "॥ ${dailyVerse.pronunciationBengali} ॥",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 26.sp,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .testTag("daily_verse_shloka_text")
                            .testTag("verse_of_the_day_shloka_text")
                            .testTag("daily_verse_pronunciation_text")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bengali Translation
            Text(
                text = "সরল বঙ্গানুবাদ:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = dailyVerse.translationBengali,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .testTag("daily_verse_translation_text")
                    .testTag("verse_of_the_day_translation_text")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Curated Daily Life Wisdom & Practical Engagement Action
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_verse_wisdom_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Header with toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showWisdomDetails = !showWisdomDetails },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = GoldAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "আজকের জীবনশিক্ষা ও আত্মচিন্তন",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = if (showWisdomDetails) "সংক্ষেপ করুন" else "বিস্তারিত দেখুন",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    AnimatedVisibility(visible = showWisdomDetails) {
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = curatedDailyVerse.dailyWisdomBengali,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.testTag("daily_verse_purport_text")
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
                                thickness = 0.8.dp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            // Practical Action for Daily User Engagement
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("daily_verse_practice_card"),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(LotusTeal.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TaskAlt,
                                        contentDescription = null,
                                        tint = LotusTeal,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "আজকের অনুশীলন / সাধনা:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LotusTeal
                                    )
                                    Text(
                                        text = curatedDailyVerse.dailyPracticeBengali,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // User's Personal Reflection Note if written
            if (dailyNote != null && dailyNote.noteText.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("daily_verse_user_reflection_box")
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.EditNote,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "আমার ব্যক্তিগত চিন্তন:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            IconButton(
                                onClick = {
                                    HapticHelper.performClick(null)
                                    viewModel.openNoteEditor(dailyVerse.chapterNumber, dailyVerse.verseNumber)
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "সম্পাদনা",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dailyNote.noteText,
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reading Streak & Daily Goal Engagement Bar
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("daily_verse_streak_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = SaffronPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "পঠন ধারা: ${progress.streakDays} দিন",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                    }
                    Text(
                        text = "আজকের পাঠ লক্ষ্য: ${progress.todayVersesCount}/${progress.dailyVerseGoal} শ্লোক",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Primary Engagement Action: "Mark Daily Verse as Read"
            val engagementBgColor by animateColorAsState(
                targetValue = if (isDailyVerseRead) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f),
                label = "daily_read_bg"
            )

            Surface(
                color = engagementBgColor,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    1.dp,
                    if (isDailyVerseRead) Color(0xFF81C784) else MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isDailyVerseRead) Icons.Default.CheckCircle else Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = if (isDailyVerseRead) Color(0xFF2E7D32) else SaffronPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isDailyVerseRead) "সাধুবাদ! আজকের সাধনা সম্পন্ন হয়েছে ✓" else "প্রতিদিনের পাঠে এই শ্লোকটি যুক্ত করুন",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDailyVerseRead) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    FilledTonalButton(
                        onClick = {
                            HapticHelper.performSuccess(context)
                            viewModel.toggleVerseRead(dailyVerse.chapterNumber, dailyVerse.verseNumber)
                            if (!isDailyVerseRead) {
                                Toast.makeText(
                                    context,
                                    "সাধুবাদ! আজকের দৈনিক শ্লোক পাঠ সম্পন্ন হয়েছে। আপনার ধারাবাহিকতা বজায় রাখুন।",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .testTag("mark_daily_verse_read_button")
                            .testTag("verse_of_the_day_mark_read_button")
                    ) {
                        Text(
                            text = if (isDailyVerseRead) "সম্পন্ন ✓" else "পাঠ সম্পন্ন (+১)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDailyVerseRead) Color(0xFF2E7D32) else SaffronPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Expandable Audio Player Component when Audio is Active
            if (playingVerseId == dailyVerse.verseId && playbackState != PlaybackState.IDLE) {
                VerseAudioPlayer(
                    verse = dailyVerse,
                    audioStateManager = viewModel.audioStateManager,
                    isCompact = true,
                    showVerseInfo = false,
                    showModeSelector = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("daily_verse_seek_slider")
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Action Buttons: Play/Pause Audio & Read Complete Verse
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        HapticHelper.performClick(null)
                        viewModel.togglePlayRecitation(dailyVerse)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SaffronPrimary),
                    modifier = Modifier
                        .testTag("play_daily_verse_audio_button")
                        .testTag("verse_of_the_day_audio_button")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "পজ" else "প্লে",
                        tint = SaffronPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlaying) "পজ করুন" else if (playbackState == PlaybackState.PAUSED && playingVerseId == dailyVerse.verseId) "পুনরায় চালান" else "আবৃত্তি শুনুন",
                        fontSize = 12.sp,
                        color = SaffronPrimary
                    )
                }

                Button(
                    onClick = {
                        HapticHelper.performClick(null)
                        viewModel.openVerseReader(dailyVerse.chapterNumber, dailyVerse.verseNumber)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SaffronPrimary
                    ),
                    modifier = Modifier
                        .testTag("open_daily_verse_reader_button")
                        .testTag("verse_of_the_day_read_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("সম্পূর্ণ শ্লোক পাঠ", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}
