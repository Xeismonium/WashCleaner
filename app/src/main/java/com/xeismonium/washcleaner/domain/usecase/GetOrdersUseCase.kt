package com.xeismonium.washcleaner.domain.usecase

import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetOrdersUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    operator fun invoke(): Flow<Result<List<Order>>> {
        return orderRepository.getOrders().map { result ->
            result.map { orders ->
                orders.sortedByDescending { it.createdAt }
            }
        }
    }
}
