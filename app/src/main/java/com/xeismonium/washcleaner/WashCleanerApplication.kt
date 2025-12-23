package com.xeismonium.washcleaner

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.xeismonium.washcleaner.worker.TransactionNotificationWorker
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class WashCleanerApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        setupWorker()
    }

    private fun setupWorker() {
        val workRequest = PeriodicWorkRequestBuilder<TransactionNotificationWorker>(
            15, TimeUnit.MINUTES // Minimum interval
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "TransactionNotificationWork",
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing work to avoid spamming on restart
            workRequest
        )
    }
}