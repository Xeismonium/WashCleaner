package com.xeismonium.washcleaner.ui.order.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xeismonium.washcleaner.domain.model.PaymentStatus
import com.xeismonium.washcleaner.ui.theme.PaymentDownPayment
import com.xeismonium.washcleaner.ui.theme.PaymentPaid
import com.xeismonium.washcleaner.ui.theme.PaymentUnpaid

@Composable
fun PaymentStatusChip(status: PaymentStatus, modifier: Modifier = Modifier) {
    val (backgroundColor, textColor) = when (status) {
        PaymentStatus.PAID -> PaymentPaid to Color.White
        PaymentStatus.UNPAID -> PaymentUnpaid to Color.White
        PaymentStatus.PARTIAL -> PaymentDownPayment to Color.White
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.name,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            )
        )
    }
}
