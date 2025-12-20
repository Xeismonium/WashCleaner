package com.xeismonium.washcleaner.ui.screen.dashboard

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.xeismonium.washcleaner.ui.components.dashboard.DashboardQuickAction
import com.xeismonium.washcleaner.ui.components.dashboard.DashboardStatCard
import com.xeismonium.washcleaner.ui.theme.StatusCompleted
import com.xeismonium.washcleaner.ui.theme.StatusProcessing
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import com.xeismonium.washcleaner.util.CurrencyUtils

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    DashboardContent(
        uiState = uiState,
        onOpenDrawer = onOpenDrawer,
        onNewTransaction = { navController.navigate("transaction_form/0") },
        onViewAllTransactions = { navController.navigate("transaction_list") },
        onNavigateToServices = { navController.navigate("service_list") },
        onNavigateToCustomers = { navController.navigate("customer_list") }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onOpenDrawer: () -> Unit = {},
    onNewTransaction: () -> Unit = {},
    onViewAllTransactions: () -> Unit = {},
    onNavigateToServices: () -> Unit = {},
    onNavigateToCustomers: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Dasbor Utama",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNewTransaction,
                shape = MaterialTheme.shapes.large,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Transaksi Baru"
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Alert Section (Deadline)
            if (uiState.overdueCount > 0) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Column {
                                Text(
                                    text = "Perhatian: Jatuh Tempo",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                    text = "${uiState.overdueCount} pesanan harus selesai hari ini atau terlambat.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }
            }

            // Financial Status (Unpaid)
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Column {
                            Text(
                                text = "Status Pembayaran",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            if (uiState.unpaidCount > 0) {
                                Text(
                                    text = "${uiState.unpaidCount} Belum Lunas (${CurrencyUtils.formatRupiah(uiState.unpaidTotal)})",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            } else {
                                Text(
                                    text = "Semua pesanan lunas!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Stats Cards
            item {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DashboardStatCard(
                        title = "Transaksi Hari Ini",
                        value = uiState.todayTransactions.toString(),
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        iconTint = MaterialTheme.colorScheme.primary
                    )
                    DashboardStatCard(
                        title = "Sedang Diproses",
                        value = uiState.processingCount.toString(),
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        iconTint = StatusProcessing
                    )
                    DashboardStatCard(
                        title = "Siap Diambil",
                        value = uiState.readyCount.toString(),
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        iconTint = StatusCompleted
                    )
                }
            }

            // Spacer
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Quick Actions
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Aksi Cepat",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Grid 2x2 untuk Quick Actions
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DashboardQuickAction(
                                title = "Tambah",
                                icon = Icons.Default.Add,
                                onClick = onNewTransaction,
                                modifier = Modifier.weight(1f)
                            )
                            DashboardQuickAction(
                                title = "Daftar",
                                icon = Icons.AutoMirrored.Filled.ListAlt,
                                onClick = onViewAllTransactions,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DashboardQuickAction(
                                title = "Layanan",
                                icon = Icons.Default.LocalLaundryService,
                                onClick = onNavigateToServices,
                                modifier = Modifier.weight(1f)
                            )
                            DashboardQuickAction(
                                title = "Pelanggan",
                                icon = Icons.Default.Groups,
                                onClick = onNavigateToCustomers,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    WashCleanerTheme {
        DashboardContent(
            uiState = DashboardUiState(
                todayTransactions = 12,
                processingCount = 8,
                readyCount = 5,
            )
        )
    }
}