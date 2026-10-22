package com.example.util

object TimeHelper {

    fun formatSecondsToTimer(totalSeconds: Long): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun formatMinutesBengali(minutes: Long): String {
        return "${minutes} মিনিট"
    }

    fun formatTodayStudySummary(totalSeconds: Long): String {
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return if (minutes > 0) {
            "${minutes} মিনিট ${seconds} সেকেন্ড"
        } else {
            "${seconds} সেকেন্ড"
        }
    }
}
