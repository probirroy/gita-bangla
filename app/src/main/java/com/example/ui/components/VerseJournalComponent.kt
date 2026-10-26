package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Verse
import com.example.data.model.VerseNote
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.LotusTeal
import com.example.ui.theme.SaffronPrimary
import com.example.util.BengaliNumberHelper
import com.example.util.HapticHelper
import com.example.util.ShareHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Full-featured Verse Journal Component that enables users to write, edit, format,
 * categorize, share, and securely save personal spiritual reflections and insights
 * for any specific verse in the Bhagavad Gita.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VerseJournalComponent(
    verse: Verse,
    currentNote: VerseNote?,
    onSaveNote: (String) -> Unit,
    onDeleteNote: () -> Unit,
    modifier: Modifier = Modifier,
    initialExpanded: Boolean = false
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var isEditing by remember(verse.verseId) { mutableStateOf(initialExpanded) }
    var noteText by remember(verse.verseId, currentNote?.noteText) { mutableStateOf(currentNote?.noteText ?: "") }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(verse.verseId) {
        if (!initialExpanded) {
            isEditing = false
        }
        noteText = currentNote?.noteText ?: ""
    }

    val chBn = BengaliNumberHelper.toBengali(verse.chapterNumber)
    val vBn = BengaliNumberHelper.toBengali(verse.verseNumber)

    val reflectionPrompts = listOf(
        "আজকের জীবনে এই শ্লোকের শিক্ষা কী?",
        "আমার কর্মে ও কর্তব্যে এটি কীভাবে প্রয়োগ করব?",
        "মনকে স্থির ও সংযত রাখতে কী নির্দেশনা পেলাম?",
        "ঈশ্বরের প্রতি আমার ভক্তি ও কৃতজ্ঞতা কেমন?",
        "কোন্ রাগ বা আসক্তি আমি ত্যাগ করতে চাই?"
    )

    val spiritualTags = listOf(
        "#শান্তি",
        "#সংকল্প",
        "#কৃতজ্ঞতা",
        "#প্রার্থনা",
        "#জ্ঞান",
        "#কর্মযোগ"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("verse_journal_component")
            .testTag("verse_reflection_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (currentNote != null && currentNote.noteText.isNotBlank())
                SaffronPrimary.copy(alpha = 0.05f)
            else
                MaterialTheme.colorScheme.surface
        ),
        border = com.example.ui.theme.blueCardBorder(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header: Title, Persistence Indicator, and Actions
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
                            .background(SaffronPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = SaffronPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ব্যক্তিগত চিন্তন ডায়েরি",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SaffronPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "$chBn.$vBn",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronPrimary,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = LotusTeal,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "রুম ডেটাবেসে স্থায়ীভাবে সংরক্ষিত",
                                fontSize = 10.sp,
                                color = LotusTeal,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Top action icons when not editing
                if (!isEditing && currentNote != null && currentNote.noteText.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Copy to clipboard
                        IconButton(
                            onClick = {
                                HapticHelper.performClick(null)
                                clipboardManager.setText(AnnotatedString(currentNote.noteText))
                                Toast.makeText(context, "চিন্তন ক্লিপবোর্ডে কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("journal_copy_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "কপি করুন",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Share
                        IconButton(
                            onClick = {
                                HapticHelper.performClick(null)
                                ShareHelper.shareVerseWithReflection(context, verse, currentNote.noteText)
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("journal_share_button")
                                .testTag("share_verse_reflection_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "চিন্তন শেয়ার করুন",
                                tint = SaffronPrimary,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Edit
                        IconButton(
                            onClick = {
                                HapticHelper.performClick(null)
                                noteText = currentNote.noteText
                                isEditing = true
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("journal_edit_button")
                                .testTag("edit_verse_reflection_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "সম্পাদনা করুন",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        // Delete
                        IconButton(
                            onClick = {
                                HapticHelper.performClick(null)
                                showDeleteConfirmDialog = true
                            },
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("journal_delete_button")
                                .testTag("delete_verse_reflection_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "মুছুন",
                                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Body: View mode vs Edit mode
            if (isEditing) {
                // EDIT MODE
                Column {
                    OutlinedTextField(
                        value = noteText,
                        onValueChange = { noteText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("journal_note_input"),
                        placeholder = {
                            Text(
                                text = "এই শ্লোক থেকে আপনি কী উপলব্ধি করলেন? কীভাবে আপনার জীবনে বা সাধনায় এটি প্রয়োগ করবেন লিখুন...",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                lineHeight = 19.sp
                            )
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Character count & word count
                    val charCount = noteText.trim().length
                    val wordCount = if (noteText.isBlank()) 0 else noteText.trim().split(Regex("\\s+")).size
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${BengaliNumberHelper.toBengali(charCount)}টি অক্ষর • ${BengaliNumberHelper.toBengali(wordCount)}টি শব্দ",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )

                        // Quick Spiritual Tags
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            spiritualTags.take(3).forEachIndexed { index, tag ->
                                Surface(
                                    onClick = {
                                        HapticHelper.performClick(null)
                                        noteText = if (noteText.isBlank()) tag else "$noteText $tag"
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    color = SaffronPrimary.copy(alpha = 0.1f),
                                    modifier = Modifier.testTag("journal_tag_chip_$index")
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Inspiration Reflection Prompts
                    Text(
                        text = "অনুপ্রেরণামূলক চিন্তন সূত্র:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        reflectionPrompts.forEachIndexed { idx, prompt ->
                            SuggestionChip(
                                onClick = {
                                    HapticHelper.performClick(null)
                                    noteText = if (noteText.isBlank()) {
                                        "$prompt\n"
                                    } else {
                                        "$noteText\n$prompt\n"
                                    }
                                },
                                label = {
                                    Text(
                                        text = prompt,
                                        fontSize = 11.sp
                                    )
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = GoldAccent,
                                        modifier = Modifier.size(13.dp)
                                    )
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                ),
                                modifier = Modifier.testTag("journal_prompt_chip_$idx")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Save and Cancel Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                HapticHelper.performClick(null)
                                isEditing = false
                                noteText = currentNote?.noteText ?: ""
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("journal_cancel_button")
                        ) {
                            Text("বাতিল")
                        }

                        Button(
                            onClick = {
                                HapticHelper.performSuccess(context)
                                onSaveNote(noteText.trim())
                                isEditing = false
                                Toast.makeText(context, "চিন্তন সফলভাবে সংরক্ষিত হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SaffronPrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("journal_save_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("সংরক্ষণ করুন", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // VIEW MODE
                if (currentNote != null && currentNote.noteText.isNotBlank()) {
                    // Saved Reflection Card
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = com.example.ui.theme.blueCardBorder(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.Top) {
                                Icon(
                                    imageVector = Icons.Default.FormatQuote,
                                    contentDescription = null,
                                    tint = SaffronPrimary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = currentNote.noteText,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .testTag("journal_reflection_text")
                                        .testTag("verse_reflection_text")
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Metadata footer: Last updated timestamp
                            val dateStr = remember(currentNote.updatedAt) {
                                val sdf = SimpleDateFormat("d MMMM yyyy, hh:mm a", Locale.getDefault())
                                BengaliNumberHelper.toBengali(sdf.format(Date(currentNote.updatedAt)))
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "সর্বশেষ সম্পাদিত: $dateStr",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )

                                TextButton(
                                    onClick = {
                                        HapticHelper.performClick(null)
                                        noteText = currentNote.noteText
                                        isEditing = true
                                    },
                                    modifier = Modifier
                                        .testTag("journal_quick_edit_button")
                                        .testTag("add_note_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp),
                                        tint = SaffronPrimary
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "সম্পাদনা",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SaffronPrimary
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Empty state: No reflection yet
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "এই শ্লোকের ওপর এখনো কোনো ব্যক্তিগত নোট লেখা হয়নি। আপনার অন্তর্দৃষ্টি, প্রার্থনা বা সংকল্প লিখে সংরক্ষণ করুন।",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                            lineHeight = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                HapticHelper.performClick(null)
                                noteText = ""
                                isEditing = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SaffronPrimary,
                                contentColor = Color.White
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("journal_start_button")
                                .testTag("add_verse_reflection_button")
                                .testTag("add_note_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.EditNote,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ব্যক্তিগত চিন্তন লিখতে শুরু করুন",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = {
                Text(
                    text = "চিন্তন মুছে ফেলতে চান?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("এই শ্লোকের সংরক্ষিত ব্যক্তিগত চিন্তনটি স্থায়ীভাবে মুছে যাবে। আপনি কি নিশ্চিত?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        HapticHelper.performSuccess(context)
                        showDeleteConfirmDialog = false
                        onDeleteNote()
                        noteText = ""
                        isEditing = false
                        Toast.makeText(context, "চিন্তন মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("মুছুন")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("বাতিল")
                }
            }
        )
    }
}
