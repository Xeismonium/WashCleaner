package com.xeismonium.washcleaner.ui.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.domain.model.Service

@Composable
fun ServiceItemRow(
    service: Service,
    formattedPrice: String,
    onEdit: (Service) -> Unit,
    onDelete: (Service) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = service.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = formattedPrice, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
            
            Row {
                IconButton(onClick = { onEdit(service) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { onDelete(service) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
