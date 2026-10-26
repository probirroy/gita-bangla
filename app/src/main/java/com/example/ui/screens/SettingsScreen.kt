package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.WifiOff
import com.example.audio.RecitationMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GitaViewModel
import com.example.util.BengaliNumberHelper
import com.example.util.HapticHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: GitaViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.userSettings.collectAsState()
    val progress by viewModel.readingProgress.collectAsState()
    val backupJson by viewModel.backupJson.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()
    val recitationMode by viewModel.recitationMode.collectAsState()
    val offlineAudioStatus by viewModel.offlineAudioStatus.collectAsState()
    val context = LocalContext.current

    var showImportDialog by remember { mutableStateOf(false) }
    var importText by remember { mutableStateOf("") }
    var showExportDialog by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleDailyReminder(true)
            Toast.makeText(context, "দৈনিক শ্লোক স্মরণের নোটিফিকেশন চালু করা হয়েছে", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "নোটিফিকেশন অনুমতি দেওয়া হয়নি", Toast.LENGTH_SHORT).show()
        }
    }

    val checkAndRequestNotificationPermission: (() -> Unit) -> Unit = { onGranted ->
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val hasPerm = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (hasPerm) {
                onGranted()
            } else {
                notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            onGranted()
        }
    }

    Scaffold(
        modifier = modifier.testTag("settings_screen"),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "সেটিংস ও সিঙ্ক (Settings)",
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme Appearance
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = com.example.ui.theme.blueCardBorder(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ColorLens,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "থিম ও ডিসপ্লে (Theme)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val themes = listOf(
                                "SYSTEM" to "সিস্টেম",
                                "LIGHT" to "শ্বেত",
                                "DARK" to "নিশীথ (ডার্ক)",
                                "SEPIA" to "তালপাতা"
                            )

                            themes.forEach { (mode, label) ->
                                FilterChip(
                                    selected = settings.themeMode == mode,
                                    onClick = {
                                        HapticHelper.performClick(null)
                                        viewModel.updateReadingSettings(settings.copy(themeMode = mode))
                                    },
                                    label = { Text(label, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                }
            }

            // Typography & Font Sizes (Font Size Adjustment Slider)
            item {
                var showFineTuning by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("font_size_settings_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = com.example.ui.theme.blueCardBorder(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Section Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatSize,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "ফন্ট সাইজ ও পাঠের অভিজ্ঞতা",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "গীতাপাঠের অক্ষর আকার ও সুপাঠ্যতা কাস্টমাইজ করুন",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Reset to defaults
                            IconButton(
                                onClick = {
                                    HapticHelper.performClick(null)
                                    viewModel.updateReadingSettings(
                                        settings.copy(
                                            shlokaFontSize = 19f,
                                            translationFontSize = 16f,
                                            purportFontSize = 15f
                                        )
                                    )
                                    Toast.makeText(context, "ডিফল্ট ফন্ট সাইজ পুনঃস্থাপন করা হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("reset_font_size_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = "ডিফল্ট সাইজে ফিরুন",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(19.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // --- DEDICATED VERSE TEXT FONT SIZE ADJUSTMENT SLIDER ---
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "শ্লোক পাঠের ফন্ট সাইজ (Verse Text Size):",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "গীতার মূল শ্লোক ও বঙ্গানুবাদ পাঠের স্পষ্টতা বাড়ান",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    val sizeLabel = when {
                                        settings.shlokaFontSize <= 16f -> "ছোট"
                                        settings.shlokaFontSize <= 20f -> "স্বাভাবিক"
                                        settings.shlokaFontSize <= 25f -> "বড়"
                                        else -> "খুব বড়"
                                    }
                                    Text(
                                        text = "${com.example.util.BengaliNumberHelper.toBengali(settings.shlokaFontSize.toInt())} sp",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "• $sizeLabel",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Visual indicator range: small 'A' to large 'A'
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ছোট ক (${com.example.util.BengaliNumberHelper.toBengali(14)} sp)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "বড় ক (${com.example.util.BengaliNumberHelper.toBengali(32)} sp)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Verse Font Size Slider
                        Slider(
                            value = settings.shlokaFontSize,
                            onValueChange = { newShlokaSize ->
                                val scale = newShlokaSize / 19f
                                val newTrans = (16f * scale).coerceIn(12f, 26f)
                                val newPurport = (15f * scale).coerceIn(11f, 24f)
                                viewModel.updateReadingSettings(
                                    settings.copy(
                                        shlokaFontSize = newShlokaSize,
                                        translationFontSize = newTrans,
                                        purportFontSize = newPurport
                                    )
                                )
                            },
                            valueRange = 14f..32f,
                            steps = 17,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("verse_font_size_slider")
                                .testTag("font_size_adjustment_slider")
                                .testTag("font_size_slider")
                                .testTag("shloka_font_size_slider")
                        )

                        // Quick Preset Chips (Small, Default, Large, Extra Large)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val presets = listOf(
                                Triple("ছোট (${com.example.util.BengaliNumberHelper.toBengali(16)} sp)", 16f, "font_size_preset_small"),
                                Triple("স্বাভাবিক (${com.example.util.BengaliNumberHelper.toBengali(19)} sp)", 19f, "font_size_preset_default"),
                                Triple("বড় (${com.example.util.BengaliNumberHelper.toBengali(23)} sp)", 23f, "font_size_preset_large"),
                                Triple("খুব বড় (${com.example.util.BengaliNumberHelper.toBengali(27)} sp)", 27f, "font_size_preset_extra_large")
                            )

                            presets.forEach { (label, shlokaSize, tag) ->
                                val isSelected = settings.shlokaFontSize.toInt() == shlokaSize.toInt()
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        HapticHelper.performClick(null)
                                        val scale = shlokaSize / 19f
                                        viewModel.updateReadingSettings(
                                            settings.copy(
                                                shlokaFontSize = shlokaSize,
                                                translationFontSize = (16f * scale).coerceIn(12f, 26f),
                                                purportFontSize = (15f * scale).coerceIn(11f, 24f)
                                            )
                                        )
                                    },
                                    label = { Text(label, fontSize = 10.sp) },
                                    modifier = Modifier.testTag(tag)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Toggle Fine-tuning of Individual Font Sizes
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showFineTuning = !showFineTuning }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (showFineTuning) "নির্দিষ্ট অংশের আকার গোপন করুন" else "শ্লোক, অনুবাদ ও তাৎপর্যের আলাদা আকার পরিবর্তন করুন...",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        if (showFineTuning) {
                            Spacer(modifier = Modifier.height(8.dp))

                            // 1. Bengali shloka font size slider
                            Text(
                                text = "বাংলা শ্লোকের ফন্টের আকার: ${settings.shlokaFontSize.toInt()} sp",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Slider(
                                value = settings.shlokaFontSize,
                                onValueChange = {
                                    viewModel.updateReadingSettings(settings.copy(shlokaFontSize = it))
                                },
                                valueRange = 14f..32f,
                                steps = 17,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("shloka_font_size_slider")
                            )

                            // 2. Bengali translation font size slider
                            Text(
                                text = "বঙ্গানুবাদের আকার: ${settings.translationFontSize.toInt()} sp",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Slider(
                                value = settings.translationFontSize,
                                onValueChange = {
                                    viewModel.updateReadingSettings(settings.copy(translationFontSize = it))
                                },
                                valueRange = 12f..26f,
                                steps = 13,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("translation_font_size_slider")
                            )

                            // 3. Purport font size slider
                            Text(
                                text = "তাৎপর্য ও আত্মচিন্তনের আকার: ${settings.purportFontSize.toInt()} sp",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Slider(
                                value = settings.purportFontSize,
                                onValueChange = {
                                    viewModel.updateReadingSettings(settings.copy(purportFontSize = it))
                                },
                                valueRange = 11f..24f,
                                steps = 12,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("purport_font_size_slider")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // --- LIVE READING PREVIEW CARD ---
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(14.dp),
                            border = com.example.ui.theme.blueCardBorder(width = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("font_size_preview_card")
                                .testTag("font_size_preview_box")
                                .testTag("verse_text_preview")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.FormatSize,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "শ্লোক পাঠের সরাসরি পূর্বরূপ:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = "অধ্যায় ২, শ্লোক ৪৭",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Bangla Shlok Preview
                                Text(
                                    text = "॥ কর্মণ্যেবাধিকারস্তে মা ফলেষু কদাচন ॥",
                                    fontSize = settings.shlokaFontSize.sp,
                                    lineHeight = (settings.shlokaFontSize * 1.4f).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Translation Preview
                                Text(
                                    text = "সরল বঙ্গানুবাদ: কর্মেই তোমার একমাত্র অধিকার, কিন্তু কর্মফলে কখনোই তোমার কোনো অধিকার নেই।",
                                    fontSize = settings.translationFontSize.sp,
                                    lineHeight = (settings.translationFontSize * 1.45f).sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Purport Preview
                                Text(
                                    text = "তাৎপর্য: নিষ্কাম কর্মই চিত্তশুদ্ধির পরম সাধন। ফলাকাঙ্ক্ষা ত্যাগ করে ঈশ্বরার্পিত চিত্তে কর্তব্য করাই গীতার অমর বাণী।",
                                    fontSize = settings.purportFontSize.sp,
                                    lineHeight = (settings.purportFontSize * 1.5f).sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Audio Recitation Settings
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = com.example.ui.theme.blueCardBorder(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "শ্লোক আবৃত্তি সেটিংস (Audio Recitation)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Offline status pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WifiOff,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "১০০% অফলাইন অডিও সিস্টেম",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "ইন্টারনেট সংযোগ ছাড়াই যেকোনো স্থানে গীতা আবৃত্তি উপভোগ করুন",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Default recitation mode
                        Text(
                            text = "আবৃত্তি বিকল্প (Recitation Content):",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = recitationMode == RecitationMode.TRANSLATION_ONLY,
                                onClick = {
                                    HapticHelper.performClick(null)
                                    viewModel.setRecitationMode(RecitationMode.TRANSLATION_ONLY)
                                },
                                label = { Text("বাংলা অনুবাদ", fontSize = 11.sp) }
                            )

                            FilterChip(
                                selected = recitationMode == RecitationMode.SHLOKA_ONLY,
                                onClick = {
                                    HapticHelper.performClick(null)
                                    viewModel.setRecitationMode(RecitationMode.SHLOKA_ONLY)
                                },
                                label = { Text("শ্লোক আবৃত্তি", fontSize = 11.sp) }
                            )

                            FilterChip(
                                selected = recitationMode == RecitationMode.BOTH,
                                onClick = {
                                    HapticHelper.performClick(null)
                                    viewModel.setRecitationMode(RecitationMode.BOTH)
                                },
                                label = { Text("উভয়ই", fontSize = 11.sp) }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "আবৃত্তি গতি: ${settings.recitationSpeed}x",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val speeds = listOf(0.8f, 0.95f, 1.1f, 1.25f)
                            speeds.forEach { speed ->
                                FilterChip(
                                    selected = settings.recitationSpeed == speed,
                                    onClick = {
                                        HapticHelper.performClick(null)
                                        viewModel.updateReadingSettings(settings.copy(recitationSpeed = speed))
                                    },
                                    label = { Text("${speed}x") }
                                )
                            }
                        }
                    }
                }
            }

            // --- OFFLINE CHAPTER DOWNLOADS & ACCESSIBILITY ---
            item {
                var isChapterListExpanded by remember { mutableStateOf(true) }
                var offlineFilterSelection by remember { mutableStateOf(0) } // 0: All 18, 1: Downloaded, 2: Not Downloaded

                val downloadedCount = settings.downloadedChapters.size
                val totalCount = viewModel.chapters.size
                val isAllDownloaded = downloadedCount == totalCount && totalCount > 0

                val displayedChapters = when (offlineFilterSelection) {
                    1 -> viewModel.chapters.filter { settings.downloadedChapters.contains(it.number) }
                    2 -> viewModel.chapters.filter { !settings.downloadedChapters.contains(it.number) }
                    else -> viewModel.chapters
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("offline_download_settings_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = com.example.ui.theme.blueCardBorder(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Section Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CloudDownload,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "অফলাইন অধ্যায় ডাউনলোড (Offline Reading)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "ইন্টারনেট সংযোগ ছাড়াই যেকোনো স্থানে গীতা পাঠের সুবিধা",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Master Toggle for Offline Chapter Reading
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (settings.offlineReadingEnabled) {
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            },
                            border = com.example.ui.theme.blueCardBorder(width = 1.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (settings.offlineReadingEnabled) Icons.Default.CloudDone else Icons.Default.WifiOff,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "অফলাইন পাঠ সক্রিয় করুন",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = if (settings.offlineReadingEnabled) {
                                            "সক্রিয়: সংরক্ষিত অধ্যায়সমূহ নেটওয়ার্ক ছাড়াই সরাসরি উন্মুক্ত থাকবে"
                                        } else {
                                            "নিষ্ক্রিয়: নির্দিষ্ট অধ্যায় ডাউনলোড করে অফলাইন পাঠের সুবিধা নিন"
                                        },
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = settings.offlineReadingEnabled,
                                    onCheckedChange = { enabled ->
                                        HapticHelper.performClick(null)
                                        viewModel.toggleOfflineReading(enabled)
                                        if (enabled && settings.downloadedChapters.isEmpty()) {
                                            viewModel.toggleChapterDownload(1)
                                            viewModel.toggleChapterDownload(2)
                                        }
                                        Toast.makeText(
                                            context,
                                            if (enabled) "অফলাইন পাঠ মোড সক্রিয় হয়েছে" else "অফলাইন পাঠ মোড নিষ্ক্রিয় করা হয়েছে",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier
                                        .testTag("offline_reading_toggle")
                                        .testTag("offline_chapters_toggle")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Offline Storage Status & Progress Bar
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = com.example.ui.theme.blueCardBorder(width = 1.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("offline_storage_badge")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "অফলাইন সংরক্ষণ স্থিতি:",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (downloadedCount > 0) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = "${BengaliNumberHelper.toBengali(downloadedCount)} / ১৮টি অধ্যায় সংরক্ষিত",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (downloadedCount > 0) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                val downloadRatio = if (totalCount > 0) downloadedCount.toFloat() / totalCount.toFloat() else 0f
                                LinearProgressIndicator(
                                    progress = { downloadRatio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .testTag("offline_download_progress_bar"),
                                    color = if (isAllDownloaded) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isAllDownloaded) "সম্পূর্ণ ১৮টি অধ্যায় অফলাইনে প্রস্তুত ✓" else "${BengaliNumberHelper.toBengali(18 - downloadedCount)}টি অধ্যায় ডাউনলোড করা বাকি",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${(downloadRatio * 100).toInt()}% সমাপ্ত",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Actions: Download All or Clear All
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    HapticHelper.performSuccess(context)
                                    viewModel.downloadAllChapters()
                                    Toast.makeText(context, "সকল ১৮টি অধ্যায় অফলাইনে সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("download_all_chapters_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudDownload,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("সকল অধ্যায় ডাউনলোড", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    HapticHelper.performClick(null)
                                    viewModel.clearDownloadedChapters()
                                    Toast.makeText(context, "অফলাইন সংরক্ষণ খালি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(0.9f)
                                    .testTag("clear_offline_chapters_button")
                            ) {
                                Text("সংরক্ষণ মুছুন", fontSize = 11.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Filter Chips for Chapters List (All, Downloaded, Pending)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = offlineFilterSelection == 0,
                                onClick = { offlineFilterSelection = 0 },
                                label = { Text("সকল ১৮", fontSize = 10.sp) },
                                modifier = Modifier.testTag("offline_filter_all")
                            )
                            FilterChip(
                                selected = offlineFilterSelection == 1,
                                onClick = { offlineFilterSelection = 1 },
                                label = { Text("ডাউনলোডকৃত (${BengaliNumberHelper.toBengali(downloadedCount)})", fontSize = 10.sp) },
                                modifier = Modifier.testTag("offline_filter_downloaded")
                            )
                            FilterChip(
                                selected = offlineFilterSelection == 2,
                                onClick = { offlineFilterSelection = 2 },
                                label = { Text("বাকি (${BengaliNumberHelper.toBengali(totalCount - downloadedCount)})", fontSize = 10.sp) },
                                modifier = Modifier.testTag("offline_filter_pending")
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Individual Chapter Download Toggles Header & Accordion
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isChapterListExpanded = !isChapterListExpanded }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "নির্দিষ্ট অধ্যায় ডাউনলোড তালিকা (${BengaliNumberHelper.toBengali(displayedChapters.size)}টি):",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Icon(
                                imageVector = if (isChapterListExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (isChapterListExpanded) {
                            Spacer(modifier = Modifier.height(6.dp))

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                displayedChapters.forEach { chapter ->
                                    val isDownloaded = settings.downloadedChapters.contains(chapter.number)

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (isDownloaded) {
                                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        },
                                        border = com.example.ui.theme.blueCardBorder(width = 1.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("offline_chapter_card_${chapter.number}")
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .padding(horizontal = 12.dp, vertical = 10.dp)
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f).padding(end = 8.dp)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(32.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            if (isDownloaded) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = BengaliNumberHelper.toBengali(chapter.number),
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isDownloaded) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = "অধ্যায় ${BengaliNumberHelper.toBengali(chapter.number)}: ${chapter.nameBengali}",
                                                        fontWeight = FontWeight.SemiBold,
                                                        fontSize = 12.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Text(
                                                            text = "${BengaliNumberHelper.toBengali(chapter.verseCount)}টি শ্লোক",
                                                            fontSize = 10.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = "•",
                                                            fontSize = 10.sp,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = if (isDownloaded) "অফলাইন প্রস্তুত ✓" else "ডাউনলোড প্রয়োজন",
                                                            fontSize = 10.sp,
                                                            fontWeight = if (isDownloaded) FontWeight.Bold else FontWeight.Normal,
                                                            color = if (isDownloaded) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            }

                                            // Toggle switch for specific chapter
                                            Switch(
                                                checked = isDownloaded,
                                                onCheckedChange = {
                                                    HapticHelper.performClick(null)
                                                    viewModel.toggleChapterDownload(chapter.number)
                                                },
                                                modifier = Modifier
                                                    .testTag("offline_chapter_toggle_${chapter.number}")
                                                    .testTag("offline_chapter_switch_${chapter.number}")
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Haptics & Mindfulness Notifications
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = com.example.ui.theme.blueCardBorder(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "অনুশীলন ও হ্যাপটিক্স (Mindfulness & Practice)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Haptics Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("হ্যাপটিক প্রতিক্রিয়া (Vibration)", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text("ট্যাপ ও পাঠে স্পর্শানুভূতি", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = settings.hapticEnabled,
                                onCheckedChange = {
                                    HapticHelper.performSuccess(context)
                                    viewModel.updateReadingSettings(settings.copy(hapticEnabled = it))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Daily Reminder Notification
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                Text("দৈনিক সাধনা স্মরণ (Daily Verse)", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                                Text("WorkManager টাস্কের মাধ্যমে প্রতিদিন একটি নতুন বাংলা শ্লোক স্মরণিকা", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                modifier = Modifier.testTag("settings_daily_reminder_switch"),
                                checked = settings.dailyReminderEnabled,
                                onCheckedChange = { enabled ->
                                    if (enabled) {
                                        checkAndRequestNotificationPermission {
                                            viewModel.toggleDailyReminder(true)
                                        }
                                    } else {
                                        viewModel.toggleDailyReminder(false)
                                    }
                                }
                            )
                        }

                        if (settings.dailyReminderEnabled) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "বিজ্ঞপ্তির সময়: ${String.format("%02d:%02d", settings.reminderHour, settings.reminderMinute)}",
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val timePresets = listOf(
                                    Pair(5, 0) to "ভোর ৫:০০",
                                    Pair(7, 0) to "সকাল ৭:০০",
                                    Pair(13, 0) to "দুপুর ১:০০",
                                    Pair(18, 0) to "সন্ধ্যা ৬:০০",
                                    Pair(21, 0) to "রাত ৯:০০"
                                )
                                timePresets.forEach { (time, label) ->
                                    val (hour, minute) = time
                                    val isSelected = settings.reminderHour == hour && settings.reminderMinute == minute
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            HapticHelper.performTick(context)
                                            viewModel.updateReminderTime(hour, minute)
                                            Toast.makeText(context, "দৈনিক নোটিফিকেশনের সময়: $label", Toast.LENGTH_SHORT).show()
                                        },
                                        label = { Text(label, fontSize = 11.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "WorkManager ব্যাকগ্রাউন্ড টাস্ক স্বয়ংক্রিয়ভাবে অফলাইনেও প্রতিদিন একটি র্যান্ডম বাংলা শ্লোক নোটিফিকেশন প্রদান করে।",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = {
                                HapticHelper.performSuccess(context)
                                checkAndRequestNotificationPermission {
                                    viewModel.triggerDailyNotification()
                                    Toast.makeText(context, "আজকের শ্লোক নোটিফিকেশন পাঠানো হয়েছে!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("test_daily_notification_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("স্মরণ নোটিফিকেশন পরীক্ষা করুন (Test)")
                        }
                    }
                }
            }

            // Daily Reading Goals (দৈনিক পাঠের লক্ষ্যমাত্রা)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("settings_daily_goals_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = com.example.ui.theme.blueCardBorder(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "দৈনিক পাঠ সাধনার লক্ষ্য (Daily Goals)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Daily Verse Goal
                        Text(
                            text = "দৈনিক শ্লোক পাঠের লক্ষ্য:",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "প্রতিদিন কতগুলো শ্লোক পাঠ সম্পন্ন করতে চান (বর্তমান: ${progress.dailyVerseGoal}টি শ্লোক)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(3, 5, 10, 15, 20).forEach { goal ->
                                FilterChip(
                                    selected = progress.dailyVerseGoal == goal,
                                    onClick = {
                                        HapticHelper.performTick(context)
                                        viewModel.updateDailyVerseGoal(goal)
                                    },
                                    label = { Text("${goal}টি", fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Daily Study Time Goal
                        Text(
                            text = "দৈনিক অধ্যয়নের সময় লক্ষ্য:",
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "প্রতিদিন কত মিনিট গীতা চর্চায় উৎসর্গ করবেন (বর্তমান: ${progress.dailyGoalMinutes} মিনিট)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(5, 10, 15, 20, 30).forEach { mins ->
                                FilterChip(
                                    selected = progress.dailyGoalMinutes == mins,
                                    onClick = {
                                        HapticHelper.performTick(context)
                                        viewModel.updateDailyGoalMinutes(mins)
                                    },
                                    label = { Text("${mins}মি", fontSize = 11.sp) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = {
                                HapticHelper.performClick(context)
                                viewModel.navigateTo(AppScreen.WEEKLY_INSIGHTS)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("settings_open_weekly_insights_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("সাপ্তাহিক অন্তর্দৃষ্টি ও ধারাবাহিকতা চার্ট দেখুন")
                        }
                    }
                }
            }

            // Multi-Device Data Sync & Backup
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = com.example.ui.theme.blueCardBorder(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudUpload,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "মাল্টি-ডিভাইস সিঙ্ক ও ব্যাকআপ (Sync & Backup)",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "আপনার সমস্ত প্রিয় বুকমার্ক, ব্যক্তিগত চিন্তন নোট এবং পাঠ সাধনার অগ্রগতি নিরাপদে অন্য ডিভাইসে স্থানান্তর ও ব্যাকআপ করুন।",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    HapticHelper.performClick(null)
                                    viewModel.exportBackup()
                                    showExportDialog = true
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("এক্সপোর্ট", fontSize = 13.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    HapticHelper.performClick(null)
                                    showImportDialog = true
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("ইম্পোর্ট / সিঙ্ক", fontSize = 13.sp)
                            }
                        }

                        if (syncMessage != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = syncMessage ?: "",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Export Dialog
    if (showExportDialog && backupJson != null) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("ব্যাকআপ ডেটা প্রস্তুত (Export Ready)") },
            text = {
                Column {
                    Text(
                        text = "আপনার বুকমার্ক ও চিন্তন নোটের ডেটা নিচে প্রস্তুত রয়েছে। কপি করে বা শেয়ার করে অন্য ডিভাইসে নিতে পারেন:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = backupJson ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Gita Backup", backupJson))
                        Toast.makeText(context, "ক্লিপবোর্ডে কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                        showExportDialog = false
                    }
                ) {
                    Text("কপি করুন")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        val shareIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, backupJson)
                            type = "application/json"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "ব্যাকআপ শেয়ার করুন"))
                        showExportDialog = false
                    }
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("শেয়ার")
                }
            }
        )
    }

    // Import Dialog
    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("অন্য ডিভাইস থেকে ডেটা সিঙ্ক করুন") },
            text = {
                Column {
                    Text(
                        text = "পূর্ববর্তী ডিভাইসের এক্সপোর্ট করা JSON ব্যাকআপ ডেটা এখানে পেস্ট করুন:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importText,
                        onValueChange = { importText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        placeholder = { Text("{ \"app\": \"GitaBangla\", ... }") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (importText.isNotBlank()) {
                            viewModel.importBackup(importText)
                            Toast.makeText(context, "সিঙ্ক সম্পন্ন হয়েছে!", Toast.LENGTH_SHORT).show()
                            showImportDialog = false
                            importText = ""
                        }
                    }
                ) {
                    Text("পুনরুদ্ধার ও সিঙ্ক")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showImportDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
