package com.xeismonium.washcleaner.ui.order.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.domain.model.OrderStatus

@Composable
fun OrderFilterChips(
    selectedStatus: OrderStatus?,
    onStatusSelected: (OrderStatus?) -> Unit,
    modifier: Modifier = Modifier
) {
    val statuses = listOf(null) + OrderStatus.values().toList()

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(statuses) { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = { onStatusSelected(status) },
                label = { Text(status?.name ?: "ALL") }
            )
        }
    }
}
