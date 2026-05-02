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
import com.xeismonium.washcleaner.domain.model.OrderStatus
import com.xeismonium.washcleaner.ui.theme.*

@Composable
fun OrderStatusBadge(status: OrderStatus, modifier: Modifier = Modifier) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.RECEIVED -> StatusReceived to Color.White
        OrderStatus.WASHING -> StatusWashing to Color.White
        OrderStatus.DRYING -> StatusWashing to Color.White
        OrderStatus.IRONING -> StatusIroning to Color.White
        OrderStatus.READY -> StatusReady to Color.White
        OrderStatus.PICKED_UP -> StatusDone to Color.White
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
