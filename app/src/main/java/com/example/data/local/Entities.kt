package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "bookmarks",
    indices = [Index(value = ["chapterNumber", "verseNumber"], unique = true)]
)
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chapterNumber: Int,
    val verseNumber: Int,
    val tag: String = "সাধারণ",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "notes",
    indices = [Index(value = ["chapterNumber", "verseNumber"], unique = true)]
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chapterNumber: Int,
    val verseNumber: Int,
    val noteText: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reading_history")
data class ReadingHistoryEntity(
    @PrimaryKey
    val key: String = "current_progress",
    val lastChapter: Int = 1,
    val lastVerse: Int = 1,
    val lastReadTimestamp: Long = System.currentTimeMillis(),
    val streakDays: Int = 1,
    val lastReadDate: String = "",
    val todayStudySeconds: Long = 0L,
    val dailyGoalMinutes: Int = 15,
    val totalStudySeconds: Long = 0L,
    val lastStudyDate: String = "",
    val dailyVerseGoal: Int = 5,
    val todayVersesRead: String = "",
    val lastVerseGoalDate: String = ""
) {
    val todayVersesCount: Int
        get() = if (todayVersesRead.isBlank()) 0 else todayVersesRead.split(",").filter { it.isNotBlank() }.distinct().size
}

@Entity(tableName = "daily_habit_records")
data class DailyHabitRecordEntity(
    @PrimaryKey
    val date: String, // "yyyy-MM-dd"
    val dayLabel: String = "", // e.g. "সোম"
    val studySeconds: Long = 0L,
    val versesReadCount: Int = 0,
    val versesReadList: String = "",
    val timeGoalMinutes: Int = 15,
    val verseGoal: Int = 5,
    val timeGoalMet: Boolean = false,
    val verseGoalMet: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_settings")
data class AppSettingsEntity(
    @PrimaryKey
    val key: String = "user_settings",
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
    val showPurport: Boolean = true
)

@Entity(tableName = "chapter_progress")
data class ChapterProgressEntity(
    @PrimaryKey
    val chapterNumber: Int,
    val totalVerses: Int,
    val readVersesCount: Int = 0,
    val completionPercentage: Float = 0f,
    val readVersesList: String = "",
    val isCompleted: Boolean = false,
    val lastReadTimestamp: Long = System.currentTimeMillis()
) {
    fun hasReadVerse(verseNumber: Int): Boolean {
        if (readVersesList.isBlank()) return false
        return readVersesList.split(",").contains(verseNumber.toString())
    }

    val readVerseNumbersSet: Set<Int>
        get() = if (readVersesList.isBlank()) emptySet() else readVersesList.split(",").mapNotNull { it.trim().toIntOrNull() }.toSet()
}
