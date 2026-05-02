package com.xeismonium.washcleaner.domain.model

enum class OrderStatus {
    RECEIVED, WASHING, DRYING, IRONING, READY, PICKED_UP
}

enum class PaymentStatus {
    UNPAID, PARTIAL, PAID
}

data class Order(
    val id: String = "",
    val orderCode: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val serviceId: String = "",
    val serviceName: String = "",
    val weight: Double = 0.0,
    val totalPrice: Double = 0.0,
    val status: OrderStatus = OrderStatus.RECEIVED,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val paidAmount: Double = 0.0,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    val pickupDate: Long = 0L
)
