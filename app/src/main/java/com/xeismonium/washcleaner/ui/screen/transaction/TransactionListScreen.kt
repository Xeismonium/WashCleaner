package com.xeismonium.washcleaner.ui.screen.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity
import com.xeismonium.washcleaner.ui.components.common.EmptyState
import com.xeismonium.washcleaner.ui.components.common.SearchTopAppBar
import com.xeismonium.washcleaner.ui.components.transaction.FilterChips
import com.xeismonium.washcleaner.ui.components.transaction.TransactionCard
import com.xeismonium.washcleaner.ui.theme.StatusCompleted
import com.xeismonium.washcleaner.ui.theme.StatusProcessing
import com.xeismonium.washcleaner.ui.theme.StatusReady
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    navController: NavController,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    TransactionListContent(
        uiState = uiState,
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        onFilterStatusChange = { viewModel.filterByStatus(it) },
        onTransactionClick = { id -> navController.navigate("transaction_detail/$id") },
        onAddTransaction = { navController.navigate("transaction_form/0") },
        onRefresh = { viewModel.refresh() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListContent(
    uiState: TransactionUiState,
    onSearchQueryChange: (String) -> Unit = {},
    onFilterStatusChange: (String?) -> Unit = {},
    onTransactionClick: (Long) -> Unit = {},
    onAddTransaction: () -> Unit = {},
    onRefresh: () -> Unit = {}
) {
    var isSearchActive by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            if (isSearchActive) {
                SearchTopAppBar(
                    query = uiState.searchQuery,
                    onQueryChange = onSearchQueryChange,
                    onDeactivateSearch = {
                        isSearchActive = false
                        onSearchQueryChange("")
                    },
                    placeholder = "Cari nama pelanggan..."
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = "Daftar Transaksi",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    actions = {
                        IconButton(onClick = { isSearchActive = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Cari Transaksi")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        actionIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Transaksi",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stats Summary Cards
            if (uiState.transactions.isNotEmpty()) {
                TransactionSummaryRow(
                    transactions = uiState.transactions,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // Filter Chips
            FilterChips(
                selectedStatus = uiState.filterStatus,
                onStatusSelected = onFilterStatusChange,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Transaction List
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.transactions.isEmpty()) {
                EmptyState(
                    message = if (uiState.searchQuery.isNotBlank() || uiState.filterStatus != null) {
                        "Tidak ada transaksi ditemukan"
                    } else {
                        "Belum ada transaksi"
                    },
                    icon = Icons.Outlined.Receipt,
                    onAddClick = onAddTransaction,
                    addButtonText = "Tambah Transaksi"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = uiState.transactions,
                        key = { _, transaction -> transaction.id }
                    ) { index, transaction ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(300, delayMillis = index * 50)) +
                                    slideInVertically(tween(300, delayMillis = index * 50)) { it / 2 }
                        ) {
                            TransactionCard(
                                transaction = transaction,
                                onClick = { onTransactionClick(transaction.id) }
                            )
                        }
                    }
                }
            }

            // Error Snackbar
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = slideInVertically { it },
                exit = slideOutVertically { it }
            ) {
                uiState.error?.let { error ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = error,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            TextButton(onClick = onRefresh) {
                                Text("Coba Lagi")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionSummaryRow(
    transactions: List<LaundryTransactionEntity>,
    modifier: Modifier = Modifier
) {
    val prosesCount = transactions.count { it.status.lowercase() == "proses" }
    val siapCount = transactions.count { it.status.lowercase() == "siap" }
    val selesaiCount = transactions.count { it.status.lowercase() == "selesai" }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            count = prosesCount,
            label = "Proses",
            color = StatusProcessing,
            icon = Icons.Default.LocalLaundryService,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            count = siapCount,
            label = "Siap",
            color = StatusReady,
            icon = Icons.Default.Inventory,
            modifier = Modifier.weight(1f)
        )
        SummaryCard(
            count = selesaiCount,
            label = "Selesai",
            color = StatusCompleted,
            icon = Icons.Default.CheckCircle,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryCard(
    count: Int,
    label: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionListPreview() {
    WashCleanerTheme {
        TransactionListContent(
            uiState = TransactionUiState(
                transactions = listOf(
                    LaundryTransactionEntity(
                        id = 1,
                        customerId = 1,
                        customerName = "John Doe",
                        totalPrice = 50000.0,
                        dateIn = System.currentTimeMillis(),
                        dateOut = null,
                        status = "proses"
                    ),
                    LaundryTransactionEntity(
                        id = 2,
                        customerId = 2,
                        customerName = "Jane Smith",
                        totalPrice = 75000.0,
                        dateIn = System.currentTimeMillis() - 3600000,
                        dateOut = System.currentTimeMillis(),
                        status = "selesai"
                    ),
                    LaundryTransactionEntity(
                        id = 3,
                        customerId = 3,
                        customerName = "Bob Wilson",
                        totalPrice = 60000.0,
                        dateIn = System.currentTimeMillis() - 7200000,
                        dateOut = null,
                        status = "siap"
                    )
                ),
                searchQuery = ""
            )
        )
    }
}
