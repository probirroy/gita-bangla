package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.data.model.VerseNote
import com.example.data.source.GitaContentProvider
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReflectionsExportHelper {

    fun getDefaultBackupFileName(): String {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "Gita_Reflections_Backup_$timeStamp.txt"
    }

    fun generateReflectionsBackupText(notes: List<VerseNote>): String {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy, hh:mm a", Locale.getDefault())
        val exportTimestamp = dateFormat.format(Date())

        return buildString {
            append("=================================================================\n")
            append("      শ্রীমদ্ভগবদ্গীতা • ব্যক্তিগত চিন্তন ও উপলব্ধি (Reflections & Notes)\n")
            append("              অফলাইন টেক্সট ফাইল ব্যাকআপ (Offline Backup)\n")
            append("=================================================================\n")
            append("রপ্তানির তারিখ ও সময়: $exportTimestamp\n")
            append("মোট সংরক্ষিত চিন্তন ও নোট: ${notes.size} টি\n")
            append("সংগ্রহকারী: গীতা বাংলা (Gita Bangla App)\n")
            append("=================================================================\n\n")

            if (notes.isEmpty()) {
                append("এখনো কোনো ব্যক্তিগত চিন্তন বা উপলব্ধি সংরক্ষণ করা হয়নি।\n")
            } else {
                notes.sortedWith(compareBy({ it.chapterNumber }, { it.verseNumber })).forEachIndexed { index, note ->
                    val chapter = GitaContentProvider.chapters.find { it.number == note.chapterNumber }
                    val verse = GitaContentProvider.getVerse(note.chapterNumber, note.verseNumber)
                    val noteDate = dateFormat.format(Date(note.updatedAt))

                    append("-----------------------------------------------------------------\n")
                    append("[${index + 1}] অধ্যায় ${note.chapterNumber}: ${chapter?.nameBengali ?: ""} (${chapter?.nameSanskrit ?: ""})\n")
                    append("    শ্লোক নম্বর: ${note.chapterNumber}.${note.verseNumber}\n")
                    append("-----------------------------------------------------------------\n\n")

                    if (verse != null) {
                        append("॥ সংস্কৃত মূল শ্লোক ॥\n")
                        append(verse.shlokaSanskrit.trim())
                        append("\n\n")

                        append("॥ উচ্চারণ (বাংলায়) ॥\n")
                        append(verse.pronunciationBengali.trim())
                        append("\n\n")

                        append("॥ সরল বঙ্গানুবাদ ॥\n")
                        append(verse.translationBengali.trim())
                        append("\n\n")
                    }

                    append("★ আমার ব্যক্তিগত চিন্তন ও আত্মোপলব্ধি (Personal Reflection):\n")
                    append(note.noteText.trim())
                    append("\n\n")

                    append("সর্বশেষ সম্পাদনার সময়: $noteDate\n\n")
                }
            }

            append("=================================================================\n")
            append("              ওঁ তৎ সৎ শ্রীকৃষ্ণার্পণমস্তু\n")
            append("=================================================================\n")
        }
    }

    fun writeTextToUri(context: Context, uri: Uri, content: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream, StandardCharsets.UTF_8).use { writer ->
                    writer.write(content)
                    writer.flush()
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun shareReflectionsText(context: Context, content: String, title: String = "গীতা চিন্তন ও নোট ব্যাকআপ") {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TITLE, title)
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, content)
            type = "text/plain"
        }
        val chooser = Intent.createChooser(sendIntent, "ব্যক্তিগত চিন্তন টেক্সট ফাইল শেয়ার / ব্যাকআপ")
        context.startActivity(chooser)
    }
}
