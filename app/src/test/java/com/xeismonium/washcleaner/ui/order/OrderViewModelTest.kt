package com.xeismonium.washcleaner.ui.order

import app.cash.turbine.test
import com.xeismonium.washcleaner.MainDispatcherRule
import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.model.OrderStatus
import com.xeismonium.washcleaner.domain.model.OrderStatus.*
import com.xeismonium.washcleaner.domain.repository.CustomerRepository
import com.xeismonium.washcleaner.domain.repository.OrderRepository
import com.xeismonium.washcleaner.domain.repository.ServiceRepository
import com.xeismonium.washcleaner.domain.usecase.CreateOrderUseCase
import com.xeismonium.washcleaner.domain.usecase.GenerateOrderCodeUseCase
import com.xeismonium.washcleaner.domain.usecase.GetOrdersUseCase
import com.xeismonium.washcleaner.domain.usecase.UpdateOrderStatusUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class OrderViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: OrderViewModel
    private val getOrdersUseCase = mockk<GetOrdersUseCase>()
    private val createOrderUseCase = mockk<CreateOrderUseCase>()
    private val updateOrderStatusUseCase = mockk<UpdateOrderStatusUseCase>()
    private val generateOrderCodeUseCase = mockk<GenerateOrderCodeUseCase>()
    private val orderRepository = mockk<OrderRepository>()
    private val customerRepository = mockk<CustomerRepository>()
    private val serviceRepository = mockk<ServiceRepository>()

    private val testOrders = listOf(
        Order(id = "1", orderCode = "WC-01", status = RECEIVED),
        Order(id = "2", orderCode = "WC-02", status = PICKED_UP)
    )

    @Before
    fun setup() {
        coEvery { getOrdersUseCase() } returns flowOf(Result.success(testOrders))
        coEvery { customerRepository.getCustomers() } returns flowOf(Result.success(emptyList()))
        coEvery { serviceRepository.getServices() } returns flowOf(Result.success(emptyList()))
        
        viewModel = OrderViewModel(
            getOrdersUseCase,
            createOrderUseCase,
            updateOrderStatusUseCase,
            generateOrderCodeUseCase,
            orderRepository,
            customerRepository,
            serviceRepository
        )
    }

    @Test
    fun `loadOrders initially emits Loading then Success with all orders`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is OrderUiState.Success)
            assertEquals(testOrders, (state as OrderUiState.Success).orders)
            assertEquals(null, state.filter)
        }
    }

    @Test
    fun `setFilter updates uiState with filtered orders`() = runTest {
        viewModel.uiState.test {
            // Initial state
            var state = awaitItem()
            assertTrue(state is OrderUiState.Success)
            
            // Set filter to PICKED_UP
            viewModel.setFilter(PICKED_UP)
            
            state = awaitItem()
            assertTrue(state is OrderUiState.Success)
            val successState = state as OrderUiState.Success
            assertEquals(1, successState.orders.size)
            assertEquals(PICKED_UP, successState.orders[0].status)
            assertEquals(PICKED_UP, successState.filter)
        }
    }

    @Test
    fun `updateOrderStatus calls use case and reloads order`() = runTest {
        val orderId = "1"
        val newStatus = WASHING
        val updatedOrder = testOrders[0].copy(status = newStatus)

        coEvery { updateOrderStatusUseCase(orderId, newStatus) } returns Result.success(Unit)
        coEvery { orderRepository.getOrderById(orderId) } returns Result.success(updatedOrder)

        viewModel.updateOrderStatus(orderId, newStatus)

        viewModel.uiState.test {
            val state = awaitItem()
            assertTrue(state is OrderUiState.Success)
            assertEquals(updatedOrder, (state as OrderUiState.Success).selectedOrder)
        }
    }
}
