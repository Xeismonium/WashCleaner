package com.xeismonium.washcleaner.ui.components.transaction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.util.PaymentStatus
import com.xeismonium.washcleaner.util.PaymentUtils

@Composable
fun PaymentStatusBadge(
    paidAmount: Double,
    totalPrice: Double,
    modifier: Modifier = Modifier,
    showIcon: Boolean = true
) {
    val status = PaymentUtils.getPaymentStatus(paidAmount, totalPrice)
    val color = PaymentUtils.getPaymentColor(status)
    val label = PaymentUtils.getPaymentLabel(status)

    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(50)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showIcon) {
                Icon(
                    imageVector = when (status) {
                        PaymentStatus.UNPAID -> Icons.Default.Cancel
                        PaymentStatus.PARTIAL -> Icons.Default.HourglassTop
                        PaymentStatus.PAID -> Icons.Default.CheckCircle
                    },
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
    }
}
