package com.xeismonium.washcleaner.ui.payment

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.xeismonium.washcleaner.MainDispatcherRule
import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.model.PaymentStatus
import com.xeismonium.washcleaner.domain.usecase.GetOrderByIdUseCase
import com.xeismonium.washcleaner.domain.usecase.ProcessPaymentUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@ExperimentalCoroutinesApi
class PaymentViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: PaymentViewModel
    private val getOrderByIdUseCase = mockk<GetOrderByIdUseCase>()
    private val processPaymentUseCase = mockk<ProcessPaymentUseCase>()
    private val savedStateHandle = SavedStateHandle(mapOf("orderId" to "order_123"))

    private val testOrder = Order(
        id = "order_123",
        orderCode = "WC-20231027-001",
        customerName = "John Doe",
        serviceName = "Wash & Fold",
        weight = 5.0,
        totalPrice = 50000.0,
        paymentStatus = PaymentStatus.UNPAID,
        paidAmount = 0.0
    )

    @Before
    fun setup() {
        coEvery { getOrderByIdUseCase("order_123") } returns Result.success(testOrder)
        viewModel = PaymentViewModel(getOrderByIdUseCase, processPaymentUseCase, savedStateHandle)
    }

    @Test
    fun `loadOrder success updates uiState with order`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(testOrder, state.order)
            assertEquals(false, state.isLoading)
        }
    }

    @Test
    fun `processPayment PAID updates status and generates WA message`() = runTest {
        coEvery { processPaymentUseCase("order_123", PaymentStatus.PAID, 50000.0) } returns Result.success(Unit)

        viewModel.processPayment(PaymentStatus.PAID)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(PaymentStatus.PAID, state.order?.paymentStatus)
            assertEquals(50000.0, state.order?.paidAmount ?: 0.0, 0.0)
            assertTrue(state.paymentProcessed)
            assertNotNull(state.whatsappMessage)
            
            val decodedMessage = URLDecoder.decode(state.whatsappMessage, StandardCharsets.UTF_8.toString())
            assertTrue(decodedMessage.contains("Kode Order: WC-20231027-001"))
            assertTrue(decodedMessage.contains("Total: Rp50.000,00"))
        }
    }

    @Test
    fun `processPayment PARTIAL validates DP amount`() = runTest {
        viewModel.processPayment(PaymentStatus.PARTIAL, 60000.0) // More than totalPrice 50000

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Down payment cannot exceed total price", state.error)
        }
    }

    @Test
    fun `processPayment PARTIAL success updates status and generates WA message with remainder`() = runTest {
        coEvery { processPaymentUseCase("order_123", PaymentStatus.PARTIAL, 20000.0) } returns Result.success(Unit)

        viewModel.processPayment(PaymentStatus.PARTIAL, 20000.0)

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(PaymentStatus.PARTIAL, state.order?.paymentStatus)
            assertEquals(20000.0, state.order?.paidAmount ?: 0.0, 0.0)
            
            val decodedMessage = URLDecoder.decode(state.whatsappMessage, StandardCharsets.UTF_8.toString())
            assertTrue(decodedMessage.contains("Jumlah Dibayar: Rp20.000,00"))
            assertTrue(decodedMessage.contains("Sisa: Rp30.000,00"))
        }
    }
}
