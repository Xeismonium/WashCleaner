package com.xeismonium.washcleaner.ui.screen.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.xeismonium.washcleaner.data.local.database.entity.CustomerEntity
import com.xeismonium.washcleaner.ui.components.common.ModernButton
import com.xeismonium.washcleaner.ui.components.customer.CustomerFormCard
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFormScreen(
    navController: NavController,
    customerId: Long = 0,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val events by viewModel.events.collectAsState()

    LaunchedEffect(events) {
        when (events) {
            is CustomerEvent.Success -> {
                viewModel.clearEvent()
                navController.navigateUp()
            }
            is CustomerEvent.Error -> {
                // Error is shown in UI
            }
            null -> {}
        }
    }

    LaunchedEffect(customerId) {
        if (customerId != 0L) {
            viewModel.loadCustomerById(customerId)
        }
    }

    CustomerFormContent(
        isEdit = customerId != 0L,
        initialCustomer = if (customerId != 0L) uiState.selectedCustomer else null,
        onSave = { name, phone, address ->
            if (customerId == 0L) {
                viewModel.addCustomer(name, phone, address)
            } else {
                viewModel.updateCustomer(customerId, name, phone, address)
            }
        },
        onCancel = { navController.navigateUp() },
        error = uiState.error
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerFormContent(
    isEdit: Boolean = false,
    initialCustomer: CustomerEntity? = null,
    onSave: (String, String, String) -> Unit = { _, _, _ -> },
    onCancel: () -> Unit = {},
    error: String? = null
) {
    var name by remember { mutableStateOf(initialCustomer?.name ?: "") }
    var phone by remember { mutableStateOf(initialCustomer?.phone ?: "") }
    var address by remember { mutableStateOf(initialCustomer?.address ?: "") }

    // Update fields when initialCustomer changes (for edit mode)
    LaunchedEffect(initialCustomer) {
        initialCustomer?.let {
            name = it.name
            phone = it.phone
            address = it.address
        }
    }

    val isFormValid = name.isNotBlank() && phone.isNotBlank()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEdit) "Edit Pelanggan" else "Tambah Pelanggan",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Customer Form Card
            CustomerFormCard(
                name = name,
                phone = phone,
                address = address,
                onNameChange = { name = it },
                onPhoneChange = { phone = it },
                onAddressChange = { address = it }
            )

            // Error Message with Animation
            AnimatedVisibility(
                visible = error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                error?.let {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Save Button
            ModernButton(
                text = if (isEdit) "Update Pelanggan" else "Simpan Pelanggan",
                enabled = isFormValid,
                onClick = { onSave(name, phone, address) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerFormPreviewAdd() {
    WashCleanerTheme {
        CustomerFormContent(
            isEdit = false
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerFormPreviewEdit() {
    WashCleanerTheme {
        CustomerFormContent(
            isEdit = true,
            initialCustomer = CustomerEntity(
                id = 1,
                name = "John Doe",
                phone = "08123456789",
                address = "Jl. Example No. 123"
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CustomerFormPreviewWithError() {
    WashCleanerTheme {
        CustomerFormContent(
            isEdit = false,
            error = "Terjadi kesalahan saat menyimpan data"
        )
    }
}
