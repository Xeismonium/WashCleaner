package com.xeismonium.washcleaner.ui.screen.customer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.xeismonium.washcleaner.data.local.database.entity.CustomerEntity
import com.xeismonium.washcleaner.ui.components.common.EmptyState
import com.xeismonium.washcleaner.ui.components.common.SearchTopAppBar
import com.xeismonium.washcleaner.ui.components.customer.CustomerListItem
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

import com.xeismonium.washcleaner.ui.components.common.WashCleanerScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    navController: NavController,
    viewModel: CustomerViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val events by viewModel.events.collectAsState()

    LaunchedEffect(events) {
        when (events) {
            is CustomerEvent.Success -> {
                viewModel.clearEvent()
            }
            else -> {}
        }
    }

    CustomerListContent(
        uiState = uiState,
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        onCustomerClick = { customerId -> navController.navigate("customer_form/$customerId") },
        onAddCustomer = { navController.navigate("customer_form/0") },
        onRefresh = { viewModel.refresh() },
        onMenuClick = onOpenDrawer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListContent(
    uiState: CustomerUiState,
    onSearchQueryChange: (String) -> Unit = {},
    onCustomerClick: (Long) -> Unit = {},
    onAddCustomer: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    WashCleanerScaffold(
        title = "Daftar Pelanggan",
        onMenuClick = onMenuClick,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onRefresh = onRefresh,
        searchQuery = uiState.searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        searchPlaceholder = "Cari nama atau no. telepon...",
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddCustomer,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Pelanggan",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.customersWithCount.isEmpty() && !uiState.isLoading) {
                EmptyState(
                    message = if (uiState.searchQuery.isNotBlank()) {
                        "Tidak ada pelanggan ditemukan"
                    } else {
                        "Belum ada pelanggan"
                    },
                    icon = Icons.Default.Person,
                    onAddClick = onAddCustomer,
                    addButtonText = "Tambah Pelanggan"
                )
            } else if (uiState.customersWithCount.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(uiState.customersWithCount) { index, customerWithCount ->
                        CustomerListItem(
                            customerWithCount = customerWithCount,
                            onClick = { onCustomerClick(customerWithCount.customer.id) }
                        )

                        // Add divider except for last item
                        if (index < uiState.customersWithCount.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant
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
fun CustomerListPreview() {
    WashCleanerTheme {
        CustomerListContent(
            uiState = CustomerUiState(
                customersWithCount = listOf(
                    CustomerWithTransactionCount(
                        customer = CustomerEntity(id = 1, name = "John Doe", phone = "08123456789", address = "Jl. Example No. 123"),
                        transactionCount = 15
                    ),
                    CustomerWithTransactionCount(
                        customer = CustomerEntity(id = 2, name = "Jane Smith", phone = "08234567890", address = "Jl. Test No. 456"),
                        transactionCount = 8
                    ),
                    CustomerWithTransactionCount(
                        customer = CustomerEntity(id = 3, name = "Bob Wilson", phone = "08345678901", address = ""),
                        transactionCount = 3
                    )
                ),
                searchQuery = ""
            )
        )
    }
}
