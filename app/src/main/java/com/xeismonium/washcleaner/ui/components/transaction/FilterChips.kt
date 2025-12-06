package com.xeismonium.washcleaner.ui.components.transaction

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.ui.theme.StatusCompleted
import com.xeismonium.washcleaner.ui.theme.StatusProcessing
import com.xeismonium.washcleaner.ui.theme.StatusReady

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChips(
    selectedStatus: String?,
    onStatusSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier.horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // All filter
        ModernFilterChip(
            selected = selectedStatus == null,
            onClick = { onStatusSelected(null) },
            label = "Semua",
            icon = Icons.Default.AllInclusive,
            selectedColor = MaterialTheme.colorScheme.primary
        )

        // Process filter
        ModernFilterChip(
            selected = selectedStatus == "proses",
            onClick = { onStatusSelected("proses") },
            label = "Proses",
            icon = Icons.Default.LocalLaundryService,
            selectedColor = StatusProcessing
        )

        // Ready filter
        ModernFilterChip(
            selected = selectedStatus == "siap",
            onClick = { onStatusSelected("siap") },
            label = "Siap",
            icon = Icons.Default.Inventory,
            selectedColor = StatusReady
        )

        // Completed filter
        ModernFilterChip(
            selected = selectedStatus == "selesai",
            onClick = { onStatusSelected("selesai") },
            label = "Selesai",
            icon = Icons.Default.CheckCircle,
            selectedColor = StatusCompleted
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    icon: ImageVector,
    selectedColor: androidx.compose.ui.graphics.Color
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) selectedColor.copy(alpha = 0.15f)
        else MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(200),
        label = "containerColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) selectedColor
        else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "contentColor"
    )

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = if (selected) Icons.Default.Check else icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        },
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = containerColor,
            labelColor = contentColor,
            iconColor = contentColor,
            selectedContainerColor = containerColor,
            selectedLabelColor = contentColor,
            selectedLeadingIconColor = contentColor
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = if (selected) selectedColor.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            selectedBorderColor = selectedColor.copy(alpha = 0.3f),
            enabled = true,
            selected = selected
        )
    )
}
