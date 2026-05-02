package com.xeismonium.washcleaner.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xeismonium.washcleaner.domain.model.Customer
import com.xeismonium.washcleaner.domain.model.Order
import com.xeismonium.washcleaner.domain.model.Service
import com.xeismonium.washcleaner.core.utils.CurrencyFormatter
import com.xeismonium.washcleaner.core.utils.DateFormatter
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@Composable
fun CreateOrderScreen(
    onNavigateBack: () -> Unit,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val customers by viewModel.customers.collectAsState()
    val services by viewModel.services.collectAsState()

    CreateOrderContent(
        customers = customers,
        services = services,
        onNavigateBack = onNavigateBack,
        onCreateOrder = { order ->
            viewModel.createOrder(order) { success ->
                if (success) onNavigateBack()
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateOrderContent(
    customers: List<Customer>,
    services: List<Service>,
    onNavigateBack: () -> Unit,
    onCreateOrder: (Order) -> Unit
) {
    var selectedCustomer by remember { mutableStateOf<Customer?>(null) }
    var selectedService by remember { mutableStateOf<Service?>(null) }
    var weight by remember { mutableStateOf("") }
    var pickupDate by remember { mutableStateOf(System.currentTimeMillis() + 86400000 * 2) }

    val totalPrice by remember {
        derivedStateOf {
            val w = weight.toDoubleOrNull() ?: 0.0
            val p = selectedService?.price ?: 0.0
            w * p
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var customerExpanded by remember { mutableStateOf(false) }
    var serviceExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buat Pesanan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = customerExpanded,
                onExpandedChange = { customerExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedCustomer?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Pelanggan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = customerExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = customerExpanded,
                    onDismissRequest = { customerExpanded = false }
                ) {
                    customers.forEach { customer ->
                        DropdownMenuItem(
                            text = { Text(customer.name) },
                            onClick = {
                                selectedCustomer = customer
                                customerExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = serviceExpanded,
                onExpandedChange = { serviceExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedService?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Layanan") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = serviceExpanded,
                    onDismissRequest = { serviceExpanded = false }
                ) {
                    services.forEach { service ->
                        DropdownMenuItem(
                            text = { Text("${service.name} (${CurrencyFormatter.formatRupiah(service.price)}/${service.unit})") },
                            onClick = {
                                selectedService = service
                                serviceExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = weight,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) weight = it },
                label = { Text("Berat / Jumlah (${selectedService?.unit ?: ""})") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = DateFormatter.format(pickupDate),
                onValueChange = {},
                readOnly = true,
                label = { Text("Tanggal Selesai") },
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Pilih Tanggal")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Total Harga", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = CurrencyFormatter.formatRupiah(totalPrice),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val customer = selectedCustomer ?: return@Button
                    val service = selectedService ?: return@Button
                    val w = weight.toDoubleOrNull() ?: return@Button
                    
                    val order = Order(
                        customerId = customer.id,
                        customerName = customer.name,
                        serviceId = service.id,
                        serviceName = service.name,
                        weight = w,
                        totalPrice = totalPrice,
                        pickupDate = pickupDate
                    )
                    onCreateOrder(order)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedCustomer != null && selectedService != null && weight.isNotBlank()
            ) {
                Text("Simpan Pesanan")
            }
        }

        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = pickupDate
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        pickupDate = datePickerState.selectedDateMillis ?: pickupDate
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Batal")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateOrderPreview() {
    WashCleanerTheme {
        CreateOrderContent(
            customers = listOf(Customer(name = "Budi")),
            services = listOf(Service(name = "Cuci Lipat", price = 5000.0)),
            onNavigateBack = {},
            onCreateOrder = {}
        )
    }
}
