package com.xeismonium.washcleaner.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.model.OrderStatus
import com.xeismonium.washcleaner.core.utils.CurrencyFormatter
import com.xeismonium.washcleaner.core.utils.DateFormatter
import com.xeismonium.washcleaner.ui.order.components.OrderStatusStepper
import com.xeismonium.washcleaner.ui.order.components.PaymentStatusChip
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@Composable
fun OrderDetailScreen(
    orderId: String,
    onNavigateBack: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(orderId) {
        viewModel.getOrderById(orderId)
    }

    val selectedOrder = (uiState as? OrderUiState.Success)?.selectedOrder

    OrderDetailContent(
        order = selectedOrder,
        onNavigateBack = onNavigateBack,
        onUpdateStatus = { status ->
            viewModel.updateOrderStatus(orderId, status)
        },
        onDeleteOrder = {
            viewModel.deleteOrder(orderId) { success ->
                if (success) onNavigateBack()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailContent(
    order: Order?,
    onNavigateBack: () -> Unit,
    onUpdateStatus: (OrderStatus) -> Unit,
    onDeleteOrder: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pesanan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Hapus Pesanan")
                    }
                }
            )
        }
    ) { padding ->
        if (order == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OrderStatusStepper(currentStatus = order.status)

                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = order.orderCode, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            PaymentStatusChip(status = order.paymentStatus)
                        }
                        
                        Divider()
                        
                        val details = listOf(
                            "Pelanggan" to order.customerName,
                            "Layanan" to order.serviceName,
                            "Berat / Jumlah" to "${order.weight} kg",
                            "Total Harga" to CurrencyFormatter.formatRupiah(order.totalPrice),
                            "Tanggal Dibuat" to DateFormatter.format(order.createdAt, "dd MMM yyyy, HH:mm"),
                            "Estimasi Selesai" to DateFormatter.format(order.pickupDate)
                        )

                        details.forEach { (label, value) ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                                Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }

                Text(text = "Update Status", style = MaterialTheme.typography.titleMedium)
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val statuses = OrderStatus.values()
                    val currentIndex = statuses.indexOf(order.status)
                    
                    if (currentIndex < statuses.size - 1) {
                        val nextStatus = statuses[currentIndex + 1]
                        Button(
                            onClick = { onUpdateStatus(nextStatus) },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Ke ${nextStatus.name}")
                        }
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Hapus Pesanan") },
                text = { Text("Apakah Anda yakin ingin menghapus pesanan ini?") },
                confirmButton = {
                    TextButton(onClick = {
                        onDeleteOrder()
                        showDeleteDialog = false
                    }) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Batal")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderDetailPreview() {
    WashCleanerTheme {
        OrderDetailContent(
            order = Order(
                orderCode = "ORD-001",
                customerName = "Budi",
                serviceName = "Cuci Lipat",
                weight = 5.0,
                totalPrice = 25000.0,
                status = OrderStatus.WASHING,
                createdAt = System.currentTimeMillis()
            ),
            onNavigateBack = {},
            onUpdateStatus = {},
            onDeleteOrder = {}
        )
    }
}
