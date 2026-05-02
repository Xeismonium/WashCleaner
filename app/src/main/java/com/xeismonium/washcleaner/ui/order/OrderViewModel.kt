package com.xeismonium.washcleaner.ui.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeismonium.washcleaner.domain.model.Customer
import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.model.OrderStatus
import com.xeismonium.washcleaner.domain.model.Service
import com.xeismonium.washcleaner.domain.repository.CustomerRepository
import com.xeismonium.washcleaner.domain.repository.OrderRepository
import com.xeismonium.washcleaner.domain.repository.ServiceRepository
import com.xeismonium.washcleaner.domain.usecase.CreateOrderUseCase
import com.xeismonium.washcleaner.domain.usecase.GenerateOrderCodeUseCase
import com.xeismonium.washcleaner.domain.usecase.GetOrdersUseCase
import com.xeismonium.washcleaner.domain.usecase.UpdateOrderStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface OrderUiState {
    object Loading : OrderUiState
    data class Success(
        val orders: List<Order> = emptyList(),
        val selectedOrder: Order? = null,
        val filter: OrderStatus? = null
    ) : OrderUiState
    data class Error(val message: String) : OrderUiState
}

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val getOrdersUseCase: GetOrdersUseCase,
    private val createOrderUseCase: CreateOrderUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase,
    private val generateOrderCodeUseCase: GenerateOrderCodeUseCase,
    private val orderRepository: OrderRepository,
    private val customerRepository: CustomerRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Loading)
    val uiState: StateFlow<OrderUiState> = _uiState.asStateFlow()

    private val _customers = MutableStateFlow<List<Customer>>(emptyList())
    val customers = _customers.asStateFlow()

    private val _services = MutableStateFlow<List<Service>>(emptyList())
    val services = _services.asStateFlow()

    private val _filter = MutableStateFlow<OrderStatus?>(null)

    init {
        observeOrders()
        loadCustomers()
        loadServices()
    }

    private fun observeOrders() {
        viewModelScope.launch {
            combine(getOrdersUseCase(), _filter) { result, filter ->
                result.map { orders ->
                    if (filter != null) orders.filter { it.status == filter } else orders
                } to filter
            }.collect { (result, filter) ->
                result.onSuccess { orders ->
                    val currentSelectedOrder = (_uiState.value as? OrderUiState.Success)?.selectedOrder
                    _uiState.value = OrderUiState.Success(
                        orders = orders,
                        selectedOrder = currentSelectedOrder,
                        filter = filter
                    )
                }.onFailure {
                    _uiState.value = OrderUiState.Error(it.message ?: "Unknown error")
                }
            }
        }
    }

    fun setFilter(filter: OrderStatus?) {
        _filter.value = filter
    }

    fun getOrderById(id: String) {
        viewModelScope.launch {
            orderRepository.getOrderById(id).onSuccess { order ->
                val currentState = _uiState.value
                if (currentState is OrderUiState.Success) {
                    _uiState.value = currentState.copy(selectedOrder = order)
                }
            }
        }
    }

    fun createOrder(order: Order, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            generateOrderCodeUseCase().onSuccess { code ->
                val orderWithCode = order.copy(
                    orderCode = code,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                createOrderUseCase(orderWithCode).onSuccess {
                    onComplete(true)
                }.onFailure {
                    onComplete(false)
                }
            }.onFailure {
                onComplete(false)
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: OrderStatus) {
        viewModelScope.launch {
            updateOrderStatusUseCase(orderId, newStatus).onSuccess {
                getOrderById(orderId)
            }
        }
    }

    fun deleteOrder(orderId: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            orderRepository.deleteOrder(orderId).onSuccess {
                onComplete(true)
            }.onFailure {
                onComplete(false)
            }
        }
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            customerRepository.getCustomers().collect { result ->
                result.onSuccess { _customers.value = it }
            }
        }
    }

    private fun loadServices() {
        viewModelScope.launch {
            serviceRepository.getServices().collect { result ->
                result.onSuccess { _services.value = it }
            }
        }
    }
}
