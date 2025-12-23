package com.xeismonium.washcleaner.ui.components.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.data.local.database.entity.LaundryTransactionEntity
import com.xeismonium.washcleaner.ui.components.common.StatusBadge
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import com.xeismonium.washcleaner.util.CurrencyUtils
import com.xeismonium.washcleaner.util.DateUtils

/**
 * Compact transaction item for recent transactions list
 *
 * Features:
 * - Customer name with colored avatar (initials)
 * - Status badge (smaller variant)
 * - Amount in compact format
 * - Time ago indicator
 * - Clickable to view details
 *
 * @param transaction Transaction entity to display
 * @param onClick Click handler for navigation to transaction details
 * @param modifier Modifier for customization
 */
@Composable
fun DashboardTransactionItem(
    transaction: LaundryTransactionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customerName = transaction.customerName ?: "Unknown"
    val initial = customerName.firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    // Generate color from customer name for avatar
    val avatarColor = when (customerName.hashCode() % 5) {
        0 -> Color(0xFF4CAF50)  // Green
        1 -> Color(0xFF2196F3)  // Blue
        2 -> Color(0xFFFF9800)  // Orange
        3 -> Color(0xFF9C27B0)  // Purple
        else -> Color(0xFFE91E63)  // Pink
    }

    val avatarGradient = Brush.linearGradient(
        colors = listOf(
            avatarColor,
            avatarColor.copy(alpha = 0.8f)
        )
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar + Name + Time
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with initial
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = avatarGradient,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = customerName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = DateUtils.getTimeAgo(transaction.dateIn),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Status + Amount
            Column(
                horizontalAlignment = Alignment.End
            ) {
                StatusBadge(status = transaction.status, compact = true)
                Text(
                    text = CurrencyUtils.formatRupiah(transaction.totalPrice),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Preview(name = "Light Mode")
@Composable
private fun DashboardTransactionItemPreview() {
    WashCleanerTheme {
        DashboardTransactionItem(
            transaction = LaundryTransactionEntity(
                id = 1,
                customerId = 1,
                customerName = "John Doe",
                totalPrice = 150000.0,
                dateIn = System.currentTimeMillis() - (5 * 60 * 1000), // 5 minutes ago
                dateOut = null,
                estimatedDate = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000),
                status = "proses",
                paidAmount = 0.0
            ),
            onClick = {},
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(name = "Dark Mode")
@Composable
private fun DashboardTransactionItemPreviewDark() {
    WashCleanerTheme(darkTheme = true) {
        DashboardTransactionItem(
            transaction = LaundryTransactionEntity(
                id = 2,
                customerId = 2,
                customerName = "Jane Smith",
                totalPrice = 250000.0,
                dateIn = System.currentTimeMillis() - (2 * 60 * 60 * 1000), // 2 hours ago
                dateOut = null,
                estimatedDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000),
                status = "siap",
                paidAmount = 250000.0
            ),
            onClick = {},
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(name = "Multiple Items")
@Composable
private fun DashboardTransactionItemPreviewMultiple() {
    WashCleanerTheme {
        Column {
            DashboardTransactionItem(
                transaction = LaundryTransactionEntity(
                    id = 1,
                    customerId = 1,
                    customerName = "Ahmad Rizki",
                    totalPrice = 75000.0,
                    dateIn = System.currentTimeMillis() - (30 * 60 * 1000), // 30 min ago
                    dateOut = null,
                    estimatedDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000),
                    status = "baru",
                    paidAmount = 0.0
                ),
                onClick = {},
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            DashboardTransactionItem(
                transaction = LaundryTransactionEntity(
                    id = 2,
                    customerId = 2,
                    customerName = "Siti Aminah",
                    totalPrice = 120000.0,
                    dateIn = System.currentTimeMillis() - (3 * 60 * 60 * 1000), // 3 hours ago
                    dateOut = null,
                    estimatedDate = System.currentTimeMillis() + (12 * 60 * 60 * 1000),
                    status = "proses",
                    paidAmount = 60000.0
                ),
                onClick = {},
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
            DashboardTransactionItem(
                transaction = LaundryTransactionEntity(
                    id = 3,
                    customerId = 3,
                    customerName = "Budi Santoso",
                    totalPrice = 200000.0,
                    dateIn = System.currentTimeMillis() - (24 * 60 * 60 * 1000), // 1 day ago
                    dateOut = System.currentTimeMillis(),
                    estimatedDate = System.currentTimeMillis(),
                    status = "selesai",
                    paidAmount = 200000.0
                ),
                onClick = {},
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
