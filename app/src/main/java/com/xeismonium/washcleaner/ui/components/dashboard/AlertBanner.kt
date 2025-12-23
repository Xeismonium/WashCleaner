package com.xeismonium.washcleaner.ui.components.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme

/**
 * Warning banner for overdue transactions
 *
 * Features:
 * - Only shown when overdueCount > 0
 * - Warning icon with pulsing animation
 * - Count display
 * - Clickable to navigate to filtered transaction list
 * - Slide-in animation on appearance
 *
 * @param overdueCount Number of overdue transactions
 * @param onClick Click handler for navigation
 * @param modifier Modifier for customization
 */
@Composable
fun AlertBanner(
    overdueCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = overdueCount > 0,
        enter = fadeIn(tween(300)) + slideInVertically(initialOffsetY = { -it / 2 })
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "pulseAnimation")
        val alpha by infiniteTransition.animateFloat(
            initialValue = 0.7f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween<Float>(1000),
                repeatMode = RepeatMode.Reverse
            ),
            label = "alphaAnimation"
        )

        Card(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    modifier = Modifier
                        .size(24.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = alpha)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "$overdueCount transaksi telah melewati batas waktu",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Preview(name = "With Overdue Items")
@Composable
private fun AlertBannerPreview() {
    WashCleanerTheme {
        AlertBanner(
            overdueCount = 5,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Dark Mode")
@Composable
private fun AlertBannerPreviewDark() {
    WashCleanerTheme(darkTheme = true) {
        AlertBanner(
            overdueCount = 3,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "No Overdue (Hidden)")
@Composable
private fun AlertBannerPreviewHidden() {
    WashCleanerTheme {
        AlertBanner(
            overdueCount = 0,
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
