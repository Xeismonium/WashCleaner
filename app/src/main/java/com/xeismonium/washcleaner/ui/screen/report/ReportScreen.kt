package com.xeismonium.washcleaner.ui.screen.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xeismonium.washcleaner.ui.components.common.StatCard
import com.xeismonium.washcleaner.ui.components.report.ServicePopularityItem
import com.xeismonium.washcleaner.ui.components.report.StatusStatisticsItem
import com.xeismonium.washcleaner.ui.components.report.TopCustomerItem
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(
    navController: NavController,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ReportContent(
        uiState = uiState,
        onPeriodChange = { period -> viewModel.changePeriod(period) },
        onRefresh = { viewModel.refresh() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportContent(
    uiState: ReportUiState,
    onPeriodChange: (String) -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Laporan",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                ),
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Period Filter
                item {
                    Card {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Periode Laporan",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = uiState.selectedPeriod == "day",
                                    onClick = { onPeriodChange("day") },
                                    label = { Text("Hari") },
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = uiState.selectedPeriod == "week",
                                    onClick = { onPeriodChange("week") },
                                    label = { Text("Minggu") },
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = uiState.selectedPeriod == "month",
                                    onClick = { onPeriodChange("month") },
                                    label = { Text("Bulan") },
                                    modifier = Modifier.weight(1f)
                                )
                                FilterChip(
                                    selected = uiState.selectedPeriod == "year",
                                    onClick = { onPeriodChange("year") },
                                    label = { Text("Tahun") },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Summary Statistics
                item {
                    Text(
                        text = "Ringkasan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            title = "Total Pendapatan",
                            value = formatter.format(uiState.totalRevenue),
                            icon = Icons.Default.AttachMoney,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            title = "Total Transaksi",
                            value = "${uiState.totalTransactions}",
                            icon = Icons.Default.Receipt,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    StatCard(
                        title = "Rata-rata Nilai Transaksi",
                        value = formatter.format(uiState.averageTransactionValue),
                        icon = Icons.Default.TrendingUp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Service Popularity
                if (uiState.servicePopularityData.isNotEmpty()) {
                    item {
                        Text(
                            text = "Layanan Populer",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                uiState.servicePopularityData.take(5).forEach { data ->
                                    ServicePopularityItem(
                                        data = data,
                                        formatter = formatter
                                    )
                                    if (data != uiState.servicePopularityData.take(5).last()) {
                                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Top Customers
                if (uiState.topCustomersData.isNotEmpty()) {
                    item {
                        Text(
                            text = "Pelanggan Teratas",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                uiState.topCustomersData.take(5).forEach { data ->
                                    TopCustomerItem(
                                        data = data,
                                        formatter = formatter
                                    )
                                    if (data != uiState.topCustomersData.take(5).last()) {
                                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Status Statistics
                if (uiState.statusStatisticsData.isNotEmpty()) {
                    item {
                        Text(
                            text = "Status Transaksi",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    item {
                        Card {
                            Column(modifier = Modifier.padding(16.dp)) {
                                uiState.statusStatisticsData.forEach { data ->
                                    StatusStatisticsItem(data = data)
                                    if (data != uiState.statusStatisticsData.last()) {
                                        Divider(modifier = Modifier.padding(vertical = 8.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        uiState.error?.let { error ->
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    TextButton(onClick = onRefresh) {
                        Text("Retry")
                    }
                }
            ) {
                Text(error)
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
                totalRevenue = 5500000.0,
                totalTransactions = 45,
                averageTransactionValue = 122222.0,
                servicePopularityData = listOf(
                    ServicePopularityData("Cuci Setrika", 25, 1500000.0),
                    ServicePopularityData("Cuci Kering", 15, 750000.0),
                    ServicePopularityData("Setrika Saja", 10, 300000.0)
                ),
                topCustomersData = listOf(
                    TopCustomerData("John Doe", 12, 1200000.0),
                    TopCustomerData("Jane Smith", 8, 800000.0),
                    TopCustomerData("Bob Wilson", 5, 500000.0)
                ),
                statusStatisticsData = listOf(
                    StatusStatisticsData("Proses", 10),
                    StatusStatisticsData("Siap", 5),
                    StatusStatisticsData("Selesai", 30)
                ),
                selectedPeriod = "month"
            )
        )
    }
}
