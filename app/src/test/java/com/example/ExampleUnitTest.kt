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
    fun verifyVerseTextFontSizeSliderFunctionalityAndReadability() {
        val settings = com.example.data.model.UserReadingSettings()

        // 1. Initial baseline: 19sp verse shloka size is default
        assertEquals(19f, settings.shlokaFontSize, 0.01f)
        assertEquals("১৯ sp", "${com.example.util.BengaliNumberHelper.toBengali(settings.shlokaFontSize.toInt())} sp")

        // 2. Adjust verse slider to minimum (14sp)
        val minShloka = 14f
        val minScale = minShloka / 19f
        val minTrans = (16f * minScale).coerceIn(12f, 26f)
        val minPurport = (15f * minScale).coerceIn(11f, 24f)
        val minSettings = settings.copy(
            shlokaFontSize = minShloka,
            translationFontSize = minTrans,
            purportFontSize = minPurport
        )
        assertEquals(14f, minSettings.shlokaFontSize, 0.01f)
        assertTrue("Translation must be >= 12sp", minSettings.translationFontSize >= 12f)
        assertTrue("Purport must be >= 11sp", minSettings.purportFontSize >= 11f)
        assertEquals("১৪ sp", "${com.example.util.BengaliNumberHelper.toBengali(minSettings.shlokaFontSize.toInt())} sp")

        // 3. Adjust verse slider to maximum (32sp) for maximum readability
        val maxShloka = 32f
        val maxScale = maxShloka / 19f
        val maxTrans = (16f * maxScale).coerceIn(12f, 26f)
        val maxPurport = (15f * maxScale).coerceIn(11f, 24f)
        val maxSettings = settings.copy(
            shlokaFontSize = maxShloka,
            translationFontSize = maxTrans,
            purportFontSize = maxPurport
        )
        assertEquals(32f, maxSettings.shlokaFontSize, 0.01f)
        assertTrue("Translation must be <= 26sp", maxSettings.translationFontSize <= 26f)
        assertTrue("Purport must be <= 24sp", maxSettings.purportFontSize <= 24f)
        assertEquals("৩২ sp", "${com.example.util.BengaliNumberHelper.toBengali(maxSettings.shlokaFontSize.toInt())} sp")

        // 4. Verify preset chips: 16sp (Small), 19sp (Default), 23sp (Large), 27sp (Extra Large)
        val presetSizes = listOf(16f, 19f, 23f, 27f)
        for (targetSize in presetSizes) {
            val scale = targetSize / 19f
            val trans = (16f * scale).coerceIn(12f, 26f)
            val purport = (15f * scale).coerceIn(11f, 24f)
            val updated = settings.copy(
                shlokaFontSize = targetSize,
                translationFontSize = trans,
                purportFontSize = purport
            )
            assertEquals(targetSize, updated.shlokaFontSize, 0.01f)
            assertTrue("Translation font size must be proportional", updated.translationFontSize > 0f)
            assertTrue("Purport font size must be proportional", updated.purportFontSize > 0f)
        }
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

    @Test
    fun verifyBlueCardBorderAttributes() {
        val border = com.example.ui.theme.blueCardBorder()
        assertNotNull(border)
        val solidColor = border.brush as androidx.compose.ui.graphics.SolidColor
        assertEquals(com.example.ui.theme.BlueCardBorder, solidColor.value)
    }

    @Test
    fun verifyMainScreenSearchBarFilteringByKeywordsAndVerseNumbers() {
        // 1. Verse number filtering with Bengali numerals (২.৪৭)
        val verse247Results = GitaContentProvider.searchVerses("২.৪৭")
        assertTrue("Filter by '২.৪৭' must return at least one verse", verse247Results.isNotEmpty())
        assertEquals(2, verse247Results.first().chapterNumber)
        assertEquals(47, verse247Results.first().verseNumber)

        // 2. Verse number filtering with ASCII digits (18.66)
        val verse1866Results = GitaContentProvider.searchVerses("18.66")
        assertTrue("Filter by '18.66' must return at least one verse", verse1866Results.isNotEmpty())
        assertEquals(18, verse1866Results.first().chapterNumber)
        assertEquals(66, verse1866Results.first().verseNumber)

        // 3. Keyword filtering: "কর্ম"
        val karmaVerses = GitaContentProvider.searchVerses("কর্ম")
        assertTrue("Filter by 'কর্ম' should return karma verses", karmaVerses.isNotEmpty())
        assertTrue(karmaVerses.any { it.translationBengali.contains("কর্ম") || it.pronunciationBengali.contains("কর্ম") })

        // 4. Keyword filtering: "ভক্তি"
        val bhaktiVerses = GitaContentProvider.searchVerses("ভক্তি")
        assertTrue("Filter by 'ভক্তি' should return bhakti verses", bhaktiVerses.isNotEmpty())

        // 5. Keyword filtering: "শান্তি"
        val shantiVerses = GitaContentProvider.searchVerses("শান্তি")
        assertTrue("Filter by 'শান্তি' should return shanti verses", shantiVerses.isNotEmpty())

        // 6. Natural Bengali phrase query: "অধ্যায় ২ শ্লোক ৪৭"
        val phraseResults = GitaContentProvider.searchVerses("অধ্যায় ২ শ্লোক ৪৭")
        assertTrue(phraseResults.any { it.chapterNumber == 2 && it.verseNumber == 47 })

        // 7. Non-matching query
        val emptyResults = GitaContentProvider.searchVerses("xyznonexistentword")
        assertTrue(emptyResults.isEmpty())
    }

    @Test
    fun verifyVerseCardAudioPlayerLinkingAndControls() {
        val testVerses = listOf(
            GitaContentProvider.getVerse(1, 1),
            GitaContentProvider.getVerse(2, 47),
            GitaContentProvider.getVerse(18, 66)
        )

        for (verse in testVerses) {
            val fileName = "gita_recitation_ch${verse.chapterNumber}_v${verse.verseNumber}.mp3"
            val uri = "asset:///audio/recitations/bengali/$fileName"
            val title = "অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber} বাংলা আবৃত্তি"
            val audioFile = com.example.audio.VerseAudioFile(
                verseId = verse.verseId,
                chapterNumber = verse.chapterNumber,
                verseNumber = verse.verseNumber,
                audioFileName = fileName,
                audioUri = uri,
                titleBengali = title,
                bengaliShlok = verse.pronunciationBengali,
                bengaliTranslation = verse.translationBengali,
                estimatedDurationSeconds = 25f
            )

            assertEquals(verse.verseId, audioFile.verseId)
            assertTrue(audioFile.audioFileName.endsWith(".mp3"))
            assertTrue(audioFile.titleBengali.contains("অধ্যায় ${verse.chapterNumber}"))
            assertTrue(audioFile.bengaliShlok.isNotBlank())
            assertTrue(audioFile.bengaliTranslation.isNotBlank())
            assertTrue(audioFile.estimatedDurationSeconds > 0f)

            // Seek progress calculation: 0%, 50%, 100%
            val dur = audioFile.estimatedDurationSeconds
            assertEquals("00:00", com.example.ui.components.formatAudioTime(0f * dur))
            assertEquals("00:12", com.example.ui.components.formatAudioTime(0.5f * dur))
            assertEquals("00:25", com.example.ui.components.formatAudioTime(1.0f * dur))

            // Rewind & Forward boundary checks
            val rewindFrom10 = (10f - 5f).coerceIn(0f, dur)
            assertEquals(5f, rewindFrom10, 0.001f)
            val rewindFrom2 = (2f - 5f).coerceIn(0f, dur)
            assertEquals(0f, rewindFrom2, 0.001f)
            val forwardFrom22 = (22f + 5f).coerceIn(0f, dur)
            assertEquals(dur, forwardFrom22, 0.001f)
        }
    }

    @Test
    fun verifyTopReadingProgressTrackerChaptersAndVersesCalculation() {
        val totalChapters = 18
        val totalVerses = 700

        // 1. Initial State: 0 verses, 0 chapters
        val initialProgress = com.example.data.model.OverallGitaProgress(
            totalChapters = totalChapters,
            completedChaptersCount = 0,
            inProgressChaptersCount = 0,
            totalVerses = totalVerses,
            totalVersesRead = 0,
            overallPercentage = 0f
        )
        assertEquals(0, initialProgress.completedChaptersCount)
        assertEquals(0, initialProgress.totalVersesRead)
        assertEquals(0f, initialProgress.overallPercentage, 0.001f)

        val initialVerseFraction = (initialProgress.totalVersesRead.toFloat() / initialProgress.totalVerses.toFloat()).coerceIn(0f, 1f)
        val initialChapterFraction = (initialProgress.completedChaptersCount.toFloat() / initialProgress.totalChapters.toFloat()).coerceIn(0f, 1f)
        assertEquals(0f, initialVerseFraction, 0.001f)
        assertEquals(0f, initialChapterFraction, 0.001f)
        assertEquals("০ / ৭০০", "${com.example.util.BengaliNumberHelper.toBengali(0)} / ${com.example.util.BengaliNumberHelper.toBengali(700)}")
        assertEquals("০ / ১৮", "${com.example.util.BengaliNumberHelper.toBengali(0)} / ${com.example.util.BengaliNumberHelper.toBengali(18)}")

        // 2. Partial Progress: 140 verses read across 4 chapters (Chapter 1 completed: 47 verses, Chapter 2 completed: 72 verses, Chapter 3 in-progress: 21 verses)
        val completedCh = 2
        val inProgressCh = 1
        val versesRead = 140
        val remainingVerses = (totalVerses - versesRead).coerceAtLeast(0)
        assertEquals(560, remainingVerses)

        val partialProgress = com.example.data.model.OverallGitaProgress(
            totalChapters = totalChapters,
            completedChaptersCount = completedCh,
            inProgressChaptersCount = inProgressCh,
            totalVerses = totalVerses,
            totalVersesRead = versesRead,
            overallPercentage = (versesRead.toFloat() / totalVerses.toFloat()) * 100f
        )

        assertEquals(2, partialProgress.completedChaptersCount)
        assertEquals(1, partialProgress.inProgressChaptersCount)
        assertEquals(140, partialProgress.totalVersesRead)
        assertEquals(20.0f, partialProgress.overallPercentage, 0.01f)

        val verseFraction = (partialProgress.totalVersesRead.toFloat() / partialProgress.totalVerses.toFloat()).coerceIn(0f, 1f)
        val chapterFraction = (partialProgress.completedChaptersCount.toFloat() / partialProgress.totalChapters.toFloat()).coerceIn(0f, 1f)
        assertEquals(0.2f, verseFraction, 0.001f)
        assertEquals(2f / 18f, chapterFraction, 0.001f)

        // Verify Bengali text representation
        assertEquals("১৪০ / ৭০০", "${com.example.util.BengaliNumberHelper.toBengali(versesRead)} / ${com.example.util.BengaliNumberHelper.toBengali(totalVerses)}")
        assertEquals("২ / ১৮", "${com.example.util.BengaliNumberHelper.toBengali(completedCh)} / ${com.example.util.BengaliNumberHelper.toBengali(totalChapters)}")
        assertEquals("২০.০% সমাপ্ত", "${com.example.util.BengaliNumberHelper.toBengali(String.format(java.util.Locale.US, "%.1f", partialProgress.overallPercentage))}% সমাপ্ত")

        // 3. Complete Progress: 700 verses, 18 chapters completed
        val fullProgress = com.example.data.model.OverallGitaProgress(
            totalChapters = totalChapters,
            completedChaptersCount = 18,
            inProgressChaptersCount = 0,
            totalVerses = totalVerses,
            totalVersesRead = 700,
            overallPercentage = 100f
        )
        val fullVerseFraction = (fullProgress.totalVersesRead.toFloat() / fullProgress.totalVerses.toFloat()).coerceIn(0f, 1f)
        val fullChapterFraction = (fullProgress.completedChaptersCount.toFloat() / fullProgress.totalChapters.toFloat()).coerceIn(0f, 1f)
        assertEquals(1.0f, fullVerseFraction, 0.001f)
        assertEquals(1.0f, fullChapterFraction, 0.001f)
        assertEquals("১০০.০% সমাপ্ত", "${com.example.util.BengaliNumberHelper.toBengali(String.format(java.util.Locale.US, "%.1f", fullProgress.overallPercentage))}% সমাপ্ত")
    }

    @Test
    fun verifyOfflineChapterDownloadSettingsAndAccessibility() {
        val initialSettings = com.example.data.model.UserReadingSettings()
        // 1. Initial State: Offline reading is disabled and no downloaded chapters
        assertFalse(initialSettings.offlineReadingEnabled)
        assertTrue(initialSettings.downloadedChapters.isEmpty())
        assertFalse(initialSettings.isChapterDownloaded(1))

        // 2. Enable offline reading and download specific chapters (e.g. Chapter 1 and Chapter 2)
        val withCh1and2 = initialSettings.copy(
            offlineReadingEnabled = true,
            downloadedChapters = setOf(1, 2)
        )
        assertTrue(withCh1and2.offlineReadingEnabled)
        assertEquals(2, withCh1and2.downloadedChapters.size)
        assertTrue(withCh1and2.isChapterDownloaded(1))
        assertTrue(withCh1and2.isChapterDownloaded(2))
        assertFalse(withCh1and2.isChapterDownloaded(3))
        assertFalse(withCh1and2.isChapterDownloaded(18))

        // 3. Entity comma-separated persistence conversion
        val entity = com.example.data.local.AppSettingsEntity(
            offlineReadingEnabled = withCh1and2.offlineReadingEnabled,
            downloadedChaptersList = withCh1and2.downloadedChapters.sorted().joinToString(",")
        )
        assertEquals("1,2", entity.downloadedChaptersList)
        assertEquals(setOf(1, 2), entity.downloadedChaptersSet)

        // 4. Download All 18 Chapters for full offline accessibility
        val allChaptersSet = (1..18).toSet()
        val allDownloadedSettings = withCh1and2.copy(downloadedChapters = allChaptersSet)
        assertEquals(18, allDownloadedSettings.downloadedChapters.size)
        for (ch in 1..18) {
            assertTrue("Chapter $ch must be downloaded", allDownloadedSettings.isChapterDownloaded(ch))
        }

        val allEntity = com.example.data.local.AppSettingsEntity(
            offlineReadingEnabled = true,
            downloadedChaptersList = allDownloadedSettings.downloadedChapters.sorted().joinToString(",")
        )
        assertEquals(18, allEntity.downloadedChaptersSet.size)
        assertTrue(allEntity.downloadedChaptersSet.contains(18))

        // 5. Clear Downloads
        val cleared = allDownloadedSettings.copy(downloadedChapters = emptySet())
        assertTrue(cleared.downloadedChapters.isEmpty())
        assertFalse(cleared.isChapterDownloaded(1))

        // 6. Bengali numbers formatting check
        val downloadedBn = com.example.util.BengaliNumberHelper.toBengali(withCh1and2.downloadedChapters.size)
        assertEquals("২", downloadedBn)
    }

    @Test
    fun verifyCuratedVerseOfTheDayRotationAndWisdom() {
        val curated = com.example.data.source.DailyVerseProvider.getCuratedDailyVerse()
        assertNotNull(curated)
        assertNotNull(curated.verse)
        assertTrue(curated.verse.chapterNumber in 1..18)
        assertTrue(curated.verse.verseNumber >= 1)
        assertTrue(curated.themeCategory.isNotBlank())
        assertTrue(curated.inspirationQuote.isNotBlank())
        assertTrue(curated.dailyWisdomBengali.isNotBlank())
        assertTrue(curated.dailyPracticeBengali.isNotBlank())
        assertTrue(curated.dateDisplayBengali.isNotBlank())
        assertTrue(curated.verse.pronunciationBengali.isNotBlank())
        assertTrue(curated.verse.translationBengali.isNotBlank())
    }

    @Test
    fun verifyAudioPlayerSeekAndProgressMath() {
        val duration = 24.0f
        var position = 6.0f

        // Progress fraction calculation
        var progressFraction = (position / duration).coerceIn(0f, 1f)
        assertEquals(0.25f, progressFraction, 0.001f)

        // Scrubbing to 50%
        val seekFraction = 0.5f
        val userSeekPos = seekFraction * duration
        assertEquals(12.0f, userSeekPos, 0.001f)

        // Seek forward by 5s
        val seekDelta = 5.0f
        val forwardedPos = (userSeekPos + seekDelta).coerceIn(0f, duration)
        assertEquals(17.0f, forwardedPos, 0.001f)

        // Seek backward by 5s
        val rewindDelta = -5.0f
        val rewoundPos = (forwardedPos + rewindDelta).coerceIn(0f, duration)
        assertEquals(12.0f, rewoundPos, 0.001f)

        // Time string formatting
        assertEquals("00:12", com.example.ui.components.formatAudioTime(rewoundPos))
        assertEquals("00:24", com.example.ui.components.formatAudioTime(duration))
        assertEquals("01:15", com.example.ui.components.formatAudioTime(75.0f))
    }

    @Test
    fun verifyVerseOfTheDayMorningNotificationPayload() {
        val verse = com.example.data.source.DailyVerseProvider.getCuratedDailyVerse().verse
        val title = "আজকের শ্লোক • অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber}"
        assertTrue(title.contains("আজকের শ্লোক"))
        assertTrue(title.contains("অধ্যায় ${verse.chapterNumber}"))
        assertTrue(title.contains("শ্লোক ${verse.verseNumber}"))

        val bigText = buildString {
            append("॥ ${verse.pronunciationBengali} ॥\n\n")
            append("সরল বঙ্গানুবাদ:\n${verse.translationBengali}\n\n")
            append("— শ্রীমদ্ভগবদ্গীতা পাঠ ও প্রাতঃকালীন সাধনা")
        }
        assertTrue(bigText.contains(verse.pronunciationBengali))
        assertTrue(bigText.contains(verse.translationBengali))
        assertTrue(bigText.contains("প্রাতঃকালীন সাধনা"))
    }
}


