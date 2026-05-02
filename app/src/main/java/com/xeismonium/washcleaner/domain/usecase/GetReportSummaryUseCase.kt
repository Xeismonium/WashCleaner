package com.xeismonium.washcleaner.domain.usecase

import com.xeismonium.washcleaner.domain.model.OrderStatus
import com.xeismonium.washcleaner.domain.model.ReportSummary
import com.xeismonium.washcleaner.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetReportSummaryUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(startDate: Long = 0L): Flow<Result<ReportSummary>> {
        return orderRepository.getOrders().map { result ->
            result.map { orders ->
                val filteredOrders = if (startDate > 0) {
                    orders.filter { it.createdAt >= startDate }
                } else {
                    orders
                }
                ReportSummary(
                    totalOrders = filteredOrders.size,
                    totalRevenue = filteredOrders.sumOf { it.totalPrice },
                    completedOrders = filteredOrders.count { it.status == OrderStatus.PICKED_UP },
                    pendingOrders = filteredOrders.count { it.status != OrderStatus.PICKED_UP }
                )
            }
        }
    }
}
