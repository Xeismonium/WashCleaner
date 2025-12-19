package com.xeismonium.washcleaner.ui.screen.transaction

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xeismonium.washcleaner.data.local.database.entity.CustomerEntity
import com.xeismonium.washcleaner.data.local.database.entity.ServiceEntity
import com.xeismonium.washcleaner.data.repository.TransactionWithServicesData
import com.xeismonium.washcleaner.ui.components.common.ModernButton
import com.xeismonium.washcleaner.ui.components.transaction.CustomerDropdownField
import com.xeismonium.washcleaner.ui.components.transaction.DatePickerField
import com.xeismonium.washcleaner.ui.components.transaction.FormField
import com.xeismonium.washcleaner.ui.components.transaction.ServiceItemRow
import com.xeismonium.washcleaner.ui.components.transaction.ServiceRow
import com.xeismonium.washcleaner.ui.components.transaction.StatusDropdownField
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import com.xeismonium.washcleaner.data.local.database.entity.TransactionWithServices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormScreen(
    navController: NavController,
    transactionId: Long = 0,
    viewModel: TransactionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val events by viewModel.events.collectAsState()

    LaunchedEffect(events) {
        when (events) {
            is TransactionEvent.Success -> {
                viewModel.clearEvent()
                navController.navigateUp()
            }
            is TransactionEvent.Error -> {}
            null -> {}
        }
    }

    LaunchedEffect(transactionId) {
        if (transactionId != 0L) {
            viewModel.loadTransactionById(transactionId)
        } else {
            // Reset selected transaction when creating new
            // Assuming viewModel has a method to clear selection or it's null by default
        }
    }

    TransactionFormContent(
        isEdit = transactionId != 0L,
        initialTransaction = uiState.selectedTransaction,
        customers = uiState.customers,
        services = uiState.services,
        onSave = { customerId, customerName, serviceRows, dateIn, estimatedDate, status ->
            val serviceItems = serviceRows.mapNotNull { row ->
                row.serviceId?.let { serviceId ->
                    val weight = row.weightKg.toDoubleOrNull() ?: 0.0
                    if (weight > 0) {
                        TransactionWithServicesData.ServiceItem(
                            serviceId = serviceId,
                            weightKg = weight,
                            subtotalPrice = row.subtotal
                        )
                    } else null
                }
            }

            if (serviceItems.isNotEmpty()) {
                if (transactionId == 0L) {
                    viewModel.createTransactionWithServices(
                        customerId = customerId,
                        customerName = customerName,
                        services = serviceItems,
                        status = status
                    )
                } else {
                    viewModel.updateTransactionWithServices(
                        transactionId = transactionId,
                        customerId = customerId,
                        customerName = customerName,
                        services = serviceItems,
                        status = status,
                        dateOut = if (status == "selesai") System.currentTimeMillis() else null
                    )
                }
            }
        },
        onCancel = { navController.navigateUp() },
        error = uiState.error
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionFormContent(
    isEdit: Boolean = false,
    initialTransaction: TransactionWithServices? = null,
    customers: List<CustomerEntity> = emptyList(),
    services: List<ServiceEntity> = emptyList(),
    onSave: (Long?, String?, List<ServiceRow>, Long?, Long?, String) -> Unit = { _, _, _, _, _, _ -> },
    onCancel: () -> Unit = {},
    error: String? = null
) {
    var selectedCustomerId by remember { mutableStateOf<Long?>(null) }
    var customerName by remember { mutableStateOf("") }
    var serviceRows by remember { mutableStateOf(listOf(ServiceRow())) }
    var dateIn by remember { mutableStateOf<Long?>(System.currentTimeMillis()) }
    var estimatedDate by remember { mutableStateOf<Long?>(null) }
    var selectedStatus by remember { mutableStateOf("baru") }

    // Populate form when initialTransaction is loaded
    LaunchedEffect(initialTransaction) {
        initialTransaction?.let { tx ->
            selectedCustomerId = tx.transaction.customerId
            customerName = tx.transaction.customerName ?: ""
            dateIn = tx.transaction.dateIn
            // Estimated date logic might need adjustment if it's not stored directly or if we want to infer it
            // For now, let's keep it null or derive if possible. The entity doesn't seem to store estimated date explicitly in the provided snippet.
            // If needed, we can set it to dateIn + default duration.
            
            selectedStatus = tx.transaction.status

            if (tx.transactionServices.isNotEmpty()) {
                serviceRows = tx.transactionServices.map { item ->
                    ServiceRow(
                        serviceId = item.serviceId,
                        weightKg = item.weightKg.toString(),
                        subtotal = item.subtotalPrice
                    )
                }
            }
        }
    }

    val totalPrice = serviceRows.sumOf { it.subtotal }
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID")) }

    val isFormValid = customerName.isNotBlank() && serviceRows.any {
        it.serviceId != null && it.weightKg.toDoubleOrNull() != null && it.weightKg.toDouble() > 0
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEdit) "Edit Transaksi" else "Tambah Transaksi Baru",
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
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 3.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    // Total Price with animation
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Harga",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        AnimatedContent(
                            targetState = formatter.format(totalPrice),
                            transitionSpec = {
                                (slideInVertically { -it } + fadeIn()) togetherWith
                                        (slideOutVertically { it } + fadeOut())
                            },
                            label = "price"
                        ) { price ->
                            Text(
                                text = price,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    // Save Button
                    ModernButton(
                        text = "Simpan Transaksi",
                        enabled = isFormValid,
                        onClick = {
                            onSave(
                                selectedCustomerId,
                                customerName,
                                serviceRows,
                                dateIn,
                                estimatedDate,
                                selectedStatus
                            )
                        }
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Error Message
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

            // Customer Name
            FormField(label = "Nama Pelanggan") {
                CustomerDropdownField(
                    value = customerName,
                    customers = customers,
                    onValueChange = { name ->
                        customerName = name
                        selectedCustomerId = customers.find { it.name == name }?.id
                    },
                    onCustomerSelected = { customer ->
                        customerName = customer.name
                        selectedCustomerId = customer.id
                    },
                    placeholder = "Masukkan nama pelanggan"
                )
            }

            // Services Section
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Layanan",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Surface(
                        onClick = { serviceRows = serviceRows + ServiceRow() },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "+ Tambah",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                serviceRows.forEachIndexed { index, row ->
                    ServiceItemRow(
                        row = row,
                        services = services,
                        index = index + 1,
                        onServiceChanged = { serviceId ->
                            serviceRows = serviceRows.toMutableList().apply {
                                this[index] = row.copy(serviceId = serviceId)
                            }
                        },
                        onWeightChanged = { weight ->
                            val selectedService = services.find { it.id == row.serviceId }
                            val weightValue = weight.toDoubleOrNull() ?: 0.0
                            val subtotal = if (selectedService != null) {
                                weightValue * selectedService.price
                            } else 0.0

                            serviceRows = serviceRows.toMutableList().apply {
                                this[index] = row.copy(weightKg = weight, subtotal = subtotal)
                            }
                        },
                        onRemove = if (serviceRows.size > 1) {
                            { serviceRows = serviceRows.filterIndexed { i, _ -> i != index } }
                        } else null
                    )
                }
            }

            // Date Fields Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tanggal Masuk
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Tanggal Masuk",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    DatePickerField(
                        selectedDate = dateIn,
                        onDateSelected = { dateIn = it },
                        placeholder = "Pilih tanggal",
                        dateFormatter = dateFormatter
                    )
                }

                // Estimasi Selesai
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Estimasi Selesai",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    DatePickerField(
                        selectedDate = estimatedDate,
                        onDateSelected = { estimatedDate = it },
                        placeholder = "Pilih tanggal",
                        dateFormatter = dateFormatter
                    )
                }
            }

            // Status Selection
            FormField(label = "Status Transaksi") {
                StatusDropdownField(
                    selectedStatus = selectedStatus,
                    onStatusSelected = { selectedStatus = it }
                )
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TransactionFormPreview() {
    WashCleanerTheme {
        TransactionFormContent(
            customers = listOf(
                CustomerEntity(id = 1, name = "John Doe", phone = "08123456789", address = "Jl. Example"),
                CustomerEntity(id = 2, name = "Jane Smith", phone = "08234567890", address = "Jl. Test")
            ),
            services = listOf(
                ServiceEntity(id = 1, name = "Cuci Kering", price = 5000, isActive = true),
                ServiceEntity(id = 2, name = "Cuci Setrika", price = 7000, isActive = true),
                ServiceEntity(id = 3, name = "Setrika Saja", price = 3000, isActive = true)
            )
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun TransactionFormPreviewDark() {
    WashCleanerTheme {
        TransactionFormContent(
            customers = listOf(
                CustomerEntity(id = 1, name = "John Doe", phone = "08123456789", address = "Jl. Example")
            ),
            services = listOf(
                ServiceEntity(id = 1, name = "Cuci Kering", price = 5000, isActive = true),
                ServiceEntity(id = 2, name = "Cuci Setrika", price = 7000, isActive = true)
            )
        )
    }
}
