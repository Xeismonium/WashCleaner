package com.xeismonium.washcleaner.ui.components.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.data.local.database.entity.ServiceEntity
import java.text.NumberFormat
import java.util.Locale
import java.util.UUID

data class ServiceRow(
    val id: String = UUID.randomUUID().toString(),
    val serviceId: Long? = null,
    val weightKg: String = "",
    val subtotal: Double = 0.0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceRowItem(
    row: ServiceRow,
    services: List<ServiceEntity>,
    onServiceChanged: (Long?) -> Unit,
    onWeightChanged: (String) -> Unit,
    onRemove: (() -> Unit)?
) {
    var showServiceDropdown by remember { mutableStateOf(false) }
    val selectedService = services.find { it.id == row.serviceId }
    val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Item Layanan",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                onRemove?.let {
                    IconButton(onClick = it, modifier = Modifier.size(32.dp)) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Hapus",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Service Selection
            ExposedDropdownMenuBox(
                expanded = showServiceDropdown,
                onExpandedChange = { showServiceDropdown = it }
            ) {
                OutlinedTextField(
                    value = selectedService?.name ?: "",
                    onValueChange = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text("Pilih Layanan") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = showServiceDropdown)
                    },
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = showServiceDropdown,
                    onDismissRequest = { showServiceDropdown = false }
                ) {
                    services.forEach { service ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(service.name, fontWeight = FontWeight.Bold)
                                    Text(
                                        formatter.format(service.price) + "/${service.unit}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            onClick = {
                                onServiceChanged(service.id)
                                showServiceDropdown = false
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Weight Input
                OutlinedTextField(
                    value = row.weightKg,
                    onValueChange = onWeightChanged,
                    modifier = Modifier.weight(1f),
                    label = { Text("Berat (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    enabled = selectedService != null
                )

                // Subtotal Display
                OutlinedTextField(
                    value = formatter.format(row.subtotal),
                    onValueChange = {},
                    modifier = Modifier.weight(1f),
                    label = { Text("Subtotal") },
                    readOnly = true,
                    enabled = false
                )
            }
        }
    }
}
