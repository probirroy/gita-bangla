package com.example.audio

import com.example.data.model.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Metadata representing a recitation audio file/asset linked to a specific Bengali verse.
 */
data class VerseAudioFile(
    val verseId: String,
    val chapterNumber: Int,
    val verseNumber: Int,
    val audioFileName: String,
    val audioUri: String,
    val titleBengali: String,
    val bengaliShlok: String,
    val bengaliTranslation: String,
    val estimatedDurationSeconds: Float
)

/**
 * State manager interface for managing playback and audio file linking for Gita verses.
 */
interface AudioStateManager {
    val playbackState: StateFlow<PlaybackState>
    val currentPlayingVerse: StateFlow<Verse?>
    val currentPlayingVerseId: StateFlow<String?>
    val currentAudioFile: StateFlow<VerseAudioFile?>
    val currentPositionSeconds: StateFlow<Float>
    val durationSeconds: StateFlow<Float>
    val playbackProgress: StateFlow<Float>
    val recitationMode: StateFlow<RecitationMode>
    val isLooping: StateFlow<Boolean>

    fun play(verse: Verse, mode: RecitationMode? = null)
    fun pause()
    fun resume()
    fun togglePlayPause(verse: Verse, mode: RecitationMode? = null)
    fun stop()
    fun seekTo(seconds: Float)
    fun seekBy(deltaSeconds: Float)
    fun setRecitationMode(mode: RecitationMode)
    fun toggleLoop()
    fun getAudioFileForVerse(verse: Verse): VerseAudioFile
}

/**
 * Implementation of AudioStateManager that links specific Bengali verses with their
 * recitation audio assets and controls playback via GitaAudioPlayer.
 */
class VerseAudioStateManager(
    private val audioPlayer: GitaAudioPlayer
) : AudioStateManager {

    private val _currentAudioFile = MutableStateFlow<VerseAudioFile?>(null)
    override val currentAudioFile: StateFlow<VerseAudioFile?> = _currentAudioFile.asStateFlow()

    override val playbackState: StateFlow<PlaybackState> = audioPlayer.playbackState
    override val currentPlayingVerse: StateFlow<Verse?> = audioPlayer.currentPlayingVerse
    override val currentPlayingVerseId: StateFlow<String?> = audioPlayer.currentPlayingVerseId
    override val currentPositionSeconds: StateFlow<Float> = audioPlayer.currentPositionSeconds
    override val durationSeconds: StateFlow<Float> = audioPlayer.durationSeconds
    override val playbackProgress: StateFlow<Float> = audioPlayer.playbackProgress
    override val recitationMode: StateFlow<RecitationMode> = audioPlayer.recitationMode
    override val isLooping: StateFlow<Boolean> = audioPlayer.isLoopingState

    override fun play(verse: Verse, mode: RecitationMode?) {
        val audioFile = getAudioFileForVerse(verse)
        _currentAudioFile.value = audioFile
        audioPlayer.playVerse(verse, mode)
    }

    override fun pause() {
        audioPlayer.pause()
    }

    override fun resume() {
        audioPlayer.resume()
    }

    override fun togglePlayPause(verse: Verse, mode: RecitationMode?) {
        val targetFile = getAudioFileForVerse(verse)
        _currentAudioFile.value = targetFile
        audioPlayer.togglePlay(verse, mode)
    }

    override fun stop() {
        audioPlayer.pauseOrStop()
        _currentAudioFile.value = null
    }

    override fun seekTo(seconds: Float) {
        audioPlayer.seekTo(seconds)
    }

    override fun seekBy(deltaSeconds: Float) {
        audioPlayer.seekBy(deltaSeconds)
    }

    override fun setRecitationMode(mode: RecitationMode) {
        audioPlayer.setRecitationMode(mode)
    }

    override fun toggleLoop() {
        audioPlayer.isLooping = !audioPlayer.isLooping
    }

    /**
     * Resolves the linked recitation audio file metadata for any given Bengali verse.
     */
    override fun getAudioFileForVerse(verse: Verse): VerseAudioFile {
        val fileName = "gita_recitation_ch${verse.chapterNumber}_v${verse.verseNumber}.mp3"
        val uri = "asset:///audio/recitations/bengali/$fileName"
        val title = "অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber} বাংলা আবৃত্তি"
        val wordCount = verse.pronunciationBengali.split(Regex("\\s+")).size + verse.translationBengali.split(Regex("\\s+")).size
        val estimatedSec = (wordCount / 2.5f).coerceIn(8f, 120f)

        return VerseAudioFile(
            verseId = verse.verseId,
            chapterNumber = verse.chapterNumber,
            verseNumber = verse.verseNumber,
            audioFileName = fileName,
            audioUri = uri,
            titleBengali = title,
            bengaliShlok = verse.pronunciationBengali,
            bengaliTranslation = verse.translationBengali,
            estimatedDurationSeconds = estimatedSec
        )
    }
}
