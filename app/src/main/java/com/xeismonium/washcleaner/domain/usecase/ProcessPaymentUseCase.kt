package com.xeismonium.washcleaner.domain.usecase

import com.xeismonium.washcleaner.domain.model.PaymentStatus
import com.xeismonium.washcleaner.domain.repository.OrderRepository
import javax.inject.Inject

class ProcessPaymentUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(
        orderId: String,
        paymentStatus: PaymentStatus,
        paidAmount: Double
    ): Result<Unit> {
        return if (orderId.isBlank()) {
            Result.failure(Exception("Order ID is required"))
        } else {
            orderRepository.updateOrderPayment(orderId, paymentStatus, paidAmount)
        }
    }
}
