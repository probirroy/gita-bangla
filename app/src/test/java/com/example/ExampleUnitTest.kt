package com.example

import com.example.data.source.GitaContentProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun verifyGitaChaptersCount() {
        assertEquals(18, GitaContentProvider.chapters.size)
    }

    @Test
    fun verifyCornerstoneVerseContent() {
        val verse247 = GitaContentProvider.getVerse(2, 47)
        assertNotNull(verse247)
        assertTrue(verse247.shlokaSanskrit.contains("कर्मण्येवाधिकारस्ते"))
        assertTrue(verse247.translationBengali.contains("কর্মেই তোমার একমাত্র অধিকার"))
    }

    @Test
    fun verifySearchFunctionality() {
        val results = GitaContentProvider.searchVerses("কর্ম")
        assertTrue(results.isNotEmpty())
    }

    @Test
    fun verifyVerseShareContent() {
        val verse = GitaContentProvider.getVerse(2, 47)
        assertTrue(verse.verseId == "2.47")
        assertTrue(verse.translationBengali.isNotBlank())
    }

    @Test
    fun verifyStudyTimerFormatting() {
        assertEquals("05:30", com.example.util.TimeHelper.formatSecondsToTimer(330L))
        assertEquals("00:45", com.example.util.TimeHelper.formatSecondsToTimer(45L))
        val summary = com.example.util.TimeHelper.formatTodayStudySummary(330L)
        assertTrue(summary.contains("5 মিনিট 30 সেকেন্ড"))
    }

    @Test
    fun verifyDailyVerseReadingGoalTracking() {
        val entity = com.example.data.local.ReadingHistoryEntity(
            dailyVerseGoal = 5,
            todayVersesRead = "1.1,1.2,2.47,1.1"
        )
        // Distinct count should be 3
        assertEquals(3, entity.todayVersesCount)
        assertEquals(5, entity.dailyVerseGoal)

        val completedEntity = entity.copy(todayVersesRead = "1.1,1.2,2.47,2.14,2.20")
        assertEquals(5, completedEntity.todayVersesCount)
        assertTrue(completedEntity.todayVersesCount >= completedEntity.dailyVerseGoal)
    }

    @Test
    fun verifyWeeklyInsightsSummaryCalculation() {
        val days = listOf(
            com.example.data.model.DayHabitStat(
                date = "2026-10-13",
                dayLabelBn = "মঙ্গল",
                dayLabelEn = "Tue",
                studySeconds = 1200L,
                studyMinutes = 20f,
                versesCount = 6,
                timeGoalMinutes = 15,
                verseGoal = 5,
                isTimeGoalMet = true,
                isVerseGoalMet = true,
                isAnyGoalMet = true
            ),
            com.example.data.model.DayHabitStat(
                date = "2026-10-14",
                dayLabelBn = "বুধ",
                dayLabelEn = "Wed",
                studySeconds = 600L,
                studyMinutes = 10f,
                versesCount = 3,
                timeGoalMinutes = 15,
                verseGoal = 5,
                isTimeGoalMet = false,
                isVerseGoalMet = false,
                isAnyGoalMet = false
            ),
            com.example.data.model.DayHabitStat(
                date = "2026-10-15",
                dayLabelBn = "বৃহঃ",
                dayLabelEn = "Thu",
                studySeconds = 900L,
                studyMinutes = 15f,
                versesCount = 5,
                timeGoalMinutes = 15,
                verseGoal = 5,
                isTimeGoalMet = true,
                isVerseGoalMet = true,
                isAnyGoalMet = true
            )
        )
        val totalSec = days.sumOf { it.studySeconds }
        val totalVerses = days.sumOf { it.versesCount }
        val metDays = days.count { it.isAnyGoalMet }
        assertEquals(2700L, totalSec)
        assertEquals(14, totalVerses)
        assertEquals(2, metDays)
    }

    @Test
    fun verifyBengaliAudioRecitationModes() {
        val verse = GitaContentProvider.getVerse(2, 47)
        assertNotNull(verse)

        // Translation recitation text
        val translationText = "অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber} এর সরল বঙ্গানুবাদ: ${verse.translationBengali}"
        assertTrue(translationText.contains("বঙ্গানুবাদ"))
        assertTrue(translationText.contains("কর্মেই তোমার একমাত্র অধিকার"))

        // Shloka pronunciation recitation text
        val shlokaText = "${verse.pronunciationBengali} ... ইতি শ্রীমদ্ভগবদ্গীতা।"
        assertTrue(shlokaText.contains("কর্মণ্যেবাধিকারস্তে"))

        // Mode enum verification
        val modes = com.example.audio.RecitationMode.values()
        assertEquals(3, modes.size)
        assertTrue(modes.contains(com.example.audio.RecitationMode.TRANSLATION_ONLY))
        assertTrue(modes.contains(com.example.audio.RecitationMode.SHLOKA_ONLY))
        assertTrue(modes.contains(com.example.audio.RecitationMode.BOTH))
    }

    @Test
    fun verifyRandomVerseGenerationForDailyNotification() {
        val randomVerse = GitaContentProvider.getRandomVerse()
        assertNotNull(randomVerse)
        assertTrue(randomVerse.chapterNumber in 1..18)
        assertTrue(randomVerse.verseNumber >= 1)
        assertTrue(randomVerse.shlokaSanskrit.isNotBlank())
        assertTrue(randomVerse.translationBengali.isNotBlank())
        assertTrue(randomVerse.pronunciationBengali.isNotBlank())
    }

    @Test
    fun verifyVerseOfTheDayFunctionality() {
        val dayVerse1 = GitaContentProvider.getDailyVerse(1)
        val dayVerse2 = GitaContentProvider.getDailyVerse(2)
        assertNotNull(dayVerse1)
        assertNotNull(dayVerse2)
        assertTrue(dayVerse1.shlokaSanskrit.isNotBlank())
        assertTrue(dayVerse1.translationBengali.isNotBlank())
        assertTrue(dayVerse1.pronunciationBengali.isNotBlank())
        // Daily verse for day 1 is deterministic
        assertEquals(dayVerse1.verseId, GitaContentProvider.getDailyVerse(1).verseId)
    }

    @Test
    fun verifyDailyNotificationFormatting() {
        val verse = GitaContentProvider.getVerse(2, 47)
        val title = "আজকের অমৃত বাণী • অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber}"
        assertTrue(title.contains("অধ্যায় 2"))
        assertTrue(title.contains("শ্লোক 47"))

        val bigText = buildString {
            append("শ্লোক:\n॥ ${verse.pronunciationBengali} ॥\n\n")
            append("সরল বঙ্গানুবাদ:\n${verse.translationBengali}\n\n")
            append("— শ্রীমদ্ভগবদ্গীতা পাঠ ও আত্মচিন্তন")
        }
        assertTrue(bigText.contains("কর্মণ্যেবাধিকারস্তে"))
        assertTrue(bigText.contains("বঙ্গানুবাদ"))
        assertTrue(bigText.contains("শ্রীমদ্ভগবদ্গীতা"))
    }

    @Test
    fun verifyChapterProgressEntityPercentageCalculations() {
        val entity = com.example.data.local.ChapterProgressEntity(
            chapterNumber = 1,
            totalVerses = 47,
            readVersesCount = 10,
            completionPercentage = (10f / 47f) * 100f,
            readVersesList = "1,2,3,4,5,6,7,8,9,10",
            isCompleted = false
        )
        assertEquals(1, entity.chapterNumber)
        assertEquals(47, entity.totalVerses)
        assertEquals(10, entity.readVersesCount)
        assertEquals(21.276596f, entity.completionPercentage, 0.01f)
        assertFalse(entity.isCompleted)
        assertTrue(entity.hasReadVerse(5))
        assertFalse(entity.hasReadVerse(11))
        assertEquals(10, entity.readVerseNumbersSet.size)

        // Completed chapter
        val completedEntity = com.example.data.local.ChapterProgressEntity(
            chapterNumber = 2,
            totalVerses = 72,
            readVersesCount = 72,
            completionPercentage = 100f,
            readVersesList = (1..72).joinToString(","),
            isCompleted = true
        )
        assertEquals(100f, completedEntity.completionPercentage, 0.001f)
        assertTrue(completedEntity.isCompleted)
        assertTrue(completedEntity.hasReadVerse(47))
    }

    @Test
    fun verifyAudioPlayerTimeFormatting() {
        assertEquals("00:00", com.example.ui.components.formatAudioTime(0f))
        assertEquals("00:15", com.example.ui.components.formatAudioTime(15.4f))
        assertEquals("01:15", com.example.ui.components.formatAudioTime(75.9f))
        assertEquals("02:30", com.example.ui.components.formatAudioTime(150f))
    }

    @Test
    fun verifyAudioPlayerPlaybackStatesAndSeekBounds() {
        val states = com.example.audio.PlaybackState.values()
        assertEquals(3, states.size)
        assertTrue(states.contains(com.example.audio.PlaybackState.IDLE))
        assertTrue(states.contains(com.example.audio.PlaybackState.PLAYING))
        assertTrue(states.contains(com.example.audio.PlaybackState.PAUSED))

        // Seek bounds clamp check
        val duration = 24.5f
        val seekTarget = 30f
        val clamped = seekTarget.coerceIn(0f, duration)
        assertEquals(24.5f, clamped, 0.001f)

        val negativeSeek = -5f
        val clampedNegative = negativeSeek.coerceIn(0f, duration)
        assertEquals(0f, clampedNegative, 0.001f)
    }

    @Test
    fun verifyAudioPlayerSkipVersesCalculation() {
        val chapter2Verses = GitaContentProvider.getAllVersesForChapter(2)
        assertTrue(chapter2Verses.isNotEmpty())

        val currentVerse = GitaContentProvider.getVerse(2, 47)
        assertNotNull(currentVerse)

        val currentIndex = chapter2Verses.indexOfFirst { it.chapterNumber == 2 && it.verseNumber == 47 }
        assertTrue(currentIndex >= 0)

        // Skip Next
        val nextIndex = (currentIndex + 1).coerceAtMost(chapter2Verses.size - 1)
        val nextVerse = chapter2Verses[nextIndex]
        assertEquals(2, nextVerse.chapterNumber)
        assertEquals(48, nextVerse.verseNumber)

        // Skip Previous
        val prevIndex = (currentIndex - 1).coerceAtLeast(0)
        val prevVerse = chapter2Verses[prevIndex]
        assertEquals(2, prevVerse.chapterNumber)
        assertEquals(46, prevVerse.verseNumber)
    }

    @Test
    fun verifyAudioSeekProgressCalculation() {
        val duration = 20f
        // 50% seek
        val targetProgress = 0.5f
        val calculatedPosition = (targetProgress.coerceIn(0f, 1f)) * duration
        assertEquals(10f, calculatedPosition, 0.001f)

        // Relative delta seek +5s from 10s
        val delta = 5f
        val newPos = (calculatedPosition + delta).coerceIn(0f, duration)
        assertEquals(15f, newPos, 0.001f)

        // Relative delta seek -8s from 15s
        val backDelta = -8f
        val backPos = (newPos + backDelta).coerceIn(0f, duration)
        assertEquals(7f, backPos, 0.001f)
    }

    @Test
    fun verifyFontSizeAdjustmentSliderScaling() {
        val defaultSettings = com.example.data.model.UserReadingSettings()
        assertEquals(19f, defaultSettings.shlokaFontSize, 0.01f)
        assertEquals(16f, defaultSettings.translationFontSize, 0.01f)
        assertEquals(15f, defaultSettings.purportFontSize, 0.01f)

        // Scale to 20sp translation size
        val targetTrans = 20f
        val scale = targetTrans / 16f
        val scaledShloka = (19f * scale).coerceIn(14f, 32f)
        val scaledPurport = (15f * scale).coerceIn(11f, 26f)

        assertEquals(23.75f, scaledShloka, 0.01f)
        assertEquals(18.75f, scaledPurport, 0.01f)

        // Preset small (14sp)
        val smallScale = 14f / 16f
        val smallShloka = (19f * smallScale).coerceIn(14f, 32f)
        assertEquals(16.625f, smallShloka, 0.01f)
    }

    @Test
    fun verifyVerseOfTheDayDailyDeterminismAndRandomFallback() {
        // Daily verse for day 42 should be deterministic
        val verseDay42 = GitaContentProvider.getDailyVerse(42)
        assertNotNull(verseDay42)
        assertEquals(verseDay42.verseId, GitaContentProvider.getDailyVerse(42).verseId)

        // Random verse should return valid content across chapters
        val randomVerse1 = GitaContentProvider.getRandomVerse()
        val randomVerse2 = GitaContentProvider.getRandomVerse()
        assertNotNull(randomVerse1)
        assertNotNull(randomVerse2)
        assertTrue(randomVerse1.verseId.isNotBlank())
        assertTrue(randomVerse1.shlokaSanskrit.isNotBlank())
        assertTrue(randomVerse1.translationBengali.isNotBlank())
    }

    @Test
    fun verifyVerseOfTheDayDisplayIntegrity() {
        val dailyVerse = GitaContentProvider.getDailyVerse(100)
        assertNotNull(dailyVerse)
        // Chapter number and verse number must be positive
        assertTrue(dailyVerse.chapterNumber in 1..18)
        assertTrue(dailyVerse.verseNumber > 0)
        // Shloka, pronunciation and translation should not be blank
        assertTrue(dailyVerse.shlokaSanskrit.isNotEmpty())
        assertTrue(dailyVerse.pronunciationBengali.isNotEmpty())
        assertTrue(dailyVerse.translationBengali.isNotEmpty())
        // Purport should give spiritual reflection
        assertTrue(dailyVerse.purportBengali.isNotEmpty())
    }

    @Test
    fun verifyVerseAudioFileLinkingToBengaliVerse() {
        val verse = GitaContentProvider.getVerse(2, 47)
        assertNotNull(verse)

        val fileName = "gita_recitation_ch${verse.chapterNumber}_v${verse.verseNumber}.mp3"
        val expectedUri = "asset:///audio/recitations/bengali/$fileName"
        val expectedTitle = "অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber} বাংলা আবৃত্তি"

        val audioFile = com.example.audio.VerseAudioFile(
            verseId = verse.verseId,
            chapterNumber = verse.chapterNumber,
            verseNumber = verse.verseNumber,
            audioFileName = fileName,
            audioUri = expectedUri,
            titleBengali = expectedTitle,
            bengaliShlok = verse.pronunciationBengali,
            bengaliTranslation = verse.translationBengali,
            estimatedDurationSeconds = 25f
        )

        assertEquals("2.47", audioFile.verseId)
        assertEquals("gita_recitation_ch2_v47.mp3", audioFile.audioFileName)
        assertEquals("asset:///audio/recitations/bengali/gita_recitation_ch2_v47.mp3", audioFile.audioUri)
        assertTrue(audioFile.titleBengali.contains("অধ্যায় 2"))
        assertTrue(audioFile.titleBengali.contains("শ্লোক 47"))
        assertTrue(audioFile.bengaliShlok.contains("কর্মণ্যেবাধিকারস্তে"))
        assertTrue(audioFile.bengaliTranslation.contains("কর্মেই তোমার একমাত্র অধিকার"))
        assertEquals(25f, audioFile.estimatedDurationSeconds, 0.001f)
    }

    @Test
    fun verifyVerseAudioPlayerControlsAndStateProperties() {
        // Playback state enum values
        val states = com.example.audio.PlaybackState.values()
        assertEquals(3, states.size)
        assertTrue(states.contains(com.example.audio.PlaybackState.IDLE))
        assertTrue(states.contains(com.example.audio.PlaybackState.PLAYING))
        assertTrue(states.contains(com.example.audio.PlaybackState.PAUSED))

        // Recitation modes enum values
        val modes = com.example.audio.RecitationMode.values()
        assertEquals(3, modes.size)
        assertTrue(modes.contains(com.example.audio.RecitationMode.SHLOKA_ONLY))
        assertTrue(modes.contains(com.example.audio.RecitationMode.TRANSLATION_ONLY))
        assertTrue(modes.contains(com.example.audio.RecitationMode.BOTH))

        // Time formatting
        assertEquals("00:00", com.example.ui.components.formatAudioTime(0f))
        assertEquals("00:45", com.example.ui.components.formatAudioTime(45.2f))
        assertEquals("01:30", com.example.ui.components.formatAudioTime(90f))
    }

    @Test
    fun verifyVerseShareFormattingAndOptions() {
        val verse = com.example.data.source.GitaContentProvider.getVerse(2, 47)
        val shareText = com.example.util.ShareHelper.buildVerseShareText(verse)

        assertTrue(shareText.contains("অধ্যায় 2"))
        assertTrue(shareText.contains("শ্লোক 47"))
        assertTrue(shareText.contains("কর্মণ্যেবাধিকারস্তে"))
        assertTrue(shareText.contains("কর্মেই তোমার একমাত্র অধিকার"))
        assertTrue(shareText.contains("— শ্রীমদ্ভগবদ্গীতা বাংলা"))
    }

    @Test
    fun verifyDailyVerseProviderCuratedVerses() {
        val curated = com.example.data.source.DailyVerseProvider.getCuratedDailyVerseForDay(100)
        assertNotNull(curated)
        assertTrue(curated.verse.chapterNumber in 1..18)
        assertTrue(curated.themeCategory.isNotEmpty())
        assertTrue(curated.inspirationQuote.isNotEmpty())
        assertTrue(curated.dailyWisdomBengali.isNotEmpty())
        assertTrue(curated.dailyPracticeBengali.isNotEmpty())

        val digitString = com.example.data.source.DailyVerseProvider.toBengaliDigits(2026)
        assertEquals("২০২৬", digitString)
        val digitSingle = com.example.data.source.DailyVerseProvider.toBengaliDigits(7)
        assertEquals("৭", digitSingle)
    }

    @Test
    fun verifyMediaPlaybackControllerTimingAndFormat() {
        assertEquals("00:00", com.example.ui.components.formatAudioTime(0f))
        assertEquals("00:05", com.example.ui.components.formatAudioTime(5f))
        assertEquals("01:15", com.example.ui.components.formatAudioTime(75.8f))
        assertEquals("10:00", com.example.ui.components.formatAudioTime(600f))
    }

    @Test
    fun verifyBengaliNumberHelper() {
        assertEquals("৪৭", com.example.util.BengaliNumberHelper.toBengali(47))
        assertEquals("২", com.example.util.BengaliNumberHelper.toBengali(2))
        assertEquals("১০০", com.example.util.BengaliNumberHelper.toBengali(100L))
        assertEquals("2.47", com.example.util.BengaliNumberHelper.normalizeBengaliToAscii("২.৪৭"))
        assertEquals("অধ্যায় 2 শ্লোক 47", com.example.util.BengaliNumberHelper.normalizeBengaliToAscii("অধ্যায় ২ শ্লোক ৪৭"))
    }

    @Test
    fun verifySearchByChapterAndVerseNumber() {
        // Bengali digits separated by dot
        val resultsBn = GitaContentProvider.searchVerses("২.৪৭")
        assertTrue("Expected 2.47 to be found", resultsBn.any { it.chapterNumber == 2 && it.verseNumber == 47 })

        // English digits separated by dot
        val resultsEn = GitaContentProvider.searchVerses("2.47")
        assertTrue("Expected 2.47 to be found", resultsEn.any { it.chapterNumber == 2 && it.verseNumber == 47 })

        // Bengali digits with colon
        val resultsColon = GitaContentProvider.searchVerses("২:৪৭")
        assertTrue("Expected 2:47 to be found", resultsColon.any { it.chapterNumber == 2 && it.verseNumber == 47 })

        // Natural Bengali text: "অধ্যায় ২ শ্লোক ৪৭"
        val resultsText = GitaContentProvider.searchVerses("অধ্যায় ২ শ্লোক ৪৭")
        assertTrue("Expected verse from 'অধ্যায় ২ শ্লোক ৪৭'", resultsText.any { it.chapterNumber == 2 && it.verseNumber == 47 })

        // Another chapter/verse: 4.7 (Yada yada hi dharmasya)
        val results47 = GitaContentProvider.searchVerses("৪.৭")
        assertTrue("Expected 4.7 to be found", results47.any { it.chapterNumber == 4 && it.verseNumber == 7 })

        // Textual: "অধ্যায় ৪ শ্লোক ৭"
        val results47Text = GitaContentProvider.searchVerses("অধ্যায় ৪ শ্লোক ৭")
        assertTrue("Expected 4.7 from text query", results47Text.any { it.chapterNumber == 4 && it.verseNumber == 7 })
    }

    @Test
    fun verifySearchByChapterNumberOnly() {
        val resultsCh2 = GitaContentProvider.searchVerses("অধ্যায় ২")
        assertTrue(resultsCh2.isNotEmpty())
        assertTrue("All results should belong to chapter 2", resultsCh2.all { it.chapterNumber == 2 })

        val resultsCh12 = GitaContentProvider.searchVerses("অধ্যায় ১২")
        assertTrue(resultsCh12.isNotEmpty())
        assertTrue(resultsCh12.any { it.chapterNumber == 12 })
    }

    @Test
    fun verifySearchByVerseNumberOnly() {
        val resultsV47 = GitaContentProvider.searchVerses("শ্লোক ৪৭")
        assertTrue(resultsV47.isNotEmpty())
        assertTrue("Results should have verseNumber 47", resultsV47.all { it.verseNumber == 47 })
        assertTrue("Should include Chapter 2 verse 47", resultsV47.any { it.chapterNumber == 2 && it.verseNumber == 47 })

        val resultsNumber47 = GitaContentProvider.searchVerses("৪৭")
        assertTrue(resultsNumber47.isNotEmpty())
        assertTrue("Results should have verseNumber 47", resultsNumber47.any { it.verseNumber == 47 })
    }

    @Test
    fun verifySearchByBengaliKeywords() {
        // Keyword: কর্ম
        val karmaResults = GitaContentProvider.searchVerses("কর্ম")
        assertTrue("Karma search should return verses", karmaResults.isNotEmpty())
        assertTrue(karmaResults.any { it.chapterNumber == 2 && it.verseNumber == 47 })

        // Keyword: ভক্তি
        val bhaktiResults = GitaContentProvider.searchVerses("ভক্তি")
        assertTrue("Bhakti search should return verses", bhaktiResults.isNotEmpty())
        assertTrue(bhaktiResults.any { it.chapterNumber == 9 || it.chapterNumber == 12 || it.chapterNumber == 18 })

        // Keyword: আত্মা
        val atmaResults = GitaContentProvider.searchVerses("আত্মা")
        assertTrue("Atma search should return verses", atmaResults.isNotEmpty())
        assertTrue(atmaResults.any { it.chapterNumber == 2 && it.verseNumber == 20 })

        // Keyword: ক্রোধ
        val krodhaResults = GitaContentProvider.searchVerses("ক্রোধ")
        assertTrue("Krodha search should return verses", krodhaResults.isNotEmpty())
        assertTrue(krodhaResults.any { it.chapterNumber == 2 && (it.verseNumber == 62 || it.verseNumber == 63) })

        // Keyword: ধ্যান
        val dhyanResults = GitaContentProvider.searchVerses("ধ্যান")
        assertTrue("Dhyan search should return verses", dhyanResults.isNotEmpty())
        assertTrue(dhyanResults.any { it.chapterNumber == 6 })
    }

    @Test
    fun verifyAll18ChaptersHaveCompleteVerses() {
        for (chapter in GitaContentProvider.chapters) {
            val verses = GitaContentProvider.getAllVersesForChapter(chapter.number)
            assertEquals("Chapter ${chapter.number} must contain ${chapter.verseCount} verses", chapter.verseCount, verses.size)

            for (verse in verses) {
                assertEquals(chapter.number, verse.chapterNumber)
                assertTrue("Shloka Sanskrit must not be blank", verse.shlokaSanskrit.isNotBlank())
                assertTrue("Pronunciation Bengali must not be blank", verse.pronunciationBengali.isNotBlank())
                assertTrue("Translation Bengali must not be blank", verse.translationBengali.isNotBlank())
                assertTrue("Purport Bengali must not be blank", verse.purportBengali.isNotBlank())
            }
        }
    }

    @Test
    fun verifyAudioRecitationTextGenerationForEveryMode() {
        val verse1 = GitaContentProvider.getVerse(1, 1)
        val verse247 = GitaContentProvider.getVerse(2, 47)
        val verse1866 = GitaContentProvider.getVerse(18, 66)

        val testVerses = listOf(verse1, verse247, verse1866)
        for (v in testVerses) {
            val shlokaAudio = "${v.pronunciationBengali} ... ইতি শ্রীমদ্ভগবদ্গীতা।"
            val transAudio = "অধ্যায় ${v.chapterNumber}, শ্লোক ${v.verseNumber} এর সরল বঙ্গানুবাদ: ${v.translationBengali}"
            val bothAudio = "${v.pronunciationBengali} ... সরল বঙ্গানুবাদ: ${v.translationBengali}"

            assertTrue(shlokaAudio.isNotBlank())
            assertTrue(transAudio.isNotBlank())
            assertTrue(bothAudio.isNotBlank())
            assertTrue(shlokaAudio.contains(v.pronunciationBengali))
            assertTrue(transAudio.contains(v.translationBengali))
        }
    }

    @Test
    fun verifyVerseJournalReflectionAndNotesFormatting() {
        val verse = GitaContentProvider.getVerse(2, 47)
        val note = com.example.data.model.VerseNote(
            id = 1L,
            chapterNumber = 2,
            verseNumber = 47,
            noteText = "ফলের আশা ত্যাগ করে নিষ্ঠার সাথে নিজের দায়িত্ব পালন করাই শ্রেষ্ঠ সাধনা। #কর্মযোগ #শান্তি",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        assertEquals(2, note.chapterNumber)
        assertEquals(47, note.verseNumber)
        assertTrue(note.noteText.contains("#কর্মযোগ"))
        assertTrue(note.noteText.contains("#শান্তি"))

        val charCount = note.noteText.trim().length
        val wordCount = note.noteText.trim().split(Regex("\\s+")).size
        assertTrue(charCount > 30)
        assertTrue(wordCount >= 10)

        // Bengali number conversion check
        val charCountBn = com.example.util.BengaliNumberHelper.toBengali(charCount)
        val wordCountBn = com.example.util.BengaliNumberHelper.toBengali(wordCount)
        assertTrue(charCountBn.isNotBlank())
        assertTrue(wordCountBn.isNotBlank())

        // NoteEntity conversion and roundtrip
        val entity = com.example.data.local.NoteEntity(
            id = note.id,
            chapterNumber = note.chapterNumber,
            verseNumber = note.verseNumber,
            noteText = note.noteText,
            createdAt = note.createdAt,
            updatedAt = note.updatedAt
        )
        assertEquals(note.noteText, entity.noteText)
        assertEquals(note.chapterNumber, entity.chapterNumber)
        assertEquals(note.verseNumber, entity.verseNumber)
    }
}


