package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AudioStateManager
import com.example.audio.PlaybackState
import com.example.data.model.Verse
import com.example.data.source.GitaContentProvider
import com.example.ui.components.VerseCardAudioPlayer
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LotusTeal
import com.example.ui.theme.SaffronPrimary
import com.example.ui.viewmodel.GitaViewModel
import com.example.util.BengaliNumberHelper
import com.example.util.HapticHelper

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    viewModel: GitaViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val currentPlayingVerse by viewModel.currentPlayingVerse.collectAsState()

    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val categoryTabs = listOf("সব পরামর্শ", "অধ্যায় ও শ্লোক", "জনপ্রিয় বিষয়")

    val chapterVerseSuggestions = listOf(
        "২.৪৭" to "কর্মণ্যেবাধিকারস্তে (কর্মযোগ)",
        "৪.৭" to "যদা যদা হি ধর্মস্য (ধর্মসংস্থাপন)",
        "৯.২২" to "অনন্যাশ্চিন্তয়ন্তো (যোগক্ষেম)",
        "১৮.৬৬" to "সর্বধর্মান্ পরিত্যজ্য (শরণাগতি)",
        "৬.৫" to "উদ্ধরেদাত্মনাত্মানং (মনোসংযম)",
        "২.২০" to "ন জায়তে ম্রিয়তে বা (আত্মার নিত্যত্ব)",
        "অধ্যায় ২" to "সাংখ্য যোগ (৭২ শ্লোক)",
        "অধ্যায় ৩" to "কর্ম যোগ (৪৩ শ্লোক)",
        "অধ্যায় ১২" to "ভক্তি যোগ (২০ শ্লোক)",
        "অধ্যায় ১৮" to "মোক্ষসংন্যাস যোগ (৭৮ শ্লোক)",
        "শ্লোক ৪৭" to "সকল অধ্যায়ের ৪৭ সংখ্যক শ্লোক"
    )

    val topicSuggestions = listOf(
        "কর্ম" to "নিষ্কাম কর্ম ও কর্তব্যসাধন",
        "ভক্তি" to "অনন্য প্রেম ও শরণাগতি",
        "জ্ঞান" to "দিব্য আত্মজ্ঞান ও প্রজ্ঞা",
        "আত্মা" to "অবিনাশী ও শাশ্বত স্বরূপ",
        "ক্রোধ" to "ক্রোধ সংবরণ ও বুদ্ধিশুদ্ধি",
        "ধ্যান" to "চিত্তৈকাগ্রতা ও স্থিরতা",
        "মন" to "মনোনিগ্রহ ও আত্মউদ্ধার",
        "শান্তি" to "স্থিতপ্রজ্ঞতার পরম প্রশান্তি",
        "মোক্ষ" to "চিরন্তন মুক্তি ও পরমগতি",
        "ধর্ম" to "স্বধর্ম পালন ও সত্যের জয়",
        "শরণ" to "ঈশ্বরে আত্মসমর্পণ"
    )

    Scaffold(
        modifier = modifier.testTag("search_screen"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SaffronPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = SaffronPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "গীতা শ্লোক অনুসন্ধান",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
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
            // Search Input Field
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                ),
                border = com.example.ui.theme.blueCardBorder(),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input_field"),
                    placeholder = {
                        Text(
                            text = "শ্লোক নম্বর (২.৪৭), অধ্যায় বা বিষয় লিখুন...",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = SaffronPrimary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    HapticHelper.performClick(null)
                                    viewModel.onSearchQueryChanged("")
                                },
                                modifier = Modifier.testTag("search_clear_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "মুছুন",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )
            }

            if (searchQuery.isBlank()) {
                // Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categoryTabs.forEachIndexed { index, title ->
                        FilterChip(
                            selected = selectedCategoryIndex == index,
                            onClick = {
                                HapticHelper.performClick(null)
                                selectedCategoryIndex = index
                            },
                            label = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SaffronPrimary,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("search_category_chip_$index")
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Quick Search Suggestions
                    item {
                        Text(
                            text = when (selectedCategoryIndex) {
                                1 -> "অধ্যায় ও নির্দিষ্ট শ্লোক সূচক:"
                                2 -> "মূল আধ্যাত্মিক ভাবধারা ও বিষয়:"
                                else -> "জনপ্রিয় অনুসন্ধান পরামর্শ:"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val displayItems = when (selectedCategoryIndex) {
                            1 -> chapterVerseSuggestions
                            2 -> topicSuggestions
                            else -> chapterVerseSuggestions.take(6) + topicSuggestions.take(6)
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            displayItems.forEachIndexed { idx, pair ->
                                SuggestionChip(
                                    onClick = {
                                        HapticHelper.performClick(null)
                                        viewModel.onSearchQueryChanged(pair.first)
                                    },
                                    label = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = pair.first,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = SaffronPrimary
                                            )
                                            if (pair.second.isNotBlank()) {
                                                Text(
                                                    text = " • ${pair.second}",
                                                    fontSize = 11.sp,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = SuggestionChipDefaults.suggestionChipColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ),
                                    modifier = Modifier.testTag("chip_suggestion_$idx")
                                )
                            }
                        }
                    }

                    // Guidance Information Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = LotusTeal.copy(alpha = 0.08f)
                            ),
                            border = com.example.ui.theme.blueCardBorder()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(LotusTeal.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = LotusTeal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "কীভাবে অনুসন্ধান করবেন?",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = LotusTeal
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "১. শ্লোক নম্বর: বাংলা বা ইংরেজি অঙ্কে লিখুন—যেমন '২.৪৭', '২:৪৭' বা 'অধ্যায় ২ শ্লোক ৪৭'।\n" +
                                                "২. অধ্যায়ভিত্তিক: 'অধ্যায় ২' বা 'অধ্যায় ৪' লিখে পুরো অধ্যায়ের সারসংক্ষেপ ও শ্লোক দেখুন।\n" +
                                                "৩. শব্দার্থ বা ভাবার্থ: 'কর্ম', 'ভক্তি', 'আত্মা', 'ক্রোধ', 'মন' ইত্যাদি শব্দ দিয়ে তাৎক্ষণিক খুঁজে নিন।",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 20.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Search Results Header with Bengali numeral count
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val countBengali = BengaliNumberHelper.toBengali(searchResults.size)
                    Text(
                        text = "$countBengali টি শ্লোক পাওয়া গেছে",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )

                    Text(
                        text = "অনুসন্ধান: '$searchQuery'",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (searchResults.isEmpty()) {
                    // Empty state when nothing matches
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            border = com.example.ui.theme.blueCardBorder()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "কোনো শ্লোক খুঁজে পাওয়া যায়নি",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "'$searchQuery' এর সাথে কোনো শ্লোক মেলেনি। বানান পরীক্ষা করুন বা অন্য শব্দ যেমন '২.৪৭', 'অধ্যায় ২', 'কর্ম', 'ভক্তি' লিখে চেষ্টা করুন।",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(
                                    onClick = {
                                        HapticHelper.performClick(null)
                                        viewModel.onSearchQueryChanged("")
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.testTag("reset_search_button")
                                ) {
                                    Text("অনুসন্ধান রিসেট করুন")
                                }
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(searchResults, key = { it.verseId }) { verse ->
                            val chapter = GitaContentProvider.chapters.find { it.number == verse.chapterNumber }
                            val isBookmarked = bookmarks.any {
                                it.chapterNumber == verse.chapterNumber && it.verseNumber == verse.verseNumber
                            }
                            SearchResultVerseCard(
                                verse = verse,
                                chapterNameBengali = chapter?.nameBengali ?: "",
                                isBookmarked = isBookmarked,
                                audioStateManager = viewModel.audioStateManager,
                                onOpenVerse = {
                                    HapticHelper.performClick(null)
                                    viewModel.openVerseReader(verse.chapterNumber, verse.verseNumber)
                                },
                                onToggleBookmark = {
                                    HapticHelper.performClick(null)
                                    viewModel.toggleBookmarkForVerse(verse.chapterNumber, verse.verseNumber)
                                },
                                onShare = {
                                    HapticHelper.performClick(null)
                                    viewModel.openShareVerseDialog(verse)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Individual Search Result Card with pure Bengali content, audio recitation trigger,
 * bookmark toggle, and two-option share action.
 */
@Composable
private fun SearchResultVerseCard(
    verse: Verse,
    chapterNameBengali: String,
    isBookmarked: Boolean,
    audioStateManager: AudioStateManager,
    onOpenVerse: () -> Unit,
    onToggleBookmark: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chBn = BengaliNumberHelper.toBengali(verse.chapterNumber)
    val vBn = BengaliNumberHelper.toBengali(verse.verseNumber)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenVerse)
            .testTag("search_result_${verse.verseId}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = com.example.ui.theme.blueCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Chapter/Verse badge, Chapter Title, Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SaffronPrimary.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "অধ্যায় $chBn • শ্লোক $vBn",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SaffronPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = chapterNameBengali,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Bookmark & Share Action Icons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("bookmark_search_result_${verse.verseId}")
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = if (isBookmarked) "বুকমার্ক সরানো" else "বুকমার্ক করা",
                            tint = if (isBookmarked) GoldAccent else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("share_search_result_${verse.verseId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "শ্লোক শেয়ার করুন",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(19.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bengali Shloka Pronunciation (Pure Bangla Script)
            Text(
                text = verse.pronunciationBengali.lines().firstOrNull() ?: "",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Bengali Translation
            Text(
                text = verse.translationBengali,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Audio Player Component for this Verse Card
            VerseCardAudioPlayer(
                verse = verse,
                audioStateManager = audioStateManager
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Open Reader link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clickable(onClick = onOpenVerse)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("open_verse_reader_${verse.verseId}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "সম্পূর্ণ শ্লোক ও তাৎপর্য পড়ুন",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
