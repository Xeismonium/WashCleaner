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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import com.xeismonium.washcleaner.ui.components.transaction.ServiceRow
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import java.text.NumberFormat
import java.util.Locale

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
        }
    }

    TransactionFormContent(
        isEdit = transactionId != 0L,
        customers = uiState.customers,
        services = uiState.services,
        onSave = { customerId, customerName, serviceRows, status ->
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
    customers: List<CustomerEntity> = emptyList(),
    services: List<ServiceEntity> = emptyList(),
    onSave: (Long?, String?, List<ServiceRow>, String) -> Unit = { _, _, _, _ -> },
    onCancel: () -> Unit = {},
    error: String? = null
) {
    var selectedCustomerId by remember { mutableStateOf<Long?>(null) }
    var customerName by remember { mutableStateOf("") }
    var serviceRows by remember { mutableStateOf(listOf(ServiceRow())) }
    var selectedStatus by remember { mutableStateOf("proses") }

    val totalPrice = serviceRows.sumOf { it.subtotal }
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

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
                            onSave(selectedCustomerId, customerName, serviceRows, selectedStatus)
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
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Tambah",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                serviceRows.forEachIndexed { index, row ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
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

@Composable
private fun FormField(
    label: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        content()
    }
}

@Composable
private fun ModernInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingContent: @Composable (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val colorScheme = MaterialTheme.colorScheme

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) colorScheme.primary else colorScheme.outline.copy(alpha = 0.3f),
        animationSpec = tween(200),
        label = "borderColor"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused) 2.dp else 1.dp,
        animationSpec = tween(200),
        label = "borderWidth"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused)
            colorScheme.primaryContainer.copy(alpha = 0.08f)
        else
            colorScheme.surfaceVariant.copy(alpha = 0.3f),
        animationSpec = tween(200),
        label = "bgColor"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = colorScheme.onSurface
                    ),
                    singleLine = true,
                    keyboardOptions = keyboardOptions,
                    cursorBrush = SolidColor(colorScheme.primary),
                    interactionSource = interactionSource
                )
            }
            trailingContent?.invoke()
        }
    }
}

@Composable
private fun CustomerDropdownField(
    value: String,
    customers: List<CustomerEntity>,
    onValueChange: (String) -> Unit,
    onCustomerSelected: (CustomerEntity) -> Unit,
    placeholder: String
) {
    var expanded by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val colorScheme = MaterialTheme.colorScheme

    val borderColor by animateColorAsState(
        targetValue = if (isFocused || expanded) colorScheme.primary else colorScheme.outline.copy(alpha = 0.3f),
        animationSpec = tween(200),
        label = "borderColor"
    )

    val borderWidth by animateDpAsState(
        targetValue = if (isFocused || expanded) 2.dp else 1.dp,
        animationSpec = tween(200),
        label = "borderWidth"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isFocused || expanded)
            colorScheme.primaryContainer.copy(alpha = 0.08f)
        else
            colorScheme.surfaceVariant.copy(alpha = 0.3f),
        animationSpec = tween(200),
        label = "bgColor"
    )

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .border(borderWidth, borderColor, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = colorScheme.onSurface
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(colorScheme.primary),
                    interactionSource = interactionSource,
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyLarge,
                                color = colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                        innerTextField()
                    }
                )
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = null,
                    tint = if (expanded) colorScheme.primary else colorScheme.onSurfaceVariant
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            customers.forEach { customer ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(
                                text = customer.name,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = customer.phone,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onCustomerSelected(customer)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceItemRow(
    row: ServiceRow,
    services: List<ServiceEntity>,
    index: Int,
    onServiceChanged: (Long?) -> Unit,
    onWeightChanged: (String) -> Unit,
    onRemove: (() -> Unit)?
) {
    val selectedService = services.find { it.id == row.serviceId }
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.surfaceVariant.copy(alpha = 0.4f),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with remove button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Layanan #$index",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = colorScheme.primary
                )
                onRemove?.let {
                    Surface(
                        onClick = it,
                        shape = RoundedCornerShape(8.dp),
                        color = colorScheme.errorContainer.copy(alpha = 0.5f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Hapus",
                            tint = colorScheme.error,
                            modifier = Modifier
                                .padding(6.dp)
                                .size(16.dp)
                        )
                    }
                }
            }

            // Service Dropdown
            ServiceDropdownField(
                selectedService = selectedService,
                services = services,
                onServiceSelected = { onServiceChanged(it.id) }
            )

            // Weight and Subtotal Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Weight Input
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Berat",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    ModernInputField(
                        value = row.weightKg,
                        onValueChange = onWeightChanged,
                        placeholder = "0.0",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        trailingContent = {
                            Text(
                                text = "kg",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Medium,
                                color = colorScheme.primary
                            )
                        }
                    )
                }

                // Subtotal Display
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Subtotal",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colorScheme.primaryContainer.copy(alpha = 0.3f))
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        AnimatedContent(
                            targetState = formatter.format(row.subtotal),
                            transitionSpec = {
                                (fadeIn(tween(150))) togetherWith (fadeOut(tween(150)))
                            },
                            label = "subtotal"
                        ) { subtotal ->
                            Text(
                                text = subtotal,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceDropdownField(
    selectedService: ServiceEntity?,
    services: List<ServiceEntity>,
    onServiceSelected: (ServiceEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme
    val formatter = remember { NumberFormat.getCurrencyInstance(Locale("id", "ID")) }

    val borderColor by animateColorAsState(
        targetValue = if (expanded) colorScheme.primary else colorScheme.outline.copy(alpha = 0.3f),
        animationSpec = tween(200),
        label = "borderColor"
    )

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colorScheme.surface)
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = selectedService?.name ?: "Pilih jenis layanan",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selectedService != null)
                        colorScheme.onSurface
                    else
                        colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = null,
                    tint = if (expanded) colorScheme.primary else colorScheme.onSurfaceVariant
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            services.forEach { service ->
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = service.name,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${formatter.format(service.price)}/${service.unit}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    onClick = {
                        onServiceSelected(service)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun StatusDropdownField(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val colorScheme = MaterialTheme.colorScheme

    val statusOptions = listOf(
        "proses" to "Diproses",
        "siap" to "Siap Diambil",
        "selesai" to "Selesai"
    )

    val displayStatus = statusOptions.find { it.first == selectedStatus }?.second ?: "Pilih status"

    val borderColor by animateColorAsState(
        targetValue = if (expanded) colorScheme.primary else colorScheme.outline.copy(alpha = 0.3f),
        animationSpec = tween(200),
        label = "borderColor"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (expanded)
            colorScheme.primaryContainer.copy(alpha = 0.08f)
        else
            colorScheme.surfaceVariant.copy(alpha = 0.3f),
        animationSpec = tween(200),
        label = "bgColor"
    )

    Box {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = displayStatus,
                    style = MaterialTheme.typography.bodyLarge,
                    color = colorScheme.onSurface
                )
                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = null,
                    tint = if (expanded) colorScheme.primary else colorScheme.onSurfaceVariant
                )
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.92f)
        ) {
            statusOptions.forEach { (value, label) ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = label,
                            fontWeight = if (value == selectedStatus) FontWeight.Bold else FontWeight.Normal,
                            color = if (value == selectedStatus) colorScheme.primary else colorScheme.onSurface
                        )
                    },
                    onClick = {
                        onStatusSelected(value)
                        expanded = false
                    }
                )
            }
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
