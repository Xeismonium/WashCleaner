package com.xeismonium.washcleaner.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xeismonium.washcleaner.domain.model.User
import com.xeismonium.washcleaner.ui.settings.components.StaffItemRow
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffManagementScreen(
    onNavigateBack: () -> Unit,
    viewModel: StaffViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    StaffManagementContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onAddStaffClick = { showAddDialog = true },
        onToggleActivation = viewModel::toggleStaffActivation,
        onDeleteStaff = { viewModel.deleteStaff(it.id) }
    )

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Create Staff Account") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.createStaff(name, email, password)
                        showAddDialog = false
                    },
                    enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank()
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffManagementContent(
    uiState: StaffUiState,
    onNavigateBack: () -> Unit,
    onAddStaffClick: () -> Unit,
    onToggleActivation: (User) -> Unit,
    onDeleteStaff: (User) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff Management") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onAddStaffClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add Staff")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(padding))
        }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            items(uiState.staffList) { staff ->
                StaffItemRow(
                    user = staff,
                    onToggleActivation = onToggleActivation,
                    onDelete = onDeleteStaff
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StaffManagementPreview() {
    WashCleanerTheme {
        StaffManagementContent(
            uiState = StaffUiState(),
            onNavigateBack = {},
            onAddStaffClick = {},
            onToggleActivation = {},
            onDeleteStaff = {}
        )
    }
}
