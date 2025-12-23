package com.xeismonium.washcleaner.ui.screen.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeismonium.washcleaner.data.local.database.entity.CustomerEntity
import com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity
import com.xeismonium.washcleaner.data.local.database.entity.ServiceEntity
import com.xeismonium.washcleaner.data.local.database.entity.TransactionWithServices
import com.xeismonium.washcleaner.data.repository.CustomerRepository
import com.xeismonium.washcleaner.data.repository.ServiceRepository
import com.xeismonium.washcleaner.data.repository.TransactionRepository
import com.xeismonium.washcleaner.data.repository.TransactionWithServicesData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionUiState(
    val transactions: List<LaundryTransactionEntity> = emptyList(),
    val selectedTransaction: TransactionWithServices? = null,
    val customers: List<CustomerEntity> = emptyList(),
    val services: List<ServiceEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val filterStatus: String? = null,
    val searchQuery: String = ""
)

sealed class TransactionEvent {
    object Success : TransactionEvent()
    data class Error(val message: String) : TransactionEvent()
}

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val customerRepository: CustomerRepository,
    private val serviceRepository: ServiceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<TransactionEvent?>(null)
    val events: StateFlow<TransactionEvent?> = _events.asStateFlow()

    init {
        loadTransactions()
        loadCustomersAndServices()
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            transactionRepository.getAll()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { transactions ->
                    _uiState.value = _uiState.value.copy(
                        transactions = applyFilters(transactions),
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    private fun loadCustomersAndServices() {
        viewModelScope.launch {
            combine(
                customerRepository.getAll(),
                serviceRepository.getAllActive()
            ) { customers, services ->
                Pair(customers, services)
            }.collect { (customers, services) ->
                _uiState.value = _uiState.value.copy(
                    customers = customers,
                    services = services
                )
            }
        }
    }

    private fun applyFilters(transactions: List<LaundryTransactionEntity>): List<LaundryTransactionEntity> {
        var filtered = transactions

        // Apply status filter
        _uiState.value.filterStatus?.let { status ->
            filtered = filtered.filter { it.status == status }
        }

        // Apply search filter
        if (_uiState.value.searchQuery.isNotBlank()) {
            filtered = filtered.filter { transaction ->
                transaction.customerName?.contains(_uiState.value.searchQuery, ignoreCase = true) == true
            }
        }

        return filtered
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        loadTransactions()
    }

    fun filterByStatus(status: String?) {
        _uiState.value = _uiState.value.copy(filterStatus = status)
        loadTransactions()
    }

    fun loadTransactionById(id: Long) {
        viewModelScope.launch {
            transactionRepository.getTransactionWithServices(id)
                .catch { e ->
                    _uiState.value = _uiState.value.copy(error = e.message)
                }
                .collect { transaction ->
                    _uiState.value = _uiState.value.copy(
                        selectedTransaction = transaction
                    )
                }
        }
    }

    fun createTransactionWithServices(
        customerId: Long?,
        customerName: String?,
        services: List<TransactionWithServicesData.ServiceItem>,
        status: String = "proses",
        dateIn: Long = System.currentTimeMillis(),
        estimatedDate: Long? = null,
        paidAmount: Double = 0.0
    ) {
        viewModelScope.launch {
            try {
                val totalPrice = services.sumOf { it.subtotalPrice }

                val transaction = LaundryTransactionEntity(
                    customerId = customerId,
                    customerName = customerName,
                    totalPrice = totalPrice,
                    dateIn = dateIn,
                    dateOut = null,
                    estimatedDate = estimatedDate,
                    status = status,
                    paidAmount = paidAmount
                )

                val data = TransactionWithServicesData(
                    transaction = transaction,
                    services = services
                )

                transactionRepository.insertTransactionWithServices(data)
                _events.value = TransactionEvent.Success
            } catch (e: Exception) {
                _events.value = TransactionEvent.Error(e.message ?: "Unknown error")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateTransactionWithServices(
        transactionId: Long,
        customerId: Long?,
        customerName: String?,
        services: List<TransactionWithServicesData.ServiceItem>,
        status: String,
        dateOut: Long?,
        dateIn: Long,
        estimatedDate: Long?,
        paidAmount: Double
    ) {
        viewModelScope.launch {
            try {
                val totalPrice = services.sumOf { it.subtotalPrice }

                val transaction = LaundryTransactionEntity(
                    id = transactionId,
                    customerId = customerId,
                    customerName = customerName,
                    totalPrice = totalPrice,
                    dateIn = dateIn,
                    dateOut = dateOut,
                    estimatedDate = estimatedDate,
                    status = status,
                    paidAmount = paidAmount
                )

                val data = TransactionWithServicesData(
                    transaction = transaction,
                    services = services
                )

                transactionRepository.updateTransactionWithServices(transactionId, data)
                _events.value = TransactionEvent.Success
            } catch (e: Exception) {
                _events.value = TransactionEvent.Error(e.message ?: "Unknown error")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateTransactionStatus(transactionId: Long, newStatus: String) {
        viewModelScope.launch {
            try {
                val dateOut = if (newStatus == "selesai") System.currentTimeMillis() else null
                transactionRepository.updateStatus(transactionId, newStatus)
                _events.value = TransactionEvent.Success
            } catch (e: Exception) {
                _events.value = TransactionEvent.Error(e.message ?: "Unknown error")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun addPayment(transactionId: Long, amount: Double) {
        viewModelScope.launch {
            try {
                transactionRepository.addPayment(transactionId, amount)
                // Don't emit success event here to prevent navigation
                // Just let the UI update via Flow
            } catch (e: Exception) {
                _events.value = TransactionEvent.Error(e.message ?: "Gagal menambah pembayaran")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun deleteTransaction(transactionId: Long) {
        viewModelScope.launch {
            try {
                transactionRepository.deleteById(transactionId)
                _events.value = TransactionEvent.Success
            } catch (e: Exception) {
                _events.value = TransactionEvent.Error(e.message ?: "Unknown error")
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun clearEvent() {
        _events.value = null
    }

    fun refresh() {
        loadTransactions()
    }
}
