package com.xeismonium.washcleaner.data.repository

import com.google.firebase.firestore.FieldValue
import com.xeismonium.washcleaner.data.local.dao.OrderDao
import com.xeismonium.washcleaner.data.local.entity.OrderEntity
import com.xeismonium.washcleaner.data.remote.FirestoreDataSource
import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.model.OrderStatus
import com.xeismonium.washcleaner.domain.model.PaymentStatus
import com.xeismonium.washcleaner.domain.repository.OrderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val firestoreDataSource: FirestoreDataSource
) : OrderRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        startSync()
    }

    private fun startSync() {
        firestoreDataSource.collectionListener("orders")
            .onEach { result ->
                result.onSuccess { documents ->
                    val entities = documents.map { doc ->
                        OrderEntity(
                            id = doc.id,
                            orderCode = doc.getString("orderCode") ?: "",
                            customerId = doc.getString("customerId") ?: "",
                            customerName = doc.getString("customerName") ?: "",
                            serviceId = doc.getString("serviceId") ?: "",
                            serviceName = doc.getString("serviceName") ?: "",
                            weight = doc.getDouble("weight") ?: 0.0,
                            totalPrice = doc.getDouble("totalPrice") ?: 0.0,
                            status = OrderStatus.valueOf(doc.getString("status") ?: "RECEIVED"),
                            paymentStatus = PaymentStatus.valueOf(doc.getString("paymentStatus") ?: "UNPAID"),
                            paidAmount = doc.getDouble("paidAmount") ?: 0.0,
                            createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L,
                            updatedAt = doc.getTimestamp("updatedAt")?.toDate()?.time ?: 0L,
                            pickupDate = doc.getTimestamp("pickupDate")?.toDate()?.time ?: 0L
                        )
                    }
                    orderDao.insertOrders(entities)
                }
            }
            .launchIn(repositoryScope)
    }

    override fun getOrders(): Flow<Result<List<Order>>> {
        return orderDao.getOrdersFlow()
            .map { entities -> Result.success(entities.map { it.toDomain() }) }
            .catch { emit(Result.failure(it)) }
    }

    override fun getOrdersByCustomerId(customerId: String): Flow<Result<List<Order>>> {
        return orderDao.getOrdersByCustomerIdFlow(customerId)
            .map { entities -> Result.success(entities.map { it.toDomain() }) }
            .catch { emit(Result.failure(it)) }
    }

    override suspend fun getOrderById(id: String): Result<Order?> = runCatching {
        orderDao.getOrderById(id)?.toDomain()
    }

    override suspend fun createOrder(order: Order): Result<Unit> = runCatching {
        val id = order.id.ifBlank { UUID.randomUUID().toString() }
        val now = System.currentTimeMillis()
        val updatedOrder = order.copy(id = id, createdAt = now, updatedAt = now)
        
        orderDao.insertOrder(OrderEntity.fromDomain(updatedOrder))
        
        val data = mapOf(
            "orderCode" to updatedOrder.orderCode,
            "customerId" to updatedOrder.customerId,
            "customerName" to updatedOrder.customerName,
            "serviceId" to updatedOrder.serviceId,
            "serviceName" to updatedOrder.serviceName,
            "weight" to updatedOrder.weight,
            "totalPrice" to updatedOrder.totalPrice,
            "status" to updatedOrder.status.name,
            "paymentStatus" to updatedOrder.paymentStatus.name,
            "paidAmount" to updatedOrder.paidAmount,
            "pickupDate" to updatedOrder.pickupDate
        )
        firestoreDataSource.setDocument("orders", id, data).getOrThrow()
    }

    override suspend fun updateOrderStatus(id: String, status: OrderStatus): Result<Unit> = runCatching {
        val now = System.currentTimeMillis()
        orderDao.updateStatus(id, status, now)
        
        val updates = mutableMapOf<String, Any>(
            "status" to status.name
        )
        
        firestoreDataSource.updateDocument("orders", id, updates).getOrThrow()
    }

    override suspend fun updateOrderPayment(id: String, paymentStatus: PaymentStatus, paidAmount: Double): Result<Unit> = runCatching {
        val now = System.currentTimeMillis()
        orderDao.updatePayment(id, paymentStatus, paidAmount, now)
        
        val updates = mapOf(
            "paymentStatus" to paymentStatus.name,
            "paidAmount" to paidAmount,
            "updatedAt" to FieldValue.serverTimestamp()
        )
        
        firestoreDataSource.updateDocument("orders", id, updates).getOrThrow()
    }

    override suspend fun deleteOrder(id: String): Result<Unit> = runCatching {
        val entity = orderDao.getOrderById(id)
        if (entity != null) {
            orderDao.deleteOrder(entity)
        }
        firestoreDataSource.deleteDocument("orders", id).getOrThrow()
    }

    override suspend fun getNextOrderCounter(): Result<Long> {
        return firestoreDataSource.incrementAndGetCounter("counters", "orders", "current")
    }
}
