package com.xeismonium.washcleaner.ui.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xeismonium.washcleaner.domain.model.Customer
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCustomerScreen(
    customerId: String?,
    onNavigateBack: () -> Unit,
    viewModel: CustomerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(customerId) {
        if (customerId != null) {
            viewModel.getCustomerDetail(customerId)
        }
    }

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    
    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }

    val phoneRegex = Regex("^(\\+62|08)[0-9]{7,12}$")

    LaunchedEffect(uiState) {
        if (uiState is CustomerUiState.Success) {
            val customer = (uiState as CustomerUiState.Success).selectedCustomer
            if (customer != null && customerId != null) {
                name = customer.name
                phone = customer.phone
                address = customer.address
            }
        }
    }

    AddEditCustomerContent(
        isEdit = customerId != null,
        name = name,
        onNameChange = { 
            name = it
            nameError = if (it.isBlank()) "Nama tidak boleh kosong" else null
        },
        nameError = nameError,
        phone = phone,
        onPhoneChange = { 
            phone = it
            phoneError = if (!phoneRegex.matches(it)) "Format telepon tidak valid (08... atau +62...)" else null
        },
        phoneError = phoneError,
        address = address,
        onAddressChange = { address = it },
        onNavigateBack = onNavigateBack,
        onSave = {
            val isNameValid = name.isNotBlank()
            val isPhoneValid = phoneRegex.matches(phone)
            
            if (!isNameValid) nameError = "Nama tidak boleh kosong"
            if (!isPhoneValid) phoneError = "Format telepon tidak valid (08... atau +62...)"
            
            if (isNameValid && isPhoneValid) {
                val customer = Customer(
                    id = customerId ?: "",
                    name = name,
                    phone = phone,
                    address = address,
                    createdAt = if (customerId == null) System.currentTimeMillis() else (uiState as? CustomerUiState.Success)?.selectedCustomer?.createdAt ?: System.currentTimeMillis()
                )
                viewModel.upsertCustomer(customer) {
                    onNavigateBack()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCustomerContent(
    isEdit: Boolean,
    name: String,
    onNameChange: (String) -> Unit,
    nameError: String?,
    phone: String,
    onPhoneChange: (String) -> Unit,
    phoneError: String?,
    address: String,
    onAddressChange: (String) -> Unit,
    onNavigateBack: () -> Unit,
    onSave: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Pelanggan" else "Tambah Pelanggan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Nama") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it) } },
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                label = { Text("Telepon") },
                modifier = Modifier.fillMaxWidth(),
                isError = phoneError != null,
                supportingText = phoneError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                placeholder = { Text("08... atau +62...") }
            )

            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("Alamat") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Simpan")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditCustomerPreview() {
    WashCleanerTheme {
        AddEditCustomerContent(
            isEdit = false,
            name = "",
            onNameChange = {},
            nameError = null,
            phone = "",
            onPhoneChange = {},
            phoneError = null,
            address = "",
            onAddressChange = {},
            onNavigateBack = {},
            onSave = {}
        )
    }
}
