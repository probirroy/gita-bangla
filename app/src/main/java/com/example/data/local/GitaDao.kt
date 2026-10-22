package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks WHERE chapterNumber = :chapterNumber AND verseNumber = :verseNumber LIMIT 1")
    suspend fun getBookmark(chapterNumber: Int, verseNumber: Int): BookmarkEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE chapterNumber = :chapterNumber AND verseNumber = :verseNumber)")
    fun isBookmarkedFlow(chapterNumber: Int, verseNumber: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity): Long

    @Query("DELETE FROM bookmarks WHERE chapterNumber = :chapterNumber AND verseNumber = :verseNumber")
    suspend fun deleteBookmark(chapterNumber: Int, verseNumber: Int)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookmarks: List<BookmarkEntity>)

    @Query("DELETE FROM bookmarks")
    suspend fun deleteAll()
}

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes ORDER BY updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("SELECT * FROM notes WHERE chapterNumber = :chapterNumber AND verseNumber = :verseNumber LIMIT 1")
    fun getNoteFlow(chapterNumber: Int, verseNumber: Int): Flow<NoteEntity?>

    @Query("SELECT * FROM notes WHERE chapterNumber = :chapterNumber AND verseNumber = :verseNumber LIMIT 1")
    suspend fun getNote(chapterNumber: Int, verseNumber: Int): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateNote(note: NoteEntity): Long

    @Query("DELETE FROM notes WHERE chapterNumber = :chapterNumber AND verseNumber = :verseNumber")
    suspend fun deleteNote(chapterNumber: Int, verseNumber: Int)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notes: List<NoteEntity>)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()
}

@Dao
interface ReadingHistoryDao {
    @Query("SELECT * FROM reading_history WHERE `key` = 'current_progress' LIMIT 1")
    fun getProgress(): Flow<ReadingHistoryEntity?>

    @Query("SELECT * FROM reading_history WHERE `key` = 'current_progress' LIMIT 1")
    suspend fun getProgressDirect(): ReadingHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: ReadingHistoryEntity)
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM app_settings WHERE `key` = 'user_settings' LIMIT 1")
    fun getSettings(): Flow<AppSettingsEntity?>

    @Query("SELECT * FROM app_settings WHERE `key` = 'user_settings' LIMIT 1")
    suspend fun getSettingsDirect(): AppSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: AppSettingsEntity)
}

@Dao
interface DailyHabitDao {
    @Query("SELECT * FROM daily_habit_records ORDER BY date DESC LIMIT 30")
    fun getRecentRecords(): Flow<List<DailyHabitRecordEntity>>

    @Query("SELECT * FROM daily_habit_records ORDER BY date DESC LIMIT 30")
    suspend fun getRecentRecordsDirect(): List<DailyHabitRecordEntity>

    @Query("SELECT * FROM daily_habit_records WHERE date = :date LIMIT 1")
    suspend fun getRecordForDate(date: String): DailyHabitRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(record: DailyHabitRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<DailyHabitRecordEntity>)
}

@Dao
interface ChapterProgressDao {
    @Query("SELECT * FROM chapter_progress ORDER BY chapterNumber ASC")
    fun getAllProgress(): Flow<List<ChapterProgressEntity>>

    @Query("SELECT * FROM chapter_progress WHERE chapterNumber = :chapterNumber LIMIT 1")
    fun getProgressForChapter(chapterNumber: Int): Flow<ChapterProgressEntity?>

    @Query("SELECT * FROM chapter_progress WHERE chapterNumber = :chapterNumber LIMIT 1")
    suspend fun getProgressForChapterDirect(chapterNumber: Int): ChapterProgressEntity?

    @Query("SELECT * FROM chapter_progress")
    suspend fun getAllProgressDirect(): List<ChapterProgressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(progress: ChapterProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<ChapterProgressEntity>)

    @Query("UPDATE chapter_progress SET readVersesCount = 0, completionPercentage = 0.0, readVersesList = '', isCompleted = 0 WHERE chapterNumber = :chapterNumber")
    suspend fun resetChapterProgress(chapterNumber: Int)

    @Query("UPDATE chapter_progress SET readVersesCount = 0, completionPercentage = 0.0, readVersesList = '', isCompleted = 0")
    suspend fun resetAllProgress()
}

