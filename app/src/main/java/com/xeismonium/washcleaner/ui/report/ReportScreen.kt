package com.xeismonium.washcleaner.ui.report

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xeismonium.washcleaner.core.utils.CurrencyFormatter
import com.xeismonium.washcleaner.domain.model.ReportPeriod
import com.xeismonium.washcleaner.ui.order.components.OrderCard
import com.xeismonium.washcleaner.ui.report.components.KpiCard
import com.xeismonium.washcleaner.ui.report.components.ReportBarChart
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    onOrderClick: (String) -> Unit,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    ReportContent(
        uiState = uiState,
        onPeriodSelected = { viewModel.setPeriod(it) },
        onOrderClick = onOrderClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportContent(
    uiState: ReportUiState,
    onPeriodSelected: (ReportPeriod) -> Unit,
    onOrderClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Laporan Keuangan") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TabRow(selectedTabIndex = uiState.period.ordinal) {
                    ReportPeriod.entries.forEach { period ->
                        Tab(
                            selected = uiState.period == period,
                            onClick = { onPeriodSelected(period) },
                            text = { 
                                Text(
                                    when(period) {
                                        ReportPeriod.DAILY -> "Harian"
                                        ReportPeriod.WEEKLY -> "Mingguan"
                                        ReportPeriod.MONTHLY -> "Bulanan"
                                    }
                                ) 
                            }
                        )
                    }
                }
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KpiCard(
                            label = "Pendapatan",
                            value = CurrencyFormatter.formatRupiah(uiState.summary.totalRevenue.toLong()),
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            label = "Total Pesanan",
                            value = uiState.summary.totalOrders.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        KpiCard(
                            label = "Selesai",
                            value = uiState.summary.completedOrders.toString(),
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            label = "Pending",
                            value = uiState.summary.pendingOrders.toString(),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Tren Pendapatan",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                ReportBarChart(
                    data = uiState.chartData,
                    labels = uiState.chartLabels
                )
            }

            item {
                Text(
                    text = "Belum Bayar",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (uiState.unpaidOrders.isEmpty()) {
                item {
                    Text(
                        text = "Tidak ada pesanan belum bayar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(uiState.unpaidOrders, key = { it.id }) { order ->
                    OrderCard(
                        order = order,
                        onClick = { onOrderClick(order.id) }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportPreview() {
    WashCleanerTheme {
        ReportContent(
            uiState = ReportUiState(),
            onPeriodSelected = {},
            onOrderClick = {}
        )
    }
}
