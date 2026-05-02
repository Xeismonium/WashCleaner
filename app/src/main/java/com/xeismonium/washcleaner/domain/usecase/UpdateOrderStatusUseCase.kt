package com.xeismonium.washcleaner.domain.usecase

import com.xeismonium.washcleaner.domain.model.OrderStatus
import com.xeismonium.washcleaner.domain.repository.OrderRepository
import javax.inject.Inject

class UpdateOrderStatusUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: String, newStatus: OrderStatus): Result<Unit> {
        return if (orderId.isBlank()) {
            Result.failure(Exception("Order ID is required"))
        } else {
            orderRepository.updateOrderStatus(orderId, newStatus)
        }
    }
}
