package com.xeismonium.washcleaner.ui.screen.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Today
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
import com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity
import com.xeismonium.washcleaner.ui.components.dashboard.AlertBanner
import com.xeismonium.washcleaner.ui.components.dashboard.DashboardHeader
import com.xeismonium.washcleaner.ui.components.dashboard.DashboardQuickAction
import com.xeismonium.washcleaner.ui.components.dashboard.DashboardTransactionItem
import com.xeismonium.washcleaner.ui.components.dashboard.HeroRevenueCard
import com.xeismonium.washcleaner.ui.components.dashboard.PaymentStatusCard
import com.xeismonium.washcleaner.ui.components.dashboard.QuickStatCard
import com.xeismonium.washcleaner.ui.components.dashboard.SectionHeader
import com.xeismonium.washcleaner.ui.theme.StatusCompleted
import com.xeismonium.washcleaner.ui.theme.StatusNew
import com.xeismonium.washcleaner.ui.theme.StatusProcessing
import com.xeismonium.washcleaner.ui.theme.StatusReady
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import com.xeismonium.washcleaner.util.PaymentStatus
import com.xeismonium.washcleaner.util.PaymentUtils

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
        onNavigateToCustomers = { navController.navigate("customer_list") },
        onTransactionClick = { transactionId -> navController.navigate("transaction_detail/$transactionId") }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DashboardContent(
    uiState: DashboardUiState,
    onOpenDrawer: () -> Unit = {},
    onNewTransaction: () -> Unit = {},
    onViewAllTransactions: () -> Unit = {},
    onNavigateToServices: () -> Unit = {},
    onNavigateToCustomers: () -> Unit = {},
    onTransactionClick: (Long) -> Unit = {}
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
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 1. Dashboard Header - Greeting and Date
            item {
                DashboardHeader(currentTime = System.currentTimeMillis())
            }

            // 2. Hero Revenue Card
            item {
                HeroRevenueCard(
                    todayRevenue = uiState.todayRevenue,
                    totalRevenue = uiState.totalRevenue,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // 3. Alert Banner (Conditional)
            item {
                AlertBanner(
                    overdueCount = uiState.overdueCount,
                    onClick = { /* TODO: Navigate to filtered transaction list */ },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            // 4. Quick Stats Grid (2x2)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionHeader(title = "Statistik Cepat")

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 2
                    ) {
                        QuickStatCard(
                            icon = Icons.Default.Today,
                            value = uiState.todayTransactions.toString(),
                            label = "Hari Ini",
                            iconTint = StatusNew,
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatCard(
                            icon = Icons.Default.HourglassTop,
                            value = uiState.processingCount.toString(),
                            label = "Diproses",
                            iconTint = StatusProcessing,
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatCard(
                            icon = Icons.Default.CheckCircle,
                            value = uiState.readyCount.toString(),
                            label = "Siap Diambil",
                            iconTint = StatusReady,
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatCard(
                            icon = Icons.Default.Done,
                            value = uiState.completedCount.toString(),
                            label = "Selesai",
                            iconTint = StatusCompleted,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // 5. Payment Status Grid (2-column)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionHeader(title = "Status Pembayaran")

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 2
                    ) {
                        PaymentStatusCard(
                            status = PaymentStatus.UNPAID,
                            count = uiState.unpaidCount,
                            amount = uiState.unpaidTotal,
                            modifier = Modifier.weight(1f)
                        )
                        PaymentStatusCard(
                            status = PaymentStatus.PARTIAL,
                            count = uiState.partiallyPaidCount,
                            amount = uiState.partiallyPaidTotal,
                            totalAmount = uiState.unpaidTotal + uiState.partiallyPaidTotal + uiState.fullyPaidTotal,
                            modifier = Modifier.weight(1f)
                        )
                        PaymentStatusCard(
                            status = PaymentStatus.PAID,
                            count = uiState.fullyPaidCount,
                            amount = uiState.fullyPaidTotal,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 6. Recent Transactions
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionHeader(
                        title = "Transaksi Terbaru",
                        actionText = if (uiState.recentTransactions.isNotEmpty()) "Lihat Semua" else null,
                        onActionClick = if (uiState.recentTransactions.isNotEmpty()) onViewAllTransactions else null
                    )

                    if (uiState.recentTransactions.isEmpty()) {
                        Text(
                            text = "Belum ada transaksi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.recentTransactions.take(5).forEach { transaction ->
                                DashboardTransactionItem(
                                    transaction = transaction,
                                    onClick = { onTransactionClick(transaction.id) }
                                )
                            }
                        }
                    }
                }
            }

            // 7. Quick Actions
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SectionHeader(title = "Aksi Cepat")

                    // 2x2 Grid for Quick Actions
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
                completedCount = 15,
                totalRevenue = 25000000.0,
                todayRevenue = 1500000.0,
                overdueCount = 3,
                unpaidCount = 10,
                unpaidTotal = 3500000.0,
                partiallyPaidCount = 5,
                partiallyPaidTotal = 1200000.0,
                fullyPaidCount = 20,
                fullyPaidTotal = 8000000.0,
                recentTransactions = listOf(
                    LaundryTransactionEntity(
                        id = 1,
                        customerId = 1,
                        customerName = "Ahmad Rizki",
                        totalPrice = 150000.0,
                        dateIn = System.currentTimeMillis() - (30 * 60 * 1000),
                        dateOut = null,
                        estimatedDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000),
                        status = "proses",
                        paidAmount = 0.0
                    ),
                    LaundryTransactionEntity(
                        id = 2,
                        customerId = 2,
                        customerName = "Siti Aminah",
                        totalPrice = 250000.0,
                        dateIn = System.currentTimeMillis() - (2 * 60 * 60 * 1000),
                        dateOut = null,
                        estimatedDate = System.currentTimeMillis() + (12 * 60 * 60 * 1000),
                        status = "siap",
                        paidAmount = 250000.0
                    ),
                    LaundryTransactionEntity(
                        id = 3,
                        customerId = 3,
                        customerName = "Budi Santoso",
                        totalPrice = 120000.0,
                        dateIn = System.currentTimeMillis() - (5 * 60 * 60 * 1000),
                        dateOut = null,
                        estimatedDate = System.currentTimeMillis() + (6 * 60 * 60 * 1000),
                        status = "baru",
                        paidAmount = 60000.0
                    )
                )
            )
        )
    }
}

@Preview(name = "Dark Mode", showBackground = true)
@Composable
fun DashboardPreviewDark() {
    WashCleanerTheme(darkTheme = true) {
        DashboardContent(
            uiState = DashboardUiState(
                todayTransactions = 8,
                processingCount = 4,
                readyCount = 3,
                completedCount = 10,
                totalRevenue = 18500000.0,
                todayRevenue = 850000.0,
                overdueCount = 0,
                unpaidCount = 5,
                unpaidTotal = 1500000.0,
                partiallyPaidCount = 3,
                partiallyPaidTotal = 800000.0,
                fullyPaidCount = 15,
                fullyPaidTotal = 5000000.0,
                recentTransactions = listOf(
                    LaundryTransactionEntity(
                        id = 1,
                        customerId = 1,
                        customerName = "John Doe",
                        totalPrice = 200000.0,
                        dateIn = System.currentTimeMillis() - (60 * 60 * 1000),
                        dateOut = null,
                        estimatedDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000),
                        status = "proses",
                        paidAmount = 100000.0
                    )
                )
            )
        )
    }
}