package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.MainActivity
import com.example.data.model.Verse
import com.example.data.source.GitaContentProvider
import java.util.Calendar
import java.util.concurrent.TimeUnit

object DailyNotificationManager {

    const val CHANNEL_ID = "daily_gita_reflection"
    const val CHANNEL_NAME = "দৈনিক গীতা পাঠ ও সাধনা"
    const val NOTIFICATION_ID = 1008
    const val DAILY_WORK_TAG = "daily_gita_reflection_work"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "প্রতিদিনের আধ্যাত্মিক সাধনা ও শ্লোক স্মরণের নোটিফিকেশন"
                enableVibration(true)
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    /**
     * Posts a notification featuring a random or specified Bhagavad Gita verse in Bengali.
     */
    fun showRandomVerseNotification(context: Context, verse: Verse = GitaContentProvider.getRandomVerse()) {
        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("navigateToVerse", true)
            putExtra("chapterNumber", verse.chapterNumber)
            putExtra("verseNumber", verse.verseNumber)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            verse.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "আজকের অমৃত বাণী • অধ্যায় ${verse.chapterNumber}, শ্লোক ${verse.verseNumber}"
        val summary = verse.translationBengali.take(100) + if (verse.translationBengali.length > 100) "..." else ""

        val bigText = buildString {
            append("শ্লোক:\n॥ ${verse.pronunciationBengali} ॥\n\n")
            append("সরল বঙ্গানুবাদ:\n${verse.translationBengali}\n\n")
            append("— শ্রীমদ্ভগবদ্গীতা পাঠ ও আত্মচিন্তন")
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(summary)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            notificationManager?.notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    /**
     * Legacy method for day-of-year mapped verse.
     */
    fun showDailyReflectionNotification(context: Context) {
        val dailyVerse = com.example.data.source.DailyVerseProvider.getCuratedDailyVerse().verse
        showRandomVerseNotification(context, dailyVerse)
    }

    /**
     * Schedules a daily recurring WorkManager task to deliver an inspiring
     * random Bhagavad Gita verse in Bengali at the desired time (default: 7:00 AM).
     */
    fun scheduleDailyReflectionWork(
        context: Context,
        hour: Int = 7,
        minute: Int = 0,
        forceReschedule: Boolean = false
    ) {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If target time has already passed today, schedule for tomorrow
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelayMillis = target.timeInMillis - now.timeInMillis

        val periodicWorkRequest = PeriodicWorkRequestBuilder<DailyVerseWorker>(
            24, TimeUnit.HOURS,
            15, TimeUnit.MINUTES // Flex interval
        )
            .setInitialDelay(initialDelayMillis, TimeUnit.MILLISECONDS)
            .addTag(DAILY_WORK_TAG)
            .build()

        val policy = if (forceReschedule) {
            ExistingPeriodicWorkPolicy.UPDATE
        } else {
            ExistingPeriodicWorkPolicy.KEEP
        }

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                DAILY_WORK_TAG,
                policy,
                periodicWorkRequest
            )
    }

    /**
     * Cancels the scheduled daily WorkManager reflection notification.
     */
    fun cancelDailyReflectionWork(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(DAILY_WORK_TAG)
    }

    /**
     * Triggers an immediate one-time WorkManager task to test random verse delivery.
     */
    fun triggerImmediateWork(context: Context) {
        val oneTimeWork = OneTimeWorkRequestBuilder<DailyVerseWorker>()
            .addTag("test_immediate_reflection_work")
            .build()

        WorkManager.getInstance(context).enqueue(oneTimeWork)
    }
}
