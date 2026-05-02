package com.xeismonium.washcleaner.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.model.OrderStatus
import com.xeismonium.washcleaner.domain.model.PaymentStatus

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val orderCode: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val serviceId: String,
    val serviceName: String,
    val weight: Double,
    val totalPrice: Double,
    val status: OrderStatus,
    val paymentStatus: PaymentStatus,
    val paidAmount: Double,
    val createdAt: Long,
    val updatedAt: Long,
    val pickupDate: Long
) {
    fun toDomain(): Order = Order(
        id = id,
        orderCode = orderCode,
        customerId = customerId,
        customerName = customerName,
        customerPhone = customerPhone,
        serviceId = serviceId,
        serviceName = serviceName,
        weight = weight,
        totalPrice = totalPrice,
        status = status,
        paymentStatus = paymentStatus,
        paidAmount = paidAmount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        pickupDate = pickupDate
    )

    companion object {
        fun fromDomain(order: Order): OrderEntity = OrderEntity(
            id = order.id,
            orderCode = order.orderCode,
            customerId = order.customerId,
            customerName = order.customerName,
            customerPhone = order.customerPhone,
            serviceId = order.serviceId,
            serviceName = order.serviceName,
            weight = order.weight,
            totalPrice = order.totalPrice,
            status = order.status,
            paymentStatus = order.paymentStatus,
            paidAmount = order.paidAmount,
            createdAt = order.createdAt,
            updatedAt = order.updatedAt,
            pickupDate = order.pickupDate
        )
    }
}
