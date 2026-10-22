package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.model.Verse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

enum class RecitationMode {
    SHLOKA_ONLY, TRANSLATION_ONLY, BOTH
}

enum class PlaybackState {
    IDLE, PLAYING, PAUSED
}

class GitaAudioPlayer(
    context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
) {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _playbackState = MutableStateFlow(PlaybackState.IDLE)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _currentPlayingVerseId = MutableStateFlow<String?>(null)
    val currentPlayingVerseId: StateFlow<String?> = _currentPlayingVerseId.asStateFlow()

    private val _currentPlayingVerse = MutableStateFlow<Verse?>(null)
    val currentPlayingVerse: StateFlow<Verse?> = _currentPlayingVerse.asStateFlow()

    private val _recitationMode = MutableStateFlow(RecitationMode.TRANSLATION_ONLY)
    val recitationMode: StateFlow<RecitationMode> = _recitationMode.asStateFlow()

    private val _currentPositionSeconds = MutableStateFlow(0f)
    val currentPositionSeconds: StateFlow<Float> = _currentPositionSeconds.asStateFlow()

    private val _durationSeconds = MutableStateFlow(15f)
    val durationSeconds: StateFlow<Float> = _durationSeconds.asStateFlow()

    private val _playbackProgress = MutableStateFlow(0f)
    val playbackProgress: StateFlow<Float> = _playbackProgress.asStateFlow()

    private val _isLoopingState = MutableStateFlow(false)
    val isLoopingState: StateFlow<Boolean> = _isLoopingState.asStateFlow()

    private val _isAutoAdvanceState = MutableStateFlow(false)
    val isAutoAdvanceState: StateFlow<Boolean> = _isAutoAdvanceState.asStateFlow()

    var autoAdvanceEnabled: Boolean
        get() = _isAutoAdvanceState.value
        set(value) {
            _isAutoAdvanceState.value = value
        }

    var onVerseRecitationCompleted: (() -> Unit)? = null

    private val _isBengaliLocaleAvailable = MutableStateFlow(true)
    val isBengaliLocaleAvailable: StateFlow<Boolean> = _isBengaliLocaleAvailable.asStateFlow()

    private val _offlineStatusText = MutableStateFlow("অফলাইন বাংলা অডিও প্রস্তুত")
    val offlineStatusText: StateFlow<String> = _offlineStatusText.asStateFlow()

    var isLooping: Boolean
        get() = _isLoopingState.value
        set(value) {
            _isLoopingState.value = value
        }

    var speechRate: Float = 0.95f
        set(value) {
            field = value
            tts?.setSpeechRate(value)
            currentVerse?.let { v ->
                val fullText = getTextForVerse(v, _recitationMode.value)
                _durationSeconds.value = calculateEstimatedDurationSeconds(fullText, field)
            }
        }

    private var currentVerse: Verse? = null
    private var tickerJob: Job? = null

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                val bnLocales = listOf(
                    Locale("bn", "BD"),
                    Locale("bn", "IN"),
                    Locale("bn")
                )
                var matchedLocale: Locale? = null
                for (loc in bnLocales) {
                    val availability = tts?.isLanguageAvailable(loc) ?: TextToSpeech.LANG_NOT_SUPPORTED
                    if (availability >= TextToSpeech.LANG_AVAILABLE) {
                        tts?.language = loc
                        matchedLocale = loc
                        _isBengaliLocaleAvailable.value = true
                        _offlineStatusText.value = "অফলাইন বাংলা আবৃত্তি ইঞ্জিন সক্রিয়"
                        break
                    }
                }

                if (matchedLocale == null) {
                    val hiLocale = Locale("hi", "IN")
                    if ((tts?.isLanguageAvailable(hiLocale) ?: -1) >= TextToSpeech.LANG_AVAILABLE) {
                        tts?.language = hiLocale
                        _isBengaliLocaleAvailable.value = true
                        _offlineStatusText.value = "অফলাইন ভয়েস সিন্থেসাইজার সক্রিয়"
                    } else {
                        tts?.language = Locale.getDefault()
                        _offlineStatusText.value = "অফলাইন ডিভাইস অডিও সক্রিয়"
                    }
                }

                tts?.setSpeechRate(speechRate)
                tts?.setPitch(0.92f)

                setupListener()
            } else {
                _offlineStatusText.value = "অফলাইন টেক্সট-টু-স্পিচ লোড করা হচ্ছে..."
            }
        }
    }

    private fun setupListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _playbackState.value = PlaybackState.PLAYING
                startTicker()
            }

            override fun onDone(utteranceId: String?) {
                stopTicker()
                if (isLooping && currentVerse != null) {
                    scope.launch {
                        delay(250)
                        _currentPositionSeconds.value = 0f
                        _playbackProgress.value = 0f
                        playVerse(currentVerse!!)
                    }
                } else if (autoAdvanceEnabled && onVerseRecitationCompleted != null) {
                    _playbackState.value = PlaybackState.IDLE
                    scope.launch {
                        delay(350)
                        onVerseRecitationCompleted?.invoke()
                    }
                } else {
                    _playbackState.value = PlaybackState.IDLE
                    _currentPositionSeconds.value = _durationSeconds.value
                    _playbackProgress.value = 1f
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                stopTicker()
                _playbackState.value = PlaybackState.IDLE
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                stopTicker()
                _playbackState.value = PlaybackState.IDLE
            }
        })
    }

    fun getTextForVerse(verse: Verse, mode: RecitationMode): String {
        return when (mode) {
            RecitationMode.SHLOKA_ONLY -> {
                "${verse.pronunciationBengali} ... ইতি শ্রীমদ্ভগবদ্গীতা।"
            }
            RecitationMode.TRANSLATION_ONLY -> {
                "অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber} এর সরল বঙ্গানুবাদ: ${verse.translationBengali}"
            }
            RecitationMode.BOTH -> {
                "${verse.pronunciationBengali} ... সরল বঙ্গানুবাদ: ${verse.translationBengali}"
            }
        }
    }

    private fun calculateEstimatedDurationSeconds(text: String, rate: Float): Float {
        val wordCount = text.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.size
        val charCount = text.length
        // Natural cadence for Bengali devotional chanting: ~2.8 words/sec or ~13 chars/sec
        val baseSeconds = (charCount / 13f).coerceAtLeast(wordCount / 2.8f)
        return (baseSeconds / rate.coerceAtLeast(0.5f)).coerceIn(5f, 150f)
    }

    private fun startTicker() {
        stopTicker()
        tickerJob = scope.launch {
            while (isActive && _playbackState.value == PlaybackState.PLAYING) {
                delay(100L)
                val duration = _durationSeconds.value.coerceAtLeast(1f)
                val next = (_currentPositionSeconds.value + 0.1f)
                if (next >= duration) {
                    _currentPositionSeconds.value = duration
                    _playbackProgress.value = 1f
                    break
                } else {
                    _currentPositionSeconds.value = next
                    _playbackProgress.value = (next / duration).coerceIn(0f, 1f)
                }
            }
        }
    }

    private fun stopTicker() {
        tickerJob?.cancel()
        tickerJob = null
    }

    fun setRecitationMode(mode: RecitationMode) {
        _recitationMode.value = mode
        currentVerse?.let { v ->
            val fullText = getTextForVerse(v, mode)
            _durationSeconds.value = calculateEstimatedDurationSeconds(fullText, speechRate)
            if (_playbackState.value == PlaybackState.PLAYING) {
                playVerse(v, mode)
            }
        }
    }

    fun playVerse(verse: Verse, mode: RecitationMode? = null) {
        if (!isInitialized) return
        if (mode != null) {
            _recitationMode.value = mode
        }
        currentVerse = verse
        _currentPlayingVerse.value = verse
        _currentPlayingVerseId.value = verse.verseId

        val activeMode = mode ?: _recitationMode.value
        val textToSpeak = getTextForVerse(verse, activeMode)
        val calculatedDuration = calculateEstimatedDurationSeconds(textToSpeak, speechRate)
        _durationSeconds.value = calculatedDuration
        _currentPositionSeconds.value = 0f
        _playbackProgress.value = 0f

        try {
            tts?.stop()
            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "gita_${verse.verseId}_${System.currentTimeMillis()}")
            _playbackState.value = PlaybackState.PLAYING
            startTicker()
        } catch (_: Exception) {
            _playbackState.value = PlaybackState.IDLE
            stopTicker()
        }
    }

    fun pause() {
        stopTicker()
        try {
            tts?.stop()
        } catch (_: Exception) {}
        _playbackState.value = PlaybackState.PAUSED
    }

    fun resume() {
        val verse = currentVerse ?: return
        if (!isInitialized) return
        _playbackState.value = PlaybackState.PLAYING
        speakFromCurrentPosition(verse)
        startTicker()
    }

    private fun speakFromCurrentPosition(verse: Verse) {
        val fullText = getTextForVerse(verse, _recitationMode.value)
        val duration = _durationSeconds.value.coerceAtLeast(1f)
        val progressFraction = (_currentPositionSeconds.value / duration).coerceIn(0f, 0.95f)
        val charIndex = (fullText.length * progressFraction).toInt().coerceIn(0, fullText.length - 1)
        val remainingText = fullText.substring(charIndex).trim()

        try {
            tts?.stop()
            val textToSpeak = if (remainingText.isNotBlank()) remainingText else fullText
            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "gita_${verse.verseId}_${System.currentTimeMillis()}")
        } catch (_: Exception) {
            _playbackState.value = PlaybackState.IDLE
            stopTicker()
        }
    }

    fun seekTo(targetSeconds: Float) {
        val duration = _durationSeconds.value.coerceAtLeast(1f)
        val clamped = targetSeconds.coerceIn(0f, duration)
        _currentPositionSeconds.value = clamped
        _playbackProgress.value = (clamped / duration).coerceIn(0f, 1f)

        if (_playbackState.value == PlaybackState.PLAYING) {
            currentVerse?.let { v ->
                speakFromCurrentPosition(v)
            }
        }
    }

    fun seekToProgress(progressFraction: Float) {
        val duration = _durationSeconds.value.coerceAtLeast(1f)
        seekTo(progressFraction * duration)
    }

    fun seekBy(deltaSeconds: Float) {
        seekTo(_currentPositionSeconds.value + deltaSeconds)
    }

    fun togglePlay(verse: Verse, preferredMode: RecitationMode? = null) {
        if (preferredMode != null && preferredMode != _recitationMode.value) {
            _recitationMode.value = preferredMode
            playVerse(verse, preferredMode)
            return
        }

        if (_currentPlayingVerseId.value == verse.verseId) {
            when (_playbackState.value) {
                PlaybackState.PLAYING -> pause()
                PlaybackState.PAUSED -> resume()
                PlaybackState.IDLE -> playVerse(verse, preferredMode)
            }
        } else {
            playVerse(verse, preferredMode)
        }
    }

    fun playBengaliTranslation(verse: Verse) {
        playVerse(verse, RecitationMode.TRANSLATION_ONLY)
    }

    fun playBengaliShloka(verse: Verse) {
        playVerse(verse, RecitationMode.SHLOKA_ONLY)
    }

    fun playFullRecitation(verse: Verse) {
        playVerse(verse, RecitationMode.BOTH)
    }

    fun pauseOrStop() {
        stopTicker()
        try {
            tts?.stop()
        } catch (_: Exception) {}
        _playbackState.value = PlaybackState.IDLE
        _currentPlayingVerseId.value = null
        _currentPlayingVerse.value = null
        _currentPositionSeconds.value = 0f
        _playbackProgress.value = 0f
    }

    fun release() {
        stopTicker()
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
        isInitialized = false
        scope.cancel()
    }
}
