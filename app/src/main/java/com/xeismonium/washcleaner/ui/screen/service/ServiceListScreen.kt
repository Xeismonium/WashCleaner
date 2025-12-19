package com.xeismonium.washcleaner.ui.screen.service

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xeismonium.washcleaner.data.local.database.entity.ServiceEntity
import com.xeismonium.washcleaner.ui.components.common.EmptyState
import com.xeismonium.washcleaner.ui.components.common.SearchTopAppBar
import com.xeismonium.washcleaner.ui.components.service.ServiceCard
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import java.text.NumberFormat
import java.util.Locale

import com.xeismonium.washcleaner.ui.components.common.WashCleanerScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceListScreen(
    navController: NavController,
    viewModel: ServiceViewModel = hiltViewModel(),
    onOpenDrawer: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val events by viewModel.events.collectAsState()

    LaunchedEffect(events) {
        when (events) {
            is ServiceEvent.Success -> {
                viewModel.clearEvent()
            }
            else -> {}
        }
    }

    ServiceListContent(
        uiState = uiState,
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        onServiceClick = { serviceId -> navController.navigate("service_form/$serviceId") },
        onToggleStatus = { service -> viewModel.toggleServiceStatus(service) },
        onAddService = { navController.navigate("service_form/0") },
        onRefresh = { viewModel.refresh() },
        onMenuClick = onOpenDrawer
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceListContent(
    uiState: ServiceUiState,
    onSearchQueryChange: (String) -> Unit = {},
    onServiceClick: (Long) -> Unit = {},
    onToggleStatus: (ServiceEntity) -> Unit = {},
    onAddService: () -> Unit = {},
    onRefresh: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    WashCleanerScaffold(
        title = "Daftar Layanan",
        onMenuClick = onMenuClick,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onRefresh = onRefresh,
        searchQuery = uiState.searchQuery,
        onSearchQueryChange = onSearchQueryChange,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddService,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Tambah Layanan",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.services.isEmpty() && !uiState.isLoading) {
                EmptyState(
                    message = if (uiState.searchQuery.isNotBlank()) {
                        "Tidak ada layanan ditemukan"
                    } else {
                        "Belum ada layanan"
                    },
                    icon = Icons.Default.LocalLaundryService,
                    onAddClick = onAddService,
                    addButtonText = "Tambah Layanan"
                )
            } else if (uiState.services.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.services) { service ->
                        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
                        ServiceCard(
                            title = service.name,
                            price = "${formatter.format(service.price)}/${service.unit}",
                            leadingIcon = Icons.Default.LocalLaundryService,
                            onClick = { onServiceClick(service.id) },
                            onMoreClick = { onToggleStatus(service) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceListPreview() {
    WashCleanerTheme {
        ServiceListContent(
            uiState = ServiceUiState(
                services = listOf(
                    ServiceEntity(id = 1, name = "Cuci Kering", price = 5000, isActive = true),
                    ServiceEntity(id = 2, name = "Cuci Setrika", price = 7000, isActive = true),
                    ServiceEntity(id = 3, name = "Setrika Saja", price = 3000, isActive = false)
                ),
                searchQuery = ""
            )
        )
    }
}

@Preview(showBackground = true, name = "Interactive Search Preview")
@Composable
fun ServiceListInteractivePreview() {
    WashCleanerTheme {
        var uiState by remember {
            mutableStateOf(
                ServiceUiState(
                    services = listOf(
                        ServiceEntity(id = 1, name = "Cuci Kering", price = 5000, isActive = true),
                        ServiceEntity(id = 2, name = "Cuci Setrika", price = 7000, isActive = true),
                        ServiceEntity(id = 3, name = "Setrika Saja", price = 3000, isActive = false)
                    )
                )
            )
        }
        ServiceListContent(
            uiState = uiState,
            onSearchQueryChange = { query ->
                uiState = uiState.copy(searchQuery = query)
            }
        )
    }
}
