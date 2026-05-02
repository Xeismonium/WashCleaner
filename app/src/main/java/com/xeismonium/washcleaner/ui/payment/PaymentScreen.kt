package com.xeismonium.washcleaner.ui.payment

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.model.OrderStatus
import com.xeismonium.washcleaner.domain.model.PaymentStatus
import com.xeismonium.washcleaner.ui.payment.components.DpInputSection
import com.xeismonium.washcleaner.ui.payment.components.OrderSummaryCard
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onNavigateBack: () -> Unit,
    viewModel: PaymentViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.paymentProcessed) {
        if (uiState.paymentProcessed) {
            val phone = uiState.order?.customerPhone?.replaceFirst("08", "62") ?: ""
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/$phone?text=${uiState.whatsappMessage}")
            }
            context.startActivity(intent)
            onNavigateBack()
        }
    }

    PaymentContent(
        uiState = uiState,
        onBackClick = onNavigateBack,
        onConfirmPayment = { status, dp -> viewModel.processPayment(status, dp) },
        onClearError = { viewModel.clearError() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentContent(
    uiState: PaymentUiState,
    onBackClick: () -> Unit,
    onConfirmPayment: (PaymentStatus, Double) -> Unit,
    onClearError: () -> Unit
) {
    var selectedStatus by remember(uiState.order) { 
        mutableStateOf(uiState.order?.paymentStatus ?: PaymentStatus.PAID) 
    }
    var dpAmountText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            onClearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Proses Pembayaran") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            uiState.order?.let { order ->
                OrderSummaryCard(order = order)

                Text("Pilih Status Pembayaran", style = MaterialTheme.typography.titleSmall)
                Column(Modifier.selectableGroup()) {
                    PaymentStatus.entries.forEach { status ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            RadioButton(
                                selected = (status == selectedStatus),
                                onClick = { selectedStatus = status }
                            )
                            Text(
                                text = when (status) {
                                    PaymentStatus.PAID -> "Lunas"
                                    PaymentStatus.PARTIAL -> "Uang Muka (DP)"
                                    PaymentStatus.UNPAID -> "Belum Bayar"
                                },
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }

                if (selectedStatus == PaymentStatus.PARTIAL) {
                    DpInputSection(
                        dpAmount = dpAmountText,
                        onDpAmountChange = { dpAmountText = it }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        val dp = dpAmountText.toDoubleOrNull() ?: 0.0
                        onConfirmPayment(selectedStatus, dp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Konfirmasi & Bagikan Nota")
                    }
                }
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (uiState.isLoading) CircularProgressIndicator() else Text("Data tidak ditemukan")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PaymentPreview() {
    WashCleanerTheme {
        PaymentContent(
            uiState = PaymentUiState(
                order = Order(
                    id = "1",
                    orderCode = "WC-20231027-001",
                    customerName = "John Doe",
                    totalPrice = 50000.0,
                    status = OrderStatus.RECEIVED
                )
            ),
            onBackClick = {},
            onConfirmPayment = { _, _ -> },
            onClearError = {}
        )
    }
}
