package com.xeismonium.washcleaner.ui.screen.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeismonium.washcleaner.data.local.database.entity.CustomerEntity
import com.xeismonium.washcleaner.data.repository.CustomerRepository
import com.xeismonium.washcleaner.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerWithTransactionCount(
    val customer: CustomerEntity,
    val transactionCount: Int
)

data class CustomerUiState(
    val customersWithCount: List<CustomerWithTransactionCount> = emptyList(),
    val selectedCustomer: CustomerEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

sealed class CustomerEvent {
    object Success : CustomerEvent()
    data class Error(val message: String) : CustomerEvent()
}

@HiltViewModel
class CustomerViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CustomerUiState())
    val uiState: StateFlow<CustomerUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<CustomerEvent?>(null)
    val events: StateFlow<CustomerEvent?> = _events.asStateFlow()

    init {
        loadCustomers()
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            customerRepository.getAll()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { customers ->
                    val filtered = if (_uiState.value.searchQuery.isBlank()) {
                        customers
                    } else {
                        customers.filter {
                            it.name.contains(_uiState.value.searchQuery, ignoreCase = true) ||
                            it.phone.contains(_uiState.value.searchQuery, ignoreCase = true)
                        }
                    }

                    // Load transaction counts for each customer
                    loadCustomersWithTransactionCount(filtered)
                }
        }
    }

    private fun loadCustomersWithTransactionCount(customers: List<CustomerEntity>) {
        viewModelScope.launch {
            try {
                val customersWithCount = customers.map { customer ->
                    var count = 0

                    // Get transaction count for this customer
                    customer.id.let { customerId ->
                        transactionRepository.getByCustomerId(customerId)
                            .collect { transactions ->
                                count = transactions.size
                            }
                    }

                    CustomerWithTransactionCount(
                        customer = customer,
                        transactionCount = count
                    )
                }

                _uiState.value = _uiState.value.copy(
                    customersWithCount = customersWithCount,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadCustomers()
    }

    fun loadCustomerById(id: Long) {
        viewModelScope.launch {
            customerRepository.getById(id)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
                .collect { customer ->
                    _uiState.value = _uiState.value.copy(selectedCustomer = customer)
                }
        }
    }

    fun addCustomer(name: String, phone: String, address: String) {
        viewModelScope.launch {
            try {
                val customer = CustomerEntity(
                    name = name,
                    phone = phone,
                    address = address
                )
                customerRepository.insert(customer)
                _events.value = CustomerEvent.Success
            } catch (e: Exception) {
                _events.value = CustomerEvent.Error(e.message ?: "Unknown error")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateCustomer(id: Long, name: String, phone: String, address: String) {
        viewModelScope.launch {
            try {
                val customer = CustomerEntity(
                    id = id,
                    name = name,
                    phone = phone,
                    address = address
                )
                customerRepository.update(customer)
                _events.value = CustomerEvent.Success
            } catch (e: Exception) {
                _events.value = CustomerEvent.Error(e.message ?: "Unknown error")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteCustomer(id: Long) {
        viewModelScope.launch {
            try {
                customerRepository.deleteById(id)
                _events.value = CustomerEvent.Success
            } catch (e: Exception) {
                _events.value = CustomerEvent.Error(e.message ?: "Unknown error")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearEvent() {
        _events.value = null
    }

    fun refresh() {
        loadCustomers()
    }
}
