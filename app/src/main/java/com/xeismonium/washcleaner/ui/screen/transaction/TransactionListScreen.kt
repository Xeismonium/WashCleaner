package com.xeismonium.washcleaner.ui.screen.transaction

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity
import com.xeismonium.washcleaner.ui.components.transaction.TransactionEmptyState
import com.xeismonium.washcleaner.ui.components.transaction.TransactionFilterChips
import com.xeismonium.washcleaner.ui.components.transaction.TransactionListCard
import com.xeismonium.washcleaner.ui.components.transaction.TransactionSearchBar
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.xeismonium.washcleaner.ui.components.common.WashCleanerScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    navController: NavController,
    viewModel: TransactionViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    TransactionListContent(
        uiState = uiState,
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        onFilterStatusChange = { viewModel.filterByStatus(it) },
        onTransactionClick = { id -> navController.navigate("transaction_detail/$id") },
        onAddTransaction = { navController.navigate("transaction_form/0") },
        onRefresh = { viewModel.refresh() },
        onMenuClick = onOpenDrawer
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
    onRefresh: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme

    WashCleanerScaffold(
        title = "Transaksi Laundry",
        onMenuClick = onMenuClick,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onRefresh = onRefresh,
        searchQuery = uiState.searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        searchPlaceholder = "Cari nama atau nomor nota...",
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTransaction,
                containerColor = colorScheme.primary,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Transaksi",
                    tint = colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            // Filter Chips
            TransactionFilterChips(
                selectedStatus = uiState.filterStatus,
                onStatusSelected = onFilterStatusChange,
                transactions = uiState.transactions,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // Transaction List
            if (uiState.transactions.isEmpty() && !uiState.isLoading) {
                TransactionEmptyState(
                    message = if (uiState.searchQuery.isNotBlank() || uiState.filterStatus != null) {
                        "Tidak ada transaksi ditemukan"
                    } else {
                        "Belum Ada Transaksi"
                    },
                    subtitle = "Tekan tombol '+' untuk menambah transaksi baru."
                )
            } else if (uiState.transactions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 100.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
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
                            TransactionListCard(
                                transaction = transaction,
                                onClick = { onTransactionClick(transaction.id) }
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
                        estimatedDate = System.currentTimeMillis() + 86400000,
                        status = "proses"
                    ),
                    LaundryTransactionEntity(
                        id = 2,
                        customerId = 2,
                        customerName = "Jane Smith",
                        totalPrice = 75000.0,
                        dateIn = System.currentTimeMillis() - 3600000,
                        dateOut = System.currentTimeMillis(),
                        estimatedDate = System.currentTimeMillis(),
                        status = "selesai"
                    ),
                    LaundryTransactionEntity(
                        id = 3,
                        customerId = 3,
                        customerName = "Bob Wilson",
                        totalPrice = 60000.0,
                        dateIn = System.currentTimeMillis() - 7200000,
                        dateOut = null,
                        estimatedDate = System.currentTimeMillis() + 172800000,
                        status = "siap"
                    )
                ),
                searchQuery = ""
            )
        )
    }
}