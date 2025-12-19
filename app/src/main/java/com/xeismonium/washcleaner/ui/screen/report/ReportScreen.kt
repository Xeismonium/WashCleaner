package com.xeismonium.washcleaner.ui.screen.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xeismonium.washcleaner.ui.components.report.ReportStatsCard
import com.xeismonium.washcleaner.ui.components.report.RevenueChartCard
import com.xeismonium.washcleaner.ui.components.report.SegmentedButtons
import com.xeismonium.washcleaner.ui.components.report.SummaryCard
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import java.text.NumberFormat
import java.util.Locale

import com.xeismonium.washcleaner.ui.components.common.WashCleanerScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    viewModel: ReportViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    ReportContent(
        uiState = uiState,
        onPeriodChange = { period -> viewModel.changePeriod(period) },
        onRefresh = { viewModel.refresh() },
        onDownloadClick = { /* TODO: Implement download */ },
        onMenuClick = onOpenDrawer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportContent(
    uiState: ReportUiState,
    onPeriodChange: (String) -> Unit = {},
    onRefresh: () -> Unit = {},
    onDownloadClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    WashCleanerScaffold(
        title = "Laporan Pendapatan",
        onMenuClick = onMenuClick,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onRefresh = onRefresh
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SegmentedButtons(
                    selectedPeriod = uiState.selectedPeriod,
                    onPeriodChange = onPeriodChange
                )
            }

            item {
                SummaryCard(
                    totalRevenue = uiState.totalRevenue,
                    trendPercentage = uiState.trendPercentage,
                    selectedPeriod = uiState.selectedPeriod,
                    formatter = formatter
                )
            }

            item {
                RevenueChartCard(chartData = uiState.chartData)
            }

            item {
                ReportStatsCard(
                    title = "Transaksi Selesai",
                    value = "${uiState.totalTransactions}",
                    icon = Icons.Default.ReceiptLong,
                    modifier = Modifier.fillMaxSize()
                )
            }

            item {
                ReportStatsCard(
                    title = "Rata-rata / Transaksi",
                    value = formatter.format(uiState.averageTransactionValue),
                    icon = Icons.Default.Payments,
                    modifier = Modifier.fillMaxSize()
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDownloadClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Download,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Unduh Laporan",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportPreview() {
    WashCleanerTheme {
        ReportContent(
            uiState = ReportUiState(
                totalRevenue = 1500000.0,
                totalTransactions = 15,
                averageTransactionValue = 100000.0,
                trendPercentage = 5.0,
                chartData = listOf(
                    ChartDataPoint("08:00", 750000.0, false),
                    ChartDataPoint("12:00", 300000.0, false),
                    ChartDataPoint("16:00", 900000.0, true),
                    ChartDataPoint("20:00", 550000.0, false)
                ),
                selectedPeriod = "day"
            )
        )
    }
}
