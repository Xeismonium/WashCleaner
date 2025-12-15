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

data class ChartDataPoint(
    val label: String,
    val value: Double,
    val isHighlighted: Boolean = false
)

data class ReportUiState(
    val totalRevenue: Double = 0.0,
    val totalTransactions: Int = 0,
    val averageTransactionValue: Double = 0.0,
    val trendPercentage: Double = 0.0,
    val revenueData: List<RevenueDataPoint> = emptyList(),
    val chartData: List<ChartDataPoint> = emptyList(),
    val servicePopularityData: List<ServicePopularityData> = emptyList(),
    val topCustomersData: List<TopCustomerData> = emptyList(),
    val statusStatisticsData: List<StatusStatisticsData> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedPeriod: String = "day" // day, week, month
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

                        // Calculate trend percentage (compare to previous period)
                        val (prevStartDate, prevEndDate) = getPreviousPeriodDateRange(period)
                        val previousTransactions = transactions.filter {
                            it.dateIn >= prevStartDate && it.dateIn <= prevEndDate && it.status == "selesai"
                        }
                        val previousRevenue = previousTransactions.sumOf { it.totalPrice }
                        val trendPercentage = if (previousRevenue > 0) {
                            ((revenue - previousRevenue) / previousRevenue) * 100
                        } else if (revenue > 0) {
                            100.0
                        } else {
                            0.0
                        }

                        // Generate chart data
                        val chartData = generateChartData(filteredTransactions, period)

                        // Load all chart data
                        loadRevenueData(filteredTransactions)
                        loadServicePopularityData()
                        loadTopCustomersData()
                        loadStatusStatistics(filteredTransactions)

                        _uiState.value = _uiState.value.copy(
                            totalRevenue = revenue,
                            totalTransactions = completed.size,
                            averageTransactionValue = average,
                            trendPercentage = trendPercentage,
                            chartData = chartData,
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
            "day" -> {
                // Start of today
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            "week" -> {
                // Start of this week
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
            "month" -> {
                // Start of this month
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
            }
        }

        val startDate = calendar.timeInMillis
        return Pair(startDate, endDate)
    }

    private fun getPreviousPeriodDateRange(period: String): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        when (period) {
            "day" -> {
                // Yesterday
                calendar.add(Calendar.DAY_OF_YEAR, -1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.timeInMillis
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                return Pair(startDate, calendar.timeInMillis)
            }
            "week" -> {
                // Last week
                calendar.add(Calendar.WEEK_OF_YEAR, -1)
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_YEAR, 6)
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                return Pair(startDate, calendar.timeInMillis)
            }
            "month" -> {
                // Last month
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val startDate = calendar.timeInMillis
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                calendar.set(Calendar.HOUR_OF_DAY, 23)
                calendar.set(Calendar.MINUTE, 59)
                calendar.set(Calendar.SECOND, 59)
                return Pair(startDate, calendar.timeInMillis)
            }
            else -> return Pair(0L, 0L)
        }
    }

    private fun generateChartData(
        transactions: List<com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity>,
        period: String
    ): List<ChartDataPoint> {
        val completedTransactions = transactions.filter { it.status == "selesai" }

        return when (period) {
            "day" -> {
                // Group by hour intervals (e.g., 08:00, 12:00, 16:00, 20:00)
                val hourIntervals = listOf(8, 12, 16, 20)
                val hourFormat = SimpleDateFormat("HH", Locale("id", "ID"))

                val grouped = completedTransactions.groupBy { transaction ->
                    val hour = hourFormat.format(Date(transaction.dateIn)).toIntOrNull() ?: 0
                    hourIntervals.minByOrNull { kotlin.math.abs(it - hour) } ?: 8
                }

                val maxRevenue = grouped.values.maxOfOrNull { txList -> txList.sumOf { it.totalPrice } } ?: 1.0

                hourIntervals.map { hour ->
                    val revenue = grouped[hour]?.sumOf { it.totalPrice } ?: 0.0
                    ChartDataPoint(
                        label = String.format("%02d:00", hour),
                        value = revenue,
                        isHighlighted = grouped[hour]?.sumOf { it.totalPrice } == maxRevenue && maxRevenue > 0
                    )
                }
            }
            "week" -> {
                // Group by day of week
                val dayFormat = SimpleDateFormat("EEE", Locale("id", "ID"))
                val calendar = Calendar.getInstance()

                val grouped = completedTransactions.groupBy { transaction ->
                    calendar.timeInMillis = transaction.dateIn
                    calendar.get(Calendar.DAY_OF_WEEK)
                }

                val maxRevenue = grouped.values.maxOfOrNull { txList -> txList.sumOf { it.totalPrice } } ?: 1.0
                val days = listOf(
                    Calendar.MONDAY to "Sen",
                    Calendar.TUESDAY to "Sel",
                    Calendar.WEDNESDAY to "Rab",
                    Calendar.THURSDAY to "Kam",
                    Calendar.FRIDAY to "Jum",
                    Calendar.SATURDAY to "Sab",
                    Calendar.SUNDAY to "Min"
                )

                days.map { (dayOfWeek, label) ->
                    val revenue = grouped[dayOfWeek]?.sumOf { it.totalPrice } ?: 0.0
                    ChartDataPoint(
                        label = label,
                        value = revenue,
                        isHighlighted = revenue == maxRevenue && maxRevenue > 0
                    )
                }
            }
            "month" -> {
                // Group by week of month
                val calendar = Calendar.getInstance()

                val grouped = completedTransactions.groupBy { transaction ->
                    calendar.timeInMillis = transaction.dateIn
                    calendar.get(Calendar.WEEK_OF_MONTH)
                }

                val maxRevenue = grouped.values.maxOfOrNull { txList -> txList.sumOf { it.totalPrice } } ?: 1.0

                (1..4).map { week ->
                    val revenue = grouped[week]?.sumOf { it.totalPrice } ?: 0.0
                    ChartDataPoint(
                        label = "Mgg $week",
                        value = revenue,
                        isHighlighted = revenue == maxRevenue && maxRevenue > 0
                    )
                }
            }
            else -> emptyList()
        }
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
