package com.xeismonium.washcleaner.ui.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeismonium.washcleaner.domain.model.Customer
import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.repository.CustomerRepository
import com.xeismonium.washcleaner.domain.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CustomerUiState {
    object Loading : CustomerUiState()
    data class Success(
        val customers: List<Customer> = emptyList(),
        val selectedCustomer: Customer? = null,
        val orderHistory: List<Order> = emptyList()
    ) : CustomerUiState()
    data class Error(val message: String) : CustomerUiState()
}

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CustomerUiState>(CustomerUiState.Loading)
    val uiState: StateFlow<CustomerUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadCustomers()
    }

    fun loadCustomers() {
        _searchQuery.value = ""
        customerRepository.getCustomers()
            .onStart { _uiState.value = CustomerUiState.Loading }
            .onEach { result ->
                result.onSuccess { customers ->
                    _uiState.value = CustomerUiState.Success(customers = customers)
                }.onFailure { error ->
                    _uiState.value = CustomerUiState.Error(error.message ?: "Failed to load customers")
                }
            }
            .launchIn(viewModelScope)
    }

    fun searchCustomers(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            loadCustomers()
            return
        }

        viewModelScope.launch {
            _uiState.value = CustomerUiState.Loading
            customerRepository.searchCustomers(query)
                .onSuccess { customers ->
                    _uiState.value = CustomerUiState.Success(customers = customers)
                }
                .onFailure { error ->
                    _uiState.value = CustomerUiState.Error(error.message ?: "Search failed")
                }
        }
    }

    fun getCustomerDetail(id: String) {
        viewModelScope.launch {
            _uiState.value = CustomerUiState.Loading
            val customerResult = customerRepository.getCustomerById(id)
            customerResult.onSuccess { customer ->
                if (customer != null) {
                    orderRepository.getOrdersByCustomerId(id)
                        .onEach { orderResult ->
                            orderResult.onSuccess { orders ->
                                _uiState.value = CustomerUiState.Success(
                                    selectedCustomer = customer,
                                    orderHistory = orders
                                )
                            }.onFailure { error ->
                                _uiState.value = CustomerUiState.Error(error.message ?: "Failed to load order history")
                            }
                        }
                        .launchIn(this)
                } else {
                    _uiState.value = CustomerUiState.Error("Customer not found")
                }
            }.onFailure { error ->
                _uiState.value = CustomerUiState.Error(error.message ?: "Failed to load customer")
            }
        }
    }

    fun upsertCustomer(customer: Customer, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            customerRepository.upsertCustomer(customer)
                .onSuccess { onSuccess() }
                .onFailure { error ->
                    _uiState.value = CustomerUiState.Error(error.message ?: "Failed to save customer")
                }
        }
    }

    fun deleteCustomer(id: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            customerRepository.deleteCustomer(id)
                .onSuccess { onSuccess() }
                .onFailure { error ->
                    _uiState.value = CustomerUiState.Error(error.message ?: "Failed to delete customer")
                }
        }
    }
}
