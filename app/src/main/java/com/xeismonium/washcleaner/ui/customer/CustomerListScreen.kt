package com.xeismonium.washcleaner.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xeismonium.washcleaner.domain.model.Customer
import com.xeismonium.washcleaner.ui.components.EmptyState
import com.xeismonium.washcleaner.ui.customer.components.CustomerCard
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListScreen(
    onAddCustomer: () -> Unit,
    onCustomerClick: (String) -> Unit,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    CustomerListContent(
        uiState = uiState,
        searchQuery = searchQuery,
        onSearchQueryChange = viewModel::searchCustomers,
        onAddCustomer = onAddCustomer,
        onCustomerClick = onCustomerClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerListContent(
    uiState: CustomerUiState,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddCustomer: () -> Unit,
    onCustomerClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pelanggan") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCustomer) {
                Icon(Icons.Default.Add, contentDescription = "Add Customer")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Cari nama atau telepon...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            when (uiState) {
                is CustomerUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is CustomerUiState.Success -> {
                    if (uiState.customers.isEmpty()) {
                        EmptyState(
                            icon = Icons.Default.Person,
                            title = "Tidak ada pelanggan",
                            message = if (searchQuery.isEmpty()) "Mulai dengan menambahkan pelanggan baru" else "Tidak ada pelanggan yang cocok dengan pencarian Anda",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.customers) { customer ->
                                CustomerCard(
                                    customer = customer,
                                    onClick = { onCustomerClick(customer.id) }
                                )
                            }
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
}

@Preview(showBackground = true)
@Composable
fun CustomerListPreview() {
    WashCleanerTheme {
        CustomerListContent(
            uiState = CustomerUiState.Success(
                customers = listOf(
                    Customer(id = "1", name = "Budi", phone = "08123456789"),
                    Customer(id = "2", name = "Siti", phone = "08987654321")
                )
            ),
            searchQuery = "",
            onSearchQueryChange = {},
            onAddCustomer = {},
            onCustomerClick = {}
        )
    }
}
