package com.xeismonium.washcleaner.domain.repository

import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.model.OrderStatus
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    fun getOrders(): Flow<Result<List<Order>>>
    suspend fun getOrderById(id: String): Result<Order?>
    suspend fun createOrder(order: Order): Result<Unit>
    suspend fun updateOrderStatus(id: String, status: OrderStatus): Result<Unit>
    suspend fun deleteOrder(id: String): Result<Unit>
}
