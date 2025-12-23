package com.xeismonium.washcleaner.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.xeismonium.washcleaner.MainActivity
import com.xeismonium.washcleaner.R

object NotificationHelper {

    const val CHANNEL_ID_DEADLINE = "deadline_channel"
    const val CHANNEL_NAME_DEADLINE = "Deadline Notifications"
    const val CHANNEL_DESC_DEADLINE = "Notifications for approaching and overdue deadlines"

    const val EXTRA_TRANSACTION_ID = "transaction_id"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID_DEADLINE, CHANNEL_NAME_DEADLINE, importance).apply {
                description = CHANNEL_DESC_DEADLINE
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showDeadlineNotification(
        context: Context,
        transactionId: Long,
        customerName: String,
        isOverdue: Boolean,
        notificationId: Int
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(EXTRA_TRANSACTION_ID, transactionId)
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            transactionId.toInt(), // Unique request code per transaction
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (isOverdue) {
            "Transaksi Terlambat!"
        } else {
            "Batas Waktu Mendekat"
        }

        val text = if (isOverdue) {
            "Transaksi milik $customerName telah melewati batas waktu."
        } else {
            "Transaksi milik $customerName akan segera berakhir."
        }

        val builder = NotificationCompat.Builder(context, CHANNEL_ID_DEADLINE)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Ensure this resource exists or use a fallback
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Permission check is usually handled before worker or suppressed for workers if configured correctly,
        // but explicit check is good practice. For simplicity in worker context we assume permission is granted or user handles it.
        try {
            NotificationManagerCompat.from(context).notify(notificationId, builder.build())
        } catch (e: SecurityException) {
            // Log or handle missing permission
            e.printStackTrace()
        }
    }
}
