package com.xeismonium.washcleaner.domain.usecase

import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.repository.OrderRepository
import javax.inject.Inject

class GetOrderByIdUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(id: String): Result<Order?> {
        return orderRepository.getOrderById(id)
    }
}
