package com.example.data.model

/**
 * Data model representing a curated Bengali Daily Verse with daily life wisdom,
 * theme, date, and actionable spiritual practice for consistent engagement.
 */
data class DailyVerseCurated(
    val dayOfYear: Int,
    val dateDisplayBengali: String,
    val verse: Verse,
    val themeCategory: String,
    val inspirationQuote: String,
    val dailyWisdomBengali: String,
    val dailyPracticeBengali: String
)
