package com.example.data.model

data class Chapter(
    val number: Int,
    val nameBengali: String,
    val nameSanskrit: String,
    val meaningBengali: String,
    val verseCount: Int,
    val summaryBengali: String
)

data class Verse(
    val chapterNumber: Int,
    val verseNumber: Int,
    val shlokaSanskrit: String,
    val pronunciationBengali: String,
    val translationBengali: String,
    val purportBengali: String
) {
    val verseId: String get() = "${chapterNumber}.${verseNumber}"
}

data class Bookmark(
    val id: Long = 0,
    val chapterNumber: Int,
    val verseNumber: Int,
    val tag: String = "সাধারণ",
    val createdAt: Long = System.currentTimeMillis()
)

data class VerseNote(
    val id: Long = 0,
    val chapterNumber: Int,
    val verseNumber: Int,
    val noteText: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class UserReadingSettings(
    val shlokaFontSize: Float = 19f,
    val translationFontSize: Float = 16f,
    val purportFontSize: Float = 15f,
    val themeMode: String = "SYSTEM",
    val hapticEnabled: Boolean = true,
    val recitationSpeed: Float = 1.0f,
    val dailyReminderEnabled: Boolean = true,
    val reminderHour: Int = 7,
    val reminderMinute: Int = 0,
    val showSanskrit: Boolean = true,
    val showPronunciation: Boolean = true,
    val showTranslation: Boolean = true,
    val showPurport: Boolean = true,
    val offlineReadingEnabled: Boolean = false,
    val downloadedChapters: Set<Int> = emptySet()
) {
    fun isChapterDownloaded(chapterNumber: Int): Boolean =
        offlineReadingEnabled && downloadedChapters.contains(chapterNumber)
}

data class BackupData(
    val version: Int = 1,
    val exportDate: String,
    val bookmarks: List<Bookmark>,
    val notes: List<VerseNote>,
    val lastChapter: Int,
    val lastVerse: Int,
    val readingStreak: Int
)

data class DayHabitStat(
    val date: String,
    val dayLabelBn: String,
    val dayLabelEn: String,
    val studySeconds: Long,
    val studyMinutes: Float,
    val versesCount: Int,
    val timeGoalMinutes: Int,
    val verseGoal: Int,
    val isTimeGoalMet: Boolean,
    val isVerseGoalMet: Boolean,
    val isAnyGoalMet: Boolean,
    val isToday: Boolean = false
)

data class WeeklyInsightsSummary(
    val days: List<DayHabitStat> = emptyList(),
    val totalStudySeconds: Long = 0L,
    val totalVersesRead: Int = 0,
    val consistencyScorePercent: Int = 0,
    val daysGoalCompletedCount: Int = 0,
    val bestDayLabel: String = "সোম",
    val bestDayMinutes: Int = 0,
    val currentStreak: Int = 1,
    val dailyGoalMinutes: Int = 15,
    val dailyVerseGoal: Int = 5
)

data class ChapterProgress(
    val chapterNumber: Int,
    val chapterNameBengali: String,
    val chapterNameSanskrit: String,
    val meaningBengali: String,
    val totalVerses: Int,
    val readVersesCount: Int = 0,
    val completionPercentage: Float = 0f,
    val isCompleted: Boolean = false,
    val readVersesSet: Set<Int> = emptySet(),
    val lastReadTimestamp: Long = 0L
)

data class OverallGitaProgress(
    val totalChapters: Int = 18,
    val completedChaptersCount: Int = 0,
    val inProgressChaptersCount: Int = 0,
    val totalVerses: Int = 700,
    val totalVersesRead: Int = 0,
    val overallPercentage: Float = 0f
)

