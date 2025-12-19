package com.xeismonium.washcleaner.ui.components.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity

@Composable
fun TransactionFilterChips(
    selectedStatus: String?,
    onStatusSelected: (String?) -> Unit,
    transactions: List<LaundryTransactionEntity>,
    modifier: Modifier = Modifier
) {
    val prosesCount = transactions.count { it.status.lowercase() in listOf("baru", "proses", "diproses") }
    val selesaiCount = transactions.count { it.status.lowercase() in listOf("selesai", "diambil") }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            TransactionFilterChip(
                label = "Semua",
                count = null,
                isSelected = selectedStatus == null,
                onClick = { onStatusSelected(null) }
            )
        }
        item {
            TransactionFilterChip(
                label = "Proses",
                count = if (prosesCount > 0) prosesCount else null,
                isSelected = selectedStatus == "proses",
                onClick = { onStatusSelected("proses") }
            )
        }
        item {
            TransactionFilterChip(
                label = "Selesai",
                count = if (selesaiCount > 0) selesaiCount else null,
                isSelected = selectedStatus == "selesai",
                onClick = { onStatusSelected("selesai") }
            )
        }
    }
}

@Composable
fun TransactionFilterChip(
    label: String,
    count: Int?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) colorScheme.primary else colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) colorScheme.onPrimary else colorScheme.onSurface
            )
            count?.let {
                Text(
                    text = "($it)",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) colorScheme.onPrimary.copy(alpha = 0.8f) else colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
