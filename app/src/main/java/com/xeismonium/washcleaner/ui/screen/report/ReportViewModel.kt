package com.xeismonium.washcleaner.ui.screen.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeismonium.washcleaner.data.repository.CustomerRepository
import com.xeismonium.washcleaner.data.repository.ServiceRepository
import com.xeismonium.washcleaner.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class RevenueDataPoint(
    val date: String,
    val revenue: Double
)

data class ServicePopularityData(
    val serviceName: String,
    val count: Int,
    val revenue: Double
)

data class TopCustomerData(
    val customerName: String,
    val transactionCount: Int,
    val totalSpent: Double
)

data class StatusStatisticsData(
    val status: String,
    val count: Int
)

data class ReportUiState(
    val totalRevenue: Double = 0.0,
    val totalTransactions: Int = 0,
    val averageTransactionValue: Double = 0.0,
    val revenueData: List<RevenueDataPoint> = emptyList(),
    val servicePopularityData: List<ServicePopularityData> = emptyList(),
    val topCustomersData: List<TopCustomerData> = emptyList(),
    val statusStatisticsData: List<StatusStatisticsData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPeriod: String = "month" // day, week, month, year
)

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val serviceRepository: ServiceRepository,
    private val customerRepository: CustomerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> = _uiState.asStateFlow()

    init {
        loadReport()
    }

    private fun loadReport() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                transactionRepository.getAll()
                    .catch { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = e.message
                        )
                    }
                    .collect { transactions ->
                        val period = _uiState.value.selectedPeriod
                        val (startDate, endDate) = getDateRange(period)

                        val filteredTransactions = transactions.filter {
                            it.dateIn >= startDate && it.dateIn <= endDate
                        }

                        val completed = filteredTransactions.filter { it.status == "selesai" }
                        val revenue = completed.sumOf { it.totalPrice }
                        val average = if (completed.isNotEmpty()) revenue / completed.size else 0.0

                        // Load all chart data
                        loadRevenueData(filteredTransactions)
                        loadServicePopularityData()
                        loadTopCustomersData()
                        loadStatusStatistics(filteredTransactions)

                        _uiState.value = _uiState.value.copy(
                            totalRevenue = revenue,
                            totalTransactions = completed.size,
                            averageTransactionValue = average,
                            isLoading = false,
                            error = null
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private fun getDateRange(period: String): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endDate = calendar.timeInMillis

        when (period) {
            "day" -> calendar.add(Calendar.DAY_OF_YEAR, -7) // Last 7 days
            "week" -> calendar.add(Calendar.WEEK_OF_YEAR, -4) // Last 4 weeks
            "month" -> calendar.add(Calendar.MONTH, -6) // Last 6 months
            "year" -> calendar.add(Calendar.YEAR, -2) // Last 2 years
        }

        val startDate = calendar.timeInMillis
        return Pair(startDate, endDate)
    }

    private suspend fun loadRevenueData(transactions: List<com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity>) {
        val period = _uiState.value.selectedPeriod
        val dateFormat = when (period) {
            "day" -> SimpleDateFormat("dd MMM", Locale("id", "ID"))
            "week" -> SimpleDateFormat("dd MMM", Locale("id", "ID"))
            "month" -> SimpleDateFormat("MMM yyyy", Locale("id", "ID"))
            "year" -> SimpleDateFormat("yyyy", Locale("id", "ID"))
            else -> SimpleDateFormat("dd MMM", Locale("id", "ID"))
        }

        val completedTransactions = transactions.filter { it.status == "selesai" }
        val groupedByDate = completedTransactions.groupBy { transaction ->
            dateFormat.format(Date(transaction.dateIn))
        }

        val revenueData = groupedByDate.map { (date, transactionsForDate) ->
            RevenueDataPoint(
                date = date,
                revenue = transactionsForDate.sumOf { it.totalPrice }
            )
        }.sortedBy {
            dateFormat.parse(it.date)?.time ?: 0L
        }

        _uiState.value = _uiState.value.copy(revenueData = revenueData)
    }

    private suspend fun loadServicePopularityData() {
        viewModelScope.launch {
            transactionRepository.getAllTransactionsWithServices()
                .collect { transactionsWithServices ->
                    val serviceCountMap = mutableMapOf<Long, Int>()
                    val serviceRevenueMap = mutableMapOf<Long, Double>()

                    transactionsWithServices.forEach { transactionWithServices ->
                        transactionWithServices.transactionServices.forEach { transactionService ->
                            val serviceId = transactionService.serviceId
                            serviceCountMap[serviceId] = (serviceCountMap[serviceId] ?: 0) + 1
                            serviceRevenueMap[serviceId] = (serviceRevenueMap[serviceId] ?: 0.0) + transactionService.subtotalPrice
                        }
                    }

                    serviceRepository.getAll().collect { services ->
                        val popularityData = services.mapNotNull { service ->
                            val count = serviceCountMap[service.id] ?: 0
                            val revenue = serviceRevenueMap[service.id] ?: 0.0
                            if (count > 0) {
                                ServicePopularityData(
                                    serviceName = service.name,
                                    count = count,
                                    revenue = revenue
                                )
                            } else null
                        }.sortedByDescending { it.count }

                        _uiState.value = _uiState.value.copy(servicePopularityData = popularityData)
                    }
                }
        }
    }

    private suspend fun loadTopCustomersData() {
        viewModelScope.launch {
            transactionRepository.getAll().collect { transactions ->
                val customerTransactionMap = mutableMapOf<Long?, MutableList<com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity>>()

                transactions.forEach { transaction ->
                    val customerId = transaction.customerId
                    if (!customerTransactionMap.containsKey(customerId)) {
                        customerTransactionMap[customerId] = mutableListOf()
                    }
                    customerTransactionMap[customerId]?.add(transaction)
                }

                val topCustomersData = customerTransactionMap.map { (customerId, customerTransactions) ->
                    val customerName = customerTransactions.firstOrNull()?.customerName ?: "Tanpa Nama"
                    val count = customerTransactions.size
                    val spent = customerTransactions.filter { it.status == "selesai" }.sumOf { it.totalPrice }

                    TopCustomerData(
                        customerName = customerName,
                        transactionCount = count,
                        totalSpent = spent
                    )
                }.sortedByDescending { it.transactionCount }.take(10)

                _uiState.value = _uiState.value.copy(topCustomersData = topCustomersData)
            }
        }
    }

    private fun loadStatusStatistics(transactions: List<com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity>) {
        val statusCounts = transactions.groupBy { it.status }
            .map { (status, transactionsForStatus) ->
                StatusStatisticsData(
                    status = when (status.lowercase()) {
                        "proses" -> "Proses"
                        "siap" -> "Siap"
                        "selesai" -> "Selesai"
                        else -> "Batal"
                    },
                    count = transactionsForStatus.size
                )
            }

        _uiState.value = _uiState.value.copy(statusStatisticsData = statusCounts)
    }

    fun changePeriod(period: String) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period)
        loadReport()
    }

    fun refresh() {
        loadReport()
    }
}
