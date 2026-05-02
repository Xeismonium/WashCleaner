package com.xeismonium.washcleaner.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xeismonium.washcleaner.domain.model.Service
import com.xeismonium.washcleaner.domain.model.UserRole
import com.xeismonium.washcleaner.ui.auth.AuthUiState
import com.xeismonium.washcleaner.ui.auth.AuthViewModel
import com.xeismonium.washcleaner.ui.components.ConfirmDialog
import com.xeismonium.washcleaner.ui.settings.components.ServiceItemRow
import com.xeismonium.washcleaner.ui.settings.components.StoreInfoForm
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showServiceDialog by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf<Service?>(null) }

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onLogout()
        }
    }

    SettingsContent(
        uiState = uiState,
        userRole = (authState as? AuthUiState.Success)?.user?.role ?: UserRole.STAFF,
        onNavigateBack = onNavigateBack,
        onNavigateToStaff = onNavigateToStaff,
        onSaveStoreInfo = viewModel::updateStoreSettings,
        onAddService = {
            selectedService = null
            showServiceDialog = true
        },
        onEditService = {
            selectedService = it
            showServiceDialog = true
        },
        onDeleteService = viewModel::deleteService,
        onLogoutClick = { showLogoutDialog = true },
        formatPrice = viewModel::formatPrice
    )

    ConfirmDialog(
        show = showLogoutDialog,
        title = "Logout",
        message = "Are you sure you want to logout?",
        onConfirm = viewModel::signOut,
        onDismiss = { showLogoutDialog = false }
    )

    if (showServiceDialog) {
        var name by remember { mutableStateOf(selectedService?.name ?: "") }
        var price by remember { mutableStateOf(selectedService?.price?.toString() ?: "") }
        
        AlertDialog(
            onDismissRequest = { showServiceDialog = false },
            title = { Text(if (selectedService == null) "Add Service" else "Edit Service") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Service Name") })
                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val priceDouble = price.toDoubleOrNull() ?: 0.0
                    viewModel.upsertService(name, priceDouble, selectedService?.id)
                    showServiceDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showServiceDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsContent(
    uiState: SettingsUiState,
    userRole: UserRole,
    onNavigateBack: () -> Unit,
    onNavigateToStaff: () -> Unit,
    onSaveStoreInfo: (String, String) -> Unit,
    onAddService: () -> Unit,
    onEditService: (Service) -> Unit,
    onDeleteService: (String) -> Unit,
    onLogoutClick: () -> Unit,
    formatPrice: (Double) -> String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (userRole == UserRole.OWNER) {
                        IconButton(onClick = onNavigateToStaff) {
                            Icon(Icons.Default.Person, contentDescription = "Staff Management")
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            item {
                StoreInfoForm(
                    initialName = uiState.storeSettings.storeName,
                    initialAddress = uiState.storeSettings.address,
                    onSave = onSaveStoreInfo
                )
            }
            
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Services", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = onAddService) {
                        Icon(Icons.Default.Add, contentDescription = "Add Service")
                    }
                }
            }
            
            items(uiState.services) { service ->
                ServiceItemRow(
                    service = service,
                    formattedPrice = formatPrice(service.price),
                    onEdit = onEditService,
                    onDelete = { onDeleteService(it.id) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsPreview() {
    WashCleanerTheme {
        SettingsContent(
            uiState = SettingsUiState(),
            userRole = UserRole.OWNER,
            onNavigateBack = {},
            onNavigateToStaff = {},
            onSaveStoreInfo = { _, _ -> },
            onAddService = {},
            onEditService = {},
            onDeleteService = {},
            onLogoutClick = {},
            formatPrice = { "$ 0.00" }
        )
    }
}
