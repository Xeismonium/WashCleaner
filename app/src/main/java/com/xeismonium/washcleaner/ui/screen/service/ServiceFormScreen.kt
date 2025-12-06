package com.xeismonium.washcleaner.ui.screen.service

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
import com.xeismonium.washcleaner.data.local.database.entity.ServiceEntity
import com.xeismonium.washcleaner.ui.components.common.ModernButton
import com.xeismonium.washcleaner.ui.components.service.ServiceFormCard
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceFormScreen(
    navController: NavController,
    serviceId: Long = 0,
    viewModel: ServiceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val events by viewModel.events.collectAsState()

    LaunchedEffect(events) {
        when (events) {
            is ServiceEvent.Success -> {
                viewModel.clearEvent()
                navController.navigateUp()
            }
            else -> {}
        }
    }

    LaunchedEffect(serviceId) {
        if (serviceId != 0L) {
            viewModel.loadServiceById(serviceId)
        }
    }

    ServiceFormContent(
        isEdit = serviceId != 0L,
        initialService = if (serviceId != 0L) uiState.selectedService else null,
        error = uiState.error,
        onSave = { name, price, unit, isActive ->
            if (serviceId == 0L) {
                viewModel.addService(name, price, unit)
            } else {
                viewModel.updateService(serviceId, name, price, unit, isActive)
            }
        },
        onCancel = { navController.navigateUp() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceFormContent(
    isEdit: Boolean = false,
    initialService: ServiceEntity? = null,
    error: String? = null,
    onSave: (String, Int, String, Boolean) -> Unit = { _, _, _, _ -> },
    onCancel: () -> Unit = {}
) {
    var name by remember { mutableStateOf(initialService?.name ?: "") }
    var price by remember { mutableStateOf(initialService?.price?.toString() ?: "") }
    var unit by remember { mutableStateOf(initialService?.unit ?: "kg") }
    var isActive by remember { mutableStateOf(initialService?.isActive ?: true) }

    // Update fields when initialService changes (for edit mode)
    LaunchedEffect(initialService) {
        initialService?.let {
            name = it.name
            price = it.price.toString()
            unit = it.unit
            isActive = it.isActive
        }
    }

    val isFormValid = name.isNotBlank() && price.toIntOrNull() != null && (price.toIntOrNull() ?: 0) > 0

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEdit) "Edit Layanan" else "Tambah Layanan",
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
            // Service Form Card
            ServiceFormCard(
                name = name,
                price = price,
                unit = unit,
                onNameChange = { name = it },
                onPriceChange = { price = it },
                onUnitChange = { unit = it }
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
                text = if (isEdit) "Update Layanan" else "Simpan Layanan",
                enabled = isFormValid,
                onClick = { onSave(name, price.toIntOrNull() ?: 0, unit, isActive) }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceFormScreenPreview() {
    WashCleanerTheme {
        ServiceFormContent()
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceFormScreenEditPreview() {
    WashCleanerTheme {
        ServiceFormContent(
            isEdit = true,
            initialService = ServiceEntity(
                id = 1,
                name = "Cuci Kering",
                price = 5000,
                unit = "kg",
                isActive = true
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceFormScreenErrorPreview() {
    WashCleanerTheme {
        ServiceFormContent(
            error = "Terjadi kesalahan saat menyimpan data"
        )
    }
}
