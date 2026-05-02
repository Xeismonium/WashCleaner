package com.xeismonium.washcleaner.ui.settings.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.domain.model.User

@Composable
fun StaffItemRow(
    user: User,
    onToggleActivation: (User) -> Unit,
    onDelete: (User) -> Unit,
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
                Text(text = user.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = user.email, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = if (user.isActive) "Active" else "Inactive",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (user.isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = user.isActive,
                    onCheckedChange = { onToggleActivation(user) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = { onDelete(user) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete Staff", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
