package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.GitaAudioPlayer
import com.example.audio.PlaybackState
import com.example.audio.RecitationMode
import com.example.audio.VerseAudioStateManager
import com.example.data.local.AppDatabase
import com.example.data.local.ReadingHistoryEntity
import com.example.data.model.Bookmark
import com.example.data.model.Chapter
import com.example.data.model.ChapterProgress
import com.example.data.model.DailyVerseCurated
import com.example.data.model.OverallGitaProgress
import com.example.data.model.UserReadingSettings
import com.example.data.model.Verse
import com.example.data.model.VerseNote
import com.example.data.repository.GitaRepository
import com.example.notification.DailyNotificationManager
import com.example.data.model.WeeklyInsightsSummary
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    HOME,
    CHAPTER_LIST,
    CHAPTER_DETAIL,
    VERSE_READER,
    BOOKMARKS,
    REFLECTIONS,
    SEARCH,
    SETTINGS,
    WEEKLY_INSIGHTS
}

data class NoteEditorState(
    val isOpen: Boolean = false,
    val chapterNumber: Int = 1,
    val verseNumber: Int = 1,
    val initialText: String = "",
    val draftText: String = ""
)

class GitaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GitaRepository
    val audioPlayer: GitaAudioPlayer = GitaAudioPlayer(application)
    val audioStateManager: VerseAudioStateManager = VerseAudioStateManager(audioPlayer)

    init {
        val db = AppDatabase.getInstance(application)
        repository = GitaRepository(db)
        DailyNotificationManager.createNotificationChannel(application)
        audioPlayer.onVerseRecitationCompleted = {
            playNextVerse()
        }
        viewModelScope.launch {
            repository.seedInitialChapterProgressIfNeeded()
        }
        viewModelScope.launch {
            repository.userSettings.collect { settings ->
                if (settings.dailyReminderEnabled) {
                    DailyNotificationManager.scheduleDailyReflectionWork(
                        application,
                        settings.reminderHour,
                        settings.reminderMinute
                    )
                } else {
                    DailyNotificationManager.cancelDailyReflectionWork(application)
                }
            }
        }
    }

    // Navigation state
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _selectedChapterNumber = MutableStateFlow(1)
    val selectedChapterNumber: StateFlow<Int> = _selectedChapterNumber.asStateFlow()

    private val _selectedVerseNumber = MutableStateFlow(1)
    val selectedVerseNumber: StateFlow<Int> = _selectedVerseNumber.asStateFlow()

    // Gita data
    val chapters: List<Chapter> = repository.getChapters()

    private val _curatedDailyVerse = MutableStateFlow(repository.getCuratedDailyVerse())
    val curatedDailyVerse: StateFlow<DailyVerseCurated> = _curatedDailyVerse.asStateFlow()

    private val _dailyVerse = MutableStateFlow(repository.getDailyVerse())
    val dailyVerse: StateFlow<Verse> = _dailyVerse.asStateFlow()

    fun refreshDailyVerse() {
        val currentId = _dailyVerse.value.verseId
        var nextCurated = repository.getRandomCuratedDailyVerse()
        var attempts = 0
        while (nextCurated.verse.verseId == currentId && attempts < 5) {
            nextCurated = repository.getRandomCuratedDailyVerse()
            attempts++
        }
        _curatedDailyVerse.value = nextCurated
        _dailyVerse.value = nextCurated.verse
    }

    fun loadRandomVerseOfTheDay() {
        refreshDailyVerse()
    }

    fun resetToTodayCuratedVerse() {
        val today = repository.getCuratedDailyVerse()
        _curatedDailyVerse.value = today
        _dailyVerse.value = today.verse
    }

    fun getRandomVerse(): Verse {
        return repository.getRandomVerse()
    }

    // State flows from repository
    val bookmarks: StateFlow<List<Bookmark>> = repository.allBookmarks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notes: StateFlow<List<VerseNote>> = repository.allNotes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val readingProgress: StateFlow<ReadingHistoryEntity> = repository.readingProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ReadingHistoryEntity())

    val weeklyInsights: StateFlow<WeeklyInsightsSummary> = repository.weeklyInsights
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WeeklyInsightsSummary())

    val userSettings: StateFlow<UserReadingSettings> = repository.userSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserReadingSettings())

    // Chapter progress tracking (stored in Room database)
    val allChapterProgress: StateFlow<List<ChapterProgress>> = repository.allChapterProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val overallGitaProgress: StateFlow<OverallGitaProgress> = repository.overallGitaProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), OverallGitaProgress())

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Verse>>(emptyList())
    val searchResults: StateFlow<List<Verse>> = _searchResults.asStateFlow()

    // Note editor state
    private val _noteEditorState = MutableStateFlow(NoteEditorState())
    val noteEditorState: StateFlow<NoteEditorState> = _noteEditorState.asStateFlow()

    // Share Shlok Dialog State (Two-option dialog: Screenshot or Text)
    private val _verseToShare = MutableStateFlow<Verse?>(null)
    val verseToShare: StateFlow<Verse?> = _verseToShare.asStateFlow()

    fun openShareVerseDialog(verse: Verse) {
        _verseToShare.value = verse
    }

    fun dismissShareVerseDialog() {
        _verseToShare.value = null
    }

    // Backup & Sync state
    private val _backupJson = MutableStateFlow<String?>(null)
    val backupJson: StateFlow<String?> = _backupJson.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // Reading Session Timer state (tracks active daily study)
    private val _sessionSeconds = MutableStateFlow(0L)
    val sessionSeconds: StateFlow<Long> = _sessionSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

    fun startStudyTimer() {
        if (_isTimerRunning.value) return
        _isTimerRunning.value = true
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value) {
                delay(1000L)
                _sessionSeconds.value += 1
                // Persist every 10 seconds to database
                if (_sessionSeconds.value % 10L == 0L) {
                    repository.recordStudyTime(10L)
                }
            }
        }
    }

    fun pauseStudyTimer() {
        if (!_isTimerRunning.value) return
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
        val remainder = _sessionSeconds.value % 10L
        if (remainder > 0) {
            viewModelScope.launch {
                repository.recordStudyTime(remainder)
            }
        }
    }

    fun toggleStudyTimer() {
        if (_isTimerRunning.value) {
            pauseStudyTimer()
        } else {
            startStudyTimer()
        }
    }

    fun resetSessionTimer() {
        pauseStudyTimer()
        _sessionSeconds.value = 0L
    }

    fun updateDailyGoalMinutes(minutes: Int) {
        viewModelScope.launch {
            repository.updateDailyGoal(minutes)
        }
    }

    fun updateDailyVerseGoal(goal: Int) {
        viewModelScope.launch {
            repository.updateDailyVerseGoal(goal)
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun openChapter(chapterNumber: Int) {
        _selectedChapterNumber.value = chapterNumber
        _selectedVerseNumber.value = 1
        _currentScreen.value = AppScreen.CHAPTER_DETAIL
    }

    fun openVerseReader(chapterNumber: Int, verseNumber: Int) {
        _selectedChapterNumber.value = chapterNumber
        _selectedVerseNumber.value = verseNumber
        _currentScreen.value = AppScreen.VERSE_READER
        startStudyTimer()
        viewModelScope.launch {
            repository.recordReading(chapterNumber, verseNumber)
        }
    }

    fun getSelectedChapter(): Chapter {
        return repository.getChapter(_selectedChapterNumber.value) ?: chapters.first()
    }

    fun getSelectedVerse(): Verse {
        return repository.getVerse(_selectedChapterNumber.value, _selectedVerseNumber.value)
    }

    fun getVersesForCurrentChapter(): List<Verse> {
        return repository.getVersesForChapter(_selectedChapterNumber.value)
    }

    fun getChapterProgressFlow(chapterNumber: Int): Flow<ChapterProgress> {
        return repository.getChapterProgressFlow(chapterNumber)
    }

    fun toggleVerseRead(chapterNumber: Int, verseNumber: Int) {
        viewModelScope.launch {
            repository.toggleVerseReadStatus(chapterNumber, verseNumber)
        }
    }

    fun markChapterAsCompleted(chapterNumber: Int, completed: Boolean) {
        viewModelScope.launch {
            repository.markChapterCompleted(chapterNumber, completed)
        }
    }

    fun resetChapterProgress(chapterNumber: Int) {
        viewModelScope.launch {
            repository.resetChapterProgress(chapterNumber)
        }
    }

    fun resetAllChapterProgress() {
        viewModelScope.launch {
            repository.resetAllChapterProgress()
        }
    }

    fun nextVerse() {
        val chapter = getSelectedChapter()
        if (_selectedVerseNumber.value < chapter.verseCount) {
            _selectedVerseNumber.value += 1
            viewModelScope.launch {
                repository.recordReading(_selectedChapterNumber.value, _selectedVerseNumber.value)
            }
        } else if (_selectedChapterNumber.value < 18) {
            _selectedChapterNumber.value += 1
            _selectedVerseNumber.value = 1
            viewModelScope.launch {
                repository.recordReading(_selectedChapterNumber.value, 1)
            }
        }
    }

    fun previousVerse() {
        if (_selectedVerseNumber.value > 1) {
            _selectedVerseNumber.value -= 1
            viewModelScope.launch {
                repository.recordReading(_selectedChapterNumber.value, _selectedVerseNumber.value)
            }
        } else if (_selectedChapterNumber.value > 1) {
            _selectedChapterNumber.value -= 1
            val prevChapter = repository.getChapter(_selectedChapterNumber.value)
            _selectedVerseNumber.value = prevChapter?.verseCount ?: 1
            viewModelScope.launch {
                repository.recordReading(_selectedChapterNumber.value, _selectedVerseNumber.value)
            }
        }
    }

    fun isCurrentVerseBookmarked(): Boolean {
        val ch = _selectedChapterNumber.value
        val v = _selectedVerseNumber.value
        return bookmarks.value.any { it.chapterNumber == ch && it.verseNumber == v }
    }

    fun isVerseBookmarked(chapterNumber: Int, verseNumber: Int): Boolean {
        return bookmarks.value.any { it.chapterNumber == chapterNumber && it.verseNumber == verseNumber }
    }

    fun toggleBookmarkForCurrentVerse(tag: String = "সাধারণ") {
        viewModelScope.launch {
            repository.toggleBookmark(_selectedChapterNumber.value, _selectedVerseNumber.value, tag)
        }
    }

    fun toggleBookmarkForVerse(chapterNumber: Int, verseNumber: Int, tag: String = "সাধারণ") {
        viewModelScope.launch {
            repository.toggleBookmark(chapterNumber, verseNumber, tag)
        }
    }

    fun removeBookmark(id: Long) {
        viewModelScope.launch {
            repository.removeBookmark(id)
        }
    }

    // Note operations
    fun openNoteEditor(chapterNumber: Int, verseNumber: Int) {
        val existingNote = notes.value.find { it.chapterNumber == chapterNumber && it.verseNumber == verseNumber }
        _noteEditorState.value = NoteEditorState(
            isOpen = true,
            chapterNumber = chapterNumber,
            verseNumber = verseNumber,
            initialText = existingNote?.noteText ?: "",
            draftText = existingNote?.noteText ?: ""
        )
    }

    fun updateDraftNoteText(text: String) {
        _noteEditorState.value = _noteEditorState.value.copy(draftText = text)
    }

    fun saveDraftNote() {
        val current = _noteEditorState.value
        if (current.draftText.isNotBlank()) {
            viewModelScope.launch {
                repository.saveNote(current.chapterNumber, current.verseNumber, current.draftText)
                _noteEditorState.value = _noteEditorState.value.copy(isOpen = false)
            }
        } else {
            // Delete if blank
            viewModelScope.launch {
                repository.deleteNote(current.chapterNumber, current.verseNumber)
                _noteEditorState.value = _noteEditorState.value.copy(isOpen = false)
            }
        }
    }

    fun dismissNoteEditor() {
        _noteEditorState.value = _noteEditorState.value.copy(isOpen = false)
    }

    fun deleteNote(chapterNumber: Int, verseNumber: Int) {
        viewModelScope.launch {
            repository.deleteNote(chapterNumber, verseNumber)
        }
    }

    // Search operations
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
        } else {
            _searchResults.value = repository.searchVerses(query)
        }
    }

    // Settings
    fun updateReadingSettings(newSettings: UserReadingSettings) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
            audioPlayer.speechRate = newSettings.recitationSpeed
        }
    }

    // Audio operations
    val recitationMode: StateFlow<RecitationMode> = audioPlayer.recitationMode
    val offlineAudioStatus: StateFlow<String> = audioPlayer.offlineStatusText
    val isBengaliLocaleAvailable: StateFlow<Boolean> = audioPlayer.isBengaliLocaleAvailable

    val audioCurrentPosition: StateFlow<Float> = audioPlayer.currentPositionSeconds
    val audioDuration: StateFlow<Float> = audioPlayer.durationSeconds
    val audioPlaybackProgress: StateFlow<Float> = audioPlayer.playbackProgress
    val currentPlayingVerse: StateFlow<Verse?> = audioPlayer.currentPlayingVerse
    val isAudioLooping: StateFlow<Boolean> = audioPlayer.isLoopingState

    private val _isAudioPlayerExpanded = MutableStateFlow(false)
    val isAudioPlayerExpanded: StateFlow<Boolean> = _isAudioPlayerExpanded.asStateFlow()

    fun openAudioPlayerExpanded() {
        _isAudioPlayerExpanded.value = true
    }

    fun closeAudioPlayerExpanded() {
        _isAudioPlayerExpanded.value = false
    }

    fun setRecitationMode(mode: RecitationMode) {
        audioPlayer.setRecitationMode(mode)
    }

    fun togglePlayRecitation(verse: Verse, preferredMode: RecitationMode? = null) {
        audioPlayer.togglePlay(verse, preferredMode)
    }

    fun pauseRecitation() {
        audioPlayer.pause()
    }

    fun resumeRecitation() {
        audioPlayer.resume()
    }

    fun seekRecitationTo(seconds: Float) {
        audioPlayer.seekTo(seconds)
    }

    fun seekRecitationBy(deltaSeconds: Float) {
        audioPlayer.seekBy(deltaSeconds)
    }

    val isAutoAdvanceEnabled: StateFlow<Boolean> = audioPlayer.isAutoAdvanceState

    fun toggleAutoAdvance() {
        audioPlayer.autoAdvanceEnabled = !audioPlayer.autoAdvanceEnabled
    }

    fun playNextVerse() {
        val current = audioPlayer.currentPlayingVerse.value ?: getSelectedVerse()
        val chapter = chapters.find { it.number == current.chapterNumber } ?: return
        val nextVerse = if (current.verseNumber < chapter.verseCount) {
            repository.getVerse(current.chapterNumber, current.verseNumber + 1)
        } else if (current.chapterNumber < 18) {
            repository.getVerse(current.chapterNumber + 1, 1)
        } else null

        if (nextVerse != null) {
            _selectedChapterNumber.value = nextVerse.chapterNumber
            _selectedVerseNumber.value = nextVerse.verseNumber
            audioPlayer.playVerse(nextVerse)
            viewModelScope.launch {
                repository.recordReading(nextVerse.chapterNumber, nextVerse.verseNumber)
            }
        }
    }

    fun playPreviousVerse() {
        val current = audioPlayer.currentPlayingVerse.value ?: getSelectedVerse()
        val prevVerse = if (current.verseNumber > 1) {
            repository.getVerse(current.chapterNumber, current.verseNumber - 1)
        } else if (current.chapterNumber > 1) {
            val prevChapter = chapters.find { it.number == current.chapterNumber - 1 } ?: return
            repository.getVerse(prevChapter.number, prevChapter.verseCount)
        } else null

        if (prevVerse != null) {
            _selectedChapterNumber.value = prevVerse.chapterNumber
            _selectedVerseNumber.value = prevVerse.verseNumber
            audioPlayer.playVerse(prevVerse)
            viewModelScope.launch {
                repository.recordReading(prevVerse.chapterNumber, prevVerse.verseNumber)
            }
        }
    }

    fun playBengaliTranslation(verse: Verse) {
        audioPlayer.playBengaliTranslation(verse)
    }

    fun playBengaliShloka(verse: Verse) {
        audioPlayer.playBengaliShloka(verse)
    }

    fun playFullRecitation(verse: Verse) {
        audioPlayer.playFullRecitation(verse)
    }

    fun toggleRecitationLoop() {
        audioPlayer.isLooping = !audioPlayer.isLooping
    }

    fun stopRecitation() {
        audioPlayer.pauseOrStop()
    }

    // Backup & Sync
    fun exportBackup() {
        viewModelScope.launch {
            val json = repository.exportBackupJson()
            _backupJson.value = json
            _syncMessage.value = "ডেটা সফলভাবে প্রস্তুত হয়েছে (ব্যাকআপ তৈরি সম্পন্ন)"
        }
    }

    fun importBackup(jsonString: String) {
        viewModelScope.launch {
            val result = repository.importBackupJson(jsonString)
            if (result.isSuccess) {
                _syncMessage.value = "${result.getOrNull()} টি আইটেম সফলভাবে পুনরুদ্ধার ও সিঙ্ক হয়েছে"
            } else {
                _syncMessage.value = "ব্যাকআপ ডেটা পড়তে সমস্যা হয়েছে: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }

    // Export Reflections / Notes to Local Text File
    fun getReflectionsBackupText(): String {
        return com.example.util.ReflectionsExportHelper.generateReflectionsBackupText(notes.value)
    }

    fun exportReflectionsToUri(context: android.content.Context, uri: android.net.Uri): Boolean {
        val content = getReflectionsBackupText()
        val success = com.example.util.ReflectionsExportHelper.writeTextToUri(context, uri, content)
        if (success) {
            _syncMessage.value = "${notes.value.size}টি ব্যক্তিগত চিন্তন সফলভাবে টেক্সট ফাইলে সংরক্ষিত হয়েছে"
        } else {
            _syncMessage.value = "ফাইল সংরক্ষণে ত্রুটি দেখা দিয়েছে"
        }
        return success
    }

    fun triggerDailyNotification() {
        DailyNotificationManager.triggerImmediateWork(getApplication())
    }

    fun toggleDailyReminder(enabled: Boolean) {
        val current = userSettings.value
        val updated = current.copy(dailyReminderEnabled = enabled)
        updateReadingSettings(updated)
        if (enabled) {
            DailyNotificationManager.scheduleDailyReflectionWork(
                getApplication(),
                current.reminderHour,
                current.reminderMinute,
                forceReschedule = true
            )
        } else {
            DailyNotificationManager.cancelDailyReflectionWork(getApplication())
        }
    }

    fun updateReminderTime(hour: Int, minute: Int) {
        val current = userSettings.value
        val updated = current.copy(reminderHour = hour, reminderMinute = minute, dailyReminderEnabled = true)
        updateReadingSettings(updated)
        DailyNotificationManager.scheduleDailyReflectionWork(
            getApplication(),
            hour,
            minute,
            forceReschedule = true
        )
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.release()
    }
}
