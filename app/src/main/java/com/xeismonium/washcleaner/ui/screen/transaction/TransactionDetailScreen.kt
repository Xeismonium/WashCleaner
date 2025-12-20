package com.xeismonium.washcleaner.ui.screen.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity
import com.xeismonium.washcleaner.data.local.database.entity.ServiceEntity
import com.xeismonium.washcleaner.data.local.database.entity.TransactionServiceEntity
import com.xeismonium.washcleaner.data.local.database.entity.TransactionWithServices
import com.xeismonium.washcleaner.ui.components.transaction.ActionButtonsFooter
import com.xeismonium.washcleaner.ui.components.transaction.CustomerInfoCard
import com.xeismonium.washcleaner.ui.components.transaction.DateCard
import com.xeismonium.washcleaner.ui.components.transaction.DeleteConfirmationDialog
import com.xeismonium.washcleaner.ui.components.transaction.PaymentSummaryCard
import com.xeismonium.washcleaner.ui.components.transaction.ServiceCard
import com.xeismonium.washcleaner.ui.components.transaction.StatusUpdateDialog
import com.xeismonium.washcleaner.ui.theme.StatusCancelled
import com.xeismonium.washcleaner.ui.theme.StatusCompleted
import com.xeismonium.washcleaner.ui.theme.StatusNew
import com.xeismonium.washcleaner.ui.theme.StatusProcessing
import com.xeismonium.washcleaner.ui.theme.StatusReady
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import com.xeismonium.washcleaner.util.CurrencyUtils
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    navController: NavController,
    transactionId: Long,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val events by viewModel.events.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showStatusDialog by remember { mutableStateOf(false) }

    LaunchedEffect(transactionId) {
        viewModel.loadTransactionById(transactionId)
    }

    LaunchedEffect(events) {
        when (events) {
            is TransactionEvent.Success -> {
                viewModel.clearEvent()
                navController.navigateUp()
            }
            else -> {}
        }
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val transaction = uiState.selectedTransaction
    if (transaction == null && !uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Transaksi tidak ditemukan")
        }
        return
    }

    transaction?.let { transactionWithServices ->
        TransactionDetailContent(
            transaction = transactionWithServices,
            services = uiState.services,
            onBack = { navController.navigateUp() },
            onEdit = { navController.navigate("transaction_form/$transactionId") },
            onDelete = { showDeleteDialog = true },
            onStatusChange = { showStatusDialog = true }
        )

        if (showDeleteDialog) {
            DeleteConfirmationDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    viewModel.deleteTransaction(transactionId)
                    showDeleteDialog = false
                }
            )
        }

        if (showStatusDialog) {
            StatusUpdateDialog(
                currentStatus = transactionWithServices.transaction.status,
                onDismiss = { showStatusDialog = false },
                onConfirm = { newStatus ->
                    viewModel.updateTransactionStatus(transactionId, newStatus)
                    showStatusDialog = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailContent(
    transaction: TransactionWithServices,
    services: List<ServiceEntity>,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onStatusChange: () -> Unit = {}
) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))

    val statusColor = when (transaction.transaction.status.lowercase()) {
        "baru" -> StatusNew
        "proses" -> StatusProcessing
        "siap" -> StatusReady
        "selesai" -> StatusCompleted
        else -> StatusCancelled
    }

    val statusLabel = when (transaction.transaction.status.lowercase()) {
        "baru" -> "Baru"
        "proses" -> "Diproses"
        "siap" -> "Siap Diambil"
        "selesai" -> "Selesai"
        else -> "Dibatalkan"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Transaksi",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.size(48.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            ActionButtonsFooter(
                onStatusChange = onStatusChange,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer Info Card
            item {
                CustomerInfoCard(
                    customerName = transaction.transaction.customerName ?: "Tanpa Nama",
                    statusLabel = statusLabel,
                    statusColor = statusColor
                )
            }

            // Services Section Header
            item {
                Text(
                    text = "Daftar Layanan (${transaction.transactionServices.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Service Cards
            items(
                items = transaction.transactionServices,
                key = { it.id }
            ) { transactionService ->
                val service = services.find { it.id == transactionService.serviceId }
                ServiceCard(
                    serviceName = service?.name ?: "Layanan",
                    price = service?.price?.toDouble() ?: 0.0,
                    unit = service?.unit ?: "kg",
                    weight = transactionService.weightKg,
                    subtotal = transactionService.subtotalPrice
                )
            }

            // Payment Summary Card
            item {
                PaymentSummaryCard(
                    totalPrice = transaction.transaction.totalPrice,
                    serviceCount = transaction.transactionServices.size
                )
            }

            // Date Card
            item {
                DateCard(
                    dateIn = transaction.transaction.dateIn,
                    dateOut = transaction.transaction.dateOut,
                    dateFormatter = dateFormatter
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionDetailPreview() {
    WashCleanerTheme {
        TransactionDetailContent(
            transaction = TransactionWithServices(
                transaction = LaundryTransactionEntity(
                    id = 1,
                    customerId = 1,
                    customerName = "John Doe",
                    totalPrice = 125000.0,
                    dateIn = System.currentTimeMillis(),
                    dateOut = null,
                    estimatedDate = System.currentTimeMillis() + 86400000,
                    status = "proses"
                ),
                transactionServices = listOf(
                    TransactionServiceEntity(
                        id = 1,
                        transactionId = 1,
                        serviceId = 1,
                        weightKg = 5.2,
                        subtotalPrice = 41600.0
                    ),
                    TransactionServiceEntity(
                        id = 2,
                        transactionId = 1,
                        serviceId = 2,
                        weightKg = 3.0,
                        subtotalPrice = 45000.0
                    ),
                    TransactionServiceEntity(
                        id = 3,
                        transactionId = 1,
                        serviceId = 3,
                        weightKg = 2.5,
                        subtotalPrice = 37500.0
                    )
                )
            ),
            services = listOf(
                ServiceEntity(id = 1, name = "Cuci Kering Setrika", price = 8000, isActive = true),
                ServiceEntity(id = 2, name = "Cuci Setrika Express", price = 15000, isActive = true),
                ServiceEntity(id = 3, name = "Setrika Saja", price = 15000, isActive = true)
            )
        )
    }
}