package com.xeismonium.washcleaner.ui.screen.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Update
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.xeismonium.washcleaner.ui.components.common.StatusBadge
import com.xeismonium.washcleaner.ui.components.transaction.InfoRow
import com.xeismonium.washcleaner.ui.components.transaction.ServiceDetailCard
import com.xeismonium.washcleaner.ui.components.transaction.StatusUpdateDialog
import com.xeismonium.washcleaner.ui.components.transaction.TimelineItem
import com.xeismonium.washcleaner.ui.theme.StatusCancelled
import com.xeismonium.washcleaner.ui.theme.StatusCompleted
import com.xeismonium.washcleaner.ui.theme.StatusProcessing
import com.xeismonium.washcleaner.ui.theme.StatusReady
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
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

    uiState.selectedTransaction?.let { transactionWithServices ->
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

@Composable
private fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                text = "Hapus Transaksi?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "Tindakan ini tidak dapat dibatalkan. Semua data transaksi akan dihapus secara permanen.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Hapus")
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Batal")
            }
        }
    )
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
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
    val dateFormatter = SimpleDateFormat("dd MMMM yyyy, HH:mm", Locale("id", "ID"))

    val statusColor = when (transaction.transaction.status.lowercase()) {
        "proses" -> StatusProcessing
        "siap" -> StatusReady
        "selesai" -> StatusCompleted
        else -> StatusCancelled
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Detail Transaksi",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Edit"
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                    actionIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // Hero Section
            item {
                HeroSection(
                    transaction = transaction.transaction,
                    formatter = formatter,
                    statusColor = statusColor
                )
            }

            // Customer Info Section
            item {
                SectionCard(
                    title = "Informasi Pelanggan",
                    icon = Icons.Default.Person,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    InfoRow(
                        label = "Nama Pelanggan",
                        value = transaction.transaction.customerName ?: "Tanpa Nama",
                        icon = Icons.Default.Person
                    )
                }
            }

            // Services Section Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalLaundryService,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "Daftar Layanan",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${transaction.transactionServices.size} item",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Services List
            itemsIndexed(
                items = transaction.transactionServices,
                key = { _, service -> service.id }
            ) { index, transactionService ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300, delayMillis = index * 50)) +
                            slideInHorizontally(tween(300, delayMillis = index * 50)) { it / 2 }
                ) {
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        ServiceDetailCard(
                            transactionService = transactionService,
                            service = services.find { it.id == transactionService.serviceId },
                            formatter = formatter
                        )
                    }
                }
            }

            // Timeline Section
            item {
                SectionCard(
                    title = "Timeline",
                    icon = Icons.Default.Schedule,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    TimelineItem(
                        icon = Icons.Default.CalendarToday,
                        label = "Tanggal Masuk",
                        value = dateFormatter.format(Date(transaction.transaction.dateIn)),
                        isCompleted = true,
                        isLast = transaction.transaction.dateOut == null
                    )

                    transaction.transaction.dateOut?.let {
                        TimelineItem(
                            icon = Icons.Default.CheckCircle,
                            label = "Tanggal Selesai",
                            value = dateFormatter.format(Date(it)),
                            isCompleted = true,
                            isLast = true
                        )
                    }
                }
            }

            // Total Section
            item {
                TotalPaymentCard(
                    totalPrice = transaction.transaction.totalPrice,
                    serviceCount = transaction.transactionServices.size,
                    formatter = formatter,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Action Button
            item {
                Button(
                    onClick = onStatusChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Ubah Status",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroSection(
    transaction: LaundryTransactionEntity,
    formatter: NumberFormat,
    statusColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        statusColor.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(statusColor, statusColor.copy(alpha = 0.7f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = (transaction.customerName?.firstOrNull() ?: 'T').uppercase(),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            // Customer Name
            Text(
                text = transaction.customerName ?: "Tanpa Nama",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Status Badge
            StatusBadge(status = transaction.status)

            // Transaction ID
            Text(
                text = "ID: #${transaction.id}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            content()
        }
    }
}

@Composable
private fun TotalPaymentCard(
    totalPrice: Double,
    serviceCount: Int,
    formatter: NumberFormat,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total Pembayaran",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalLaundryService,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "$serviceCount layanan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            Text(
                text = formatter.format(totalPrice),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
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
                    dateOut = System.currentTimeMillis() + 86400000,
                    status = "selesai"
                ),
                transactionServices = listOf(
                    TransactionServiceEntity(
                        id = 1,
                        transactionId = 1,
                        serviceId = 1,
                        weightKg = 5.0,
                        subtotalPrice = 75000.0
                    ),
                    TransactionServiceEntity(
                        id = 2,
                        transactionId = 1,
                        serviceId = 2,
                        weightKg = 2.5,
                        subtotalPrice = 50000.0
                    )
                )
            ),
            services = listOf(
                ServiceEntity(id = 1, name = "Cuci Setrika", price = 15000, isActive = true),
                ServiceEntity(id = 2, name = "Setrika Saja", price = 20000, isActive = true)
            )
        )
    }
}
