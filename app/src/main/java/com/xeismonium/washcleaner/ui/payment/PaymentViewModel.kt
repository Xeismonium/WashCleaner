package com.xeismonium.washcleaner.ui.payment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.model.PaymentStatus
import com.xeismonium.washcleaner.domain.usecase.GetOrderByIdUseCase
import com.xeismonium.washcleaner.domain.usecase.ProcessPaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

data class PaymentUiState(
    val isLoading: Boolean = false,
    val order: Order? = null,
    val error: String? = null,
    val paymentProcessed: Boolean = false,
    val whatsappMessage: String = ""
)

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val processPaymentUseCase: ProcessPaymentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val orderId: String = checkNotNull(savedStateHandle["orderId"])

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    init {
        loadOrder()
    }

    private fun loadOrder() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getOrderByIdUseCase(orderId).fold(
                onSuccess = { order ->
                    _uiState.update { it.copy(isLoading = false, order = order) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    fun processPayment(status: PaymentStatus, dpAmount: Double = 0.0) {
        val currentOrder = _uiState.value.order ?: return
        
        // Mitigation T-04-05: Validate that DP amount does not exceed total price
        if (status == PaymentStatus.PARTIAL && dpAmount > currentOrder.totalPrice) {
            _uiState.update { it.copy(error = "Down payment cannot exceed total price") }
            return
        }

        val paidAmount = when (status) {
            PaymentStatus.PAID -> currentOrder.totalPrice
            PaymentStatus.PARTIAL -> dpAmount
            PaymentStatus.UNPAID -> 0.0
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            processPaymentUseCase(orderId, status, paidAmount).fold(
                onSuccess = {
                    val updatedOrder = currentOrder.copy(paymentStatus = status, paidAmount = paidAmount)
                    val message = generateWhatsAppMessage(updatedOrder)
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            order = updatedOrder,
                            paymentProcessed = true,
                            whatsappMessage = message
                        ) 
                    }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, error = error.message) }
                }
            )
        }
    }

    private fun generateWhatsAppMessage(order: Order): String {
        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        
        val sb = StringBuilder()
        sb.append("*NOTA WASHCLEANER*\n")
        sb.append("----------------------------\n")
        sb.append("Kode Order: ${order.orderCode}\n")
        sb.append("Pelanggan: ${order.customerName}\n")
        sb.append("Layanan: ${order.serviceName}\n")
        sb.append("Berat: ${order.weight} kg\n")
        sb.append("Total: ${currencyFormat.format(order.totalPrice)}\n")
        sb.append("Status Pembayaran: ${order.paymentStatus.name}\n")
        sb.append("Jumlah Dibayar: ${currencyFormat.format(order.paidAmount)}\n")
        
        val sisa = order.totalPrice - order.paidAmount
        if (sisa > 0) {
            sb.append("Sisa: ${currencyFormat.format(sisa)}\n")
        }
        
        sb.append("----------------------------\n")
        sb.append("Terima kasih telah menggunakan jasa kami!")
        
        return try {
            URLEncoder.encode(sb.toString(), StandardCharsets.UTF_8.toString()).replace("+", "%20")
        } catch (e: Exception) {
            ""
        }
    }
    
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
