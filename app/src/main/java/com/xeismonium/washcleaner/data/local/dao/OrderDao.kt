package com.xeismonium.washcleaner.data.local.dao

import androidx.room.*
import com.xeismonium.washcleaner.data.local.entity.OrderEntity
import com.xeismonium.washcleaner.domain.model.OrderStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getOrdersFlow(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: OrderStatus): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: String): OrderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET status = :status, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: String, status: OrderStatus, updatedAt: Long)

    @Delete
    suspend fun deleteOrder(order: OrderEntity)
}
