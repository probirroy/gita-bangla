package com.example.notification

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.source.GitaContentProvider

/**
 * WorkManager CoroutineWorker that runs daily to deliver an inspiring
 * random Bhagavad Gita verse in Bengali to encourage reflection.
 */
class DailyVerseWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val randomVerse = GitaContentProvider.getRandomVerse()
            DailyNotificationManager.showRandomVerseNotification(
                context = applicationContext,
                verse = randomVerse
            )
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            // If failed to post notification, retry
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
