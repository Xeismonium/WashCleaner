package com.xeismonium.washcleaner.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.model.PaymentStatus
import com.xeismonium.washcleaner.domain.model.ReportPeriod
import com.xeismonium.washcleaner.domain.model.ReportSummary
import com.xeismonium.washcleaner.domain.usecase.GetOrdersUseCase
import com.xeismonium.washcleaner.domain.usecase.GetReportSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

data class ReportUiState(
    val isLoading: Boolean = false,
    val period: ReportPeriod = ReportPeriod.DAILY,
    val summary: ReportSummary = ReportSummary(),
    val unpaidOrders: List<Order> = emptyList(),
    val chartData: Map<Float, Double> = emptyMap(),
    val chartLabels: Map<Float, String> = emptyMap(),
    val error: String? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReportViewModel @Inject constructor(
    private val getReportSummaryUseCase: GetReportSummaryUseCase,
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    private val _selectedPeriod = MutableStateFlow(ReportPeriod.DAILY)
    val selectedPeriod: StateFlow<ReportPeriod> = _selectedPeriod.asStateFlow()

    val uiState: StateFlow<ReportUiState> = _selectedPeriod.flatMapLatest { period ->
        val startDate = getStartDateForPeriod(period)
        combine(
            getReportSummaryUseCase(startDate),
            getOrdersUseCase()
        ) { summaryResult, ordersResult ->
            val summary = summaryResult.getOrDefault(ReportSummary())
            val allOrders = ordersResult.getOrDefault(emptyList())
            
            val unpaidOrders = allOrders.filter { it.paymentStatus != PaymentStatus.PAID }
                .sortedByDescending { it.createdAt }
            val (chartData, chartLabels) = prepareChartData(allOrders, period)

            ReportUiState(
                isLoading = false,
                period = period,
                summary = summary,
                unpaidOrders = unpaidOrders,
                chartData = chartData,
                chartLabels = chartLabels
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ReportUiState(isLoading = true)
    )

    fun setPeriod(period: ReportPeriod) {
        _selectedPeriod.value = period
    }

    private fun getStartDateForPeriod(period: ReportPeriod): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        when (period) {
            ReportPeriod.DAILY -> {} // Today
            ReportPeriod.WEEKLY -> calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
            ReportPeriod.MONTHLY -> calendar.set(Calendar.DAY_OF_MONTH, 1)
        }
        return calendar.timeInMillis
    }

    private fun prepareChartData(orders: List<Order>, period: ReportPeriod): Pair<Map<Float, Double>, Map<Float, String>> {
        val chartData = mutableMapOf<Float, Double>()
        val chartLabels = mutableMapOf<Float, String>()

        when (period) {
            ReportPeriod.DAILY -> {
                // Last 7 days
                for (i in 6 downTo 0) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.DAY_OF_YEAR, -i)
                    val start = getStartOfDay(cal)
                    val end = getEndOfDay(cal)
                    
                    val revenue = orders.filter { it.createdAt in start..end }.sumOf { it.totalPrice }
                    val x = (6 - i).toFloat()
                    chartData[x] = revenue
                    chartLabels[x] = "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH) + 1}"
                }
            }
            ReportPeriod.WEEKLY -> {
                // Last 4 weeks
                for (i in 3 downTo 0) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.WEEK_OF_YEAR, -i)
                    val start = getStartOfWeek(cal)
                    val end = getEndOfWeek(cal)
                    
                    val revenue = orders.filter { it.createdAt in start..end }.sumOf { it.totalPrice }
                    val x = (3 - i).toFloat()
                    chartData[x] = revenue
                    chartLabels[x] = "W${cal.get(Calendar.WEEK_OF_YEAR)}"
                }
            }
            ReportPeriod.MONTHLY -> {
                // Last 6 months
                for (i in 5 downTo 0) {
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.MONTH, -i)
                    val start = getStartOfMonth(cal)
                    val end = getEndOfMonth(cal)
                    
                    val revenue = orders.filter { it.createdAt in start..end }.sumOf { it.totalPrice }
                    val x = (5 - i).toFloat()
                    chartData[x] = revenue
                    chartLabels[x] = getMonthName(cal.get(Calendar.MONTH))
                }
            }
        }
        return chartData to chartLabels
    }

    private fun getStartOfDay(cal: Calendar): Long {
        val c = cal.clone() as Calendar
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }

    private fun getEndOfDay(cal: Calendar): Long {
        val c = cal.clone() as Calendar
        c.set(Calendar.HOUR_OF_DAY, 23)
        c.set(Calendar.MINUTE, 59)
        c.set(Calendar.SECOND, 59)
        c.set(Calendar.MILLISECOND, 999)
        return c.timeInMillis
    }

    private fun getStartOfWeek(cal: Calendar): Long {
        val c = cal.clone() as Calendar
        c.set(Calendar.DAY_OF_WEEK, c.firstDayOfWeek)
        return getStartOfDay(c)
    }

    private fun getEndOfWeek(cal: Calendar): Long {
        val c = cal.clone() as Calendar
        c.set(Calendar.DAY_OF_WEEK, c.firstDayOfWeek + 6)
        return getEndOfDay(c)
    }

    private fun getStartOfMonth(cal: Calendar): Long {
        val c = cal.clone() as Calendar
        c.set(Calendar.DAY_OF_MONTH, 1)
        return getStartOfDay(c)
    }

    private fun getEndOfMonth(cal: Calendar): Long {
        val c = cal.clone() as Calendar
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH))
        return getEndOfDay(c)
    }

    private fun getMonthName(month: Int): String {
        return when (month) {
            Calendar.JANUARY -> "Jan"
            Calendar.FEBRUARY -> "Feb"
            Calendar.MARCH -> "Mar"
            Calendar.APRIL -> "Apr"
            Calendar.MAY -> "May"
            Calendar.JUNE -> "Jun"
            Calendar.JULY -> "Jul"
            Calendar.AUGUST -> "Aug"
            Calendar.SEPTEMBER -> "Sep"
            Calendar.OCTOBER -> "Oct"
            Calendar.NOVEMBER -> "Nov"
            Calendar.DECEMBER -> "Dec"
            else -> ""
        }
    }
}
