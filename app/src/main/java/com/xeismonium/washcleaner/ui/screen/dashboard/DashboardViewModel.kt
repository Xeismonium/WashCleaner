package com.xeismonium.washcleaner.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity
import com.xeismonium.washcleaner.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class DashboardUiState(
    val todayTransactions: Int = 0,
    val processingCount: Int = 0,
    val readyCount: Int = 0,
    val completedCount: Int = 0,
    val totalRevenue: Double = 0.0,
    val todayRevenue: Double = 0.0,
    val recentTransactions: List<LaundryTransactionEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Get start of today timestamp
            val todayStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis

            val todayEnd = Calendar.getInstance().timeInMillis

            combine(
                transactionRepository.getAll(),
                transactionRepository.getCountByStatus("proses"),
                transactionRepository.getCountByStatus("siap"),
                transactionRepository.getCountByStatus("selesai"),
                transactionRepository.getTotalPriceByStatus("selesai"),
                transactionRepository.getByDateRange(todayStart, todayEnd)
            ) { flows ->
                @Suppress("UNCHECKED_CAST")
                DashboardData(
                    allTransactions = flows[0] as List<LaundryTransactionEntity>,
                    processingCount = flows[1] as Int,
                    readyCount = flows[2] as Int,
                    completedCount = flows[3] as Int,
                    totalRevenue = (flows[4] as? Double) ?: 0.0,
                    todayTransactions = flows[5] as List<LaundryTransactionEntity>
                )
            }.catch { e ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }.collect { data ->
                val todayCount = data.todayTransactions.size
                val todayRev = data.todayTransactions
                    .filter { it.status == "selesai" }
                    .sumOf { it.totalPrice }

                // Get 5 most recent transactions
                val recent = data.allTransactions.take(5)

                _uiState.value = DashboardUiState(
                    todayTransactions = todayCount,
                    processingCount = data.processingCount,
                    readyCount = data.readyCount,
                    completedCount = data.completedCount,
                    totalRevenue = data.totalRevenue,
                    todayRevenue = todayRev,
                    recentTransactions = recent,
                    isLoading = false,
                    error = null
                )
            }
        }
    }

    fun refresh() {
        loadDashboardData()
    }

    private data class DashboardData(
        val allTransactions: List<LaundryTransactionEntity>,
        val processingCount: Int,
        val readyCount: Int,
        val completedCount: Int,
        val totalRevenue: Double,
        val todayTransactions: List<LaundryTransactionEntity>
    )
}
