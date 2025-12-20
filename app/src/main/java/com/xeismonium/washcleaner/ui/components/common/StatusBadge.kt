package com.xeismonium.washcleaner.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FiberNew
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.ui.theme.StatusCancelled
import com.xeismonium.washcleaner.ui.theme.StatusCompleted
import com.xeismonium.washcleaner.ui.theme.StatusNew
import com.xeismonium.washcleaner.ui.theme.StatusProcessing
import com.xeismonium.washcleaner.ui.theme.StatusReady

@Composable
fun StatusBadge(
    status: String,
    showIcon: Boolean = true
) {
    val (backgroundColor, textColor, text, icon) = when (status.lowercase()) {
        "baru" -> StatusData(
            StatusNew.copy(alpha = 0.15f),
            StatusNew,
            "Baru",
            Icons.Default.FiberNew
        )
        "proses" -> StatusData(
            StatusProcessing.copy(alpha = 0.15f),
            StatusProcessing,
            "Proses",
            Icons.Default.LocalLaundryService
        )
        "siap" -> StatusData(
            StatusReady.copy(alpha = 0.15f),
            StatusReady,
            "Siap",
            Icons.Default.Inventory
        )
        "selesai" -> StatusData(
            StatusCompleted.copy(alpha = 0.15f),
            StatusCompleted,
            "Selesai",
            Icons.Default.CheckCircle
        )
        else -> StatusData(
            StatusCancelled.copy(alpha = 0.15f),
            StatusCancelled,
            "Batal",
            Icons.Default.Cancel
        )
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showIcon) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )
        }
    }
}

private data class StatusData(
    val backgroundColor: androidx.compose.ui.graphics.Color,
    val textColor: androidx.compose.ui.graphics.Color,
    val text: String,
    val icon: ImageVector
)
