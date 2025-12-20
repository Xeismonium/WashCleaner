package com.xeismonium.washcleaner.ui.components.report

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xeismonium.washcleaner.ui.screen.report.ChartDataPoint
import com.xeismonium.washcleaner.ui.theme.StatusCancelled
import com.xeismonium.washcleaner.ui.theme.StatusCompleted
import com.xeismonium.washcleaner.util.CurrencyUtils

@Composable
fun SegmentedButtons(
    selectedPeriod: String,
    onPeriodChange: (String) -> Unit
) {
    val periods = listOf(
        "day" to "Hari Ini",
        "week" to "Minggu Ini",
        "month" to "Bulan Ini"
    )

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            periods.forEach { (period, label) ->
                val isSelected = selectedPeriod == period
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .clickable { onPeriodChange(period) },
                    shape = RoundedCornerShape(50),
                    color = if (isSelected) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0f),
                    shadowElevation = if (isSelected) 2.dp else 0.dp
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryCard(
    totalRevenue: Double,
    trendPercentage: Double,
    selectedPeriod: String
) {
    val periodLabel = when (selectedPeriod) {
        "day" -> "Hari Ini"
        "week" -> "Minggu Ini"
        "month" -> "Bulan Ini"
        else -> "Periode"
    }

    val trendLabel = when (selectedPeriod) {
        "day" -> "dari kemarin"
        "week" -> "dari minggu lalu"
        "month" -> "dari bulan lalu"
        else -> "dari periode sebelumnya"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Total Pendapatan $periodLabel",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = CurrencyUtils.formatRupiah(totalRevenue),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isPositive = trendPercentage >= 0
                Icon(
                    imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (isPositive) StatusCompleted else StatusCancelled
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${if (isPositive) "+" else ""}${String.format("%.1f", trendPercentage)}% $trendLabel",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = if (isPositive) StatusCompleted else StatusCancelled
                )
            }
        }
    }
}

@Composable
fun RevenueChartCard(chartData: List<ChartDataPoint>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Grafik Pendapatan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (chartData.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada data",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val maxValue = chartData.maxOfOrNull { it.value } ?: 1.0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    chartData.forEach { dataPoint ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            // Bar
                            val heightFraction = if (maxValue > 0) (dataPoint.value / maxValue).toFloat().coerceIn(0.05f, 1f) else 0.05f
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.6f)
                                    .height((120 * heightFraction).dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (dataPoint.isHighlighted)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Label
                            Text(
                                text = dataPoint.label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (dataPoint.isHighlighted) FontWeight.Bold else FontWeight.Medium,
                                color = if (dataPoint.isHighlighted)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReportStatsCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}