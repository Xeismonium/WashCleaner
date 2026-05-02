package com.xeismonium.washcleaner.ui.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun StoreInfoForm(
    initialName: String,
    initialAddress: String,
    onSave: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var address by remember(initialAddress) { mutableStateOf(initialAddress) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Store Information", style = MaterialTheme.typography.titleMedium)
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Store Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { onSave(name, address) },
            modifier = Modifier.align(androidx.compose.ui.Alignment.End),
            enabled = name.isNotBlank() && address.isNotBlank()
        ) {
            Text("Save Settings")
        }
    }
}
