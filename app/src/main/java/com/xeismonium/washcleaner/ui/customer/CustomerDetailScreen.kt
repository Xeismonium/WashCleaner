package com.xeismonium.washcleaner.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xeismonium.washcleaner.domain.model.Customer
import com.xeismonium.washcleaner.ui.order.components.OrderCard
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: String,
    onNavigateBack: () -> Unit,
    onEditCustomer: (String) -> Unit,
    onOrderClick: (String) -> Unit,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(customerId) {
        viewModel.getCustomerDetail(customerId)
    }

    CustomerDetailContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onEditCustomer = { onEditCustomer(customerId) },
        onOrderClick = onOrderClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailContent(
    uiState: CustomerUiState,
    onNavigateBack: () -> Unit,
    onEditCustomer: () -> Unit,
    onOrderClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pelanggan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState is CustomerUiState.Success && uiState.selectedCustomer != null) {
                        IconButton(onClick = onEditCustomer) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        when (uiState) {
            is CustomerUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is CustomerUiState.Success -> {
                val customer = uiState.selectedCustomer
                if (customer != null) {
                    Column(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize()
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = customer.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = customer.phone,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (customer.address.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = customer.address,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Riwayat Pesanan",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        if (uiState.orderHistory.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                Text("Belum ada riwayat pesanan")
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(uiState.orderHistory) { order ->
                                    OrderCard(
                                        order = order,
                                        onClick = { onOrderClick(order.id) }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        Text("Pelanggan tidak ditemukan")
                    }
                }
            }
            is CustomerUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerDetailPreview() {
    WashCleanerTheme {
        CustomerDetailContent(
            uiState = CustomerUiState.Success(
                selectedCustomer = com.xeismonium.washcleaner.domain.model.Customer(
                    id = "1",
                    name = "Budi",
                    phone = "08123456789",
                    address = "Jl. Merdeka No. 1"
                ),
                orderHistory = emptyList()
            ),
            onNavigateBack = {},
            onEditCustomer = {},
            onOrderClick = {}
        )
    }
}
