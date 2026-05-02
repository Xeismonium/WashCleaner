package com.xeismonium.washcleaner.domain.usecase

import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.repository.OrderRepository
import javax.inject.Inject

class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(order: Order): Result<Unit> {
        return when {
            order.customerId.isBlank() -> Result.failure(Exception("Customer is required"))
            order.serviceId.isBlank() -> Result.failure(Exception("Service is required"))
            order.weight <= 0 -> Result.failure(Exception("Weight must be greater than 0"))
            else -> orderRepository.createOrder(order)
        }
    }
}
