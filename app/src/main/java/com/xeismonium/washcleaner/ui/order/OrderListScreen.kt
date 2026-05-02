package com.xeismonium.washcleaner.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.model.OrderStatus
import com.xeismonium.washcleaner.ui.components.EmptyState
import com.xeismonium.washcleaner.ui.order.components.OrderCard
import com.xeismonium.washcleaner.ui.order.components.OrderFilterChips
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    onAddOrder: () -> Unit,
    onOrderClick: (String) -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    OrderListContent(
        uiState = uiState,
        onAddOrder = onAddOrder,
        onOrderClick = onOrderClick,
        onFilterSelected = viewModel::setFilter
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListContent(
    uiState: OrderUiState,
    onAddOrder: () -> Unit,
    onOrderClick: (String) -> Unit,
    onFilterSelected: (OrderStatus?) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pesanan") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddOrder) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Pesanan")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            if (uiState is OrderUiState.Success) {
                OrderFilterChips(
                    selectedStatus = uiState.filter,
                    onStatusSelected = onFilterSelected,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            when (uiState) {
                is OrderUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is OrderUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = uiState.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is OrderUiState.Success -> {
                    if (uiState.orders.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.Inbox,
                            title = "Belum ada pesanan",
                            message = "Mulai dengan menambahkan pesanan baru",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(uiState.orders, key = { it.id }) { order ->
                                OrderCard(
                                    order = order,
                                    onClick = { onOrderClick(order.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderListPreview() {
    WashCleanerTheme {
        OrderListContent(
            uiState = OrderUiState.Success(
                orders = listOf(
                    Order(id = "1", orderCode = "ORD-001", customerName = "Budi", serviceName = "Cuci Lipat", totalPrice = 25000.0)
                )
            ),
            onAddOrder = {},
            onOrderClick = {},
            onFilterSelected = {}
        )
    }
}
