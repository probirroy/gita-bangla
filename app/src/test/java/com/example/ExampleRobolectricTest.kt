package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlinx.coroutines.flow.first

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Gita Bangla", appName)
  }

  @Test
  fun `verify daily notification channel and scheduling`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    com.example.notification.DailyNotificationManager.createNotificationChannel(context)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
    val channel = notificationManager.getNotificationChannel(com.example.notification.DailyNotificationManager.CHANNEL_ID)
    org.junit.Assert.assertNotNull(channel)
    assertEquals(com.example.notification.DailyNotificationManager.CHANNEL_NAME, channel.name)

    // Initialize WorkManager for testing environment if not already initialized
    try {
        val config = androidx.work.Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
        androidx.work.WorkManager.initialize(context, config)
    } catch (_: Exception) {}

    // Verify scheduling does not crash
    com.example.notification.DailyNotificationManager.scheduleDailyReflectionWork(context, 7, 0)
    com.example.notification.DailyNotificationManager.showRandomVerseNotification(context)
  }

  @Test
  fun `verify Room database chapter progress tracking and completion percentage`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    val progressDao = db.chapterProgressDao()
    val repo = com.example.data.repository.GitaRepository(db)

    // Initially table is empty, seed 18 chapters
    repo.seedInitialChapterProgressIfNeeded()
    val initialList = progressDao.getAllProgressDirect()
    assertEquals(18, initialList.size)
    assertEquals(0f, initialList[0].completionPercentage, 0.001f)

    // Record reading verse 1 and verse 2 of chapter 1
    repo.recordReading(1, 1)
    repo.recordReading(1, 2)

    val updatedCh1 = progressDao.getProgressForChapterDirect(1)
    org.junit.Assert.assertNotNull(updatedCh1)
    assertEquals(2, updatedCh1!!.readVersesCount)
    val expectedPct = (2f / 47f) * 100f
    assertEquals(expectedPct, updatedCh1.completionPercentage, 0.05f)
    org.junit.Assert.assertFalse(updatedCh1.isCompleted)
    org.junit.Assert.assertTrue(updatedCh1.hasReadVerse(1))
    org.junit.Assert.assertTrue(updatedCh1.hasReadVerse(2))

    // Mark entire chapter 1 completed
    repo.markChapterCompleted(1, true)
    val completedCh1 = progressDao.getProgressForChapterDirect(1)
    org.junit.Assert.assertNotNull(completedCh1)
    assertEquals(47, completedCh1!!.readVersesCount)
    assertEquals(100f, completedCh1.completionPercentage, 0.001f)
    org.junit.Assert.assertTrue(completedCh1.isCompleted)

    // Reset chapter progress
    repo.resetChapterProgress(1)
    val resetCh1 = progressDao.getProgressForChapterDirect(1)
    org.junit.Assert.assertNotNull(resetCh1)
    assertEquals(0, resetCh1!!.readVersesCount)
    assertEquals(0f, resetCh1.completionPercentage, 0.001f)
    org.junit.Assert.assertFalse(resetCh1.isCompleted)

    db.close()
  }

  @Test
  fun `verify Room database verse notes and personal reflections CRUD`() = kotlinx.coroutines.runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = androidx.room.Room.inMemoryDatabaseBuilder(context, com.example.data.local.AppDatabase::class.java)
        .allowMainThreadQueries()
        .build()

    val repo = com.example.data.repository.GitaRepository(db)
    val noteDao = db.noteDao()

    // 1. Initial state: no notes
    val initialNote = noteDao.getNote(2, 47)
    org.junit.Assert.assertNull(initialNote)

    // 2. Insert personal reflection note for Chapter 2, Verse 47
    val reflectionText1 = "কর্মযোগের মূল তত্ত্ব: ফলাসক্তি ত্যাগ করে কর্মে সমর্পণ।"
    repo.saveNote(2, 47, reflectionText1)

    val savedNote = noteDao.getNote(2, 47)
    org.junit.Assert.assertNotNull(savedNote)
    assertEquals(2, savedNote!!.chapterNumber)
    assertEquals(47, savedNote.verseNumber)
    assertEquals(reflectionText1, savedNote.noteText)

    // 3. Insert second note for Chapter 18, Verse 66
    val reflectionText2 = "সর্বধর্ম্মান পরিত্যজ্য: পরম শরণাগতির মহিমা।"
    repo.saveNote(18, 66, reflectionText2)

    val allNotesList = repo.allNotes.first()
    assertEquals(2, allNotesList.size)
    org.junit.Assert.assertTrue(allNotesList.any { it.chapterNumber == 2 && it.verseNumber == 47 })
    org.junit.Assert.assertTrue(allNotesList.any { it.chapterNumber == 18 && it.verseNumber == 66 })

    // 4. Update the reflection for Chapter 2, Verse 47
    val updatedReflectionText = "সংশোধিত উপলব্ধি: নিষ্কাম কর্মই চিত্তশুদ্ধির পরম উপায়।"
    repo.saveNote(2, 47, updatedReflectionText)

    val updatedNote = noteDao.getNote(2, 47)
    org.junit.Assert.assertNotNull(updatedNote)
    assertEquals(updatedReflectionText, updatedNote!!.noteText)

    // 5. Delete note for Chapter 18, Verse 66
    repo.deleteNote(18, 66)
    val remainingNote = noteDao.getNote(18, 66)
    org.junit.Assert.assertNull(remainingNote)

    val remainingNotesList = repo.allNotes.first()
    assertEquals(1, remainingNotesList.size)
    assertEquals(2, remainingNotesList[0].chapterNumber)
    assertEquals(47, remainingNotesList[0].verseNumber)

    db.close()
  }

  @Test
  fun `verify Verse of the Day random loading and state updates`() = kotlinx.coroutines.runBlocking {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.viewmodel.GitaViewModel(application)

    val initialVerse = viewModel.dailyVerse.value
    org.junit.Assert.assertNotNull(initialVerse)
    org.junit.Assert.assertTrue(initialVerse.shlokaSanskrit.isNotBlank())
    org.junit.Assert.assertTrue(initialVerse.translationBengali.isNotBlank())

    // Trigger loadRandomVerseOfTheDay
    viewModel.loadRandomVerseOfTheDay()
    val updatedVerse = viewModel.dailyVerse.value
    org.junit.Assert.assertNotNull(updatedVerse)
    org.junit.Assert.assertTrue(updatedVerse.shlokaSanskrit.isNotBlank())

    // Verify getRandomVerse delegate returns valid content
    val randomVerse = viewModel.getRandomVerse()
    org.junit.Assert.assertNotNull(randomVerse)
    org.junit.Assert.assertTrue(randomVerse.verseId.isNotBlank())
  }

  @Test
  fun `verify VerseAudioStateManager controls and audio file resolution`() = kotlinx.coroutines.runBlocking {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.viewmodel.GitaViewModel(application)
    val audioStateManager = viewModel.audioStateManager

    val verse = com.example.data.source.GitaContentProvider.getVerse(2, 47)
    val audioFile = audioStateManager.getAudioFileForVerse(verse)

    org.junit.Assert.assertNotNull(audioFile)
    assertEquals("2.47", audioFile.verseId)
    assertEquals("gita_recitation_ch2_v47.mp3", audioFile.audioFileName)
    org.junit.Assert.assertTrue(audioFile.bengaliShlok.contains("কর্মণ্যেবাধিকারস্তে"))

    // Initial state is IDLE
    assertEquals(com.example.audio.PlaybackState.IDLE, audioStateManager.playbackState.value)

    // Toggle play/pause
    audioStateManager.togglePlayPause(verse)
    // Audio file should be linked in the state manager
    org.junit.Assert.assertNotNull(audioStateManager.currentAudioFile.value)
    assertEquals("2.47", audioStateManager.currentAudioFile.value?.verseId)

    // Pause
    audioStateManager.pause()
    assertEquals(com.example.audio.PlaybackState.PAUSED, audioStateManager.playbackState.value)

    // Seek
    audioStateManager.seekTo(5f)
    assertEquals(5f, audioStateManager.currentPositionSeconds.value, 0.001f)

    // Mode change
    audioStateManager.setRecitationMode(com.example.audio.RecitationMode.SHLOKA_ONLY)
    assertEquals(com.example.audio.RecitationMode.SHLOKA_ONLY, audioStateManager.recitationMode.value)

    // Stop
    audioStateManager.stop()
    assertEquals(com.example.audio.PlaybackState.IDLE, audioStateManager.playbackState.value)
  }

  @Test
  fun `verify verse card bitmap generation and screenshot saving`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val verse = com.example.data.source.GitaContentProvider.getVerse(2, 47)

    // Generate bitmap
    val bitmap = com.example.util.ShareHelper.generateVerseCardBitmap(context, verse)
    org.junit.Assert.assertNotNull(bitmap)
    assertEquals(1080, bitmap.width)
    org.junit.Assert.assertTrue(bitmap.height >= 800)

    // Save bitmap to cache
    val file = com.example.util.ShareHelper.saveVerseBitmapToFile(context, bitmap, verse)
    org.junit.Assert.assertNotNull(file)
    org.junit.Assert.assertTrue(file.exists())
    org.junit.Assert.assertTrue(file.length() > 0)
    org.junit.Assert.assertTrue(file.name.contains("shlok_2_47.png"))
  }

  @Test
  fun `verify verse share dialog state in ViewModel`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val viewModel = com.example.ui.viewmodel.GitaViewModel(application)
    val verse = com.example.data.source.GitaContentProvider.getVerse(1, 1)

    // Initially no dialog
    org.junit.Assert.assertNull(viewModel.verseToShare.value)

    // Open dialog
    viewModel.openShareVerseDialog(verse)
    assertEquals(verse, viewModel.verseToShare.value)

    // Dismiss dialog
    viewModel.dismissShareVerseDialog()
    org.junit.Assert.assertNull(viewModel.verseToShare.value)
  }
}


