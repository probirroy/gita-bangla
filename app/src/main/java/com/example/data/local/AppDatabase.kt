package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        BookmarkEntity::class,
        NoteEntity::class,
        ReadingHistoryEntity::class,
        AppSettingsEntity::class,
        DailyHabitRecordEntity::class,
        ChapterProgressEntity::class
    ],
    version = 5,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun noteDao(): NoteDao
    abstract fun readingHistoryDao(): ReadingHistoryDao
    abstract fun settingsDao(): SettingsDao
    abstract fun dailyHabitDao(): DailyHabitDao
    abstract fun chapterProgressDao(): ChapterProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gita_bangla_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
