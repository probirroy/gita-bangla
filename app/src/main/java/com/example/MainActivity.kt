package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.components.AudioFloatingBar
import com.example.ui.components.GitaAudioPlayerBottomSheet
import com.example.ui.components.GitaBottomNavigation
import com.example.ui.components.NoteDialog
import com.example.ui.components.VerseShareDialog
import com.example.ui.screens.BookmarksScreen
import com.example.ui.screens.ChapterDetailScreen
import com.example.ui.screens.ChapterListScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ReflectionsScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VerseReaderScreen
import com.example.ui.screens.WeeklyInsightsScreen
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.GitaBanglaTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GitaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GitaViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Notification permission granted; ensure daily reflection work is scheduled
            val settings = viewModel.userSettings.value
            if (settings.dailyReminderEnabled) {
                com.example.notification.DailyNotificationManager.scheduleDailyReflectionWork(
                    applicationContext,
                    settings.reminderHour,
                    settings.reminderMinute,
                    forceReschedule = true
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleNotificationIntent(intent)
        checkNotificationPermission()

        setContent {
            val userSettings by viewModel.userSettings.collectAsState()
            val themeMode = when (userSettings.themeMode) {
                "LIGHT" -> AppThemeMode.LIGHT
                "DARK" -> AppThemeMode.DARK
                "SEPIA" -> AppThemeMode.SEPIA
                else -> AppThemeMode.SYSTEM
            }

            GitaBanglaTheme(themeMode = themeMode) {
                GitaApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        if (intent == null) return
        if (intent.getBooleanExtra("navigateToVerse", false)) {
            val ch = intent.getIntExtra("chapterNumber", 1)
            val v = intent.getIntExtra("verseNumber", 1)
            viewModel.openVerseReader(ch, v)
        }
    }

    private fun checkNotificationPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun GitaApp(viewModel: GitaViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val noteEditorState by viewModel.noteEditorState.collectAsState()
    val playingVerseId by viewModel.audioPlayer.currentPlayingVerseId.collectAsState()
    val playbackState by viewModel.audioPlayer.playbackState.collectAsState()
    val isAudioExpanded by viewModel.isAudioPlayerExpanded.collectAsState()
    val currentPosition by viewModel.audioCurrentPosition.collectAsState()
    val duration by viewModel.audioDuration.collectAsState()
    val isLooping by viewModel.isAudioLooping.collectAsState()
    val verseToShare by viewModel.verseToShare.collectAsState()

    // Handle system back navigation gracefully
    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        when (currentScreen) {
            AppScreen.VERSE_READER -> viewModel.navigateTo(AppScreen.CHAPTER_DETAIL)
            AppScreen.CHAPTER_DETAIL -> viewModel.navigateTo(AppScreen.CHAPTER_LIST)
            else -> viewModel.navigateTo(AppScreen.HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Floating recitation bar when audio is active
                AudioFloatingBar(
                    playingVerseId = playingVerseId,
                    playbackState = playbackState,
                    isLooping = isLooping,
                    currentPosition = currentPosition,
                    duration = duration,
                    onPlayPause = {
                        val activeVerse = viewModel.currentPlayingVerse.value ?: viewModel.getSelectedVerse()
                        viewModel.togglePlayRecitation(activeVerse)
                    },
                    onSkipPrevious = { viewModel.playPreviousVerse() },
                    onSkipNext = { viewModel.playNextVerse() },
                    onToggleLoop = { viewModel.toggleRecitationLoop() },
                    onStop = { viewModel.stopRecitation() },
                    onBarClick = {
                        viewModel.openAudioPlayerExpanded()
                    }
                )

                // Bottom Navigation
                GitaBottomNavigation(
                    currentScreen = currentScreen,
                    onNavigate = { screen ->
                        viewModel.navigateTo(screen)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(targetState = currentScreen, label = "screen_crossfade") { screen ->
                when (screen) {
                    AppScreen.HOME -> HomeScreen(viewModel = viewModel)
                    AppScreen.CHAPTER_LIST -> ChapterListScreen(viewModel = viewModel)
                    AppScreen.CHAPTER_DETAIL -> ChapterDetailScreen(viewModel = viewModel)
                    AppScreen.VERSE_READER -> VerseReaderScreen(viewModel = viewModel)
                    AppScreen.BOOKMARKS -> BookmarksScreen(viewModel = viewModel)
                    AppScreen.REFLECTIONS -> ReflectionsScreen(viewModel = viewModel)
                    AppScreen.SEARCH -> SearchScreen(viewModel = viewModel)
                    AppScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    AppScreen.WEEKLY_INSIGHTS -> WeeklyInsightsScreen(viewModel = viewModel)
                }
            }

            // Note writing dialog overlay
            if (noteEditorState.isOpen) {
                NoteDialog(
                    state = noteEditorState,
                    onTextChanged = { viewModel.updateDraftNoteText(it) },
                    onSave = { viewModel.saveDraftNote() },
                    onDelete = {
                        viewModel.deleteNote(noteEditorState.chapterNumber, noteEditorState.verseNumber)
                        viewModel.dismissNoteEditor()
                    },
                    onDismiss = { viewModel.dismissNoteEditor() }
                )
            }

            // Expanded Audio Player Bottom Sheet
            if (isAudioExpanded) {
                GitaAudioPlayerBottomSheet(
                    viewModel = viewModel,
                    onDismiss = { viewModel.closeAudioPlayerExpanded() }
                )
            }

            // Share Shlok Dialog Overlay (Options: Whole Shlok Screenshot or Text)
            verseToShare?.let { verse ->
                VerseShareDialog(
                    verse = verse,
                    onDismiss = { viewModel.dismissShareVerseDialog() }
                )
            }
        }
    }
}
