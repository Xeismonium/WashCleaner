package com.xeismonium.washcleaner.ui.components.dashboard

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.ui.theme.WashCleanerTheme
import com.xeismonium.washcleaner.util.CurrencyUtils

/**
 * Premium hero card for revenue display
 *
 * Features:
 * - Gradient background
 * - Animated counter for today's revenue
 * - Today's revenue as primary metric (large display)
 * - Total revenue as secondary metric
 *
 * @param todayRevenue Revenue from today's completed transactions
 * @param totalRevenue Total revenue from all completed transactions
 * @param modifier Modifier for customization
 */
@Composable
fun HeroRevenueCard(
    todayRevenue: Double,
    totalRevenue: Double,
    modifier: Modifier = Modifier
) {
    var startAnimation by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    val animatedTodayRevenue by animateFloatAsState(
        targetValue = if (startAnimation) todayRevenue.toFloat() else 0f,
        animationSpec = tween(
            durationMillis = 1200,
            easing = FastOutSlowInEasing
        ),
        label = "todayRevenueAnimation"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val gradient = Brush.verticalGradient(
        colors = listOf(
            primaryColor,
            primaryColor.copy(alpha = 0.85f)
        )
    )

    // Dynamic text style based on amount length to prevent overflow
    val formattedTarget = CurrencyUtils.formatRupiah(todayRevenue)
    val revenueTextStyle = when {
        formattedTarget.length > 16 -> MaterialTheme.typography.headlineSmall
        formattedTarget.length > 12 -> MaterialTheme.typography.headlineMedium
        else -> MaterialTheme.typography.headlineLarge
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .background(gradient)
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Pendapatan Hari Ini",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = CurrencyUtils.formatRupiah(animatedTodayRevenue.toDouble()),
                        style = revenueTextStyle,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = "Revenue icon",
                    modifier = Modifier.size(48.dp),
                    tint = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column {
                Text(
                    text = "Total Pendapatan",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = CurrencyUtils.formatRupiah(totalRevenue),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}

@Preview(name = "Light Mode")
@Composable
private fun HeroRevenueCardPreview() {
    WashCleanerTheme {
        HeroRevenueCard(
            todayRevenue = 1500000.0,
            totalRevenue = 25000000.0,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Dark Mode")
@Composable
private fun HeroRevenueCardPreviewDark() {
    WashCleanerTheme(darkTheme = true) {
        HeroRevenueCard(
            todayRevenue = 850000.0,
            totalRevenue = 18500000.0,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Zero Revenue")
@Composable
private fun HeroRevenueCardPreviewZero() {
    WashCleanerTheme {
        HeroRevenueCard(
            todayRevenue = 0.0,
            totalRevenue = 5000000.0,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Large Revenue")
@Composable
private fun HeroRevenueCardPreviewLarge() {
    WashCleanerTheme {
        HeroRevenueCard(
            todayRevenue = 125500000.0,
            totalRevenue = 1250000000.0,
            modifier = Modifier.padding(16.dp)
        )
    }
}
