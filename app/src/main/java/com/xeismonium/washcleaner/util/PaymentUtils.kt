package com.xeismonium.washcleaner.util

import androidx.compose.ui.graphics.Color
import com.xeismonium.washcleaner.ui.theme.StatusCancelled
import com.xeismonium.washcleaner.ui.theme.StatusCompleted
import com.xeismonium.washcleaner.ui.theme.StatusProcessing

/**
 * Payment status enum representing the three possible payment states
 */
enum class PaymentStatus {
    UNPAID,     // paidAmount == 0
    PARTIAL,    // 0 < paidAmount < totalPrice
    PAID        // paidAmount >= totalPrice
}

/**
 * Sealed class for payment validation results
 */
sealed class PaymentValidationResult {
    object Valid : PaymentValidationResult()
    data class Invalid(val message: String) : PaymentValidationResult()
    data class Overpayment(val amount: Double) : PaymentValidationResult()
}

/**
 * Utility object for payment-related operations and calculations
 */
object PaymentUtils {

    /**
     * Determine payment status from amounts
     */
    fun getPaymentStatus(paidAmount: Double, totalPrice: Double): PaymentStatus {
        return when {
            paidAmount <= 0.0 -> PaymentStatus.UNPAID
            paidAmount >= totalPrice -> PaymentStatus.PAID
            else -> PaymentStatus.PARTIAL
        }
    }

    /**
     * Get localized label for payment status
     */
    fun getPaymentLabel(status: PaymentStatus): String {
        return when (status) {
            PaymentStatus.UNPAID -> "Belum Dibayar"
            PaymentStatus.PARTIAL -> "Dibayar Sebagian"
            PaymentStatus.PAID -> "Lunas"
        }
    }

    /**
     * Get color for payment status
     */
    fun getPaymentColor(status: PaymentStatus): Color {
        return when (status) {
            PaymentStatus.UNPAID -> StatusCancelled      // Red
            PaymentStatus.PARTIAL -> StatusProcessing     // Orange
            PaymentStatus.PAID -> StatusCompleted         // Green
        }
    }

    /**
     * Calculate remaining amount to be paid
     */
    fun getRemainingAmount(paidAmount: Double, totalPrice: Double): Double {
        return (totalPrice - paidAmount).coerceAtLeast(0.0)
    }

    /**
     * Calculate payment progress percentage (0.0 to 1.0)
     */
    fun getPaymentProgress(paidAmount: Double, totalPrice: Double): Float {
        if (totalPrice <= 0.0) return 0f
        return (paidAmount / totalPrice).coerceIn(0.0, 1.0).toFloat()
    }

    /**
     * Validate payment amount before processing
     */
    fun validatePaymentAmount(
        currentPaidAmount: Double,
        additionalPayment: Double,
        totalPrice: Double
    ): PaymentValidationResult {
        if (additionalPayment < 0) {
            return PaymentValidationResult.Invalid("Jumlah pembayaran harus positif")
        }

        if (additionalPayment == 0.0) {
            return PaymentValidationResult.Invalid("Masukkan jumlah pembayaran")
        }

        val newTotal = currentPaidAmount + additionalPayment
        if (newTotal > totalPrice) {
            val overpayment = newTotal - totalPrice
            return PaymentValidationResult.Overpayment(overpayment)
        }

        return PaymentValidationResult.Valid
    }
}
