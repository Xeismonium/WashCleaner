package com.xeismonium.washcleaner.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.xeismonium.washcleaner.R
import com.xeismonium.washcleaner.WashCleanerApp
import com.xeismonium.washcleaner.domain.model.OrderStatus
import com.xeismonium.washcleaner.domain.repository.OrderRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class PickupReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val orderRepository: OrderRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val ordersResult = orderRepository.getOrders().first()
        
        if (ordersResult.isSuccess) {
            val orders = ordersResult.getOrNull() ?: emptyList()
            val threeDaysAgo = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000L)
            
            val overdueOrders = orders.filter { 
                it.status == OrderStatus.READY && it.updatedAt < threeDaysAgo 
            }
            
            overdueOrders.forEach { order ->
                showNotification(order.orderCode, order.customerName)
            }
        }
        
        return Result.success()
    }

    private fun showNotification(orderCode: String, customerName: String) {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Cannot show notification without permission
            return
        }

        val notification = NotificationCompat.Builder(applicationContext, WashCleanerApp.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Order Ready for Pickup")
            .setContentText("Order $orderCode for $customerName is still waiting.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(orderCode.hashCode(), notification)
        }
    }
}
