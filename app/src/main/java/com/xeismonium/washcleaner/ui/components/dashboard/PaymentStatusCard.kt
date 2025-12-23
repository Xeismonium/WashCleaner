package com.xeismonium.washcleaner.ui.components.dashboard

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.ui.components.transaction.PaymentStatusBadge
import com.xeismonium.washcleaner.ui.theme.StatusCancelled
import com.xeismonium.washcleaner.ui.theme.StatusCompleted
import com.xeismonium.washcleaner.ui.theme.StatusProcessing
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import com.xeismonium.washcleaner.util.CurrencyUtils
import com.xeismonium.washcleaner.util.PaymentStatus
import com.xeismonium.washcleaner.util.PaymentUtils

/**
 * Compact card for payment status metrics
 *
 * Features:
 * - Payment status badge
 * - Transaction count
 * - Amount display
 * - Linear progress indicator
 * - Percentage text
 *
 * @param status Payment status (UNPAID, PARTIAL, PAID)
 * @param count Number of transactions with this status
 * @param amount Total or remaining amount for this status
 * @param totalAmount Optional total amount for calculating progress (used for partial payments)
 * @param modifier Modifier for customization
 */
@Composable
fun PaymentStatusCard(
    status: PaymentStatus,
    count: Int,
    amount: Double,
    totalAmount: Double? = null,
    modifier: Modifier = Modifier
) {
    val color = PaymentUtils.getPaymentColor(status)
    val label = PaymentUtils.getPaymentLabel(status)

    // Calculate progress
    val progress = if (totalAmount != null && totalAmount > 0) {
        (amount / totalAmount).toFloat().coerceIn(0f, 1f)
    } else {
        1f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(600, easing = EaseOutCubic),
        label = "progressAnimation"
    )

    val percentage = (progress * 100).toInt()

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Status badge
            PaymentStatusBadge(
                paidAmount = if (status == PaymentStatus.UNPAID) 0.0 else if (status == PaymentStatus.PAID) 100.0 else 50.0,
                totalPrice = 100.0
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Count and Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "$count",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Transaksi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = CurrencyUtils.formatRupiah(amount),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (status == PaymentStatus.PARTIAL) {
                        Text(
                            text = "Tersisa",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Progress",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = color
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = color,
                    trackColor = color.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Preview(name = "Unpaid Status")
@Composable
private fun PaymentStatusCardPreviewUnpaid() {
    WashCleanerTheme {
        PaymentStatusCard(
            status = PaymentStatus.UNPAID,
            count = 12,
            amount = 3500000.0,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(name = "Partial Status")
@Composable
private fun PaymentStatusCardPreviewPartial() {
    WashCleanerTheme {
        PaymentStatusCard(
            status = PaymentStatus.PARTIAL,
            count = 8,
            amount = 1200000.0,
            totalAmount = 2000000.0,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(name = "Paid Status")
@Composable
private fun PaymentStatusCardPreviewPaid() {
    WashCleanerTheme {
        PaymentStatusCard(
            status = PaymentStatus.PAID,
            count = 45,
            amount = 15000000.0,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(name = "Dark Mode")
@Composable
private fun PaymentStatusCardPreviewDark() {
    WashCleanerTheme(darkTheme = true) {
        PaymentStatusCard(
            status = PaymentStatus.PARTIAL,
            count = 5,
            amount = 850000.0,
            totalAmount = 1500000.0,
            modifier = Modifier.padding(8.dp)
        )
    }
}