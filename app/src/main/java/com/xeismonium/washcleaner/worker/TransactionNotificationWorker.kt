package com.xeismonium.washcleaner.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.xeismonium.washcleaner.data.repository.TransactionRepository
import com.xeismonium.washcleaner.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

@HiltWorker
class TransactionNotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val transactionRepository: TransactionRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            // Create channel if needed
            NotificationHelper.createNotificationChannel(applicationContext)

            // Fetch transactions
            // Ideally we should have a specific query for this to avoid loading all data
            // For now, we filter in memory as per current constraints
            val transactions = transactionRepository.getAll().first()
            val currentTime = System.currentTimeMillis()

            // 1. Check for Overdue
            val overdueTransactions = transactions.filter {
                it.status != "selesai" && it.status != "batal" &&
                        it.estimatedDate != null && it.estimatedDate < currentTime
            }

            overdueTransactions.forEach { transaction ->
                // Use transaction ID as notification ID to update existing ones if needed
                NotificationHelper.showDeadlineNotification(
                    context = applicationContext,
                    transactionId = transaction.id,
                    customerName = transaction.customerName ?: "Pelanggan",
                    isOverdue = true,
                    notificationId = transaction.id.toInt()
                )
            }

            // 2. Check for Approaching Deadline (e.g., within 24 hours)
            val oneDayInMillis = TimeUnit.DAYS.toMillis(1)
            val approachingTransactions = transactions.filter {
                it.status != "selesai" && it.status != "batal" &&
                        it.estimatedDate != null &&
                        it.estimatedDate > currentTime &&
                        (it.estimatedDate - currentTime) < oneDayInMillis
            }

            approachingTransactions.forEach { transaction ->
                NotificationHelper.showDeadlineNotification(
                    context = applicationContext,
                    transactionId = transaction.id,
                    customerName = transaction.customerName ?: "Pelanggan",
                    isOverdue = false,
                    notificationId = transaction.id.toInt()
                )
            }

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
